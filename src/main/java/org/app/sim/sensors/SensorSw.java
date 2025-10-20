package org.app.sim.sensors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;

/**
 * Simula un sensor tipo Switch (consumo eléctrico, tensión, corriente, energía, temperatura).
 * Basado en el formato Shelly (por ejemplo Shelly Pro 1PM).
 *
 * - Los atributos id, source, output y ts (en milisegundos) se definen al construir el objeto.
 * - Todos los atributos tienen getters y setters.
 * - Incluye toJson() para serializar el estado actual al formato JSON realista.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorSw {

    private int id;
    private String source;
    private boolean output;
    private long ts; // timestamp en milisegundos

    private double apower = 0.0;        // Potencia activa instantánea [W]
    private double voltage = 225.9;     // Voltaje RMS [V]
    private double current = 0.0;       // Corriente RMS [A]
    private double freq = 50.0;         // Frecuencia [Hz]
    private Aenergy aenergy;
    private RetAenergy ret_aenergy;
    private Temperature temperature = new Temperature(53.3, 127.9);

    // -------------------- Constructor --------------------
    public SensorSw(int id, String source, boolean output, long ts) {
        this.id = id;
        this.source = source;
        this.output = output;
        this.ts = ts;

        // Inicializar con el timestamp dado
        this.aenergy = new Aenergy(11.679, new double[]{0, 0, 0}, ts);
        this.ret_aenergy = new RetAenergy(5.817, new double[]{0, 0, 0}, ts);
    }

    // -------------------- Getters --------------------
    public int getId() { return id; }
    public String getSource() { return source; }
    public boolean isOutput() { return output; }
    public long getTs() { return ts; }
    public double getApower() { return apower; }
    public double getVoltage() { return voltage; }
    public double getCurrent() { return current; }
    public double getFreq() { return freq; }
    public Aenergy getAenergy() { return aenergy; }
    public RetAenergy getRet_aenergy() { return ret_aenergy; }
    public Temperature getTemperature() { return temperature; }

    // -------------------- Setters --------------------
    public void setId(int id) { this.id = id; }
    public void setSource(String source) { this.source = source; }
    public void setOutput(boolean output) { this.output = output; }

    public void setTs(long ts) {
        this.ts = ts;
        if (this.aenergy != null) this.aenergy.setMinute_ts(ts);
        if (this.ret_aenergy != null) this.ret_aenergy.setMinute_ts(ts);
    }

    public void setApower(double apower) { this.apower = apower; }
    public void setVoltage(double voltage) { this.voltage = voltage; }
    public void setCurrent(double current) { this.current = current; }
    public void setFreq(double freq) { this.freq = freq; }
    public void setAenergy(Aenergy aenergy) { this.aenergy = aenergy; }
    public void setRet_aenergy(RetAenergy ret_aenergy) { this.ret_aenergy = ret_aenergy; }
    public void setTemperature(Temperature temperature) { this.temperature = temperature; }

    // -------------------- Subclases --------------------
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Aenergy {
        private double total;
        private double[] by_minute;
        private long minute_ts;

        public Aenergy() { }

        public Aenergy(double total, double[] by_minute, long minute_ts) {
            this.total = total;
            this.by_minute = by_minute;
            this.minute_ts = minute_ts;
        }

        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }

        public double[] getBy_minute() { return by_minute; }
        public void setBy_minute(double[] by_minute) { this.by_minute = by_minute; }

        public long getMinute_ts() { return minute_ts; }
        public void setMinute_ts(long minute_ts) { this.minute_ts = minute_ts; }

        @Override
        public String toString() {
            return "Aenergy{" +
                    "total=" + total +
                    ", by_minute=" + Arrays.toString(by_minute) +
                    ", minute_ts=" + minute_ts +
                    '}';
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RetAenergy {
        private double total;
        private double[] by_minute;
        private long minute_ts;

        public RetAenergy() { }

        public RetAenergy(double total, double[] by_minute, long minute_ts) {
            this.total = total;
            this.by_minute = by_minute;
            this.minute_ts = minute_ts;
        }

        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }

        public double[] getBy_minute() { return by_minute; }
        public void setBy_minute(double[] by_minute) { this.by_minute = by_minute; }

        public long getMinute_ts() { return minute_ts; }
        public void setMinute_ts(long minute_ts) { this.minute_ts = minute_ts; }

        @Override
        public String toString() {
            return "RetAenergy{" +
                    "total=" + total +
                    ", by_minute=" + Arrays.toString(by_minute) +
                    ", minute_ts=" + minute_ts +
                    '}';
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Temperature {
        private double tC;
        private double tF;

        public Temperature() { }

        public Temperature(double tC, double tF) {
            this.tC = tC;
            this.tF = tF;
        }

        public double gettC() { return tC; }
        public void settC(double tC) { this.tC = tC; }

        public double gettF() { return tF; }
        public void settF(double tF) { this.tF = tF; }

        @Override
        public String toString() {
            return "Temperature{" +
                    "tC=" + tC +
                    ", tF=" + tF +
                    '}';
        }
    }

    // -------------------- Serialización --------------------
    /** Devuelve el JSON completo del estado actual del sensor. */
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException("Error serializando SensorSw a JSON", e);
        }
    }


}
