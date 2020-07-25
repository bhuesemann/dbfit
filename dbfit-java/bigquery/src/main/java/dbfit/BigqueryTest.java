package dbfit;


public class BigqueryTest extends DatabaseTest {
    public BigqueryTest() {
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Bigquery"));
    }
}

