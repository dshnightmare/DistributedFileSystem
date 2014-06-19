package common.observe.event;

public interface EventDispatcher {
	public void addListener(EventListener listener);

	public void removeListener(EventListener listener);

	public void fireEvent(Event event);
}
