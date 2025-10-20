package org.app;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite del simulador completo");

        suite.addTestSuite(CalefactorTest.class);
        suite.addTestSuite(EngineUnitAdapterTest.class);
        suite.addTestSuite(ModeloTermicoTest.class);
        suite.addTestSuite(RoomTest.class);
        suite.addTestSuite(SimulacionTest.class);

        return suite;
    }
}
