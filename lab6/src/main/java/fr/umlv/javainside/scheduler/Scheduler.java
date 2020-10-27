package fr.umlv.javainside.scheduler;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class Scheduler {

    private final PolicyImplementation policyImplementation;

    public Scheduler(PolicyImplementation policyImplementation) {
        this.policyImplementation = policyImplementation;
    }

    private interface PolicyImplementation {
        boolean isEmpty();
        void add(Continuation continuation);
        Continuation remove();
    }

    public enum Policies {
        STACK {
            @Override
            PolicyImplementation createImplementation() {
                return new PolicyImplementation() {

                    private final ArrayDeque<Continuation> queue = new ArrayDeque<>();
                    @Override
                    public boolean isEmpty() {
                        return queue.isEmpty();
                    }

                    @Override
                    public void add(Continuation continuation) {
                        queue.offerFirst(continuation);
                    }

                    @Override
                    public Continuation remove() {
                        return queue.removeFirst();
                    }
                };
            }
        },
        FIFO {
            @Override
            PolicyImplementation createImplementation() {
                return new PolicyImplementation() {

                    private final ArrayDeque<Continuation> queue = new ArrayDeque<>();
                    @Override
                    public boolean isEmpty() {
                        return queue.isEmpty();
                    }

                    @Override
                    public void add(Continuation continuation) {
                        queue.offerLast(continuation);
                    }

                    @Override
                    public Continuation remove() {
                        return queue.removeFirst();
                    }
                };
            }
        },
        RANDOM {
            @Override
            PolicyImplementation createImplementation() {
                return new PolicyImplementation() {

                    private final TreeMap<Integer, ArrayDeque<Continuation>> tree = new TreeMap<>();
                    @Override
                    public boolean isEmpty() {
                        return tree.isEmpty();
                    }

                    @Override
                    public void add(Continuation continuation) {
                        var random = ThreadLocalRandom.current().nextInt();
                        tree.computeIfAbsent(random, __ -> new ArrayDeque<>()).offer(continuation);
                    }

                    @Override
                    public Continuation remove() {
                        var random = ThreadLocalRandom.current().nextInt();
                        var key = tree.floorKey(random);
                        if (key == null) {
                            key = tree.firstKey();
                        }
                        var queue = tree.get(key);
                        var continuation = queue.poll();
                        if (queue.isEmpty()) {
                            tree.remove(key);
                        }
                        return continuation;
                    }
                };
            }

        };

        abstract PolicyImplementation createImplementation();

    }


    public void enqueue(ContinuationScope scope) {
        var continuation = Continuation.getCurrentContinuation(Objects.requireNonNull(scope));
        if (continuation == null) {
            throw new IllegalStateException("enqueue is not called inside a continuation");
        }
        policyImplementation.add(continuation);
        Continuation.yield(scope);
    }

    public void runLoop() {
        while (!policyImplementation.isEmpty()) {
            var continuation = policyImplementation.remove();
            continuation.run();
        }
    }
}
