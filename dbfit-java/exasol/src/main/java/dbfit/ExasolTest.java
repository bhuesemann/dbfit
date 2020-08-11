package dbfit;


public class ExasolTest extends DatabaseTest {
    public ExasolTest() {
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Exasol"));
    }
}

