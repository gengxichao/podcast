package Test2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.PseudoColumnUsage;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.ObjectUtils.Null;

public class HttpFileRecevieServerCore extends Thread{
	private static Connection connection;
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
	/*
	 * POST /aaaaaaaa/Filename//username HTTP/1.1  
		Accept-Encoding: gzip  
		Content-Length: 225873  
		Content-Type: multipart/form-data; boundary=OCqxMF6-JxtxoMDHmoG5W5eY9MGRsTBp  
		Host: www.myhost.com  
		Connection: Keep-Alive  
		  
		--OCqxMF6-JxtxoMDHmoG5W5eY9MGRsTBp  
		Content-Disposition: form-data; name="lng"  
		Content-Type: text/plain; charset=UTF-8  
		Content-Transfer-Encoding: 8bit  
		  
		116.361545  
		--OCqxMF6-JxtxoMDHmoG5W5eY9MGRsTBp  
		Content-Disposition: form-data; name="lat"  
		Content-Type: text/plain; charset=UTF-8  
		Content-Transfer-Encoding: 8bit  
		  
		39.979006  
		--OCqxMF6-JxtxoMDHmoG5W5eY9MGRsTBp  
		Content-Disposition: form-data; name="images"; filename="/storage/emulated/0/Camera/jdimage/1xh0e3yyfmpr2e35tdowbavrx.jpg"  
		Content-Type: application/octet-stream  
		Content-Transfer-Encoding: binary  
		  
		这里是图片的二进制数据  
		--OCqxMF6-JxtxoMDHmoG5W5eY9MGRsTBp--  
	 */
	private OutputStream fileOut;
	private InputStream inputStream;
	public HttpFileRecevieServerCore(Socket socket) throws IOException, SQLException{
		super();
		inputStream = socket.getInputStream();
		parseUpload(socket);
	}
	
	public synchronized static void connectToDatabse(){
		String driver = "com.mysql.jdbc.Driver";
		String db_username = "root";
		String db_password = "www";
		String dbName = "HttpServerTest";
		String url = "jdbc:mysql://localhost:3306/" + dbName;
		try{
			Class.forName(driver);
			connection = DriverManager.getConnection(url, db_username, db_password);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public long FileSize(File file){
		return file.length();
	}
	
	public String parseUpload(Socket socket) throws IOException, SQLException{
		/*
		 * 解析头部POST请求
		 */
		connectToDatabse();
		DataInputStream bufferedReader = new DataInputStream(socket.getInputStream());
		String handle = bufferedReader.readLine();
		System.out.println("handle:"+handle);
		String filename = handle.substring(handle.indexOf(" /")+12, handle.indexOf("//"));
		System.out.println("filename:"+filename);
		String username = handle.substring(handle.indexOf("//")+2,handle.indexOf(" HTTP"));
		System.out.println("username:"+username);
		String confirmedCode = handle.substring(handle.indexOf(" /")+2,handle.indexOf(filename)-1);
		System.out.println("confirmcode:"+confirmedCode);
		String sql = "select * from userinfo where username=?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, username);
		ResultSet resultSet = preparedStatement.executeQuery();
		String boundary;
		boundary = null;
		while(resultSet.next()){
			String file_Path = resultSet.getString(8);
			System.out.println("filepath:"+file_Path);//done
			String confirmCode_indb = resultSet.getString(9);
			System.out.println("confirmCode:"+confirmCode_indb);//done
			if(confirmCode_indb.equals(confirmedCode)){
				System.out.println("full path:"+file_Path+"/"+filename);
				String fullpath = file_Path+"/"+filename;
				File upload_temp = new File(file_Path+"/"+filename);
				if(!upload_temp.exists()){
					upload_temp.createNewFile();
				}
				if(upload_temp.exists()){
					upload_temp.delete();
					upload_temp.createNewFile();
				}
				String aString = null;
				doUpload(bufferedReader, filename, fullpath, boundary);
			}
		}
		return "UpLoad done";
	}
	
	public boolean doUpload(DataInputStream reader, String Filename,String FullPath,String boundary) throws IOException{
		String line;
		int contentLength = 0;
		line = reader.readLine();
		 while (line != null) {  
	            System.out.println(line);  
	            line = reader.readLine();  
	            if ("".equals(line)) {  
	                break;  
	            } else if (line.indexOf("content-length") != -1) {  
	                contentLength = Integer.parseInt(line.substring(line.indexOf("content-length") + 16));  
	                System.out.println("contentLength: " + contentLength); 
	            }
	            else if (line.indexOf("boundary") != -1) {  
	                //获取multipart分隔符  
	                boundary = line.substring(line.indexOf("boundary") + 9);  
	                System.out.println("boundary="+boundary);
	            }  
				if (contentLength != 0) {
			            //把所有的提交的正文，包括附件和其他字段都先读到buf.
			            byte[] buf = new byte[contentLength*3];
//			            int totalRead = 0;
//			            int size = 0;
			            reader.read(buf,0,buf.length);
			            //用buf构造一个字符串，可以用字符串方便的计算出附件所在的位置
			            String dataString = new String(buf, 0, buf.length);
			            System.out.println("the data user posted:\n" + dataString);
			            int pos = dataString.indexOf(boundary);
			            System.out.println("pos1:"+pos);
			            //以下略过4行就是第一个附件的位置
			            pos = dataString.indexOf("\n", pos) + 1;
			            pos = dataString.indexOf("\n", pos) + 1;
			            pos = dataString.indexOf("\n", pos) + 1;
			            System.out.println("pos2:"+pos);
			            pos = dataString.indexOf("\n", pos) + 1;
			            System.out.println("pos3:"+pos);
			            //附件开始位置
			            int start = dataString.substring(0, pos).getBytes().length;
			            System.out.println("start:"+start);
			            pos = dataString.indexOf(boundary, pos)-4;
			            //附件结束位置
			            System.out.println("pos4:"+pos);
			            int end = dataString.substring(0, pos).getBytes().length;
			            System.out.println(end);
			            fileOut = new FileOutputStream(FullPath);
			            fileOut.write(buf, start, end-start);
			            fileOut.close();
			            fileOut.close();
			            return true;
			        }
	            }
		 return false;
	}
}
