package dbfit.environment;

import java.net.URL;

import dbfit.api.DBEnvironment;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExasolDbEnvironmentTest {

  @Test
  public void newDbEnvironmentTest() throws Exception {
    DBEnvironment env = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Exasol");
    assertNotNull(env);
    URL location =
        ExasolDbEnvironmentTest.class.getProtectionDomain().getCodeSource().getLocation();
    System.out.println(location);
    // env.connectUsingFile(location.getFile() + "bigquery.prop");
    // check case OBJECTNAME without schemaname
    /*
     * TODO: implement procedure testing features Map<String, DbParameterAccessor> result =
     * env.getAllProcedureParameters("COMPRESS_FILE"); DbParameterAccessor[] acc =
     * result.values().toArray(new DbParameterAccessor[0]); assertTrue(acc != null && acc.length >
     * 0); if (acc != null && acc[0] != null) { assertEquals("FILENAME", acc[0].getName()); }
     */
    // check case SCHEMA.OBJEKTNAME
    /*
     * result = env.getAllProcedureParameters("SYS.COMPRESS_FILE"); acc =
     * result.values().toArray(new DbParameterAccessor[0]); assertTrue(acc != null && acc.length >
     * 0); if (acc != null && acc[0] != null) { assertEquals("FILENAME", acc[0].getName()); }
     *
     * // check case "SCHEMA"."path./objectname" result = env.getAllProcedureParameters(
     * "\"SYS\".\"db.infrastructure.security.protected.procedures::grant_sap_common_role\"" ); acc =
     * result.values().toArray(new DbParameterAccessor[0]); assertTrue(acc != null && acc.length >
     * 0); if (acc != null && acc[0] != null) { assertEquals("ROLE_NAME", acc[0].getName()); }
     *
     * DbStoredProcedureCall mycall = env.newStoredProcedureCall("COMPRESS_FILE", acc);
     * assertEquals("{ call COMPRESS_FILE(?)}", mycall.toSqlString());
     */
  }
}
