package org.app;

import org.app.model.Calefactor;

import junit.framework.TestCase;

public class CalefactorTest extends TestCase {

    private Calefactor calefactor;

    // Se ejecuta antes de cada test
    protected void setUp() throws Exception {
        calefactor = new Calefactor();
    }

    // Se ejecuta después de cada test (opcional)
    protected void tearDown() throws Exception {
        calefactor = null;
    }

    // ---- TESTS ----

    public void testSetAndGetType() {
        calefactor.setType("Radiador");
        assertEquals("Radiador", calefactor.getType());
    }

    public void testSetAndGetPotenciaElectrica() {
        calefactor.setP_electrica(1500);
        assertEquals(1500, calefactor.getP_electrica());
    }

    public void testSetAndGetPotenciaEntregada() {
        calefactor.setP_entregada(1200);
        assertEquals(1200, calefactor.getP_entregada());
    }

    public void testSetAndGetState() {
        calefactor.setState(true);
        assertTrue(calefactor.isState());
    }

    public void testToStringFormat() {
        calefactor.setType("Radiador");
        calefactor.setP_electrica(1500);
        calefactor.setP_entregada(1200);
        calefactor.setState(true);

        String result = calefactor.toString();

        assertTrue(result.contains("Radiador"));
        assertTrue(result.contains("1500"));
        assertTrue(result.contains("1200"));
        assertTrue(result.contains("true"));
    }

    public void testDefaultValues() {
        assertNull(calefactor.getType());
        assertEquals(0, calefactor.getP_electrica());
        assertEquals(0, calefactor.getP_entregada());
        assertFalse(calefactor.isState());
    }
}
