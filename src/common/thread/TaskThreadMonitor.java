package common.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import common.observe.event.Event;
import common.observe.event.EventDispatcher;
import common.observe.event.EventListener;

public class TaskThreadMonitor implements EventDispatcher {
	private TaskThreadPool pool;
	private List<EventListener> listeners = new ArrayList<EventListener>();
	private Timer timer = new Timer();
	private TimerTask task = new MonitorTask();
	private boolean isMonitoring = false;

	public TaskThreadMonitor(TaskThreadPool pool) {
		pool = this.pool;
	}

	public void startMonitoring(long period) {
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
	
	public void restartMonitoring(long period) {
		stopMonitoring();
		startMonitoring(period);
	}

	@Override
	public synchronized void addListener(EventListener listener) {
		listeners.add(listener);
	}

	@Override
	public synchronized void removeListener(EventListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void fireEvent(Event event) {
		for (EventListener l : listeners)
			l.handleEvent(event);
	}

	private class MonitorTask extends TimerTask {

		@Override
		public void run() {
			TaskThread thread = pool.nextThread();
			if (null != thread) {
				// TODO
			}
		}

	}
}
