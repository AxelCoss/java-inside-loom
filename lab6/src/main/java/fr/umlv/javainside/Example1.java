package fr.umlv.javainside;

import java.util.concurrent.locks.ReentrantLock;

public class Example1 {

    private static final Object lock = new Object();
    private static final ReentrantLock reentrantLock = new ReentrantLock();

    public static void main(String[] args) {
        var scope = new ContinuationScope("hello1");
        Runnable runnable = () -> {
//            synchronized (lock) {
//                Continuation.yield(scope);
//            }
            reentrantLock.lock();
            try {
                Continuation.yield(scope);
            } finally {
                reentrantLock.unlock();
            }
            System.out.println("hello continuation");
            System.out.println(Continuation.getCurrentContinuation(scope));
        };
        var continuation = new Continuation(scope, runnable);

        continuation.run();
        continuation.run();
        System.out.println(Continuation.getCurrentContinuation(scope));
    }
}

/*
Q3
un yield = bloque la continuation
Donc rien ne s'affiche
Un deuxieme run = ca s'affiche

Q4
Ca met un espece de wait
et le run fait un espece de notify

Q5
La continuation s'execute une fois puis une exception est throw ou la continuation est relancé

Q6
Le current dans la continuation revoie la classe de la continuation et son scope
Dans le main il renvoi null

Il me semble à peut près equivalent mais il ne considère pas le main comme
une continuation (au contraire des thread car le main est un thread system).

Q7
synchronyzed ca fonctionne pas car c'est codé est assembleur et faut le mettre a jour (c'est chiant)
Reentrant lock ca fonctionne car c'est ecrit en java et que ca a été mis à jour


NOTES thread et Continuation
Comment utiliser des continuation comme des threads
Thread probleme = c'est géré par l'OS (du coup ca doit parler a l'OS dès que ca fait des trucs)
Du coup c'est un appel système et ca archi lent (donc pas ouf)
Et en plus ca crée des grosses piles quand ca démarre (quand on fait start)

Mais la continuation quand on la run ca la lance sur un thread et quand on fait yield ca
la remet dans la VM (deschedule)
C'est un système coopératif
Ya aucun switch de contexte (pas d'appel système)

 */
