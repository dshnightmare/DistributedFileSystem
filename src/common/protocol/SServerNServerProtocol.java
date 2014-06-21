package common.protocol;

import java.io.IOException;
/**
 * implemented by NameServer and call by handler(), handler() then send back return value
 * @author dsh
 */
public interface SServerNServerProtocol {
	/**
	 * Register StorageNode.
	 * 
	 * @param registration
	 *            storagenode registration information
	 * @return updated registration information
	 */
	public StorageNodeInfo registerStorageNode(
			StorageNodeInfo registration) throws IOException;

	/**
	 * sendHeartbeat() tells the NameNode that the StorageNode is still alive and
	 * well. Includes some status info, too. It also gives the NameNode a chance
	 * to return an array of "DatanodeCommand" objects in HeartbeatResponse. A
	 * DatanodeCommand tells the StorageNode to invalidate local block(s), or to
	 * copy them to other DataNodes, etc.
	 * 
	 * @param registration
	 *            datanode registration information
	 * @param reports
	 *            utilization report per storage
	 * @param taskCount
	 *            number of active transceiver threads
	 * @throws IOException
	 *             on error
	 */
	public HeartbeatResponse sendHeartbeat(
			StorageNodeInfo registration, StorageReport report,
			int taskCount) throws IOException;

	public Command fileReport(StorageNodeInfo registration, StorageFileReport[] reports) throws IOException;

	public Command fileRecievedAndDelete(StorageNodeInfo registration, StorageFileReport[] reports) throws IOException;

	public void leaseRecovery(StorageNodeInfo registration, long taskid) throws IOException;
}
