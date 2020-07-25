package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.NameNormaliser;

import javax.sql.RowSet;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/*
* Based on article posted on
* http://scn.sap.com/community/hana-in-memory/blog/2015/01/08/using-dbfit-to-automate-hana-procedure-testing
*
*  @author: Bodo Huesemann (bhuesemann@informationsfabrik.de)
*
*/
@DatabaseEnvironment(name = "Hana", driver = "com.sap.db.jdbc.Driver")
public class HanaEnvironment extends AbstractDbEnvironment {
    public HanaEnvironment(String driverClassName) {
        super(driverClassName);
    }

    protected String getConnectionString(String dataSource) {
        return "jdbc:sap://" + dataSource;
    }

    protected String getConnectionString(String dataSource, String database) {
        return "jdbc:sap://" + dataSource + "/" + database;
    }

    private static String paramNamePattern = "_:([A-Za-z0-9_]+)";
    private static Pattern paramsNames = Pattern.compile(paramNamePattern);

    public Pattern getParameterPattern() {
        return paramsNames;
    }

    // override the buildInsertPreparedStatement to leave out RETURN_GENERATED_KEYS
    // http://scn.sap.com/thread/3340106 (feature not supported yet)
    public PreparedStatement buildInsertPreparedStatement(String tableName, DbParameterAccessor[] accessors)
            throws SQLException {
        return getConnection().prepareStatement(buildInsertCommand(tableName, accessors));
    }

    // hana jdbc driver does not support named parameters - so just map them
    // to standard jdbc question marks
    protected String parseCommandText(String commandText) {
        commandText = commandText.replaceAll(paramNamePattern, "?");
        return super.parseCommandText(commandText);
    }

