package common.protocol;

public abstract class Command {
	private final int action;

	public Command(int action) {
		this.action = action;
	}

	public int getAction() {
		return action;
	}

}
