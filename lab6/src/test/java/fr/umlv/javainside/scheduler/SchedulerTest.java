package fr.umlv.javainside.scheduler;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SchedulerTest {

    @Test
    void fifo() {
        var resultList = new ArrayList<Integer>();
        var scope = new ContinuationScope("scope");
        var scheduler = new SchedulerOld(SchedulerPolitics.FIFO);
        new Continuation(scope, () -> { // Run en premier
            scheduler.enqueue(scope);
            resultList.add(0);
        }).run();
        new Continuation(scope, () -> { // Run en second
            scheduler.enqueue(scope);
            resultList.add(1);
        }).run();
        new Continuation(scope, () -> { // Run en dernier
            scheduler.enqueue(scope);
            resultList.add(2);
        }).run();
        scheduler.runLoop();
        assertAll(
                () -> assertEquals(0, resultList.get(0)),
                () -> assertEquals(1, resultList.get(1)),
                () -> assertEquals(2, resultList.get(2))
        );
    }


    @Test
    void stack() {
        var resultList = new ArrayList<Integer>();
        var scope = new ContinuationScope("scope");
        var scheduler = new SchedulerOld(SchedulerPolitics.STACK);
        new Continuation(scope, () -> { // Run en premier
            scheduler.enqueue(scope);
            resultList.add(0);
        }).run();
        new Continuation(scope, () -> { // Run en second
            scheduler.enqueue(scope);
            resultList.add(1);
        }).run();
        new Continuation(scope, () -> { // Run en dernier
            scheduler.enqueue(scope);
            resultList.add(2);
        }).run();
        scheduler.runLoop();
        assertAll(
                () -> assertEquals(2, resultList.get(0)),
                () -> assertEquals(1, resultList.get(1)),
                () -> assertEquals(0, resultList.get(2))
        );
    }
}
