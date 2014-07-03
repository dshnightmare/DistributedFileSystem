package common.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import common.event.TaskEvent;
import common.event.TaskEvent.Type;
import common.event.TaskEventDispatcher;
import common.event.TaskEventListener;
import common.util.Configuration;
import common.util.Logger;

public class TaskMonitor implements TaskEventDispatcher, TaskEventListener {
	private Map<Long, Task> tasks = new HashMap<Long, Task>();

	private List<TaskEventListener> listeners = new ArrayList<TaskEventListener>();

	private long period;

	private Timer timer = new Timer();

	private TimerTask task = new MonitorTask();

	private boolean isMonitoring = false;

	private static Logger logger = Logger.getLogger(TaskMonitor.class);

	public TaskMonitor() {
		this.period = Configuration.getInstance().getLong(
				Configuration.LEASE_PERIOD_KEY) * 2;
	}

	public synchronized void monitor(Task thread) {
		tasks.put(thread.getTaskId(), thread);
		thread.addListener(this);
	}

	public void startMonitoring() {
		if (!isMonitoring) {
			timer.scheduleAtFixedRate(task, 0, period);
			isMonitoring = true;
		}
	}

	public void stopMonitoring() {
		if (isMonitoring) {
			timer.cancel();
			isMonitoring = false;
		}
	}

	// private void restartMonitoring()
	// {
	// stopMonitoring();
	// startMonitoring();
	// }

	@Override
	public synchronized void addListener(TaskEventListener listener) {
		listeners.add(listener);
	}

	@Override
	public synchronized void removeListener(TaskEventListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void fireEvent(TaskEvent event) {
		for (TaskEventListener l : listeners)
			l.handle(event);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Once a task has finished, it will notify task monitor and taskmonitor
	 * will notify its listeners.
	 */
	@Override
	public void handle(TaskEvent event) {
		fireEvent(event);
		synchronized (tasks) {
			tasks.remove(event.getTaskThread());
			logger.debug("TASK_FINISHED");
		}
	}

	private class MonitorTask extends TimerTask {
		@Override
		public void run() {
			synchronized (tasks) {
				List<Long> abortedList = new ArrayList<Long>();
				for (Entry<Long, Task> e : tasks.entrySet()) {
					if (!e.getValue().isLeaseValid())
						abortedList.add(e.getKey());
				}
				for (Long l : abortedList) {
					fireEvent(new TaskEvent(Type.TASK_ABORTED, tasks.get(l)));
					listeners.remove(tasks.remove(l));
					logger.debug("TASK_ABORTED");
				}
			}
		}
	}
}
