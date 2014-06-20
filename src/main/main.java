package main;

import java.util.Scanner;

import common.network.ClientConnector;
import common.network.ServerConnector;
import common.observe.call.Call;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner input = new Scanner(System.in);
		String startType = input.next();
		
		if(startType.equals("client")){
			System.out.println("Start as client...");
			ClientConnector client = new ClientConnector();
			client.setupSocket();
			while(true){
				System.out.println("waiting for command(type(int) params[])");
				Scanner in = new Scanner(System.in);
				String cmd = in.next();
				String param = in.nextLine();
				String[] params = param.split(" ");
				Call rc = new Call(Integer.parseInt(cmd), params);
				client.sendCommand(rc);
				System.out.println("Command:"+cmd+" "+param);
			}
		}
		else if(startType.equals("Start as server...")){
			System.out.println("dsh sb");
			ServerConnector server = new ServerConnector();
			server.setupSocket();
		}
	}

}
