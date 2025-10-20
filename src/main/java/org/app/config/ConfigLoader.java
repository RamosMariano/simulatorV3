package org.app.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ConfigLoader {

    // Valor por defecto solo para el caso en que una unit no traiga "modeloTermico.intervaloSegundos".
    private static final int DEFAULT_INTERVALO_SEGUNDOS = 10;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SimulationBundle {
        private List<UnitConfig> units;
        private Simulacion simulacion;

        public List<UnitConfig> getUnits() { return units; }
        public void setUnits(List<UnitConfig> units) { this.units = units; }
        public Simulacion getSimulacion() { return simulacion; }
        public void setSimulacion(Simulacion simulacion) { this.simulacion = simulacion; }
    }

    private final SimulationBundle bundle;

    /** Carga y parsea el JSON. No inicia nada, no corre nada. */
    public ConfigLoader() {
        try (InputStream in = getClass().getResourceAsStream("/config/simulation_config.json")) {
            if (in == null) throw new IllegalStateException("No se encontró /config/simulation_config.json");

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            this.bundle = mapper.readValue(in, SimulationBundle.class);

            // Validaciones mínimas (sin efectos secundarios)
            if (bundle.getUnits() == null || bundle.getUnits().isEmpty()) {
                throw new IllegalStateException("El archivo de configuración no contiene 'units'.");
            }
            if (bundle.getSimulacion() == null) {
                throw new IllegalStateException("El archivo de configuración no contiene 'simulacion'.");
            }
            for (UnitConfig u : bundle.getUnits()) {
                Room r = u.getRoom();
                if (r == null) throw new IllegalStateException("Cada unit debe contener 'room'.");
                if (r.getC() <= 0) throw new IllegalStateException("Room.C debe ser > 0 (id=" + r.getId() + ")");
                if (r.getUA() <= 0) throw new IllegalStateException("Room.UA debe ser > 0 (id=" + r.getId() + ")");
            }
            // >>> DEFAULT opcional para 'escenario' si no viene en JSON
            Simulacion sim = bundle.getSimulacion();
            if (sim.getEscenario() <= 0) {
                sim.setEscenario(1);
            }
            // <<<
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo configuración: " + e.getMessage(), e);
        }
    }

    /** Devuelve los datos crudos mapeados desde el JSON (Room/Calefactor/ModeloTermicoConfig por unit). */
    public List<UnitConfig> getUnitConfigs() {
        return bundle.getUnits();
    }

    /** Devuelve la configuración general de simulación (solo datos, sin ejecutar nada). */
    public Simulacion getSimulacionConfig() {
        return bundle.getSimulacion();
    }

    /**
     * Fabrica objetos listos para usar: por cada unit crea un ModeloTermico con t=0
     * y además crea SensorHt y SensorSw inicializados con startEpochMs.
     */
    public List<UnitRuntime> buildRuntimeUnits(long startEpochMs) {
        List<UnitRuntime> out = new ArrayList<>();
        for (UnitConfig u : bundle.getUnits()) {
            Room r = u.getRoom();
            Calefactor h = u.getCalefactor();
            ModeloTermicoConfig mCfg = u.getModeloTermico();

            int intervalo = (mCfg != null && mCfg.getIntervaloSegundos() != null && mCfg.getIntervaloSegundos() > 0)
                    ? mCfg.getIntervaloSegundos()
                    : DEFAULT_INTERVALO_SEGUNDOS;

            double T0   = r.getT0();
            double TOut = r.getT_out();
            double PIn  = (h != null && h.isState()) ? h.getP_entregada() : 0.0;
            double C    = r.getC();
            double UA   = r.getUA();

            ModeloTermico mt = new ModeloTermico(T0, TOut, PIn, C, UA, 0.0, intervalo);

            // >>> Construye UnitRuntime con sensores usando startEpochMs
            out.add(new UnitRuntime(r, h, mt, startEpochMs));
            // <<<
        }
        return out;
    }

    public UnitRuntime findUnitByRoomId(int id, List<UnitRuntime> cache) {
        if (cache == null) return null;
        for (UnitRuntime u : cache) {
            if (u.getRoom().getId() == id) return u;
        }
        return null;
    }

}
