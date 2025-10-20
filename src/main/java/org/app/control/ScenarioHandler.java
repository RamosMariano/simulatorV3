package org.app.control;

public interface ScenarioHandler {
    /**
     * Se llama periódicamente desde el loop del main.
     * @param simTimeMs           tiempo simulado transcurrido (ms)
     * @param currentTimestampMs  timestamp "real" simulado (epoch ms)
     */
    void onTick(long simTimeMs, long currentTimestampMs);
}
