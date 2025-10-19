package org.app.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimulationEngine {

    private final long stepMs;
    private final double warp;
    private final long startTimestampEpochMs;

    private final List<EngineUnit> units = new ArrayList<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread loopThread;

    private long simTimeMs = 0L;

    public SimulationEngine(long stepMs, double warp, long startTimestampEpochMs) {
        if (stepMs <= 0) throw new IllegalArgumentException("stepMs > 0");
        if (warp   <= 0) throw new IllegalArgumentException("warp > 0");
        this.stepMs = stepMs;
        this.warp = warp;
        this.startTimestampEpochMs = startTimestampEpochMs;
    }

    public long getSimTimeMs() { return simTimeMs; }

    public long getCurrentTimestampMs() { return startTimestampEpochMs + simTimeMs; }

    public void addUnit(EngineUnit unit) { units.add(unit); }

    public void start() {
        if (running.getAndSet(true)) return;
        loopThread = new Thread(this::runLoop, "simulation-loop");
        loopThread.start();
    }

    public void stop() {
        running.set(false);
        if (loopThread != null) {
            try { loopThread.join(2000); } catch (InterruptedException ignored) {}
        }
    }

    private void runLoop() {
        final long sleepTargetNsPerStep = (long)((stepMs / warp) * 1_000_000.0);
        while (running.get()) {
            final long startNs = System.nanoTime();

            simTimeMs += stepMs;
            long ts = getCurrentTimestampMs();

            for (EngineUnit u : units) {
                u.tick(stepMs, simTimeMs, ts);
            }

            long elapsedNs = System.nanoTime() - startNs;
            long remainingNs = sleepTargetNsPerStep - elapsedNs;
            if (remainingNs > 0) {
                try {
                    long ms = remainingNs / 1_000_000L;
                    int ns  = (int)(remainingNs % 1_000_000L);
                    Thread.sleep(ms, ns);
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
