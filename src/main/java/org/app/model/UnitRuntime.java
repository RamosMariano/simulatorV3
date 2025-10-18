package org.app.model;

public class UnitRuntime {
    private final Room room;
    private final Calefactor calefactor;
    private final ModeloTermico modeloTermico;

    public UnitRuntime(Room room, Calefactor calefactor, ModeloTermico modeloTermico) {
        this.room = room; this.calefactor = calefactor; this.modeloTermico = modeloTermico;
    }

    public Room getRoom() { return room; }
    public Calefactor getCalefactor() { return calefactor; }
    public ModeloTermico getModeloTermico() { return modeloTermico; }
}
