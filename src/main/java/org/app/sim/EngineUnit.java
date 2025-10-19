package org.app.sim;

/**
 * Contrato mínimo para cualquier unidad simulada.
 */
public interface EngineUnit {
    void tick(long dtMs, long simTimeMs, long currentTimestampMs);
}
