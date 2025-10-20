package org.app.model;

import org.app.sim.sensors.SensorHt;
import org.app.sim.sensors.SensorSw;

public class UnitRuntime {
    private final Room room;
    private final Calefactor calefactor;
    private final ModeloTermico modeloTermico;

    // Sensores asociados a esta unidad
    private final SensorHt sensorHt;
    private final SensorSw sensorSw;

    public UnitRuntime(Room room, Calefactor calefactor, ModeloTermico modeloTermico) {
        this.room = room; this.calefactor = calefactor; this.modeloTermico = modeloTermico; this.sensorHt = null;
        this.sensorSw = null;
    }

    /** Constructor preferido: crea sensores usando el timestamp de inicio de simulación (ms epoch) */
    public UnitRuntime(Room room, Calefactor calefactor, ModeloTermico modeloTermico, long startEpochMs) {
        this.room = room;
        this.calefactor = calefactor;
        this.modeloTermico = modeloTermico;

        // ---- SensorHt ----
        SensorHt ht = new SensorHt();
        ht.setTimestampSeconds(startEpochMs / 1000.0);  // ts en segundos (top-level y params.ts)
        ht.setTemperatureC(room.getT0());               // arranca con T0 del Room
        ht.setTemperatureChannelId(room.getId());       // "id" del canal = id del Room
        this.sensorHt = ht;

        // ---- SensorSw ----
        int swId = room.getId();
        String source = (calefactor != null && calefactor.getType() != null) ? calefactor.getType() : "unknown";
        boolean output = (calefactor != null) && calefactor.isState();
        SensorSw sw = new SensorSw(swId, source, output, startEpochMs); // ts en MILISEGUNDOS
        this.sensorSw = sw;
    }

    public Room getRoom() { return room; }
    public Calefactor getCalefactor() { return calefactor; }
    public ModeloTermico getModeloTermico() { return modeloTermico; }

    public SensorHt getSensorHt() { return sensorHt; }
    public SensorSw getSensorSw() { return sensorSw; }
}
