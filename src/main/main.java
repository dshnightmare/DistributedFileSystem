package main;

import java.util.Scanner;

import javax.swing.JFrame;

import client.ClientGUI;
import common.network.ClientConnector;
import common.network.ServerConnector;
import common.network.ServerListener;
import common.call.Call;
import common.call.c2n.AddFileCallC2N;
import common.call.c2n.MoveFileCallC2N;
import common.call.c2n.RemoveFileCallC2N;

public class main
{
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

        // 设置Swing窗口使用Java风格
        JFrame.setDefaultLookAndFeelDecorated(true);
        new ClientGUI().init();

//        System.out
//            .print("Start type is(c:client, n:nameserver, s:storageserver):");
//        Scanner input = new Scanner(System.in);
//        String startType = input.next();
//
//        if (startType.equals("c"))
//        {
//            System.out.println("Start as client...");
//            ClientConnector client = new ClientConnector();
//            client.setupSocket();
//            while (true)
//            {
//                System.out.println("waiting for command(type(int) params[])");
//                Scanner in = new Scanner(System.in);
//                // String cmd = in.next();
//                // String param = in.nextLine();
//                // String[] params = param.split(" ");
//                // Call rc = null;
//                // if (cmd.equalsIgnoreCase("add"))
//                // {
//                // rc = new AddFileCallC2N(params[0], params[1]);
//                // }
//                // else if (cmd.equalsIgnoreCase("mv"))
//                // {
//                // rc =
//                // new MoveFileCallC2N(params[0], params[1], params[2],
//                // params[3]);
//                // }
//                // else if (cmd.equalsIgnoreCase("rm"))
//                // {
//                // rc = new RemoveFileCallC2N(params[0], params[1]);
//                // }
//                // client.sendCall(rc);
//            }
//        }
//        else if (startType.equals("n"))
//        {
//            System.out.println("Start as nameserver...");
//            ServerConnector sc = new ServerConnector();
//            sc.start();
//            // ServerConnector sc = new ServerConnector();
//            // sc.setupSocket();
//        }
//        else if (startType.equals("s"))
//        {
//            System.out.println("not implemented");
//        }
//        else
//        {
//            System.out.println("Wrong type code!");
//        }
    }

}
