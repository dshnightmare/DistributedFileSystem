package storageserver;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

import storageserver.task.HeartbeatResponseEvent;
import storageserver.task.HeartbeatTask;
import storageserver.task.MigrateFileTask;
import storageserver.task.RegisterTask;
import common.call.Call;
import common.call.CallListener;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.network.ClientConnector;
import common.network.XConnector;
import common.task.Task;
import common.task.TaskMonitor;
import common.util.Configuration;
import common.util.Logger;

public class StorageServer implements TaskEventListener, CallListener {
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

	public StorageServer(String location) {
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

			Configuration conf = Configuration.getInstance();

			taskExecutor = Executors.newFixedThreadPool(MAX_THREADS);

			taskMonitor = new TaskMonitor();
			taskMonitor.addListener(this);

			connector = ClientConnector.getInstance();
			connector.addListener(this);

			// xConnector = XConnector.getInstance();
			// xConnector.addListener(this);
			xConnector = new XConnector(port);
			address = InetAddress.getLocalHost().getHostAddress() + ":" + port;

			// start the registration task, if registration success, then start
			// heartbeat task
			Task task = null;
			synchronized (taskIDCount) {
				task = new RegisterTask(taskIDCount++, address);
			}
			tasks.put(task.getTaskId(), task);
			taskExecutor.execute(task);
			taskMonitor.addTask(task);

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
		logger.info(call.getToTaskId());
		final Task task = tasks.get(call.getToTaskId());
		if (null == task)
			logger.error("StorageServer" + address
					+ " couldn't find a task to handle the call.");
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
			}
		} else if (event.getType() == TaskEvent.Type.HEARTBEAT_RESPONSE) {
			if (task instanceof HeartbeatTask) {
				Map<String, List<String>> working = ((HeartbeatResponseEvent) event)
						.getWorking();
				for (String key : working.keySet()) {
					if (working.get(key).isEmpty() == false) {
						Task workingTask = null;
						synchronized (taskIDCount) {
							workingTask = new MigrateFileTask(taskIDCount++, key, working.get(key));
						}
						tasks.put(workingTask.getTaskId(), workingTask);
						taskExecutor.execute(workingTask);
						taskMonitor.addTask(workingTask);
					}
				}
			}
		} else {

		}
	}

	public void startHeartbeat() {
		Task task = null;
		int id;
		synchronized (taskIDCount) {
			id = taskIDCount++;
		}
		task = new HeartbeatTask(id, overMigrateFile, onMigrateFile);
		tasks.put(task.getTaskId(), task);
		taskExecutor.execute(task);
		taskMonitor.addTask(task);
	}

	public void startSyncTask() {
		// Task task = null;
		// synchronized (taskIDCount) {
		// task = new SyncTask(taskIDCount++, address);
		// }
		// tasks.put(task.getTaskId(), task);
		// taskExecutor.execute(task);
		// taskMonitor.addTask(task);
	}
}
