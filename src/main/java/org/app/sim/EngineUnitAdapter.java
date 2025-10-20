package org.app.sim;

import org.app.model.Calefactor;
import org.app.model.ModeloTermico;
import org.app.model.Room;
import org.app.model.UnitRuntime;

/**
 * Enlaza tu UnitRuntime (Room + Calefactor + ModeloTermico) al EngineUnit simple.
 * Actualiza T0 con el modelo RC en cada tick.
 *
 * CONCURRENCIA:
 * - El hilo del engine ejecuta tick().
 * - El handler del escenario (en el hilo del main) puede modificar Room/Calefactor.
 * - Para evitar condiciones de carrera, todo el avance de estado se hace dentro de
 *   synchronized(rt), el MISMO monitor que debe usar el handler (synchronized(u)).
 */
public class EngineUnitAdapter implements EngineUnit {

    private final UnitRuntime rt;
    private final Room room;
    private final Calefactor heater;
    private final ModeloTermico mt;

    // muestreo opcional (cada N ms simulados)
    private final long sampleEveryMs;
    private long lastSampleMs = -1;
    private double energiaAcumuladaJ = 0.0;

    public EngineUnitAdapter(UnitRuntime runtime, long sampleEveryMs) {
        this.rt = runtime;
        this.room = runtime.getRoom();
        this.heater = runtime.getCalefactor();
        this.mt = runtime.getModeloTermico();
        this.sampleEveryMs = Math.max(0, sampleEveryMs);
    }

    @Override
    public void tick(long dtMs, long simTimeMs, long currentTimestampMs) {

        // Variables para logeo fuera del lock (snapshot)
        boolean doPrint = false;
        int snapshotRoomId = -1;
        double snapshotT = 0.0;
        double snapshotTout = 0.0;
        double snapshotPin = 0.0;
        String snapshotHeaterType = "";
        boolean snapshotHeaterOn = false;
        int snapshotPElectrica = 0;
        double snapshotEnergiaWh = 0.0;

        synchronized (rt) { // 🔒 sección crítica: estado consistente de la unidad

            // 1) potencia de entrada (si heater ON => p_entregada; si no, 0)
            double P_in = heater.isState() ? heater.getP_entregada() : 0.0;
            room.setP_in(P_in);

            // 2) integrar el modelo térmico RC con dt en segundos
            double dt = dtMs / 1000.0;
            double T0    = room.getT0();
            double T_out = room.getT_out();
            double C     = room.getC();
            double UA    = room.getUA();

            if (C > 0 && UA > 0) {
                double tau   = C / UA;
                double Tss   = T_out + P_in / UA;
                double Tnext = Tss + (T0 - Tss) * Math.exp(-dt / tau);
                room.setT0(Tnext);
            }

            // 3) acumular energía consumida eléctrica (J = W * s)
            if (heater.isState() && heater.getP_electrica() > 0) {
                energiaAcumuladaJ += heater.getP_electrica() * dt;
            }

            // 4) ¿toca muestrear/imprimir?
            if (sampleEveryMs > 0) {
                if (lastSampleMs < 0 || (simTimeMs - lastSampleMs) >= sampleEveryMs) {
                    lastSampleMs = simTimeMs;

                    // snapshot para imprimir fuera del lock
                    snapshotRoomId       = room.getId();
                    snapshotT            = room.getT0();
                    snapshotTout         = room.getT_out();
                    snapshotPin          = room.getP_in();
                    snapshotHeaterType   = heater.getType();
                    snapshotHeaterOn     = heater.isState();
                    snapshotPElectrica   = heater.getP_electrica();
                    snapshotEnergiaWh    = energiaAcumuladaJ / 3600.0;
                    doPrint = true;
                }
            }

            // (Si tu ModeloTermico mantiene t interno)
            // mt.setT(mt.getT() + dt);
        } // 🔓 fin sección crítica

        // Log fuera del lock (minimiza tiempo bloqueado)
        if (doPrint) {
            System.out.printf(
                    "[t=%6.1fs] Room #%d | T=%.2f°C, Tout=%.1f°C, Pin=%.0fW | Heater=%s (%s) | p_electrica=%d W | Energy=%.2f Wh | ts=%d%n",
                    simTimeMs / 1000.0,
                    snapshotRoomId,
                    snapshotT,
                    snapshotTout,
                    snapshotPin,
                    snapshotHeaterType,
                    snapshotHeaterOn ? "ON" : "OFF",
                    snapshotPElectrica,
                    snapshotEnergiaWh,
                    currentTimestampMs
            );
        }
    }

    /** 🔹 Devuelve energía total en Wh (si lo llamás desde otro hilo, sincronizá sobre rt) */
    public double getEnergiaWh() {
        synchronized (rt) {
            return energiaAcumuladaJ / 3600.0;
        }
    }

    /** 🔹 Para reporte final (si lo llamás desde otro hilo distinto al engine, queda sincronizado) */
    public void printResumenFinal() {
        double wh;
        int id;
        String tipo;
        synchronized (rt) {
            wh = energiaAcumuladaJ / 3600.0;
            id = room.getId();
            tipo = heater.getType();
        }
        System.out.printf("Room #%d | %s | Consumo total: %.2f Wh (%.3f kWh)%n",
                id, tipo, wh, wh / 1000.0);
    }
}
