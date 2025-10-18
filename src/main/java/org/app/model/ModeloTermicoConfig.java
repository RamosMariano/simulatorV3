package org.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModeloTermicoConfig {
    @JsonProperty("intervaloSegundos")
    private Integer intervaloSegundos;

    public ModeloTermicoConfig() {}

    public Integer getIntervaloSegundos() { return intervaloSegundos; }
    public void setIntervaloSegundos(Integer intervaloSegundos) { this.intervaloSegundos = intervaloSegundos; }
}
