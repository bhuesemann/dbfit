package dbfit;


public class HanaTest extends DatabaseTest {
    public HanaTest() {
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Hana"));
    }
}

