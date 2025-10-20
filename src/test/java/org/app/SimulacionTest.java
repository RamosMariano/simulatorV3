package org.app;

import org.app.model.Calefactor;
import org.app.model.ModeloTermico;
import org.app.model.Room;
import org.app.model.UnitRuntime;
import org.app.sim.EngineUnitAdapter;
import org.app.sim.SimulationEngine;

import junit.framework.TestCase;

public class SimulacionTest extends TestCase {
   
	// TEST PARA EL SIMULATION ENGINE
    public void testDosRoomsSubeTemperatura() throws Exception {
        // === Room #1 ===
        Room room1 = new Room();
        room1.setId(1);
        room1.setT0(18.0);
        room1.setT_out(8.0);
        room1.setP_in(0.0);
        room1.setC(1600000.0);
        room1.setUA(80.0);

        Calefactor heater1 = new Calefactor();
        heater1.setType("loza electrica");
        heater1.setP_electrica(1800);
        heater1.setP_entregada(2000);
        heater1.setState(false); // 🔴 apagado

        ModeloTermico mt1 = new ModeloTermico(
                room1.getT0(), room1.getT_out(), room1.getP_in(),
                room1.getC(), room1.getUA(), 0, 1
        );

        // === Room #2 ===
        Room room2 = new Room();
        room2.setId(2);
        room2.setT0(20.0);
        room2.setT_out(8.0);
        room2.setP_in(0.0);
        room2.setC(1600000.0);
        room2.setUA(80.0);

        Calefactor heater2 = new Calefactor();
        heater2.setType("loza hidraulica");
        heater2.setP_electrica(1500);
        heater2.setP_entregada(1500);
        heater2.setState(true); // 🟢 encendido

        ModeloTermico mt2 = new ModeloTermico(
                room2.getT0(), room2.getT_out(), room2.getP_in(),
                room2.getC(), room2.getUA(), 0, 1
        );

        // === Unidades de ejecución ===
        UnitRuntime ur1 = new UnitRuntime(room1, heater1, mt1);
        UnitRuntime ur2 = new UnitRuntime(room2, heater2, mt2);

        EngineUnitAdapter adapter1 = new EngineUnitAdapter(ur1, 0);
        EngineUnitAdapter adapter2 = new EngineUnitAdapter(ur2, 0);

        // === Motor de simulación ===
        long stepMs = 100;       // 0.1 s simulada por iteración
        double warp = 1000.0;    // simular rápido (1000× más rápido que tiempo real)
        SimulationEngine engine = new SimulationEngine(stepMs, warp, 0);

        engine.addUnit(adapter1);
        engine.addUnit(adapter2);

        // === Ejecutar simulación ===
        engine.start();
        Thread.sleep(500); // ≈ simula unos 6 segundos físicos → 5000 s simulados aprox
        engine.stop();

        // === Verificar cambios de temperatura ===
        double finalT1 = room1.getT0();
        double finalT2 = room2.getT0();

        System.out.printf("TEST : 2 Rooms - Temperaturas%n");
        System.out.printf("Room1: %.2f°C (heater OFF) (Anteriormente 18.0)%n", finalT1);
        System.out.printf("Room2: %.2f°C (heater ON) (Anteriormente 20.0)%n", finalT2);

        // La habitación 1 (heater OFF) debería enfriarse
        assertTrue("Room #1 debería bajar su temperatura", finalT1 < 18.0);

        // La habitación 2 (heater ON) debería calentarse
        assertTrue("Room #2 debería subir su temperatura", finalT2 > 20.0);

        // Energía consumida
        double e1 = adapter1.getEnergiaWh();
        double e2 = adapter2.getEnergiaWh();

        assertEquals("Room1 no debería consumir energía", 0.0, e1, 0.001);
        assertTrue("Room2 debería haber consumido energía", e2 > 0.0);
    }
    
    // TEST PARA EL MODELO TERMICO
    public void testSteadyStateTemperature() {
        double T0 = 20.0;
        double T_out = 10.0;
        double P_in = 800.0;
        double C = 1_000_000.0;
        double UA = 100.0;
        double dt = 10_000.0; // 10 segundos

        double T = ModeloTermico.calcularTemperatura(T0, T_out, P_in, C, UA, dt);

        // Equilibrio Termico : T_out + P_in / UA = 10 + 800 / 100 = 18°C
        System.out.printf("TEST : Equilibrio Termico%n");
        System.out.printf("Temperatura Inicial 20.0 | Equilibrio Termico en este caso 18.0 | Resultado " + T + "%n");
        assertTrue("Temperatura debería acercarse a 18°C", T < 20.0 && T > 17.0);
    }
    
    // TEST PARA EL MODELO TERMICO
    public void testCeroPotenciaTiendeAExterior() {
        double T = ModeloTermico.calcularTemperatura(22.0, 10.0, 0.0, 1_000_000.0, 80.0, 60.0);
        System.out.printf("TEST : Sin potencia, Temperatura Disminuye%n");
        System.out.printf("Temperatura Inicial 22.0 | Resultado " + T + " %n");
        assertTrue("Sin potencia, la temperatura debería bajar", T < 22.0);
    }
    
    // TEST PARA EL MODELO TERMICO
    public void testSinPerdidasNoCambiaTemperatura() {
        double T = ModeloTermico.calcularTemperatura(20.0, 10.0, 0.0, 1_000_000.0, 0.000001, 100.0);
        System.out.printf("TEST : Sin Perdida Termica - No Cambia Temperatura %n");
        System.out.printf("Temperatura Inicial 20.0 | Resultado " + T + " %n");
        assertEquals(20.0, T, 0.0001);
    }
    
}
