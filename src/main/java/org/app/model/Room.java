package org.app.model;

public class Room {

    private double T0;     // Temperatura inicial (°C)
    private double T_out;  // Temperatura exterior (°C)
    private double P_in;   // Potencia de entrada (W)
    private double C;      // Capacidad térmica (J/K)
    private double UA;     // Coeficiente global de pérdidas (W/K)

    public Room() {
    }

    public Room(double T0, double T_out, double P_in, double C, double UA) {
        this.T0 = T0;
        this.T_out = T_out;
        this.P_in = P_in;
        this.C = C;
        this.UA = UA;
    }

    public double getT0() {
        return T0;
    }

    public void setT0(double T0) {
        this.T0 = T0;
    }

    public double getT_out() {
        return T_out;
    }

    public void setT_out(double T_out) {
        this.T_out = T_out;
    }

    public double getP_in() {
        return P_in;
    }

    public void setP_in(double P_in) {
        this.P_in = P_in;
    }

    public double getC() {
        return C;
    }

    public void setC(double c) {
        this.C = c;
    }

    public double getUA() {
        return UA;
    }

    public void setUA(double UA) {
        this.UA = UA;
    }

    @Override
    public String toString() {
        return "Room{" +
                "T0=" + T0 +
                ", T_out=" + T_out +
                ", P_in=" + P_in +
                ", C=" + C +
                ", UA=" + UA +
                '}';
    }
}