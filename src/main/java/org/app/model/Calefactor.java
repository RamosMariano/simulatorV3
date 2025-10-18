package org.app.model;

public class Calefactor {

    private String type;        // Tipo de calefactor (por ejemplo: "radiador", "losa", etc.)
    private int p_electrica;    // Potencia eléctrica nominal (W)
    private boolean state;      // Estado: encendido (true) o apagado (false)
    private int p_entregada;    // Potencia realmente entregada (W)

    public Calefactor() {
    }

    public Calefactor(String type, int p_electrica, boolean state, int p_entregada) {
        this.type = type;
        this.p_electrica = p_electrica;
        this.state = state;
        this.p_entregada = p_entregada;
    }

    // Getters y Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getP_electrica() {
        return p_electrica;
    }

    public void setP_electrica(int p_electrica) {
        this.p_electrica = p_electrica;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getP_entregada() {
        return p_entregada;
    }

    public void setP_entregada(int p_entregada) {
        this.p_entregada = p_entregada;
    }

    @Override
    public String toString() {
        return "Calefactor{" +
                "type='" + type + '\'' +
                ", p_electrica=" + p_electrica +
                ", state=" + state +
                ", p_entregada=" + p_entregada +
                '}';
    }
}