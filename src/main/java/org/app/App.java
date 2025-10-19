package org.app;

import org.app.config.ConfigLoader;
import org.app.model.*;
import org.app.sim.EngineUnitAdapter;
import org.app.sim.SimulationEngine;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        // 0) Timestamp ficticio de inicio
        long startEpochMs = Instant.parse("2025-01-01T00:00:00Z").toEpochMilli();

        System.out.println("=== Cargando configuración ===");
        ConfigLoader loader = new ConfigLoader();

        List<UnitConfig> configs = loader.getUnitConfigs();
        Simulacion sim = loader.getSimulacionConfig();

        System.out.println("Duración de la simulación: " + sim.getDuracionSegundos() + " segundos");
        System.out.println("Unidades definidas: " + configs.size());

        List<UnitRuntime> runtimes = loader.buildRuntimeUnits();

        System.out.println("\n=== Objetos creados ===");
        for (UnitRuntime rt : runtimes) {
            Room r = rt.getRoom();
            Calefactor h = rt.getCalefactor();
            ModeloTermico mt = rt.getModeloTermico();

            System.out.printf(
                    "Room #%d | T0=%.1f°C, T_out=%.1f°C, C=%.0f J/K, UA=%.1f W/K | Heater: %s | ModeloTermico: dt=%ds, T(t=%.1fs)=%.2f°C%n",
                    r.getId(), r.getT0(), r.getT_out(), r.getC(), r.getUA(),
                    h,
                    mt.getIntervaloSegundos(), mt.getT(), mt.temperaturaActual()
            );
        }

        System.out.println("\n✅ Configuración cargada y objetos creados correctamente.");

        // Mapa de acceso rápido (si lo querés seguir usando)
        Map<Integer, UnitRuntime> roomMap = new HashMap<>();
        for (UnitRuntime u : runtimes) roomMap.put(u.getRoom().getId(), u);

        // ======== Engine simple ========
        // Paso de sim: usamos el intervalo del modelo (todas tus units lo tienen en 1 s)
        long stepMs = 1000L;
        if (!runtimes.isEmpty()) {
            int dt = runtimes.get(0).getModeloTermico().getIntervaloSegundos();
            stepMs = Math.max(1, dt) * 1000L;
        }

        // warp: 1.0 = tiempo real (cambiar para más rápido)
        double warp = 8000.0;

        // duración desde el JSON (en ms simulados)
        long durationMs = sim.getDuracionSegundos() * 1000L;

        SimulationEngine engine = new SimulationEngine(stepMs, warp, startEpochMs);

        // Adaptar cada UnitRuntime al EngineUnit
        long sampleEveryMs = 5000; // imprimir cada 5s simulados (ajustable)
        for (UnitRuntime rt : runtimes) {
            engine.addUnit(new EngineUnitAdapter(rt, sampleEveryMs));
        }

        // Arrancar
        engine.start();

        // Bloquear hasta completar la duración simulada
        while (engine.getSimTimeMs() < durationMs) {
            try {
                Thread.sleep(100);
                UnitRuntime u1 = roomMap.get(1);


            } catch (InterruptedException ignored) {}
        }

        // Parar
        engine.stop();

        System.out.println("Fin de simulación. Tiempo simulado = " + engine.getSimTimeMs() + " ms");
    }
}
