package org.app;

import org.app.model.Calefactor;
import org.app.model.ModeloTermico;
import org.app.model.Room;
import org.app.model.UnitRuntime;
import org.app.sim.EngineUnitAdapter;

import junit.framework.TestCase;

public class EngineUnitAdapterTest extends TestCase {

	private Room room;
    private Calefactor heater;
    private ModeloTermico mt;
    private UnitRuntime ur;
    private EngineUnitAdapter adapter;

    protected void setUp() {
        room = new Room();
        room.setId(1);
        room.setT0(18.0);
        room.setT_out(8.0);
        room.setP_in(0.0);
        room.setC(1_600_000.0);
        room.setUA(80.0);

        heater = new Calefactor();
        heater.setType("loza electrica");
        heater.setP_electrica(1800);
        heater.setP_entregada(2000);
        heater.setState(false); // 🔴 apagado al inicio

        mt = new ModeloTermico(
                room.getT0(), room.getT_out(), room.getP_in(),
                room.getC(), room.getUA(), 0, 1
        );

        ur = new UnitRuntime(room, heater, mt);
        adapter = new EngineUnitAdapter(ur, 0);
    }
    
    // === TEST 1 ===
    public void testTemperaturaNoCambiaSiHeaterApagado() {
        double tempInicial = room.getT0();

        adapter.tick(1000, 1000, 0); // 1 segundo de simulación

        double tempFinal = room.getT0();
        System.out.printf("Temp inicial: %.3f, final: %.3f%n", tempInicial, tempFinal);

        // Sin potencia, el cambio debería ser muy pequeño (enfriamiento leve)
        assertTrue("Temperatura no debería aumentar con heater apagado", tempFinal <= tempInicial);
    }
}
