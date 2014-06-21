package common.thread;

import java.util.LinkedList;
import java.util.Queue;

public class TaskThreadPool {
	private Queue<TaskThread> threads = new LinkedList<TaskThread>();

	public synchronized void addThread(TaskThread thread) {
		threads.add(thread);
	}

	public synchronized void removeThread(TaskThread thread) {
		threads.remove(thread);
	}

	public synchronized TaskThread nextThread() {
		TaskThread thread = threads.poll();
		if (null != thread)
			threads.add(thread);
		return thread;
	}
}
