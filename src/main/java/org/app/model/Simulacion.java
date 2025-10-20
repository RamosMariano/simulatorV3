package org.app.model;

public class Simulacion {
    private int duracionSegundos;
    private int escenario; // NUEVO

    public Simulacion() {}

    public int getDuracionSegundos() { return duracionSegundos; }
    public void setDuracionSegundos(int duracionSegundos) { this.duracionSegundos = duracionSegundos; }
    public int getEscenario() {
        return escenario;
    }
    public void setEscenario(int escenario) {
        this.escenario = escenario;
    }
    @Override
    public String toString() {
        return "Simulacion{duracionSegundos=" + duracionSegundos + ", escenario=" + escenario + '}';
    }
}
