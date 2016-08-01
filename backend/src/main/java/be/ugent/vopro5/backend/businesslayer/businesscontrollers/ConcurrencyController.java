package be.ugent.vopro5.backend.businesslayer.businesscontrollers;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Michael on 4/28/16.
 */
public class ConcurrencyController {

    private static final int MAX_CAPACITY = 10000;

    private final int maxCapacity;

    private final Map<String, Semaphore> semaphores = new ConcurrentHashMap<>();
    private final Semaphore master = new Semaphore(1);

    private final Phaser capacity;
    private final Phaser cleanup;

    /**
     * Initializes the ConcurrencyController with the default max capacity.
     */
    public ConcurrencyController() {
        this(MAX_CAPACITY);
    }

    /**
     *
     * Initializes the ConcurrencyController with a specific max capacity.
     *
     * @param maxCapacity The maximum capacity
     */
    public ConcurrencyController(int maxCapacity) {
        capacity = new Phaser();
        cleanup = new Phaser();
        capacity.bulkRegister(maxCapacity);
        this.maxCapacity = maxCapacity;
        new CleanupThread().start();
    }

    /**
     *
     * Return the current amount of users in the map.
     *
     * @return the capacity
     */
    public int getCapacity() {
        return semaphores.size();
    }

    /**
     * Clear the map.
     */
    public void doCleanup() {
        semaphores.clear();
    }

    /**
     *
     * A User enters an individualized critical section.
     * This ensures that all critical actions for one User are handled sequentially.
     * The master Semaphore handles concurrent modifications to the semaphore Map.
     *
     * @param userId
     */
    public void enter(String userId) {

        master.acquireUninterruptibly();

        Semaphore semaphore = semaphores.get(userId);
        if (semaphore == null) {
            semaphore = new Semaphore(1);
            semaphores.put(userId, semaphore);
            if (capacity.getRegisteredParties() > 1) {
                capacity.arriveAndDeregister();
            }
        }

        // Increase the count of threads for which
        // we will have to wait before cleanup can commence.
        cleanup.register();
        master.release();

        semaphore.acquireUninterruptibly();
    }

    /**
     *
     * A User leaves the individualized critical section.
     * This allows the same User to enter a new critical section.
     *
     * @param userId
     */
    public void leave(String userId) {
        Semaphore semaphore = semaphores.get(userId);
        semaphore.release();

        // Decrease the count of threads for which
        // we will have to wait before cleanup can commence.
        cleanup.arriveAndDeregister();
    }

    /**
     * This thread clears the semaphore map after it reaches the maximum capacity.
     */
    private class CleanupThread extends Thread {

        private CleanupThread() {
            capacity.register();
            cleanup.register();
        }

        @Override
        public void run() {
            while (true) {
                // Wait until capacity is reached.
                capacity.arriveAndAwaitAdvance();

                // Ensure all threads are blocked from master.
                master.acquireUninterruptibly();

                // Wait until cleanup can commence.
                cleanup.arriveAndAwaitAdvance();

                // Do the cleanup.
                doCleanup();

                // Reset the capacity.
                capacity.bulkRegister(maxCapacity);

                // Allow threads to regain access.
                master.release();
            }
        }
    }
}
