package client;

import java.io.DataOutputStream;
import java.net.Socket;

public class WriteThread extends Thread {

	private Socket socket;
	private DataOutputStream outToServer;

	public WriteThread(Socket socket, DataOutputStream outToServer) {
		this.socket = socket;
		this.outToServer = outToServer;
	}

}
