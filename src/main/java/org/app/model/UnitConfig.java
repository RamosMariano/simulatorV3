package org.app.model;

public class UnitConfig {
    private Room room;
    private Calefactor calefactor;
    private ModeloTermicoConfig modeloTermico; // opcional en JSON

    public UnitConfig() {}

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public Calefactor getCalefactor() { return calefactor; }
    public void setCalefactor(Calefactor calefactor) { this.calefactor = calefactor; }

    public ModeloTermicoConfig getModeloTermico() { return modeloTermico; }
    public void setModeloTermico(ModeloTermicoConfig modeloTermico) { this.modeloTermico = modeloTermico; }
}
