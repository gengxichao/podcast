package Test2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils.Null;

public class HttpServerCore extends Thread{
	
	ServerSocket serverSocket;
	Socket socket;
	BufferedReader bufferedReader;
	OutputStream outputStream;
	InputStream inputStream;
	FileInputStream fileInputStream = null;
	private final String CRLF = "\r\n";
	private String filename;
	private String filetype;
	private String filepath;
	private String handler;
	private String requestType;
	
	
	public HttpServerCore() {
		super();
	}

	public HttpServerCore(ServerSocket serverSocket, Socket socket, BufferedReader bufferedReader,
			OutputStream outputStream, InputStream inputStream) {
		super();
		this.serverSocket = serverSocket;
		this.socket = socket;
		this.bufferedReader = bufferedReader;
		this.outputStream = outputStream;
		this.inputStream = inputStream;
	}
	
	public HttpServerCore(Socket socket) throws IOException {
		super();
		this.socket = socket;
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	}

	@Override
	public void run(){
		try {
			processRequest();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void processRequest() throws SQLException{
		//首先确定前端发来的是Get还是Post
			try {
				handler = bufferedReader.readLine();//Read the first line
				if(handler.equals("")||handler.equals(CRLF)||handler.equals(null)){
					handler = bufferedReader.readLine();
				}
				System.out.println(handler);
				if(handler.contains("GET")){
					doGet();
//					socket.close();
				}
				if(handler.contains("POST")){
					doPost();
//					socket.close();
				}
			} catch (IOException e) {
				System.out.println("Warning");
			}
	}
	
	public String getRequiredFileType(String filename){
		if (filename.endsWith(".html")) {
			return "text/html";
		}
		if(filename.endsWith(".json")){
			return "text/json";
		}
		if(filename.endsWith(".mp4")){
			return "audio/mp4";
		}
		if(filename.endsWith(".jpg")||filename.endsWith(".jpeg")){
			return "image/jpg";
		}
		if(filename.endsWith(".png")){
			return "image/png";
		}
		return null;
	}
	
	//GET方法实现了能够从服务器端下载或得到某个用户登录的信息
	public void doGet() throws IOException{
		while(true){
			//每次读取一行，读取http协议具体内容
			if(handler.equals(CRLF)||handler.equals("")||handler.equals(null)){
				break;
			}
			//读取要求的文件名
			if(handler.indexOf(" /")!=-1){
//			filename = handler.substring(handler.indexOf(" /")+2, handler.indexOf(" HTTP"));
//			filetype = getRequiredFileType(filename);
//			GET方法中，格式为GET //Users/XXXXX HTTP/1.1
//			filepath = "/Users/ComingWind/Documents/WX/res/"+filename;
			filepath = "/"+handler.substring(handler.indexOf(" /")+2, handler.indexOf(" HTTP"));
			filetype = getRequiredFileType(filepath);
			System.out.println(filepath);
			/*
			 * 判断请求文件是否存在
			 */
			File file = new File(filepath);
			boolean fileExists = file.exists();
			System.out.println(fileExists);
			if (fileExists){
				fileInputStream = new FileInputStream("/"+filepath);
				sendByBytes(fileInputStream, outputStream);
//				String statusLine = "HTTP/1.1 200 OK" + CRLF;
//				Date today = new Date();
//				String headers = today.toString();
//				String contentType = filetype+";";
//				String reponseBody = "<html>"+CRLF+
//						"<head>"+CRLF+
//						"<title>"+"Reponse Well"+"</title>"+CRLF+
//						"</head>"+CRLF+
//						"<body>"+CRLF+
//						"Server works well"+CRLF+
//						"</body>"+CRLF+
//						"</html>";
//				outputStream.write(statusLine.getBytes());
//				outputStream.write(headers.getBytes());
//				outputStream.write(contentType.getBytes());
//				outputStream.write(reponseBody.getBytes());
//				outputStream.flush();
				}
			if(!fileExists){
				String statusLine = "HTTP/1.1 404 Not Found" + CRLF;
				Date today = new Date();
				String headers = today.toString();
				String contentType = filetype+";";
				String reponseBody = "<html>"+CRLF+
						"<head>"+CRLF+
						"<title>"+"404"+"</title>"+CRLF+
						"</head>"+CRLF+
						"<body>"+CRLF+
						"Not Found"+CRLF+
						"</body>"+CRLF+
						"</html>";
				outputStream.write(statusLine.getBytes());
				outputStream.write(headers.getBytes());
				outputStream.write(contentType.getBytes());
				outputStream.write(reponseBody.getBytes());
//				outputStream.flush();
				}
			}
			handler = bufferedReader.readLine();
			System.out.println(handler);
		}
		
		bufferedReader.close();
//		socket.shutdownOutput();
	}
	
	public void sendByBytes(FileInputStream fileInputStream, OutputStream outputStream) throws IOException{
		byte[] buffer = new byte[1024];
		int bytes = 0;
		while((bytes = fileInputStream.read(buffer))!=-1){
			outputStream.write(buffer, 0, bytes);
			outputStream.flush();
		}
	}
	
	public void doPost() throws IOException, SQLException{
		while(true){
			//如果是CRLF读取下一行，可能会有post主体
			System.out.println(handler);
			handler = bufferedReader.readLine();
//			if (handler.equals(CRLF)||handler.equals("")||handler.equals("\n")) {
//				handler = bufferedReader.readLine();
//			}
			if(handler.contains("{")){
				//解析post数据
				handler = bufferedReader.readLine();//读取request
				System.out.println(handler);
				requestType = handler.substring(handler.indexOf(":\"")+2,handler.indexOf("\","));
				parsePostRequest();//读取完请求后到达倒数第二行
			}
			if(handler.contains("}")){
				break;
			}
		}
	}

	public void parsePostRequest() throws IOException, SQLException{
		/*
		 * 1.创建PostRequestHandle对象，以处理所发来的post请求
		 * 2.判断post中所要处理的请求
		 * 3.结束处理，关闭流等
		 */
		PostRequestProcess postRequestProcess = new PostRequestProcess();
        
		//确定post所请求的文件究竟要干什么
		if(requestType.equalsIgnoreCase("Register")){
			int flag = postRequestProcess.doSignUp(handler,bufferedReader);
			if(flag == 0){
//				File return_info_file = new File("/root/xiaochengxu/json/sign_up_return_info_0.txt");
//				fileInputStream = new FileInputStream(return_info_file);
//				sendByBytes(fileInputStream, outputStream);
//				BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

	            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(new File("/root/xiaochengxu/json/sign_up_return_info_0.txt")));
//	            System.out.println("1");
	            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
//	            System.out.println("2");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
//	            osw.write("c9\r\n");
////	            System.out.println("3");
	            osw.write(bufferedReader2.readLine());
//	            System.out.println("4");
	            osw.write(bufferedReader2.readLine());
//	            System.out.println("5");
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
//	            System.out.println("6");
//	            osw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n");
//	            osw.write("<HTML>\r\n");
//	            osw.write("  <HEAD><TITLE>A Servlet</TITLE></HEAD>\r\n");
//	            osw.write("  <BODY>\r\n");
//	            osw.write("    This is class com.serv.myServ, using the GET method\r\n");
//	            osw.write("  </BODY>\r\n");
//	            osw.write("</HTML>\r\n");
//	            bufferedReader2.close();
	            osw.write("\r\n");
//	            osw.write("\r\n");
//	            osw.write("0");

	            osw.write("\r\n");
	            osw.write("\r\n");
//	            System.out.println(7);
	            osw.flush();
	            socket.shutdownOutput();
//	            System.out.println(8);
//	            bufferedReader.close();
//	            osw.close();
//				String response = "";  
//		        response += "HTTP/1.1 200 OK\r\n";  
////		        response += "Server: Sunpache 1.0\r\n";  
//		        response += "Content-Type: text/html\r\n";  
//		        response += "Last-Modified: Mon, 11 Jan 2016 13:23:42 GMT\r\n";  
//		        response += "Accept-ranges: bytes\r\n";  
//		        response += "\r\n";  
//		        response += "<html><head><title>test server</title></head><body><p>Post is ok</p></body></html>";
		        
//		        outputStream.write(response.getBytes());
//		        outputStream.flush();  
			}
			if(flag == 1){
				File return_info_file = new File("/root/xiaochengxu/json/sign_up_return_info_1.txt");
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
			if(flag == 2){
				File return_info_file = new File("/root/xiaochengxu/json/sign_up_return_info_2.txt");
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
			if(flag == 3){
				File return_info_file = new File("/root/xiaochengxu/json/sign_up_return_info_3.txt");
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
		}
		if(requestType.equalsIgnoreCase("Login")){
			File return_info_file = postRequestProcess.doLogin(handler,bufferedReader,socket);
			File returnfile = new File("/root/xiaochengxu/json/log_in_return_info_failed.txt");
			if(!return_info_file.equals(returnfile)){
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
			if(return_info_file.equals(returnfile)){
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
		}
		if(requestType.equalsIgnoreCase("Logout")){
			boolean flag = postRequestProcess.doLogout(handler, bufferedReader, socket);
			if(flag){
				File return_info_file = new File("/root/xiaochengxu/json/log_out_return_info_successful.txt");
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
			if(!flag){
				File return_info_file = new File("/root/xiaochengxu/json/log_out_return_info_failed.txt");
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
			socket.close();
		}
//		if(requestType.equalsIgnoreCase("foruploadpermission")){
//			int flag = postRequestProcess.doUpload(handler,bufferedReader,socket);
//			if(flag == 0){
//				File return_info_file = new File("/root/xiaochengxu/"+postRequestProcess.getUsername()+"/upload_return_info_0.txt");
//				fileInputStream = new FileInputStream(return_info_file);
//				sendByBytes(fileInputStream, outputStream);
//			}
//			if(flag == 1){
//				File return_info_file = new File("/root/xiaochengxu/json/upload_return_info_1.txt");
//				fileInputStream = new FileInputStream(return_info_file);
//				sendByBytes(fileInputStream, outputStream);
//			}
//			if(flag == 2){
//				File return_info_file = new File("/root/xiaochengxu/json/upload_return_info_2.txt");
//				fileInputStream = new FileInputStream(return_info_file);
//				sendByBytes(fileInputStream, outputStream);
//			}
//			if(flag == 3){
//				File return_info_file = new File("/root/xiaochengxu/json/upload_return_info_3.txt");
//				fileInputStream = new FileInputStream(return_info_file);
//				sendByBytes(fileInputStream, outputStream);
//			}
//		}
//		if(requestType.equalsIgnoreCase("upload")){
//			boolean flag = postRequestProcess.Upload(handler,bufferedReader, socket);
//			if(flag){
//				File return_info_file = new File("/Users/ComingWind/Documents/WX/res/json/uploadwithpermission_return_info_0.txt");
//				fileInputStream = new FileInputStream(return_info_file);
//				sendByBytes(fileInputStream, outputStream);
//			}
//			if(!flag){
//				File return_info_file = new File("/Users/ComingWind/Documents/WX/res/json/uploadwithpermission_return_info_1.txt");
//				fileInputStream = new FileInputStream(return_info_file);
//				sendByBytes(fileInputStream, outputStream);
//			}
//		}
		if(requestType.equalsIgnoreCase("AskForResource")){
			int flag = postRequestProcess.doAskForResource(handler, bufferedReader);
			if(flag == 0){
				File return_info_file = new File("/root/xiaochengxu/"+postRequestProcess.getUsername()+"/download_return_info.txt");
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
			if(flag == 1){
				File return_info_file = new File("/root/xiaochengxu/json/download_return_info_failed.txt");
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
//	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
			if(flag == 2){
				File return_info_file = new File("/root/xiaochengxu/"+postRequestProcess.getImageAuthor()+"/download_return_info_image.txt");
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
//			fileInputStream.close();
		}
		if(requestType.equalsIgnoreCase("RequestForSpecificKind")){
			List<String> list = postRequestProcess.doRequestForSpecificKind(handler,bufferedReader);
			System.out.println(list.toString());
			int size = list.size();
			File kind_name_list = new File("/root/xiaochengxu/"+postRequestProcess.getUsername()+"/kind_name_list.txt");
			System.out.println(postRequestProcess.getUsername());
			String begin = "{\n";
			String end = "}\n";
			if(kind_name_list.exists()){
				kind_name_list.delete();
				kind_name_list.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(kind_name_list);
				PrintStream printStream = new PrintStream(fileOutputStream);
				printStream.print(begin);
				printStream.println("\"filenamelist\":[");
				for(int i = 0; i < list.size(); i++ ){
					printStream.println("{\"FileID\":"+"\""+String.valueOf(i)+"\",");
					if(i!=list.size()-1)
						printStream.println("\"FileNAME\":\""+list.get(i)+"\"},");
					if(i == list.size()-1)
						printStream.println("\"FileNAME\":\""+list.get(i)+"\"}");
				}
				printStream.println("]");
				printStream.print(end);
				printStream.close();
			}
			if(!kind_name_list.exists()){
				kind_name_list.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(kind_name_list);
				PrintStream printStream = new PrintStream(fileOutputStream);
				printStream.println(begin);
				printStream.println("\"filenamelist\":[");
				for(int i = 0; i < list.size(); i++ ){
					printStream.println("{\"FileID\":"+"\""+String.valueOf(i)+"\",");
					if(i!=list.size()-1)
						printStream.println("\"FileNAME\":\""+list.get(i)+"\"},");
					if(i == list.size()-1)
						printStream.println("\"FileNAME\":\""+list.get(i)+"\"}");
				}
				printStream.println("]");
				printStream.print(end);
				printStream.close();
			}
//			fileInputStream = new FileInputStream(kind_name_list);
			BufferedReader bufferedReader2 = new BufferedReader(new FileReader(kind_name_list));
			OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
            osw.write("HTTP/1.1 200 OK\r\n");
            osw.write("Server: Apache-Coyote/1.1\r\n");
            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
            osw.write("\r\n");
            for(int i = 0; i< (size*2+4); i++)
            {
            	osw.write(bufferedReader2.readLine());
            }
            bufferedReader2.close();
            osw.write("\r\n");
            osw.write("\r\n");
            osw.write("\r\n");
            osw.flush();
            socket.shutdownOutput();
		}
		if(requestType.equalsIgnoreCase("SpecificHostAudio")){
			List<String> list = postRequestProcess.doSpecificHostAudio(handler,bufferedReader);
			int size = list.size();
			File audio_of_host = new File("/root/xiaochengxu/"+postRequestProcess.getUsername()+"/audio_of_host.txt");
			String begin = "{\n";
			String end = "}\n";
			if(audio_of_host.exists()){
				audio_of_host.delete();
				audio_of_host.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(audio_of_host);
				PrintStream printStream = new PrintStream(fileOutputStream);
				printStream.print(begin);
				printStream.println("\"filenamelist\":[");
				for(int i = 0; i < list.size(); i++ ){
					printStream.println("{\"FileID\":"+"\""+String.valueOf(i)+"\",");
					if(i!=list.size()-1)
						printStream.println("\"FileNAME\":\""+list.get(i)+"\"},");
					if(i == list.size()-1)
						printStream.println("\"FileNAME\":\""+list.get(i)+"\"}");
				}
				printStream.println("]");
				printStream.print(end);
				printStream.close();
			}
			if(!audio_of_host.exists()){
				audio_of_host.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(audio_of_host);
				PrintStream printStream = new PrintStream(fileOutputStream);
				printStream.print(begin);
				printStream.println("\"filenamelist\":[");
				for(int i = 0; i < list.size(); i++ ){
					printStream.println("{\"FileID\":"+"\""+String.valueOf(i)+"\",");
					if(i!=list.size()-1)
						printStream.println("\"FileNAME\":\""+list.get(i)+"\"},");
					if(i == list.size()-1)
						printStream.println("\"FileNAME\":\""+list.get(i)+"\"}");
				}
				printStream.println("]");
				printStream.print(end);
				printStream.close();
			}
			BufferedReader bufferedReader2 = new BufferedReader(new FileReader(audio_of_host));
			OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
            osw.write("HTTP/1.1 200 OK\r\n");
            osw.write("Server: Apache-Coyote/1.1\r\n");
            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
            osw.write("\r\n");
            for(int i = 0; i< (size*2+4); i++)
            {
            	osw.write(bufferedReader2.readLine());
            }
            bufferedReader2.close();
            osw.write("\r\n");
            osw.write("\r\n");
            osw.write("\r\n");
            osw.flush();
            socket.shutdownOutput();
		}
		if(requestType.equalsIgnoreCase("RequestForUserInfo")){
			List<String> user_info = postRequestProcess.doRequestForUserInfo(handler, bufferedReader);
			System.out.println(user_info.toString());
			File user_info_file = new File("/root/xiaochengxu/"+postRequestProcess.getUsername()+"/user_info_file.txt");
			if(user_info_file.exists()){
				user_info_file.delete();
				user_info_file.createNewFile();
				PrintStream printStream = new PrintStream(new FileOutputStream(user_info_file));
				printStream.println("{");
				for(int i = 0; i < user_info.size(); i++ ){
//					if(i == 1)
//						continue;
//					System.out.println(user_info.get(i));
//					printStream.println(user_info.get(i));
//					My_Test_Account
//					WHY1
//					1996-09-08
//					Male
//					haoyuwu1996@gmail.com
//					18603839870
//					/Users/ComingWind/Documents/WX/res/My_Test_Account/Files
//					XaeFoEANI
					switch (i) {
					case 0:
						printStream.println("\"Username\":\""+user_info.get(i)+"\",");
						break;
					case 1:
						continue;
					case 2:
						printStream.println("\"Nickname\":\""+user_info.get(i)+"\",");
						break;
					case 3:
						printStream.println("\"DataofBirth\":\""+user_info.get(i)+"\",");
						break;
					case 4:
						printStream.println("\"Gender\":\""+user_info.get(i)+"\",");
						break;
					case 5:
						printStream.println("\"Email\":\""+user_info.get(i)+"\",");
						break;
					case 6:
						printStream.println("\"Phonenumber\":\""+user_info.get(i)+"\",");
						break;
					case 7:
						printStream.println("\"Directory\":\""+user_info.get(i)+"\",");
						break;
					case 8:
						printStream.println("\"ConfirmCode\":\""+user_info.get(i)+"\"");
						break;
					default:
						break;
					}
				}
				printStream.print("}");
				printStream.close();
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(user_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            for(int i = 0; i< 10; i++)
	            {
	            	osw.write(bufferedReader2.readLine());
	            }
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
			if(!user_info_file.exists()){
				user_info_file.createNewFile();
				PrintStream printStream = new PrintStream(new FileOutputStream(user_info_file));
				printStream.println("{");
				for(int i = 0; i < user_info.size(); i++ ){
//					if(i == 1)
//						continue;
					switch (i) {
					case 0:
						printStream.println("\"Username\":\""+user_info.get(i)+"\",");
						break;
					case 1:
						continue;
					case 2:
						printStream.println("\"Nickname\":\""+user_info.get(i)+"\",");
						break;
					case 3:
						printStream.println("\"DataofBirth\":\""+user_info.get(i)+"\",");
						break;
					case 4:
						printStream.println("\"Gender\":\""+user_info.get(i)+"\",");
						break;
					case 5:
						printStream.println("\"Email\":\""+user_info.get(i)+"\",");
						break;
					case 6:
						printStream.println("\"Phonenumber\":\""+user_info.get(i)+"\",");
						break;
					case 7:
						printStream.println("\"Directory\":\""+user_info.get(i)+"\",");
						break;
					case 8:
						printStream.println("\"ConfirmCode\":\""+user_info.get(i)+"\"");
						break;
					default:
						break;
					}
				}
				printStream.print("}");
				printStream.close();
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(user_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            for(int i = 0; i< 10; i++)
	            {
	            	osw.write(bufferedReader2.readLine());
	            }
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
		}
		if(requestType.equalsIgnoreCase("ChangeUserInfo")){
			boolean flag = postRequestProcess.doChangeUserInfo(handler, bufferedReader);
			if(flag){
				File return_info_file = new File("/root/xiaochengxu/json/change_user_info_done.txt");
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
			if(!flag){
				File return_info_file = new File("/root/xiaochengxu/json/change_user_info_failed.txt");
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
		}
		if(requestType.equalsIgnoreCase("ChangePassword")){
			boolean flag = postRequestProcess.doChangePassword(handler, bufferedReader);
			if(flag){
				File return_info_file = new File("/root/xiaochengxu/json/change_password_done.txt");
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
			if(!flag){
				File return_info_file = new File("/root/xiaochengxu/json/change_password_failed.txt");
				BufferedReader bufferedReader2 = new BufferedReader(new FileReader(return_info_file));
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
	            osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            osw.write(bufferedReader2.readLine());
	            bufferedReader2.close();
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
			}
		}
		if(requestType.equalsIgnoreCase("OpenpageDate")){
			List<String> filelist = postRequestProcess.doOpenpageSortByData(handler, bufferedReader);
			System.out.println(filelist.toString());
			OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
			int size = filelist.size();
			 osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write("{");
	            osw.write("\"filelist\":[\n");
	            /*
	             * { "filelist":[
	             * 		{"fileID":"0","audioname":"test","imagepath":"balabal"}
	             * 	]
	             * }	
	             * 
	             */
	            for(int i = 0; i < size/2; i++ ){
	            	if(i== size/2-1){
	            		osw.write("{\"fileID\":"+"\""+String.valueOf(i)+"\",");
	            		osw.write("\"audioname\":\""+filelist.get(2*i)+"\",");
	            		osw.write("\"imagepath\":\""+filelist.get(2*i+1)+"\"}\n]\n}");
	            	}
	            	if(i != size/2-1){
	            		osw.write("{\"fileID\":"+"\""+String.valueOf(i)+"\",");
	            		osw.write("\"audioname\":\""+filelist.get(2*i)+"\",");
	            		osw.write("\"imagepath\":\""+filelist.get(2*i+1)+"\"},");
	            	}
	            }
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
		}
		if(requestType.equalsIgnoreCase("Openpagehot")){
			List<String> filelist = postRequestProcess.doOpenpageSortByHot(handler, bufferedReader);
			System.out.println(filelist.toString());
			OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8"); 
			int size = filelist.size();
			 osw.write("HTTP/1.1 200 OK\r\n");
	            osw.write("Server: Apache-Coyote/1.1\r\n");
	            osw.write("Set-Cookie: JSESSIONID=03493794995CE31A0F131787B6C6CBB2; Path=/; HttpOnly\r\n");
	            osw.write("Content-Type: text/plain;charset=UTF-8\r\n");
//	            osw.write("Transfer-Encoding: chunked\r\n");加上会出错
	            osw.write("Date: Tue, 19 May 2015 02:48:27 GMT\r\n");
	            osw.write("\r\n");
	            osw.write("{");
	            osw.write("\"filelist\":[\n");
	            /*
	             * { "filelist":[
	             * 		{"fileID":"0","audioname":"test","imagepath":"balabal"}
	             * 	]
	             * }	
	             * 
	             */
	            for(int i = 0; i < size/2; i++ ){
	            	if(i== size/2-1){
	            		osw.write("{\"fileID\":"+"\""+String.valueOf(i)+"\",");
	            		osw.write("\"audioname\":\""+filelist.get(2*i)+"\",");
	            		osw.write("\"imagepath\":\""+filelist.get(2*i+1)+"\"}\n]\n}");
	            	}
	            	if(i != size/2-1){
	            		osw.write("{\"fileID\":"+"\""+String.valueOf(i)+"\",");
	            		osw.write("\"audioname\":\""+filelist.get(2*i)+"\",");
	            		osw.write("\"imagepath\":\""+filelist.get(2*i+1)+"\"},");
	            	}
	            }
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.write("\r\n");
	            osw.flush();
	            socket.shutdownOutput();
		}
	}
}
