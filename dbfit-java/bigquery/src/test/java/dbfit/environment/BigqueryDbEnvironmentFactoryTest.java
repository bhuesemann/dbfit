package dbfit.environment;

import dbfit.api.DBEnvironment;

import org.junit.Test;

import static org.junit.Assert.*;

public class BigqueryDbEnvironmentFactoryTest {

    @Test
    public void newDbEnvironmentTest() throws Exception {
        DBEnvironment env = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Bigquery");
        assertNotNull(env);
    }
}
