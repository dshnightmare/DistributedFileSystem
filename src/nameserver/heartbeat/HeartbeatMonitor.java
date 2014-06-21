package nameserver.heartbeat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import nameserver.meta.StorageStatus;

public class HeartbeatMonitor
    implements HeartbeatEventDispatcher
{
    private HeartbeatEventListener listener;

    private Map<StorageStatus, MonitoringThread> monitors =
        new HashMap<StorageStatus, MonitoringThread>();

    private long period;

    public HeartbeatMonitor(long period)
    {
        this.period = period;
    }

    @Override
    public void setEventListener(HeartbeatEventListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void removeEventListener()
    {
        listener = null;
    }

    @Override
    public void fireEvent(HeartbeatEvent event)
    {
        listener.handle(event);
    }

    public void startMonitoring(StorageStatus node)
    {
        MonitoringThread monitor = new MonitoringThread(new Timer(), node);
        monitors.put(node, monitor);
        monitor.start();
    }

    public void stopMonitoring(StorageStatus node)
    {
        MonitoringThread monitor = monitors.get(node);
        monitors.remove(node);
        monitor.stop();
    }

    public void stopAllMonitoring()
    {
        for (Entry<StorageStatus, MonitoringThread> e : monitors.entrySet())
            e.getValue().stop();
        monitors.clear();
    }

    private class MonitoringThread
        extends TimerTask
        implements Runnable
    {
        private Timer timer;

        private StorageStatus node;

        public MonitoringThread(Timer timer, StorageStatus node)
        {
            this.timer = timer;
            this.node = node;
        }

        public void start()
        {
            timer.scheduleAtFixedRate(this, 0, period);
        }

        public void stop()
        {
            timer.cancel();
        }

        @Override
        public void run()
        {
            if (!node.isAlive())
            {
                HeartbeatEvent event =
                    new HeartbeatEvent(HeartbeatEvent.Type.DIED, node);
                fireEvent(event);
                stopMonitoring(node);
            }
            else
            {
                node.setAlive(false);
            }
        }

    }
}
