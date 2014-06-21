package common.util;

import java.util.logging.Handler;
import java.util.logging.Level;

public class Logger
{
    private final java.util.logging.Logger loggerDelegate;

    private Logger(java.util.logging.Logger logger)
    {
        this.loggerDelegate = logger;
    }

    public static Logger getLogger(Class<?> clazz) throws NullPointerException
    {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String name) throws NullPointerException
    {
        return new Logger(java.util.logging.Logger.getLogger(name));
    }

    public boolean isTraceEnabled()
    {
        return loggerDelegate.isLoggable(Level.FINER);
    }

    public void trace(Object msg)
    {
        loggerDelegate.finer(msg != null ? msg.toString() : "null");
    }

    public void trace(Object msg, Throwable t)
    {
        loggerDelegate.log(Level.FINER, msg != null ? msg.toString() : "null",
            t);
    }

    public boolean isDebugEnabled()
    {
        return loggerDelegate.isLoggable(Level.FINE);
    }

    public void debug(Object msg)
    {
        loggerDelegate.fine(msg != null ? msg.toString() : "null");
    }

    public void debug(Object msg, Throwable t)
    {
        loggerDelegate
            .log(Level.FINE, msg != null ? msg.toString() : "null", t);
    }

    public boolean isInfoEnabled()
    {
        return loggerDelegate.isLoggable(Level.INFO);
    }

    public void info(Object msg)
    {
        loggerDelegate.info(msg != null ? msg.toString() : "null");
    }

    public void info(Object msg, Throwable t)
    {
        loggerDelegate
            .log(Level.INFO, msg != null ? msg.toString() : "null", t);
    }

    public void warn(Object msg)
    {
        loggerDelegate.warning(msg != null ? msg.toString() : "null");
    }

    public void warn(Object msg, Throwable t)
    {
        loggerDelegate.log(Level.WARNING,
            msg != null ? msg.toString() : "null", t);
    }

    public void error(Object msg)
    {
        loggerDelegate.severe(msg != null ? msg.toString() : "null");
    }

    public void error(Object msg, Throwable t)
    {
        loggerDelegate.log(Level.SEVERE, msg != null ? msg.toString() : "null",
            t);
    }

    public void fatal(Object msg)
    {
        loggerDelegate.severe(msg != null ? msg.toString() : "null");
    }

    public void fatal(Object msg, Throwable t)
    {
        loggerDelegate.log(Level.SEVERE, msg != null ? msg.toString() : "null",
            t);
    }

    public void setLevelFatal()
    {
        setLevel(Level.SEVERE);
    }

    public void setLevelError()
    {
        setLevel(Level.SEVERE);
    }

    public void setLevelWarn()
    {
        setLevel(Level.WARNING);
    }

    public void setLevelInfo()
    {
        setLevel(Level.INFO);
    }

    public void setLevelDebug()
    {
        setLevel(Level.FINE);
    }

    public void setLevelTrace()
    {
        setLevel(Level.FINER);
    }

    public void setLevelAll()
    {
        setLevel(Level.ALL);
    }

    public void setLevelOff()
    {
        setLevel(Level.OFF);
    }

    private void setLevel(java.util.logging.Level level)
    {
        Handler[] handlers = loggerDelegate.getHandlers();
        for (Handler handler : handlers)
            handler.setLevel(level);

        loggerDelegate.setLevel(level);
    }
}
