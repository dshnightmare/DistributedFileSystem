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

	// Unit test
	public static void main(String[] args) {
		TaskThreadPool pool = new TaskThreadPool();

		pool.addThread(new TaskThread(1) {
			@Override
			public void run() {
				System.out.println("TaskThread A");
			}
		});
		pool.addThread(new TaskThread(2) {
			@Override
			public void run() {
				System.out.println("TaskThread B");
			}
		});

		new Thread(pool.nextThread()).start();
		new Thread(pool.nextThread()).start();
		new Thread(pool.nextThread()).start();
		new Thread(pool.nextThread()).start();

		TaskThread thread = pool.nextThread();
		pool.removeThread(thread);
		new Thread(pool.nextThread()).start();
		new Thread(pool.nextThread()).start();
	}
}
