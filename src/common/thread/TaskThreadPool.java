package common.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskThreadPool
{
    private BlockingQueue<TaskThread> threads = new LinkedBlockingQueue<TaskThread>();
}
