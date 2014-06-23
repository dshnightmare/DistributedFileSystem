package main;

import java.util.Scanner;

import common.network.ClientConnector;
import common.network.ServerConnector;
import common.network.ServerListener;
import common.observe.call.Call;
import common.observe.call.CallFactory;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.print("Start type is(c:client, n:nameserver, s:storageserver):");
		Scanner input = new Scanner(System.in);
		String startType = input.next();
		
		if(startType.equals("c")){
			System.out.println("Start as client...");
			ClientConnector client = new ClientConnector();
			client.setupSocket();
			while(true){
				System.out.println("waiting for command(type(int) params[])");
				Scanner in = new Scanner(System.in);
				String cmd = in.next();
//				String param = in.nextLine();
//				String[] params = param.split(" ");
				Call.Type type = Call.Type.INVALID;
				if (cmd.equalsIgnoreCase("add"))
				    type = Call.Type.ADD_FILE;
				else if (cmd.equalsIgnoreCase("mv"))
				    type = Call.Type.MOVE_FILE;
				else if (cmd.equalsIgnoreCase("rm"))
				    type = Call.Type.REMOVE_FILE;
//				Call rc = new Call(Integer.parseInt(cmd), params);
				Call rc = CallFactory.createCall(type);
				client.sendCommand(rc);
			}
		}
		else if(startType.equals("n")){
			System.out.println("Start as nameserver...");
			ServerConnector sc = new ServerConnector();
			sc.start();
			//ServerConnector sc = new ServerConnector();
			//sc.setupSocket();
		}
		else if(startType.equals("s")){
			System.out.println("not implemented");
		}
		else {
			System.out.println("Wrong type code!");
		}
	}

}
