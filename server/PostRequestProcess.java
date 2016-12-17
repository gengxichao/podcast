package Test2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;

import org.apache.commons.lang3.RandomStringUtils;

import com.mysql.jdbc.Buffer;


public class PostRequestProcess {
	
	/*
	 * 目前的handle是 "Request":"SomeKind",
	 * handle = bufferedReader.readlin()指向了下一行，即所需要的信息
	 */
	
	

	private static Connection connection;
	private String username;
	private String imageauthor;
	private String password;
	private String nickname;
	private String dateofbirth;
	private String gender;
	private String email;
	private String phonenumber;
	private String filetype;
	private String file_path;
	private static final int SIGN_UP_DONE = 0;
	private static final int USER_ALREADY_EXISTS = 1;
	private static final int UNKNOWN_ERROR = 2;
	private static final int NICKNAME_ALREADY_EXISTS = 3;
	private static final int FILE_UPLOAD_PERMITED= 0;
	private static final int FILE_ALREADY_EXISTS = 1;
	private static final int FILE_UPLOAD_UNKNOWN_ERROR = 2;
	private static final int FILE_UPLOAD_DENIED = 3;
	private static final int RESOURCE_REQUIREMENT_PERMITED = 0;
	private static final int RESOURCE_REQUIREMENT_DENIED = 1;
	private static final int IMAGE_RESOURCE_REQUIREMENT_PERMITED = 2;
	FileOutputStream fileOutputStream;
	/*
	 * 连接存储信息所用的数据库
	 */
	public synchronized static void connectToDatabse(){
		String driver = "com.mysql.jdbc.Driver";
		String db_username = "root";
		String db_password = "use your own password";
		String dbName = "your own database";
		String url = "jdbc:mysql://localhost:3306/" + dbName;
		try{
			Class.forName(driver);
			connection = DriverManager.getConnection(url, db_username, db_password);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getFile_path() {
		return file_path;
	}
	
	public String getImageAuthor(){
		return imageauthor;
	}
	/*
	 * 用户注册，通过发送注册信息以加入数据库，并创建用户所持有的文件夹，其中包含着用户上传的图片以及音频，通过测试
	 */
	public int doSignUp(String handle, BufferedReader bufferedReader) throws IOException, SQLException{
//		  "UserName":"admin",
//		  "Password":"admin",
//		  "ConfirmPassword":"admin",
//		  "Nickname":"Nickname",
//		  "DateofBirth":"1990-01-01",(月份和日期都是两位数)
//		  "Gender":"Male",
//		  "E-mail":"aaabbbccc@gmail.com",
//		  "PhoneNumber":"1234567890"
		/*
		 * 1.连接数据库
		 * 2.获得用户注册信息
		 * 3.是否成功
		 * 4.若失败，判断失败类型
		 * 5.若成功，在相应资源库建立用户文件夹，用于保存用户数据
		 */
		connectToDatabse();
		System.out.println("********************************");
		System.out.print("Process start date:");
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		System.out.println(df.format(day));
		try{
			handle = bufferedReader.readLine();//username
			username = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			System.out.println(username);
			handle = bufferedReader.readLine();//password
			password = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			System.out.println(password);
			handle = bufferedReader.readLine();//confirmpassword
			handle = bufferedReader.readLine();//nickname
			nickname = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			
			handle = bufferedReader.readLine();//dateofbirthday
			dateofbirth = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			handle = bufferedReader.readLine();//gender
			gender = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			handle = bufferedReader.readLine();//email
			email = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			handle = bufferedReader.readLine();//phonenumber
			phonenumber = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			String user_dir = "/root/xiaochengxu/"+username+"/Files";
			String sql = "insert into userinfo values(?,?,?,?,?,?,?,?,?)";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, nickname);
			preparedStatement.setString(4, dateofbirth);
			preparedStatement.setString(5, gender);
			preparedStatement.setString(6, email);
			preparedStatement.setString(7, phonenumber);
			preparedStatement.setString(8, user_dir);
			preparedStatement.setString(9, RandomStringUtils.randomAlphanumeric(9));
			preparedStatement.executeUpdate();
			File user_package = new File(user_dir);
			if(!user_package.exists()){
				user_package.mkdirs();
			}
//			System.out.println("***");
			return SIGN_UP_DONE;
		}catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
			String sql_check = "select username from userinfo";
			PreparedStatement pStatement = connection.prepareStatement(sql_check);
			ResultSet rs = pStatement.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equals(username))
					return USER_ALREADY_EXISTS;
			}
			String sql_nickname_check = "select nickname from userinfo";
			PreparedStatement preparedStatement = connection.prepareStatement(sql_nickname_check);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				if(resultSet.getString(1).equals(nickname))
					return NICKNAME_ALREADY_EXISTS;
			}
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ee) {
                    ee.printStackTrace();
                }
            }
			if (resultSet != null) {
                try {
                    rs.close();
                } catch (SQLException ee) {
                    ee.printStackTrace();
                }
            }
			return UNKNOWN_ERROR;
		}finally{
			connection.close();
		}
	}
	/*
	 * 用户登录，通过发送来的用户名获取数据库中所包含信息，判断是否一致，返回是否登录成功,通过测试
	 */
	public File doLogin(String handle, BufferedReader bufferedReader,Socket socket) throws IOException, SQLException{
		//此处socket是和用户通信的socket
//		"Request":"Login",
//		  "UserName":"admin",（为用户名或手机号或邮箱号)
//		  "UserPassword":"admin",
		/*
		 * 当前handler在第1行，即request行，需要向下读取
		 *1.连接数据库
		 *2.判断是否存在该用户名
		 *3.从数据库中得到相应用户名的密码，判断是否一致
		 *4.若成功，更新本次登陆的确认码
		 */
		//连接数据库，得到连接
		connectToDatabse();
		System.out.println("********************************");
		System.out.print("Process start date:");
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		System.out.println(df.format(day));
		handle = bufferedReader.readLine();
		username = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
		System.out.println(username);
		handle = bufferedReader.readLine();
		password = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
		System.out.println(password);
		String sql = "select password from userinfo where username=?";
		PreparedStatement pStatement = connection.prepareStatement(sql);
		pStatement.setString(1, username);
		ResultSet resultSet = pStatement.executeQuery();
		while(resultSet.next()){
			String pssword_in_db = resultSet.getString(1);
			if (password.equals(pssword_in_db)) {
				String sql_1 = "update userinfo set confirmCode=? where username=?";
				PreparedStatement statement = connection.prepareStatement(sql_1);
				String randomcode = RandomStringUtils.randomAlphanumeric(9);
				System.out.println(randomcode);
				statement.setString(1, randomcode);
				statement.setString(2, username);
				statement.executeUpdate();
				String sql_2 = "select * from userinfo where username=?";
				PreparedStatement pStatement2 = connection.prepareStatement(sql_2);
				pStatement2.setString(1,username);
				ResultSet resultSet2 = pStatement2.executeQuery();
				while(resultSet2.next()){
					String con = resultSet2.getString(9);
					File return_info = new File(resultSet2.getString(8)+"/login_return_info.txt");
					if(!return_info.exists()){
						return_info.createNewFile();
					}
					PrintStream printStream = new PrintStream(new FileOutputStream(return_info));
					printStream.println("{");
					printStream.println("\"Return_info\":\"Login successfully\",");
					printStream.println("\"ConfirmCode\":\""+con+"\"");
					printStream.println("}");
					printStream.close();
					return return_info;
				}
				connection.close();
			}
		}
		connection.close();
		File returnfile = new File("/root/xiaochengxu/json/log_in_return_info_failed.txt");
		return returnfile;
	}
	/*
	 * 用户登出，通过发来的用户名来删除数据库信息，通过测试
	 */
	public boolean doLogout(String handle, BufferedReader bufferedReader, Socket socket) throws IOException, SQLException{
		connectToDatabse();
		System.out.println("********************************");
		System.out.print("Process start date:");
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		System.out.println(df.format(day));
		try{
			handle = bufferedReader.readLine();
			String user_to_Logout = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			String sql = "delete from userlogininfo where username="+user_to_Logout;
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.executeUpdate();
			connection.close();
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	/*
	 * 用户上传文件，通过用户名获取用户所持有文件夹路径，上传文件，加入数据库信息，返回文件路径以及是否可以上传，通过测试
	 */
	public int doUpload(String handle, BufferedReader bufferedReader, Socket socket) throws IOException, SQLException{
		/*
		 * 1.连接数据库
		 * 2.获取用户名，得到用户的文件夹地址
		 * 3.上传文件
		 * 4.判断是否上传成功
		 * 5.加入数据库信息
		 * 6.关闭数据库
		 */
		connectToDatabse();
		handle = bufferedReader.readLine();//filetype
		filetype = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
		handle = bufferedReader.readLine();
		username = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
		handle = bufferedReader.readLine();
		String Lable = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
		handle = bufferedReader.readLine();
		String file_name = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
		String sql = "select directory from userinfo where username=?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, username);
		ResultSet resultSet = preparedStatement.executeQuery();//获取了用户相关的文件路径的结果集合
		while (resultSet.next()) {
			file_path = resultSet.getString(1) +"/" + file_name+".mp3";//获取了路径
			File upload_file = new File(file_path);
			if(upload_file.exists()){//同名文件
				connection.close();
				return FILE_ALREADY_EXISTS;
			}
			if(!upload_file.exists()){//用户上传非同名文件
				upload_file.createNewFile();
//				DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
//				FileOutputStream tempFileOutputStream = new FileOutputStream(upload_file);
//				byte[] buffer = new byte[1024];
				try{//写入文件，储存在服务器端
//					while((b = dataInputStream.read())!=-1){
//						tempFileOutputStream.write(buffer, 0, buffer.length);
//					}
					if(filetype.equalsIgnoreCase("audio")){
						//加入数据库信息
//						audioname
//						author
//						directory
//						audiotype
//						hotindex
						String sql_insert = "insert into audio values(?,?,?,?,?,?)";
						PreparedStatement preparedStatement2 = connection.prepareStatement(sql_insert);
						preparedStatement2.setString(1, file_name);
						preparedStatement2.setString(2, username);
						preparedStatement2.setString(3, file_path);
						preparedStatement2.setString(4, Lable);
						preparedStatement2.setInt(5, 0);
						preparedStatement2.setLong(6, System.currentTimeMillis());
						preparedStatement2.executeUpdate();
						File return_info = new File("/root/xiaochengxu/"+username+"/upload_return_info_0.txt");
						if(return_info.exists()){
							return_info.delete();
							return_info.createNewFile();
							PrintStream printStream = new PrintStream(new FileOutputStream(return_info));
							printStream.println("{");
							printStream.println("\"Return_info\":\"File upload Permitted\",");
							printStream.println("\"FilePath\":\""+file_path+"\",");
							printStream.println("\"FileType\":\"Audio\"");
							printStream.print("}");
							printStream.close();
						}
						if(!return_info.exists()){
							return_info.createNewFile();
							PrintStream printStream = new PrintStream(new FileOutputStream(return_info));
							printStream.println("{");
							printStream.println("\"return_info\":\"File upload Permitted\",");
							printStream.println("\"FilePath\":\""+file_path+"\",");
							printStream.println("\"FileType\":\"Audio\"");
							printStream.print("}");
							printStream.close();
						}
					}
					if(filetype.equalsIgnoreCase("images")){
						String sql_insert = "insert into images values(?,?,?,?)";
						PreparedStatement preparedStatement3 = connection.prepareStatement(sql_insert);
						preparedStatement3.setString(1, file_name);
						preparedStatement3.setString(2, file_path);
						preparedStatement3.setString(3, username);
						preparedStatement3.setLong(4,System.currentTimeMillis());
						preparedStatement3.executeQuery();
						File return_info = new File("/root/xiaochengxu/"+username+"/upload_return_info_0.txt");
						if(return_info.exists()){
							return_info.delete();
							return_info.createNewFile();
							PrintStream printStream = new PrintStream(new FileOutputStream(return_info));
							printStream.println("{");
							printStream.println("\"return_info\":\"File upload Permitted\",");
							printStream.println("\"FilePath\":\""+file_path+"\",");
							printStream.println("\"FileType\":\"Image\"");
							printStream.print("}");
							printStream.close();
						}
						if(!return_info.exists()){
							return_info.createNewFile();
							PrintStream printStream = new PrintStream(new FileOutputStream(return_info));
							printStream.println("{");
							printStream.println("\"return_info\":\"File upload Permitted\",");
							printStream.println("\"FilePath\":\""+file_path+"\",");
							printStream.println("\"FileType\":\"Image\"");
							printStream.print("}");
							printStream.close();
						}
					}
					connection.close();
//					dataInputStream.close();
//					tempFileOutputStream.close();
					return FILE_UPLOAD_PERMITED;
				}catch (Exception e) {
					connection.close();
					return FILE_UPLOAD_UNKNOWN_ERROR;
				}
			}
		}
		connection.close();
		return FILE_UPLOAD_DENIED;
	}
	/*
	 * 具体上传文件（待测试）
	 */
	public boolean Upload(String handle, BufferedReader bufferedReader, Socket socket) throws IOException {
		try{
			File file = new File(file_path);
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			byte[] buffer = new byte[1024*1024];//1MB buffer
			int b;
			while((b = dataInputStream.read(buffer))!=-1){
				fileOutputStream.write(buffer, 0, b);
			}
			fileOutputStream.close();
			dataInputStream.close();
			return true;
		}catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	/*
	 * 前端数据要求，通过发送来的路径以获取资源，并返回文件是否可以被传送，在HttpServerCore中实现文件传送，通过测试
	 */
	public synchronized int doAskForResource(String handle, BufferedReader bufferedReader) throws IOException, SQLException{
		/*
		 * 此时handle是request行的askforesource
		 * 1.连接数据库
		 * 2.解析请求
		 * 3.回传文件
		 */
		connectToDatabse();
		System.out.println("********************************");
		System.out.print("Process start date:");
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		System.out.println(df.format(day));
		handle = bufferedReader.readLine();
		filetype = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
		System.out.println("filetype:"+filetype);
		handle = bufferedReader.readLine();//文件名
		String fileName = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
		System.out.println("filename:"+fileName);
		if(filetype.equalsIgnoreCase("audio")){
			String sql = "select * from audio where audioname=?";
			String sql_1 = "select hotindex from audio where audioname=?";
			String sql_2 = "update audio set hotindex=? where audioname=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1,fileName);
			ResultSet resultSet = preparedStatement.executeQuery();
			PreparedStatement preparedStatement2 = connection.prepareStatement(sql_1);
			preparedStatement2.setString(1, fileName);
			ResultSet resultSet2 = preparedStatement2.executeQuery();
			PreparedStatement preparedStatement3 = connection.prepareStatement(sql_2);
			preparedStatement3.setString(2, fileName);
			while(resultSet.next()){
				String file_path = resultSet.getString(3);
				String author = resultSet.getString(2);
				username = author;
				int hotindex = 0;
				while(resultSet2.next()){
					hotindex = resultSet2.getInt(1)+1;
				}
				preparedStatement3.setInt(1,hotindex);
				preparedStatement3.executeUpdate();
				File return_info = new File("/root/xiaochengxu/"+author+"/download_return_info.txt");
				if(return_info.exists()){
					return_info.delete();
					return_info.createNewFile();
					PrintStream printStream = new PrintStream(new FileOutputStream(return_info));
					printStream.println("{");
					printStream.println("\"Return_info\":\"RESOURCE_REQUIREMENT_PERMITED\",");
					printStream.println("\"FilePath\":\""+file_path+"\"");
					printStream.print("}");
					printStream.close();
				}
				if(!return_info.exists()){
					return_info.createNewFile();
					PrintStream printStream = new PrintStream(new FileOutputStream(return_info));
					printStream.println("{");
					printStream.println("\"Return_info\":\"RESOURCE_REQUIREMENT_PERMITED\",");
					printStream.println("\"FilePath\":\""+file_path+"\"");
					printStream.print("}");
					printStream.close();
				}
				resultSet.close();
				resultSet2.close();
				return RESOURCE_REQUIREMENT_PERMITED;
			}
		}
		if(filetype.equalsIgnoreCase("images")){
			String sql = "select * from audio where audioname=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, fileName);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				String author = resultSet.getString(2);
				String imagepath = resultSet.getString(7);
				resultSet.close();
				imageauthor = author;
				File return_info = new File("/root/xiaochengxu/"+author+"/download_return_info_image.txt");
				if(return_info.exists()){
					return_info.delete();
					return_info.createNewFile();
					PrintStream printStream = new PrintStream(new FileOutputStream(return_info));
					printStream.println("{");
					printStream.println("\"Return_info\":\"RESOURCE_REQUIREMENT_PERMITED\",");
					printStream.println("\"FilePath\":\""+imagepath+"\"");
					printStream.print("}");
					printStream.close();
				}
				if(!return_info.exists()){
					return_info.createNewFile();
					PrintStream printStream = new PrintStream(new FileOutputStream(return_info));
					printStream.println("{");
					printStream.println("\"Return_info\":\"RESOURCE_REQUIREMENT_PERMITED\",");
					printStream.println("\"FilePath\":\""+imagepath+"\"");
					printStream.print("}");
					printStream.close();
				}
				return IMAGE_RESOURCE_REQUIREMENT_PERMITED;
			}
		}
		connection.close();
		return RESOURCE_REQUIREMENT_DENIED;
	}
	/*
	 * 前端某一类型音频要求，通过发送来的类型来获取所有该类型的文件列表,通过测试
	 */
	public synchronized List<String> doRequestForSpecificKind(String handle, BufferedReader bufferedReader) throws IOException, SQLException{
		connectToDatabse();
		System.out.println("********************************");
		System.out.print("Process start date:");
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		System.out.println(df.format(day));
		List<String> nameList = new ArrayList<String>();
		handle = bufferedReader.readLine();
		String catagory = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));//得到所需要的类型
		handle = bufferedReader.readLine();
		String sort_by = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));//得到所需要的排列方式
		if(sort_by.equalsIgnoreCase("Hotindex")){
			String sql = "select * from audio where audiotype=? order by hotindex desc";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1,catagory);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				nameList.add(resultSet.getString(1));
				username = resultSet.getString(2);
			}
			return nameList;
		}
		if(sort_by.equalsIgnoreCase("uploaddate")){
			String sql = "select audioname from audio where audiotype=? order by uploaddate desc";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, catagory);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				nameList.add(resultSet.getString(1));
				username = resultSet.getString(2);
			}
			return nameList;
		}
		return nameList;
	}
	/*
	 * 前端某一主播的所有音频要求，通过发送来的主播名获取所有该主播的音频列表，通过测试
	 */
	public synchronized List<String> doSpecificHostAudio(String handle, BufferedReader bufferedReader) throws IOException, SQLException{
		connectToDatabse();
		System.out.println("********************************");
		System.out.print("Process start date:");
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		System.out.println(df.format(day));
		List<String> nameList = new ArrayList<String>();
		handle = bufferedReader.readLine();
		String host_name = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));//得到所需要的类型
		username = host_name;
		handle = bufferedReader.readLine();
		String sort_by = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
		if(sort_by.equalsIgnoreCase("HotIndex")){
			String sql = "select audioname from audio where author=? order by hotindex desc";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, host_name);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				
				nameList.add(resultSet.getString(1));
			}
		}
		if(sort_by.equalsIgnoreCase("uploaddate")){
			String sql = "select audioname from audio where author=? order by uploaddate desc";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, host_name);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				nameList.add(resultSet.getString(1));
			}
		}
		return nameList;
	}
	/*
	 * 前端请求某一用户的所有信息，通过发来的账号或者用户名获取该用户的所有信息，返回的是包含该用户信息的文件，通过测试
	 */
	public synchronized List<String> doRequestForUserInfo(String handle, BufferedReader bufferedReader) throws IOException, SQLException{
		connectToDatabse();
		System.out.println("********************************");
		System.out.print("Process start date:");
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		System.out.println(df.format(day));
		handle = bufferedReader.readLine();
		String searchBy = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
		handle = bufferedReader.readLine();
		String content = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
		List<String> user_info =new ArrayList<String>();
		if(searchBy.equalsIgnoreCase("UserName")){
			username = content;
			String sql = "select * from userinfo where username=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, content);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				for(int i = 1; i <= 9; i++){
					user_info.add(resultSet.getString(i));
				}
			}
			System.out.println(user_info.toString());
		}
		if(searchBy.equalsIgnoreCase("nickname")){
			String sql = "select * from userinfo where nickname=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, content);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				for(int i = 1; i <= 9; i++){
					if(i == 1){
						username = resultSet.getString(i);
					}
					user_info.add(resultSet.getString(i));
				}
			}
		}
		return user_info;
	}
	/*
	 * 用户请求更改昵称等，返回是否成功，通过测试
	 */
	public synchronized boolean doChangeUserInfo(String handle, BufferedReader bufferedReader) throws IOException, SQLException{
		connectToDatabse();
		System.out.println("********************************");
		System.out.print("Process start date:");
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		System.out.println(df.format(day));
		try{
			handle = bufferedReader.readLine();
			String old_username = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			System.out.println(old_username);
			handle = bufferedReader.readLine();
			String new_NickName = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			System.out.println(new_NickName);
			handle = bufferedReader.readLine();
			String new_DateOfBirth = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			System.out.println(new_DateOfBirth);
			handle = bufferedReader.readLine();
			String new_gender = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			System.out.println(new_gender);
			handle = bufferedReader.readLine();
			String new_email = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			System.out.println(new_email);
			handle = bufferedReader.readLine();
			String new_phonenumber = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			System.out.println(new_phonenumber);
			String sql = "update userinfo set nickname=?,dateofbirth=?,gender=?,email=?,phonenumber=? where username=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, new_NickName);
			preparedStatement.setString(2, new_DateOfBirth);
			preparedStatement.setString(3, new_gender);
			preparedStatement.setString(4, new_email);
			preparedStatement.setString(5, new_phonenumber);
			preparedStatement.setString(6, old_username);
			preparedStatement.executeUpdate();
			return true;
		}catch(Exception exception){
			exception.printStackTrace();
			return false;
		}
	}
	/*
	 * 用户请求更改密码，返回是否成功，通过测试
	 */
	public synchronized boolean doChangePassword(String handle, BufferedReader bufferedReader) throws IOException, SQLException{
		connectToDatabse();
		System.out.println("********************************");
		System.out.print("Process start date:");
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		System.out.println(df.format(day));
		try{
			handle = bufferedReader.readLine();
			String userName = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			handle = bufferedReader.readLine();
			String new_password = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			String sql = "update userinfo set password=? where username=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, new_password);
			preparedStatement.setString(2, userName);
			preparedStatement.executeUpdate();
			return true;
		}catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	/*
	 * 用户请求首页信息，按照hotindex
	 */
	public synchronized List<String> doOpenpageSortByHot(String handle, BufferedReader bufferedReader) throws IOException, SQLException{
		connectToDatabse();
		System.out.println("********************************");
		System.out.print("Process start date:");
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		System.out.println(df.format(day));
		List<String> filelist = new ArrayList<String>();
		try{
			handle = bufferedReader.readLine();
			String sort_by = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));//按照什么排序
			String sql = "select * from audio order by hotindex desc limit 10";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				filelist.add(resultSet.getString(1));
				filelist.add(resultSet.getString(7));
			}
			return filelist;
		}catch (Exception e) {
			// TODO: handle exception
			return filelist;
		}
	}
	/*
	 * 用户请求首页信息，按照uploaddate
	 */
	public synchronized List<String> doOpenpageSortByData(String handle, BufferedReader bufferedReader) throws IOException, SQLException{
		connectToDatabse();
		System.out.println("********************************");
		System.out.print("Process start date:");
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		System.out.println(df.format(day));
		List<String> filelist = new ArrayList<String>();
		try{
			handle = bufferedReader.readLine();//按照什么排序
			String sort_by = handle.substring(handle.indexOf("\":\"")+3, handle.indexOf("\","));
			String sql = "select * from audio order by uploaddate desc limit 10";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			int i = 0;
			while (resultSet.next()) {
				filelist.add(resultSet.getString(1));
				filelist.add(resultSet.getString(7));
			}
			return filelist;
		}catch (Exception e) {
			return filelist;
		}
	}
}
