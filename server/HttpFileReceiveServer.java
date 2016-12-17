package Test2;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class HttpFileReceiveServer{
	/*
	 * 文件服务器有两个端口
	 * 1.监听8001，接受来自用户的上传文件
	 * 2.监听8002，接受来自主服务器的用户文件路径
	 */
	/*
	 * 文件服务器实现方式：
	 * 1.接收来自用户的post
	 * 2.在Method行中有着文件的上传路径以及确认码
	 * 3.确定boundary
	 * 4.将该post写入本地文本文件中
	 * 5.去除所有多余的文本信息，剩余的为需要上传的文件内容
	 * 6.将文件内容从文本文件中得到，写入目标文件中
	 * 7.关闭socket
	 */
	public static void main(String[] args) {
		int port = 8001;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("FileServer start run on port "+port);
			while(true){
				Socket socket = serverSocket.accept();
				try {
					HttpFileRecevieServerCore request = new HttpFileRecevieServerCore(socket);
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
