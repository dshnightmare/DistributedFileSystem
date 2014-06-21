package test;

import common.thread.TaskThread;
import common.thread.TaskThreadPool;
import junit.framework.TestCase;

public class TestTaskThreadPool extends TestCase
{
    private static TaskThreadPool pool;
    
    @Override
    protected void setUp()
    {
        pool = new TaskThreadPool();
    }

    public void testStartMonitoring()
    {
        assertNull(pool.nextThread());
        
        long sidA = 1;
        pool.addThread(new TaskThread(sidA) {
            @Override
            public void run() {
                System.out.println("TaskThread A");
            }
        });
        assertEquals(pool.nextThread().getSid(), sidA);
        
        long sidB = 2;
        pool.addThread(new TaskThread(sidB) {
            @Override
            public void run() {
                System.out.println("TaskThread B");
            }
        });
        assertEquals(pool.nextThread().getSid(), sidA);
        assertEquals(pool.nextThread().getSid(), sidB);

        TaskThread thread = pool.nextThread();
        pool.removeThread(thread);
        assertEquals(pool.nextThread().getSid(), sidB);
    }

    @Override
    protected void tearDown()
    {
    }
}
