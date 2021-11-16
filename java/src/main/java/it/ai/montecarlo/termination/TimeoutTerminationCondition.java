package it.ai.montecarlo.termination;

public class TimeoutTerminationCondition implements TerminationCondition{
    private final long end;

    public TimeoutTerminationCondition(int timeout_s) {
        this.end = System.currentTimeMillis() + timeout_s * 1000L;
    }

    @Override
    public boolean reached() {
        return System.currentTimeMillis() >= end;
    }
}
