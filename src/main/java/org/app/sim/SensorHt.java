package org.app.sim;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Simulador de sensor HT (temperatura y humedad) estilo Shelly HT Gen3.
 * Genera un JSON con estructura realista. Los únicos atributos mutables “en caliente” son:
 *  - src, dst
 *  - ts (top-level) y params.ts (se actualizan juntos con setTimestampSeconds)
 *  - temperature:0 (id, tC, tF)
 * Resto de campos quedan con valores fijos plausibles pero con getters para mostrarlos.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorHt {

    // -------------------- Top-level --------------------
    private String src = "shellyhtg3-84fce63ad204";
    private String dst = "ht-suite/events";
    private String method = "NotifyFullStatus";

    // ts “espejo”: suele venir dentro de params, pero a veces arriba también.
    // Lo mantenemos en ambos (cuando seteás el timestamp, se sincronizan).
    private double ts = 1_752_192_302.55;

    private final Params params = new Params();

    public SensorHt() {
        // Defaults “realistas” similares al ejemplo del usuario
        params.ts = this.ts;
    }

    // -------------------- Setters “en caliente” --------------------

    /** Cambia el source (ej: "shellyhtg3-<mac>"). */
    public void setSrc(String src) { this.src = src; }

    /** Cambia el destino (topic lógico). */
    public void setDst(String dst) { this.dst = dst; }

    /**
     * Setea el timestamp (segundos epoch con decimales) en top-level y dentro de params.
     * Úsalo para mantener ambos sincronizados.
     */
    public void setTimestampSeconds(double secondsEpoch) {
        this.ts = secondsEpoch;
        this.params.ts = secondsEpoch;
    }

    /** Setea temperatura en °C; si no pasás tF, se calcula automáticamente. */
    public void setTemperatureC(double tC) {
        this.params.temperature0.tC = tC;
        this.params.temperature0.tF = cToF(tC);
    }

    /** Setea temperaturas explícitamente. */
    public void setTemperature(double tC, double tF) {
        this.params.temperature0.tC = tC;
        this.params.temperature0.tF = tF;
    }

    /** Setea el ID del canal de temperatura (por defecto 0). */
    public void setTemperatureChannelId(int id) {
        this.params.temperature0.id = id;
    }

    // -------------------- Getters útiles (incluyen los “fijos”) --------------------

    public String getSrc() { return src; }
    public String getDst() { return dst; }
    public String getMethod() { return method; }
    public double getTs() { return ts; }
    public Params getParams() { return params; }

    // -------------------- Serialización --------------------

    /** Devuelve el JSON completo tal como lo publicaría el dispositivo. */
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException("Error serializando SensorHt a JSON", e);
        }
    }

    // ==================== Clases internas que modelan el payload ====================

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Params {
        public double ts = 1_752_192_302.55;

        public Map<String, Object> ble = Collections.emptyMap();

        public Cloud cloud = new Cloud();

        // Clave con dos puntos -> usar @JsonProperty para mapear exactamente
        @JsonProperty("devicepower:0")
        public DevicePower devicepower0 = new DevicePower();

        public Map<String, Object> ht_ui = Collections.emptyMap();

        @JsonProperty("humidity:0")
        public Humidity humidity0 = new Humidity();

        public Mqtt mqtt = new Mqtt();

        public Sys sys = new Sys();

        @JsonProperty("temperature:0")
        public Temperature temperature0 = new Temperature();

        public Wifi wifi = new Wifi();

        public Ws ws = new Ws();

        // -------- Sub-secciones fijas pero con getters --------

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Cloud {
            public boolean connected = false;
            public boolean isConnected() { return connected; }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class DevicePower {
            public int id = 0;
            public Battery battery = new Battery();
            public External external = new External();

            public static class Battery {
                @JsonProperty("V")
                public double volts = 5.32;
                public int percent = 65;
            }
            public static class External {
                public boolean present = false;
            }

            public int getId() { return id; }
            public Battery getBattery() { return battery; }
            public External getExternal() { return external; }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Humidity {
            public int id = 0;
            @JsonProperty("rh")
            public double relativeHumidity = 58.9;

            public int getId() { return id; }
            public double getRh() { return relativeHumidity; }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Mqtt {
            public boolean connected = true;
            public boolean isConnected() { return connected; }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Sys {
            public String mac = "84FCE63AD204";
            @JsonProperty("restart_required")
            public boolean restartRequired = false;
            public Object time = null;
            public Object unixtime = null;
            public long uptime = 1;
            @JsonProperty("ram_size")
            public long ramSize = 256_644;
            @JsonProperty("ram_free")
            public long ramFree = 120_584;
            @JsonProperty("fs_size")
            public long fsSize = 1_048_576;
            @JsonProperty("fs_free")
            public long fsFree = 774_144;
            @JsonProperty("cfg_rev")
            public int cfgRev = 14;
            @JsonProperty("kvs_rev")
            public int kvsRev = 0;
            @JsonProperty("webhook_rev")
            public int webhookRev = 0;
            @JsonProperty("available_updates")
            public Map<String, Object> availableUpdates = Collections.emptyMap();

            @JsonProperty("wakeup_reason")
            public WakeupReason wakeupReason = new WakeupReason();

            @JsonProperty("wakeup_period")
            public int wakeupPeriod = 7200;

            @JsonProperty("reset_reason")
            public int resetReason = 8;

            public static class WakeupReason {
                public String boot = "deepsleep_wake";
                public String cause = "periodic";
            }

            public String getMac() { return mac; }
            public long getUptime() { return uptime; }
            public long getRamSize() { return ramSize; }
            public long getRamFree() { return ramFree; }
            public long getFsSize() { return fsSize; }
            public long getFsFree() { return fsFree; }
            public int getCfgRev() { return cfgRev; }
            public int getWakeupPeriod() { return wakeupPeriod; }
            public int getResetReason() { return resetReason; }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Temperature {
            public int id = 0;
            @JsonProperty("tC")
            public double tC = 19.9;
            @JsonProperty("tF")
            public double tF = 67.8;

            public int getId() { return id; }
            public double gettC() { return tC; }
            public double gettF() { return tF; }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Wifi {
            @JsonProperty("sta_ip")
            public String staIp = "192.168.1.81";
            public String status = "got ip";
            public String ssid = "IOTNET";
            public int rssi = -68;

            public String getStaIp() { return staIp; }
            public String getStatus() { return status; }
            public String getSsid() { return ssid; }
            public int getRssi() { return rssi; }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Ws {
            public boolean connected = false;
            public boolean isConnected() { return connected; }
        }
    }

    // ==================== Utilidades ====================

    private static double cToF(double c) {
        return c * 9.0 / 5.0 + 32.0;
    }

}
