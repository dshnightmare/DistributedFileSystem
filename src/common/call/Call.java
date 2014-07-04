package common.call;

import java.io.Serializable;

public abstract class Call implements Serializable {
	private static final long serialVersionUID = -1157466700148810064L;

	private final Type type;

	private String initiator;

	private long fromTaskId = -1;

	private long toTaskId = -1;

	public Call(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public void setFromTaskId(long fromTaskId) {
		this.fromTaskId = fromTaskId;
	}

	public long getFromTaskId() {
		return fromTaskId;
	}

	public void setToTaskId(long toTaskId) {
		this.toTaskId = toTaskId;
	}

	public long getToTaskId() {
		return toTaskId;
	}

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public static enum Type {
		/**
		 * It's the default value when you create a Call
		 */
		INVALID("INVALID"),

		/**
		 * Abort the task
		 */
		ABORT_N2C("ABORT_N2C"),

		/**
		 * Heartbeat call from storage server to names erver
		 */
		HEARTBEAT_S2N("HEARTBEAT_S2N"),

		/**
		 * Registration call from storage server to name server
		 */
		REGISTRATION_S2N("REGISTRATION_S2N"),

		/**
		 * Add file call from client to name server
		 */
		ADD_FILE_C2N("ADD_FILE_C2N"),

		/**
		 * Add file return call from name server to client
		 */
		ADD_FILE_N2C("ADD_FILE_N2C"),

		ADD_DIRECTORY_C2N("ADD_DIRECTORY_C2N"),

		/**
		 * Get file
		 */
		GET_FILE_C2N("GET_FILE_C2N"), GET_FILE_N2C("GET_FILE_N2C"),

		GET_DIRECTORY_C2N("GET_DIRECTORY_C2N"),

		GET_DIRECTORY_N2C("GET_DIRECTORY_N2C"),

		/**
		 * Append file from client to name server.
		 */
		APPEND_FILE_C2N("APPEND_FILE_C2N"),

		/**
		 * Append file return call from name server to client.
		 */
		APPEND_FILE_N2C("APPEND_FILE_N2C"),

		/**
		 * Remove file call from client to name server
		 */
		REMOVE_FILE_C2N("REMOVE_FILE_C2N"),

		/**
		 * Move file call from client to name server
		 */
		MOVE_FILE_C2N("MOVE_FILE_C2N"),

		/**
		 * Synchronize call from storage server to name server
		 */
		SYNC_S2N("SYNC_S2N"),

		/**
		 * Synchronize return call from name server to storage server.
		 */
		SYNC_N2S("SYNC_N2S"),

		/**
		 * Migrate data from one storage server to others when it's dead.
		 */
		MIGRATE_FILE_N2S("MIGRATE_FILE_N2S"),

		/**
		 * Notify task finish.
		 */
		FINISH_C2N("FINISH_C2N"),

		/**
		 * Renew lease call.
		 */
		LEASE_C2N("LEASE_C2N");

		private String name;

		private Type(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}