package org.app.control;

import org.app.model.UnitRuntime;
import org.app.model.Room;
import org.app.model.Calefactor;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Iterator;

/**
 * Handler de escenarios.
 * Permite definir acciones "a tal tiempo simulado => hacer X".
 * CONCURRENCIA:
 * - El EngineUnitAdapter sincroniza su tick() con synchronized(rt).
 * - Aquí sincronizamos todos los cambios de estado con synchronized(u),
 *   donde u es el MISMO UnitRuntime, garantizando exclusión mutua.
 */
public class DefaultScenarioHandler implements ScenarioHandler {

    private final Map<Integer, UnitRuntime> roomMap;
    private final int escenario;
    private volatile long nowEpochMs = -1;

    // timeline: en qué milisegundo simulado ejecutar qué acción
    private final NavigableMap<Long, Runnable> timeline = new TreeMap<>();

    // para saber hasta dónde ya ejecutamos
    private long lastExecutedUpToMs = -1;

    public DefaultScenarioHandler(Map<Integer, UnitRuntime> roomMap, int escenario) {
        this.roomMap = roomMap;
        this.escenario = escenario;
        buildTimeline(escenario);
    }

    @Override
    public void onTick(long simTimeMs, long currentTimestampMs) {
        // Ejecuta todas las acciones programadas con tiempo <= simTimeMs y > lastExecutedUpToMs
        if (timeline.isEmpty()) return;
        // Dejar disponible el ts actual para las acciones
        this.nowEpochMs = currentTimestampMs;

        // submap de acciones vencidas y no ejecutadas aún
        NavigableMap<Long, Runnable> due = timeline.subMap(lastExecutedUpToMs + 1, true, simTimeMs, true);
        if (due.isEmpty()) return;

        Iterator<Map.Entry<Long, Runnable>> it = due.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Runnable> e = it.next();
            try {
                e.getValue().run();
            } catch (Exception ex) {
                System.err.println("[Scenario] Error ejecutando acción @ " + e.getKey() + " ms: " + ex.getMessage());
            }
            lastExecutedUpToMs = e.getKey();
            it.remove(); // la quitamos de la timeline una vez ejecutada
        }
    }

    /* ===================== helpers de acceso ===================== */

    private UnitRuntime rt(int roomId) {
        UnitRuntime r = roomMap.get(roomId);
        if (r == null) throw new IllegalArgumentException("No existe Room con id=" + roomId);
        return r;
    }

    private void heaterOn(int roomId) {
        UnitRuntime u = rt(roomId);
        synchronized (u) { // 🔒 proteger cambios contra tick()
            Calefactor h = u.getCalefactor();
            Room r = u.getRoom();
            h.setState(true);
            // asumimos que P_in lo alimenta el calefactor entregado
            r.setP_in(h.getP_entregada());
            System.out.printf("[Scenario] Heater ON en room %d | P_in=%.0f W%n", roomId, r.getP_in());
        }
    }

    private void heaterOff(int roomId) {
        UnitRuntime u = rt(roomId);
        synchronized (u) { // 🔒
            Calefactor h = u.getCalefactor();
            Room r = u.getRoom();
            h.setState(false);
            r.setP_in(0.0);
            System.out.printf("[Scenario] Heater OFF en room %d%n", roomId);
        }
    }

    private void setT_out(int roomId, double newTout) {
        UnitRuntime u = rt(roomId);
        synchronized (u) { // 🔒
            Room r = u.getRoom();
            r.setT_out(newTout);
            System.out.printf("[Scenario] Room %d => T_out=%.1f°C%n", roomId, newTout);
        }
    }

    private void setT0(int roomId, double newT0) {
        UnitRuntime u = rt(roomId);
        synchronized (u) { // 🔒
            Room r = u.getRoom();
            r.setT0(newT0);
            System.out.printf("[Scenario] Room %d => T0=%.1f°C%n", roomId, newT0);
        }
    }

    private void at(long ms, Runnable action) {
        if (ms < 0) throw new IllegalArgumentException("Tiempo negativo no válido");
        // si ya hubiera algo exactamente en ese ms, lo encadenamos
        timeline.merge(ms, action, (a, b) -> () -> { a.run(); b.run(); });
    }

    /* ===================== definición de escenarios ===================== */

    private void buildTimeline(int escenario) {
        switch (escenario) {

            case 1:
                // Escenario 1: Room #1 enciende a los 5s, baja T_out a los 120s, apaga a los 300s.
                at(0_000,   () -> System.out.println("[Scenario 1] Inicio"));
                at(5_000,   () -> heaterOn(1));
                at(120_000, () -> setT_out(1, 5.0));
                at(300_000, () -> heaterOff(1));
                break;

            case 2:
                // Escenario 2: Interacción con Room #1 y #2
                at(0_000,   () -> System.out.println("[Scenario 2] Inicio"));
                at(2_000,   () -> setT0(1, 19.5));
                at(10_000,  () -> heaterOn(2));
                at(60_000,  () -> setT_out(2, 2.0));
                at(180_000, () -> heaterOff(2));
                break;

            case 3:
                // Escenario 3: "pulso" de calor corto en #1 y largo en #2
                at(0_000,   () -> System.out.println("[Scenario 3] Inicio"));
                at(15_000,  () -> heaterOn(1));
                at(45_000,  () -> heaterOff(1));
                at(30_000,  () -> heaterOn(2));
                at(240_000, () -> heaterOff(2));
                break;

            default:
                // Por defecto: no hace nada especial pero deja un log
                at(0_000, () -> System.out.println("[Scenario default] Sin acciones programadas"));
        }
    }
}