    private String[] parseObjectName(String tableOrViewName) {
        String[] qualifiers = new String[2];
        int firstStop = tableOrViewName.indexOf(".");
        if (firstStop > 1) {
            qualifiers[0] = tableOrViewName.substring(firstStop + 1); // table/procedure/function name
            qualifiers[1] = tableOrViewName.substring(0, firstStop); // schemaname
        } else {
            // we have no given schema, this may lead to multiple hits
            // if the same table_name is used in different schemas
            // TODO consider grabbing the current schema here to account for the same table
            // in multiple schemas
            qualifiers = new String[] { tableOrViewName };
        }

        // strip quotes from qualifiers to match dictionary tables
        // or initialize to empty string if null
        for (int i = 0; i < qualifiers.length; i++) {
            qualifiers[i] = (qualifiers[i] != null) ? qualifiers[i].replace("\"", "") : "";
        }
        return qualifiers;
    }

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName) throws SQLException {
        String[] qualifiers = parseObjectName(tableOrViewName);
        String qry = " SELECT COLUMN_NAME, DATA_TYPE_NAME FROM SYS.TABLE_COLUMNS \n" + "WHERE --~schemaclause~ \n"
                + "TABLE_NAME = ?  \n" + "ORDER BY POSITION";

        if (qualifiers.length > 1) {
            // add schema_clause
            qry = qry.replace("--~schemaclause~", " SCHEMA_NAME = ? AND ");
        }

        return readIntoParams(qualifiers, qry);
    }

    private Map<String, DbParameterAccessor> readIntoParams(String[] queryParameters, String query)
            throws SQLException {
        try (PreparedStatement dc = currentConnection.prepareStatement(query)) {
            for (int i = 0; i < queryParameters.length; i++) {
                dc.setString(i + 1, queryParameters[i]);
            }
            ResultSet rs = dc.executeQuery();

            Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();
            int position = 0;
            while (rs.next()) {
                String paramName = defaultIfNull(rs.getString(1), "");

                String dataType = rs.getString(2);
                DbParameterAccessor dbp = new DbParameterAccessor(paramName, Direction.INPUT, getSqlType(dataType),
                        getJavaClass(dataType), position++, this.dbfitToJdbcTransformerFactory);
                allParams.put(NameNormaliser.normaliseName(paramName), dbp);
            }
            rs.close();
            return allParams;
        }
    }

    // List interface has sequential search, so using list instead of array to
    // map types
    private static List<String> stringTypes = Arrays
            .asList(new String[] { "NVARCHAR", "VARCHAR", "CHAR", "TEXT", "ALPHANUM" });
    private static List<String> intTypes = Arrays.asList(new String[] { "TINYINT", "SMALLINT", "INT", "INTEGER" });
    private static List<String> longTypes = Arrays
            .asList(new String[] { "BIGINT", "INTEGER UNSIGNED", "INT UNSIGNED" });
    private static List<String> floatTypes = Arrays.asList(new String[] { "REAL", "FLOAT" });
    private static List<String> doubleTypes = Arrays.asList(new String[] { "DOUBLE" });
    private static List<String> decimalTypes = Arrays.asList(new String[] { "DECIMAL", "DEC" });
    private static List<String> dateTypes = Arrays.asList(new String[] { "DATE" });
    private static List<String> timestampTypes = Arrays.asList(new String[] { "TIMESTAMP", "DATETIME" });
    private static List<String> timeTypes = Arrays.asList(new String[] { "TIME" });
    private static List<String> refCursorTypes = Arrays.asList(new String[] {});
    private static List<String> booleanTypes = Arrays.asList(new String[] {});
    private static List<String> varBinaryTypes = Arrays.asList(new String[] { "VARBINARY" });
    private static List<String> blobTypes = Arrays.asList(new String[] { "BLOB", "LOB" });
    private static List<String> clobTypes = Arrays.asList(new String[] { "CLOB", "NCLOB" });

    private static String normaliseTypeName(String dataType) {
        if (dataType.indexOf("(") <= 0) {
            dataType = dataType.toUpperCase().trim();
        } else {
            dataType = dataType.toUpperCase().trim().substring(0, dataType.indexOf("("));
        }
        return dataType;
    }

    private static int getSqlType(String dataType) {
        // todo:strip everything from first blank
        dataType = normaliseTypeName(dataType);

        if (stringTypes.contains(dataType))
            return java.sql.Types.VARCHAR;
        if (decimalTypes.contains(dataType))
            return java.sql.Types.NUMERIC;
        if (intTypes.contains(dataType))
            return java.sql.Types.INTEGER;
        if (floatTypes.contains(dataType))
            return java.sql.Types.FLOAT;
        if (doubleTypes.contains(dataType))
            return java.sql.Types.DOUBLE;
        if (longTypes.contains(dataType))
            return java.sql.Types.BIGINT;
        if (timestampTypes.contains(dataType))
            return java.sql.Types.TIMESTAMP;
        if (dateTypes.contains(dataType))
            return java.sql.Types.DATE;
        if (dateTypes.contains(dataType))
            return java.sql.Types.TIME;
        if (refCursorTypes.contains(dataType))
            return java.sql.Types.REF;
        if (booleanTypes.contains(dataType))
            return java.sql.Types.BOOLEAN;
        if (clobTypes.contains(dataType))
            return java.sql.Types.CLOB;
        if (blobTypes.contains(dataType))
            return java.sql.Types.BLOB;
        if (varBinaryTypes.contains(dataType))
            return java.sql.Types.VARBINARY;

        throw new UnsupportedOperationException("Type " + dataType + " is not supported");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Class getJavaClass(String dataType) {
        dataType = normaliseTypeName(dataType);
        if (stringTypes.contains(dataType))
            return String.class;
        if (decimalTypes.contains(dataType))
            return BigDecimal.class;
        if (intTypes.contains(dataType))
            return Integer.class;
        if (floatTypes.contains(dataType))
            return Float.class;
        if (dateTypes.contains(dataType))
            return java.sql.Date.class;
        if (refCursorTypes.contains(dataType))
            return RowSet.class;
        if (doubleTypes.contains(dataType))
            return Double.class;
        if (longTypes.contains(dataType))
            return Long.class;
        if (timeTypes.contains(dataType))
            return java.sql.Time.class;
        if (timestampTypes.contains(dataType))
            return java.sql.Timestamp.class;
        if (booleanTypes.contains(dataType))
            return Boolean.class;
        if (clobTypes.contains(dataType))
            return String.class;
        if (blobTypes.contains(dataType))
            return java.sql.Blob.class;
        if (varBinaryTypes.contains(dataType))
            return java.sql.Blob.class;
        throw new UnsupportedOperationException("Type " + dataType + " is not supported");
    }

    public Map<String, DbParameterAccessor> getAllProcedureParameters(String procName) throws SQLException {

        String[] qualifiers = parseObjectName(procName);

        String qry = " WITH PARAMS as (" + "select ? objectname \n" + " --~schemaclause1~ \n" + " from dummy " + ") "
                + "select PARAMETER_NAME, DATA_TYPE_NAME, LENGTH, PARAMETER_TYPE, POSITION "
                + "from SYS.PROCEDURE_PARAMETERS, PARAMS\n" + "where --~schemaclause2~ \n"
                + "procedure_name = params.objectname \n" + "UNION ALL \n"
                + "select PARAMETER_NAME, DATA_TYPE_NAME, LENGTH, PARAMETER_TYPE, POSITION "
                + "from SYS.FUNCTION_PARAMETERS, PARAMS\n" + "where --~schemaclause2~ \n"
                + "function_name = params.objectname \n" + "order by position";

        if (qualifiers.length > 1) {
            // activate schema_clause
            qry = qry.replaceAll("--~schemaclause1~", " ,? schemaname ");
            qry = qry.replaceAll("--~schemaclause2~", " SCHEMA_NAME = params.schemaname AND ");
        }
        return readIntoParams(qualifiers, qry);
    }

    public String buildInsertCommand(String tableName, DbParameterAccessor[] accessors) {
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(tableName).append("(");
        String comma = "";
        String retComma = "";

        StringBuilder values = new StringBuilder();
        StringBuilder retNames = new StringBuilder();
        StringBuilder retValues = new StringBuilder();

        for (DbParameterAccessor accessor : accessors) {
            if (accessor.hasDirection(Direction.INPUT)) {
                sb.append(comma);
                values.append(comma);
                sb.append(accessor.getName());
                values.append("?");
                comma = ",";
            } else {
                retNames.append(retComma);
                retValues.append(retComma);
                retNames.append(accessor.getName());
                retValues.append("?");
                retComma = ",";
            }
        }
        sb.append(") values (");
        sb.append(values);
        sb.append(")");
        return sb.toString();
    }
}
