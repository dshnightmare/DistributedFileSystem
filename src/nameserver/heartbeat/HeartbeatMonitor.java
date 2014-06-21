package nameserver.heartbeat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import nameserver.meta.StorageNode;

public class HeartbeatMonitor
    implements HeartbeatEventDispatcher
{
    private HeartbeatEventListener listener;

    private Map<StorageNode, MonitoringThread> monitors =
        new HashMap<StorageNode, MonitoringThread>();

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
    public void removeEventListener(HeartbeatEventListener listener)
    {
        listener = null;
    }

    @Override
    public void fireEvent(HeartbeatEvent event)
    {
        listener.handle(event);
    }

    public void startMonitoring(StorageNode node)
    {
        MonitoringThread monitor = new MonitoringThread(new Timer(), node);
        monitors.put(node, monitor);
        monitor.start();
    }

    public void stopMonitoring(StorageNode node)
    {
        MonitoringThread monitor = monitors.get(node);
        monitors.remove(node);
        monitor.stop();
    }

    public void stopAllMonitoring()
    {
        for (Entry<StorageNode, MonitoringThread> e : monitors.entrySet())
            e.getValue().stop();
        monitors.clear();
    }

    private class MonitoringThread
        extends TimerTask
        implements Runnable
    {
        private Timer timer;

        private StorageNode node;

        public MonitoringThread(Timer timer, StorageNode node)
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
                    new HeartbeatEvent(node, HeartbeatEvent.Type.DIED);
                fireEvent(event);
                stopMonitoring(node);
            }
        }

    }
}
