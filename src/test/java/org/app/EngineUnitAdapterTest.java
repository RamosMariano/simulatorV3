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
        System.out.printf("testTemperaturaNoCambiaSiHeaterApagado%n");
        System.out.printf("Temp inicial: %.3f, final: %.3f%n", tempInicial, tempFinal);

        // Sin potencia, el cambio debería ser muy pequeño (enfriamiento leve)
        assertTrue("Temperatura no debería aumentar con heater apagado", tempFinal <= tempInicial);
    }
    
    // === TEST 2 ===
    public void testTemperaturaSubeConHeaterEncendido() {
        heater.setState(true); // 🟢 encendido

        double tempInicial = room.getT0();
        adapter.tick(1000, 1000, 0); // simula 1 s
        double tempFinal = room.getT0();
        System.out.printf("testTemperaturaSubeConHeaterEncendido%n");
        System.out.printf("Temp inicial: %.3f, final: %.3f%n", tempInicial, tempFinal);

        assertTrue("Temperatura debe subir con heater encendido",
                tempFinal > tempInicial);
    }
    
    // === TEST 3 ===
    public void testEnergiaAcumuladaConHeaterEncendido() {
        heater.setState(true);
        double energiaInicial = adapter.getEnergiaWh();

        adapter.tick(1000, 1000, 0);
        double energiaFinal = adapter.getEnergiaWh();

        double deltaWh = energiaFinal - energiaInicial;
        System.out.printf("testEnergiaAcumuladaConHeaterEncendido%n");
        System.out.printf("Energía consumida en 1s: %.5f Wh%n", deltaWh);

        assertEquals("Debe haber energía acumulada correcta", 1800.0 / 3600.0, deltaWh, 0.0001);

    }
    
 // === TEST 4 ===
    public void testSinConsumoConHeaterApagado() {
        heater.setState(false);

        adapter.tick(2000, 2000, 0);
        assertEquals("Con heater apagado, energía no debe acumularse",
                0.0, adapter.getEnergiaWh(), 0.0001);
        
        System.out.printf("testSinConsumoConHeaterApagado%n");
        System.out.printf("Energía consumida en 2s: " + adapter.getEnergiaWh()+ "%n");

    }
}
