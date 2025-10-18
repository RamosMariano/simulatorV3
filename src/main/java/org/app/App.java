package org.app;
import org.app.config.ConfigLoader;
import org.app.model.*;

import java.util.List;

public class App 
{
    public static void main(String[] args) {
        System.out.println("=== Cargando configuración ===");

        // 1️⃣ Cargar configuración desde el JSON
        ConfigLoader loader = new ConfigLoader();

        // 2️⃣ Obtener lista de unidades (cada una tiene Room, Calefactor y ModeloTermicoConfig)
        List<UnitConfig> configs = loader.getUnitConfigs();
        Simulacion sim = loader.getSimulacionConfig();

        System.out.println("Duración de la simulación: " + sim.getDuracionSegundos() + " segundos");
        System.out.println("Unidades definidas: " + configs.size());

        // 3️⃣ Construir los objetos listos (Room + Calefactor + ModeloTermico)
        List<UnitRuntime> runtimes = loader.buildRuntimeUnits();

        System.out.println("\n=== Objetos creados ===");
        for (UnitRuntime rt : runtimes) {
            Room r = rt.getRoom();
            Calefactor h = rt.getCalefactor();
            ModeloTermico mt = rt.getModeloTermico();

            System.out.printf(
                    "Room #%d | T0=%.1f°C, T_out=%.1f°C, C=%.0f J/K, UA=%.1f W/K | Heater: %s | ModeloTermico: dt=%ds, T(t=%.1fs)=%.2f°C%n",
                    r.getId(), r.getT0(), r.getT_out(), r.getC(), r.getUA(),
                    h, // usa toString()
                    mt.getIntervaloSegundos(), mt.getT(), mt.temperaturaActual()
            );

        }

        System.out.println("\n✅ Configuración cargada y objetos creados correctamente.");
    }
}
