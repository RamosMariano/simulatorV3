package org.app.sim;

import org.app.model.Calefactor;
import org.app.model.ModeloTermico;
import org.app.model.Room;
import org.app.model.UnitRuntime;

/**
 * Enlaza tu UnitRuntime (Room + Calefactor + ModeloTermico) al EngineUnit simple.
 * Actualiza T0 con el modelo RC en cada tick.
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
        // 1) potencia de entrada (simple): si heater ON => usa p_entregada; si no, 0
        double P_in = heater.isState() ? heater.getP_entregada() : 0.0;
        room.setP_in(P_in);

        // 2) integrar el modelo térmico RC con dt en segundos
        double dt = dtMs / 1000.0;
        double T0   = room.getT0();
        double T_out= room.getT_out();
        double C    = room.getC();
        double UA   = room.getUA();

        double tau = C / UA;
        double Tss = T_out + P_in / UA;
        double Tnext = Tss + (T0 - Tss) * Math.exp(-dt / tau);
        room.setT0(Tnext);

        // (opcional) si tu ModeloTermico tiene t interno, lo podés avanzar si querés:
        // mt.setT(mt.getT() + dt);

        if (heater.isState() && heater.getP_electrica() > 0) {
            energiaAcumuladaJ += heater.getP_electrica() * dt;  // W * s = J
        }

        // 3) muestreo/impresión simple cada N ms simulados
        if (sampleEveryMs > 0) {
            if (lastSampleMs < 0 || (simTimeMs - lastSampleMs) >= sampleEveryMs) {
                lastSampleMs = simTimeMs;
                double energiaWh = energiaAcumuladaJ / 3600.0; // convertir Joules → Wh
                System.out.printf(
                        "[t=%6.1fs] Room #%d | T=%.2f°C, Tout=%.1f°C, Pin=%.0fW | " +
                                "Heater=%s (%s) | p_electrica=%d W | Energy=%.2f Wh | ts=%d%n",
                        simTimeMs / 1000.0,
                        room.getId(),
                        room.getT0(),
                        room.getT_out(),
                        room.getP_in(),
                        heater.getType(),
                        heater.isState() ? "ON" : "OFF",
                        heater.getP_electrica(),
                        energiaWh,
                        currentTimestampMs
                );
            }
        }
    }

    /** 🔹 Devuelve energía total en Wh */
    public double getEnergiaWh() {
        return energiaAcumuladaJ / 3600.0;
    }

    /** 🔹 Para reporte final */
    public void printResumenFinal() {
        System.out.printf("Room #%d | %s | Consumo total: %.2f Wh (%.3f kWh)%n",
                room.getId(),
                heater.getType(),
                getEnergiaWh(),
                getEnergiaWh() / 1000.0
        );
    }
}