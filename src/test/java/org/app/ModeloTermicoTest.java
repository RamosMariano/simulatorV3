package org.app;

import org.app.model.ModeloTermico;

import junit.framework.TestCase;

public class ModeloTermicoTest extends TestCase {

    private ModeloTermico modelo;

    protected void setUp() throws Exception {
        modelo = new ModeloTermico(20.0, 10.0, 1000.0, 5000.0, 200.0, 0.0, 60);
    }

    protected void tearDown() throws Exception {
        modelo = null;
    }

    // ---- TESTS ----

    /** Prueba de cálculo teórico estático */
    public void testCalcularTemperatura() {
        double T0 = 20.0;
        double T_out = 10.0;
        double P_in = 1000.0;
        double C = 5000.0;
        double UA = 200.0;
        double t = 100.0;

        double result = ModeloTermico.calcularTemperatura(T0, T_out, P_in, C, UA, t);

        // Se espera que esté entre la temp. inicial (20) y el equilibrio (Tss = 10 + 1000/200 = 15)
        double Tss = 15.0;
        assertTrue(result < T0);
        assertTrue(result > Tss);
    }

    /** Prueba del método de instancia temperaturaActual() */
    public void testTemperaturaActual() {
        modelo.setT(0.0);
        double t0 = modelo.temperaturaActual();
        modelo.setT(200.0);
        double t200 = modelo.temperaturaActual();

        // A mayor tiempo, la temperatura debería acercarse al equilibrio
        assertTrue(t200 < t0);
    }

    /** Prueba del método advance() */
    public void testAdvance() {
        double tiempoInicial = modelo.getT();
        modelo.advance(50.0);
        assertEquals(tiempoInicial + 50.0, modelo.getT(), 0.001);
    }

    /** Prueba de getters y setters */
    public void testGettersYSetters() {
        modelo.setT0(25.0);
        modelo.setT_out(5.0);
        modelo.setP_in(800.0);
        modelo.setC(4000.0);
        modelo.setUA(250.0);
        modelo.setT(100.0);
        modelo.setIntervaloSegundos(30);

        assertEquals(25.0, modelo.getT0(), 0.001);
        assertEquals(5.0, modelo.getT_out(), 0.001);
        assertEquals(800.0, modelo.getP_in(), 0.001);
        assertEquals(4000.0, modelo.getC(), 0.001);
        assertEquals(250.0, modelo.getUA(), 0.001);
        assertEquals(100.0, modelo.getT(), 0.001);
        assertEquals(30, modelo.getIntervaloSegundos());
    }

    /** Prueba de estabilidad: cuando el tiempo es muy largo, debería acercarse al equilibrio */
    public void testTemperaturaEquilibrio() {
        modelo.setT(1e6); // tiempo muy grande
        double temp = modelo.temperaturaActual();
        double Tss = modelo.getT_out() + modelo.getP_in() / modelo.getUA();

        assertEquals(Tss, temp, 0.001);
    }
}
