package org.app;

import org.app.model.Room;

import junit.framework.TestCase;

public class RoomTest extends TestCase {

    private Room room;

    // Se ejecuta antes de cada test
    protected void setUp() throws Exception {
        room = new Room();
    }

    // Se ejecuta después de cada test (opcional)
    protected void tearDown() throws Exception {
        room = null;
    }

    // ---- TESTS ----

    public void testSetAndGetId() {
        room.setId(42);
        assertEquals(42, room.getId());
    }

    public void testSetAndGetTemperaturas() {
        room.setT0(21.5);
        room.setT_out(12.3);

        assertEquals(21.5, room.getT0(), 0.001);
        assertEquals(12.3, room.getT_out(), 0.001);
    }

    public void testSetAndGetPotenciaYCoefficientes() {
        room.setP_in(1500.0);
        room.setC(5000.0);
        room.setUA(220.0);

        assertEquals(1500.0, room.getP_in(), 0.001);
        assertEquals(5000.0, room.getC(), 0.001);
        assertEquals(220.0, room.getUA(), 0.001);
    }

    public void testDefaultValues() {
        // Un Room recién creado debería tener valores 0 o 0.0
        assertEquals(0, room.getId());
        assertEquals(0.0, room.getT0(), 0.001);
        assertEquals(0.0, room.getT_out(), 0.001);
        assertEquals(0.0, room.getP_in(), 0.001);
        assertEquals(0.0, room.getC(), 0.001);
        assertEquals(0.0, room.getUA(), 0.001);
    }

    public void testToStringFormat() {
        room.setId(7);
        room.setT0(22.5);
        room.setT_out(15.0);
        room.setC(4500.0);
        room.setUA(300.0);

        String text = room.toString();

        // Verifica que el texto contenga todos los valores esperados
        assertTrue(text.contains("Room"));
        assertTrue(text.contains("id=7"));
        assertTrue(text.contains("4500"));
        assertTrue(text.contains("300"));
    }
}
