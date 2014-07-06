package storageserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import storageserver.event.AddFileDuplicateEvent;
import storageserver.event.BeforeRegFinishEvent;
import storageserver.event.DuplicateFinishEvent;
import storageserver.event.HeartbeatResponseEvent;
import storageserver.event.MigrateFileFinishEvent;
import storageserver.task.AddFileTask;
import storageserver.task.DuplicateFileTask;
import storageserver.task.GetFileTask;
import storageserver.task.HeartbeatTask;
import storageserver.task.MigrateFileTask;
import storageserver.task.RegisterTask;
import storageserver.task.SyncTask;
import common.call.Call;
import common.call.CallListener;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.network.ClientConnector;
import common.network.SocketListener;
import common.network.XConnector;
import common.task.Task;
import common.task.TaskMonitor;
import common.util.Configuration;
import common.util.Logger;

public class StorageServer implements TaskEventListener, CallListener,
		SocketListener {
	private static final Logger logger = Logger.getLogger(StorageServer.class);
	private static final int MAX_THREADS = 20;
	private String address;
	private final Storage storage;
	private ClientConnector connector = null;
	private XConnector xConnector = null;
	private Map<Long, Task> tasks = new HashMap<Long, Task>();
	private Map<String, List<String>> onMigrateFile = new HashMap<String, List<String>>();
	private Map<String, List<String>> overMigrateFile = new HashMap<String, List<String>>();
	private TaskMonitor taskMonitor = null;
	private ExecutorService taskExecutor = null;
	private boolean initialized = false;
	private Boolean registered = false;
	private Integer taskIDCount = 0;
	private long NStid = -1;

	public StorageServer(String location) throws IOException {
		storage = new Storage(location);
	}

	public void initAndstart(int port) throws Exception {
		if (initialized) {
			logger.warn("StorgeServer has been initialized before, you can't do it twice.");
			return;
		} else {
			// check configuration
			if (null == Configuration.getInstance()) {
				throw new Exception(
						"Initiation failed, couldn't load configuration file.");
			}
			// check connector
			if (null == ClientConnector.getInstance()) {
				throw new Exception(
						"Initiation failed, couldn't create storage connector.");
			}
			// check xconnector
			// if (null == XConnector.getInstance()) {
			// throw new Exception(
			// "Initiation failed, couldn't create storage xconnector.");
			// }

			taskExecutor = Executors.newFixedThreadPool(MAX_THREADS);

			taskMonitor = new TaskMonitor();
			taskMonitor.addListener(this);

			connector = ClientConnector.getInstance();
			connector.addListener(this);

			// xConnector = XConnector.getInstance();
			// xConnector.addListener(this);
			xConnector = new XConnector(port);
			xConnector.start();
			xConnector.addSocketListener(this);
			address = InetAddress.getLocalHost().getHostAddress() + ":" + port;

			// start the registration task, if registration success, then start
			// heartbeat task
			startRegister();

			logger.info("StorageServer" + connector.getLocalAddress()
					+ " initialization finished.");

			initialized = true;
		}
	}

	@Override
	public void handleCall(Call call) {
		logger.info("StorageServer" + connector.getLocalAddress()
				+ " recievced a call: " + call.getType());
		// switch (call.getType()) {
		// // TODO 需要添加注册成功之后的处理
		// case FINISH:{
		// tasks.get(key)
		// break;
		// }
		// case MIGRATE_FILE_N2S: {
		// MigrateFileCallN2S handlecall = (MigrateFileCallN2S) call;
		// for (String key : handlecall.getFiles().keySet()) {
		// }
		// break;
		// }
		// case SYNC_N2S:
		// break;
		// default:
		// break;
		// }
		logger.info("dispatch to task: " + call.getToTaskId());
		final Task task = tasks.get(call.getToTaskId());
		if (null == task)
			logger.error("StorageServer" + address + " couldn't find a task "
					+ call.getToTaskId() + " to handle the call.");
		else {
			logger.info("StorageServer" + address + " start handle the call.");
			task.handleCall(call);
		}
	}

	@Override
	public void handle(TaskEvent event) {
		// TODO Auto-generated method stub
		final Task task = event.getTaskThread();

		if (event.getType() == TaskEvent.Type.TASK_FINISHED) {
			if (task instanceof RegisterTask) {
				registered = true;
				logger.info("RegisterTask: " + task.getTaskId() + " "
						+ event.getType());
				startHeartbeat();
				startSyncTask();

			} else if (task instanceof HeartbeatTask) {
				logger.info("HeartbeatTask: " + task.getTaskId() + " "
						+ event.getType());
				// 需要重新注册
				NStid = -1;
				registered = false;
				startRegister();
			}
		} else if (event.getType() == TaskEvent.Type.REG_FINISHED) {
			NStid = ((BeforeRegFinishEvent) event).getNStid();
		} else if (event.getType() == TaskEvent.Type.HEARTBEAT_RESPONSE) {
			if (task instanceof HeartbeatTask) {
				Map<String, List<String>> working = ((HeartbeatResponseEvent) event)
						.getWorking();
				for (String key : working.keySet()) {
					if (working.get(key).isEmpty() == false) {
						startMigrateFileTask(key, working.get(key));
					}
				}
			}
		} else if (event.getType() == TaskEvent.Type.MIGRATE_FINISHED) {
			if (task instanceof MigrateFileTask) {
				String key = ((MigrateFileFinishEvent) event).getAddress();
				List<String> filenames = ((MigrateFileFinishEvent) event)
						.getFiles();
				synchronized (overMigrateFile) {
					synchronized (onMigrateFile) {
						onMigrateFile.get(key).removeAll(filenames);
						if (overMigrateFile.get(key) == null)
							overMigrateFile.put(key, new ArrayList<String>());
						overMigrateFile.get(key).addAll(filenames);
					}
				}
			}
		} else if (event.getType() == TaskEvent.Type.ADDFILE_DUPLICATE) {
			if (task instanceof AddFileTask) {

				for (String address : ((AddFileDuplicateEvent) event).getTodo()) {
					startDuplicateFileTask(address,
							((AddFileDuplicateEvent) event).getFilename(),
							task.getTaskId());
				}
			}
		} else if (event.getType() == TaskEvent.Type.DUPLICATE_FINISHED) {
			if (task instanceof DuplicateFileTask) {
				tasks.get(((DuplicateFinishEvent) event).getParent())
						.handleCall(null);
//				if (((DuplicateFinishEvent) event).getStatus() == XConnector.Type.OP_FINISH_SUC) {
//				} else {
//
//				}
			}
		} else {

		}
	}

	@Override
	public void handleSocket(Socket s) {
		byte op;
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(s.getInputStream());
			op = dis.readByte();
			switch (op) {
			case XConnector.Type.OP_WRITE_BLOCK:
				logger.info("Storage " + address + " start a addFileTask.");
				startAddFileTask(s, dis);
				break;
			case XConnector.Type.OP_READ_BLOCK:
				logger.info("Storage " + address + " start a getFileTask.");
				startGetFileTask(s, dis);
				break;
			case XConnector.Type.OP_APPEND_BLOCK:
				logger.info("Storage " + address + " start a appendFileTask.");
				startAppendFileTask();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void startRegister() {
		Task task = null;
		int id;
		synchronized (taskIDCount) {
			id = taskIDCount++;
		}
		task = new RegisterTask(id, address);
		tasks.put(task.getTaskId(), task);
		taskExecutor.execute(task);
		taskMonitor.addTask(task);
	}

	public void startHeartbeat() {
		Task task = null;
		int id;
		synchronized (taskIDCount) {
			id = taskIDCount++;
		}
		task = new HeartbeatTask(id, overMigrateFile, onMigrateFile, NStid,
				taskMonitor);
		tasks.put(task.getTaskId(), task);
		taskExecutor.execute(task);
		taskMonitor.addTask(task);
	}

	public void startSyncTask() {
		Task task = null;
		int id;
		synchronized (taskIDCount) {
			id = taskIDCount++;
		}
		task = new SyncTask(id, storage, address);
		tasks.put(task.getTaskId(), task);
		taskExecutor.execute(task);
		taskMonitor.addTask(task);
	}

	public void startAddFileTask(Socket socket, DataInputStream dis) {
		Task task = null;
		int id;
		synchronized (taskIDCount) {
			id = taskIDCount++;
		}
		task = new AddFileTask(id, socket, dis, storage);
		tasks.put(task.getTaskId(), task);
		taskExecutor.execute(task);
		taskMonitor.addTask(task);
	}

	public void startGetFileTask(Socket socket, DataInputStream dis) {
		Task task = null;
		int id;
		synchronized (taskIDCount) {
			id = taskIDCount++;
		}
		task = new GetFileTask(id, socket, dis, storage);
		tasks.put(task.getTaskId(), task);
		taskExecutor.execute(task);
		taskMonitor.addTask(task);
	}

	public void startAppendFileTask() {

	}

	public void startDuplicateFileTask(String address, String filename,
			long parent) {
		Task task = null;
		int id;
		synchronized (taskIDCount) {
			id = taskIDCount++;
		}
		logger.info("----------------->storage start a duplicateTask");
		task = new DuplicateFileTask(id, storage, address, filename, parent);
		tasks.put(task.getTaskId(), task);
		taskExecutor.execute(task);
		taskMonitor.addTask(task);
	}

	public void startMigrateFileTask(String address, List<String> files) {
		Task workingTask = null;
		int id;
		synchronized (taskIDCount) {
			id = taskIDCount++;
		}
		workingTask = new MigrateFileTask(id, address, files, storage);
		tasks.put(workingTask.getTaskId(), workingTask);
		taskExecutor.execute(workingTask);
		taskMonitor.addTask(workingTask);
	}
}
