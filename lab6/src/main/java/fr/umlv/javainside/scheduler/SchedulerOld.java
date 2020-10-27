package fr.umlv.javainside.scheduler;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SchedulerOld {

    private final List<Continuation> waitingContinuations = new ArrayList<>();
    private final SchedulerPolitics politic;

    public SchedulerOld(SchedulerPolitics politic) {
        this.politic = Objects.requireNonNull(politic);
    }

    public void enqueue(ContinuationScope scope) {
        var continuation = Continuation.getCurrentContinuation(Objects.requireNonNull(scope));
        if (continuation == null) {
            throw new IllegalStateException("enqueue is not called inside a continuation");
        }

        waitingContinuations.add(continuation);

        Continuation.yield(scope);
    }

    private int getRemoveIndex() {
        return switch (politic) {
            case STACK -> waitingContinuations.size() - 1;
            case FIFO -> 0;
            case RANDOM -> waitingContinuations.size() > 1 ?
                    ThreadLocalRandom.current().nextInt(0, waitingContinuations.size()) :
                    0;
        };
    }

    public void runLoop() {
        while (!waitingContinuations.isEmpty()) {
            var aled = waitingContinuations.remove(getRemoveIndex());
            aled.run();
        }
    }
}
