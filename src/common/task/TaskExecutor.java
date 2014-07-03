package common.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.util.Logger;

public class TaskExecutor {
	private final static Logger logger = Logger
			.getLogger(TaskExecutor.class);

	private final static int MAX_TASK = 20;

	private ExecutorService executor = Executors.newFixedThreadPool(MAX_TASK);

	public TaskExecutor() {
	}

	public synchronized void executeTask(Task task) {
		logger.info("Fire a task");
		executor.execute(task);
	}

	public synchronized void shutDown() {
		logger.info("shutdown");
		executor.shutdown();
	}
}
