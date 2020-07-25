package dbfit.environment;

/*
import java.net.URL;
import java.util.Map;
import dbfit.api.DbStoredProcedureCall;
import dbfit.util.DbParameterAccessor;
*/
import dbfit.api.DBEnvironment;

import org.junit.Test;

import static org.junit.Assert.*;

public class HanaDbEnvironmentTest {

    @Test
    public void newDbEnvironmentTest() throws Exception {
        DBEnvironment env = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Hana");
        assertNotNull(env);
        // URL location =
        // HanaDbEnvironmentTest.class.getProtectionDomain().getCodeSource().getLocation();
        // env.connectUsingFile(location.getFile() + "hana.prop");
        // // check case OBJECTNAME without schemaname
        // Map<String, DbParameterAccessor> result =
        // env.getAllProcedureParameters("COMPRESS_FILE");
        // DbParameterAccessor[] acc = result.values().toArray(new
        // DbParameterAccessor[0]);
        // assertTrue(acc != null && acc.length > 0);
        // if (acc != null && acc[0] != null) {
        // assertEquals("FILENAME", acc[0].getName());
        // }

        // // check case SCHEMA.OBJEKTNAME
        // result = env.getAllProcedureParameters("SYS.COMPRESS_FILE");
        // acc = result.values().toArray(new DbParameterAccessor[0]);
        // assertTrue(acc != null && acc.length > 0);
        // if (acc != null && acc[0] != null) {
        // assertEquals("FILENAME", acc[0].getName());
        // }

        // // check case "SCHEMA"."path./objectname"
        // result = env.getAllProcedureParameters(
        // "\"SYS\".\"db.infrastructure.security.protected.procedures::grant_sap_common_role\"");
        // acc = result.values().toArray(new DbParameterAccessor[0]);
        // assertTrue(acc != null && acc.length > 0);
        // if (acc != null && acc[0] != null) {
        // assertEquals("ROLE_NAME", acc[0].getName());
        // }

        // DbStoredProcedureCall mycall = env.newStoredProcedureCall("COMPRESS_FILE",
        // acc);
        // assertEquals("{ call COMPRESS_FILE(?)}", mycall.toSqlString());
    }
}
