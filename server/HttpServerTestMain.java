package Test2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServerTestMain {
	/*
	 * 主服务器有两个ServerSocket
	 * 1.监听8000端口，接受来自用户的信息
	 * 2.监听8002端口，向文件服务器发送用户的文件路径
	 */
	public static void main(String[] args) {
		int port = 8000;
		try {
			ServerSocket serversocket = new ServerSocket(port);
			System.out.println("Server start run on port "+port);
			
			while(true){
				Socket socket = serversocket.accept();
				System.out.println("New connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
				try {
					HttpServerCore request = new HttpServerCore(socket);
					Thread thread = new Thread(request);
					thread.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
