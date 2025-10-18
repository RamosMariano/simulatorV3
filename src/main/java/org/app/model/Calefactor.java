package org.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Calefactor {
    @JsonProperty("type")
    private String type;

    @JsonProperty("p_electrica")
    private int p_electrica;

    @JsonProperty("state")
    private boolean state;

    @JsonProperty("p_entregada")
    private int p_entregada;
    public Calefactor() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getP_electrica() { return p_electrica; }
    public void setP_electrica(int p_electrica) { this.p_electrica = p_electrica; }
    public boolean isState() { return state; }
    public void setState(boolean state) { this.state = state; }
    public int getP_entregada() { return p_entregada; }
    public void setP_entregada(int p_entregada) { this.p_entregada = p_entregada; }
    @Override
    public String toString() {
        return String.format("%s (p_electrica=%d W, p_entregada=%d W, state=%s)",
                type, p_electrica, p_entregada, state);
    }

}
