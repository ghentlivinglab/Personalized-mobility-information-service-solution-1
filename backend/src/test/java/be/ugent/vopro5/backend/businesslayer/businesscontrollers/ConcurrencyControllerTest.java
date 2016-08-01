package be.ugent.vopro5.backend.businesslayer.businesscontrollers;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by Michael on 5/5/16.
 */
public class ConcurrencyControllerTest {

    private static int MAX_CAPACITY = 100;

    private ConcurrencyController concurrencyController;
    private final Random random = new Random();

    @Before
    public void setUp() {
        this.concurrencyController = new ConcurrencyController(MAX_CAPACITY);
    }

    @Test
    public void testEnter() throws InterruptedException {
        String userId = UUID.randomUUID().toString();

        Thread first = new EnterThread(userId);
        first.start();
        first.join();

        Thread second = new EnterThread(userId);
        second.start();
        while(second.getState() != Thread.State.WAITING);
        assertEquals(Thread.State.WAITING, second.getState());
    }

    @Test
    public void testLeave() throws InterruptedException {
        String userId = UUID.randomUUID().toString();

        Thread first = new EnterThread(userId);
        first.start();
        first.join();

        Thread second = new EnterThread(userId);
        second.start();
        while(second.getState() != Thread.State.WAITING);
        concurrencyController.leave(userId);
        while (second.getState() != Thread.State.TERMINATED);
        assertEquals(Thread.State.TERMINATED, second.getState());
    }

    @Test
    public void testCleanup() throws InterruptedException {
        try {
            for (int run = 0; run < 100; run++) {
                TimerThread timer = new TimerThread(Thread.currentThread());
                timer.start();

                List<Thread> threads = new ArrayList<>();
                int factor = random.nextInt(5) + 3;
                for (int i = 0; i < MAX_CAPACITY * factor; i++) {
                    String userId = UUID.randomUUID().toString();
                    threads.add(new EnterAndLeaveThread(userId));
                    threads.get(i).start();
                }

                for (Thread thread : threads) {
                    thread.join();
                }

                while (concurrencyController.getCapacity() >= MAX_CAPACITY) ;
                assertNotEquals(MAX_CAPACITY, concurrencyController.getCapacity());
                assertTrue(concurrencyController.getCapacity() < MAX_CAPACITY);

                threads.clear();

                int left = MAX_CAPACITY - concurrencyController.getCapacity();
                for (int i = 0; i < left; i++) {
                    String userId = UUID.randomUUID().toString();
                    threads.add(new EnterAndLeaveThread(userId));
                    threads.get(i).start();
                }

                for (Thread thread : threads) {
                    thread.join();
                }

                threads.clear();

                int numThreads = random.nextInt(MAX_CAPACITY);
                for (int i = 0; i < numThreads; i++) {
                    String userId = UUID.randomUUID().toString();
                    threads.add(new EnterAndLeaveThread(userId));
                    threads.get(i).start();
                }

                for (Thread thread : threads) {
                    thread.join();
                }

                while (concurrencyController.getCapacity() >= MAX_CAPACITY);
                assertEquals(numThreads, concurrencyController.getCapacity());
                timer.halt();
            }
        } catch (InterruptedException e) {
            fail();
        }
    }

    private class EnterThread extends Thread {

        protected String userId;

        private EnterThread(String userId) {
            this.userId = userId;
        }

        @Override
        public void run() {
            concurrencyController.enter(userId);
        }
    }

    private class EnterAndLeaveThread extends EnterThread {

        private EnterAndLeaveThread(String userId) {
            super(userId);
        }

        @Override
        public void run() {
            super.run();
            try {
                this.sleep(10);
            } catch(InterruptedException e) {
                // shouldn't happen
            } finally {
                concurrencyController.leave(userId);
            }
        }
    }

    private class TimerThread extends Thread {

        private long begin;
        private long threshold;
        private volatile boolean running;

        private Thread parent;

        private TimerThread(Thread parent) {
            begin = System.currentTimeMillis();
            threshold = 600000;
            running = true;

            this.parent = parent;
        }

        private void halt() {
            running = false;
        }

        @Override
        public void run() {
            while (System.currentTimeMillis() - begin < threshold && running);
            if (running) {
                parent.interrupt();
            }
        }
    }
}
