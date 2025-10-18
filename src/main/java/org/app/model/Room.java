package org.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Room {
    @JsonProperty("id")
    private int id;

    @JsonProperty("T0")
    private double T0;

    @JsonProperty("T_out")
    private double T_out;

    @JsonProperty("P_in")
    private double P_in;

    @JsonProperty("C")
    private double C;

    @JsonProperty("UA")
    private double UA;

    public Room() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getT0() { return T0; }
    public void setT0(double t0) { T0 = t0; }
    public double getT_out() { return T_out; }
    public void setT_out(double t_out) { T_out = t_out; }
    public double getP_in() { return P_in; }
    public void setP_in(double p_in) { P_in = p_in; }
    public double getC() { return C; }
    public void setC(double c) { C = c; }
    public double getUA() { return UA; }
    public void setUA(double UA) { this.UA = UA; }
    @Override
    public String toString() {
        return String.format("Room{id=%d, T0=%.1f, T_out=%.1f, C=%.0f, UA=%.0f}", id, T0, T_out, C, UA);
    }
}
