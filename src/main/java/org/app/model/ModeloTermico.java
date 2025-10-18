package org.app.model;

public class ModeloTermico {
    private double T0, T_out, P_in, C, UA;
    private double t;
    private int intervaloSegundos;

    public ModeloTermico() {}

    public ModeloTermico(double T0, double T_out, double P_in, double C, double UA, double t, int intervaloSegundos) {
        this.T0 = T0; this.T_out = T_out; this.P_in = P_in; this.C = C; this.UA = UA;
        this.t = t; this.intervaloSegundos = intervaloSegundos;
    }

    public static double calcularTemperatura(double T0, double T_out, double P_in, double C, double UA, double t) {
        double tau = C / UA;
        double Tss = T_out + P_in / UA;
        return Tss + (T0 - Tss) * Math.exp(-t / tau);
    }

    public double temperaturaActual() { return calcularTemperatura(T0, T_out, P_in, C, UA, t); }
    public void advance(double dt) { this.t += dt; }

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
    public double getT() { return t; }
    public void setT(double t) { this.t = t; }
    public int getIntervaloSegundos() { return intervaloSegundos; }
    public void setIntervaloSegundos(int intervaloSegundos) { this.intervaloSegundos = intervaloSegundos; }
}
