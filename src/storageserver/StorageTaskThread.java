package storageserver;

import common.call.Call;
import common.thread.TaskThread;

public class StorageTaskThread extends TaskThread
{
	private Call call;
	private Storage storage;
    @Override
    public void run() {
    	if(call == null || storage == null)
    		return;
    	switch (call.getType()) {
		case ADD_FILE_SS:
			// TODO 
			DataReciever reciever = new DataReciever();
			break;
		case GET_FILE_SS:
			//TODO
			DataSender sender = new DataSender();
			break;
		default:
			break;
		}
    	setFinish();
    }
    
	public StorageTaskThread(long sid) {
		super(sid);
		// TODO Auto-generated constructor stub
		init(null, null);
	}
    
    public void init(Call call, Storage storage){
    	this.call = call;
    	this.storage = storage;
    }

    @Override
    public void release()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handleCall(Call call)
    {
        // TODO Auto-generated method stub
        
    }
}
