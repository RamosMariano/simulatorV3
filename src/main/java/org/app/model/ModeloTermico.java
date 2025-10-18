package org.app.model;

public class ModeloTermico {

    private double T0;     // Temperatura inicial
    private double T_out;  // Temperatura exterior
    private double P_in;   // Potencia de entrada (W)
    private double C;      // Capacidad térmica (J/K)
    private double UA;     // Coeficiente global de pérdidas (W/K)
    private double t;      // Tiempo (s)

    public ModeloTermico(double T0, double T_out, double P_in, double C, double UA, double t) {
        this.T0 = T0;
        this.T_out = T_out;
        this.P_in = P_in;
        this.C = C;
        this.UA = UA;
        this.t = t;
    }


    public double calcularTemperatura() {
        double tau = C / UA;                // Constante de tiempo
        double Tss = T_out + P_in / UA;     // Temperatura en estado estacionario
        return Tss + (T0 - Tss) * Math.exp(-t / tau);
    }

    public static double calcularTemperatura(double T0, double T_out, double P_in, double C, double UA, double t) {
        double tau = C / UA;                // Constante de tiempo
        double Tss = T_out + P_in / UA;     // Temperatura en estado estacionario
        return Tss + (T0 - Tss) * Math.exp(-t / tau);
    }

    // Getters y Setters
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

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }
}