import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JTextArea;

//聊天室服务器处理线程    具体实现为客户端提供的各种服务
public class ClientThread extends Thread
{
	// 处理要记得 构造对象时处理一次 下线再处理一次
	public Socket socket = null;
	public DataInputStream din = null;
	public DataOutputStream dout = null;
	public String ClientName = null;
	
	//UDP
	//接收缓冲区
	public static byte[] recbuf = new byte[8192];
	public static DatagramPacket recdp = new DatagramPacket(recbuf, recbuf.length);
	//UDP socket
	public static DatagramSocket serverds = null;

	// 对所有用户的一个映射关系 用户名->用户
	public static Hashtable<String, ClientThread> nametouser = new Hashtable<String, ClientThread>();

	// 对所有用户的一个映射关系UDP 用户名->用户的Socket
	public static Hashtable<String, SocketAddress> nametosocket = new Hashtable<String,SocketAddress>();
	
	// 消息区对象
	public static JTextArea messege = null;
	// 在线用户文本区对象
	public static JTextArea onlineClients = null;
	// 在线群组文本区对象
	public static JTextArea onlineGroups = null;
	   //服务器文件文本区
    public static JTextArea onlineFiles = null;
    
	public ClientThread() throws IOException
	{
		serverds=ServerLogin.serverds;
		messege = CreateServerFrame.messege;
		onlineClients = CreateServerFrame.onlineClients;
		onlineGroups = CreateServerFrame.onlineGroups;
		onlineFiles = CreateServerFrame.onlineFiles;
	}
	
	//根据数据源的dp构建响应包
    public static DatagramPacket constructDPByDp(String msg,DatagramPacket dp)
    {
    	return new DatagramPacket(msg.getBytes(), msg.getBytes().length, dp.getSocketAddress());
    }
    
    //根据用户名构建向其发送的数据包
    public static DatagramPacket constructDPByName(String msg,String name)
    {
    	return new DatagramPacket(msg.getBytes(), msg.getBytes().length, nametosocket.get(name));
    }

	public static void login(String username,DatagramPacket dp) throws IOException
	{
		// 客户的登录验证 要求名字不能有重复
		if(!"".equals(username) && Server.clientNames.add(username))
		{
			serverds.send(constructDPByDp("ok", dp));
		}
		
		nametosocket.put(username, dp.getSocketAddress());
		
		messege.append(username + " is online.\n");
		
		//服务器端更新在线用户 
		updateOnlineClientsUDP();
		
		//为客户端发送登陆成功的提示
		serverds.send(constructDPByDp("hello " + username + " you're login.", dp));
		
		//让客户端更新在线用户、群组和文件列表
		informUpdateOnlineClientsUDP();
		informUpdateGroupsUDP();
		informUpdateFileListUDP();
		
		sendMsgAllUDP(username + " online!");

	}
	
	// 公屏聊天 给连接到服务器的所有客户端发消息msg UDP
	public static void sendMsgAllUDP(String msg) throws IOException
	{
		// 遍历所有用户列表 找到所有用户的SocketAddress构建dp并发送
		for (String name : Server.clientNames)
		{
			serverds.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, nametosocket.get(name)));
		}
	}
	
	//私聊 给一个特定用户发消息
	public static void sendMsgPriUDP(String name,String msg) throws IOException
	{
		serverds.send(constructDPByName(msg, name));
	}
	
	//通知所有客户端更新用户列表 UDP
	public static void informUpdateOnlineClientsUDP() throws IOException
	{
		String msg = new String("100");
		for(String name : Server.clientNames)
		{
			msg = msg + " " + name;
		}
		sendMsgAllUDP(msg);
	}
	
	//更新在线用户列表 在用户上线和下线时会更新
	public static void updateOnlineClientsUDP()
	{
		onlineClients.setText("");
		for( String username : Server.clientNames )
		{
			onlineClients.append(username + "\n");
			onlineClients.append(nametosocket.get(username).toString()+"\n");
		}
	}
	
	//服务器更新群组列表
	public static void updateOnlineGroupsUDP()
	{
		onlineGroups.setText("");
		
		HashSet<String> members = null;
		
		Iterator<Integer> iterator = Server.groups.keySet().iterator();
		
		while( iterator.hasNext() )
		{
			int groupnum = iterator.next();
			//获取当前群组
			members = Server.groups.get(groupnum);
			//人数大于0的组才显示
			if( members.size() > 0 )
			{
				onlineGroups.append("Group : " + groupnum + "\n");
				//再发每个组的成员
				for( String name : members )
				{
					onlineGroups.append(name + "\n");
				}
			}
			else
			{
				//一边遍历一边修改容器内的值了会报异常 用迭代器就不会出异常了
				iterator.remove();
			}
		}
	}
		
	public static void updateFileListUDP()
	{
		onlineFiles.setText("");
		// 使用File类的方法查看上传到服务器的所有文件
		for (String string : Server.root.list())
		{
			onlineFiles.append(string + "\n");
		}
	}
	//通知客户端更新在线群组列表
	public static void informUpdateGroupsUDP() throws IOException
	{
		String msg = "101";
		Set<Integer> groupnums = Server.groups.keySet();
		HashSet<String> members = null;
		
		for(int groupnum : groupnums)
		{
			members = Server.groups.get(groupnum);
			msg = msg + " !" + String.valueOf(groupnum);
			for(String name : members)
			{
				msg = msg + " " + name;
			}
		}
		
		sendMsgAllUDP(msg);
	}

	public static void informUpdateFileListUDP() throws IOException
	{
		String msg = "102";
		for( String filepath : Server.root.list() )
		{
			msg = msg + " " + filepath;
		}
		sendMsgAllUDP(msg);
	}

	//在服务端创建群组
	public static void createGroupUDP(String groupNames)
	{
		String[] names = groupNames.split("&&");
		HashSet<String> group = new HashSet<String>();
		
		for(String name : names)
		{
			group.add(name);
		}
		Server.groups.put(++Server.groupnum, group);
	}

	//退群
	public static void exitAllGroupUDP(String name)
	{
		for (int i = 1; i <= Server.groupnum; i++)
		{
			HashSet<String> members = Server.groups.get(i);
			if (members == null || members.size() == 0)
				continue;
			members.remove(name);
		}
	}
	
	public static void clientOffLineUDP(String name) throws IOException
	{
		nametosocket.remove(name);
		Server.clientNames.remove(name);
		exitAllGroupUDP(name);
		
		updateOnlineClientsUDP();
		updateOnlineGroupsUDP();
		
		informUpdateGroupsUDP();
		informUpdateOnlineClientsUDP();
		
		messege.append(name + " offline.");
		sendMsgAllUDP(name + " offline.");
	}
	//群聊的实现
	public static void sendMsgGroupUDP(int groupnum,String msg) throws IOException
	{
		// 判断这个组存不存在
		HashSet<String> members = Server.groups.get(groupnum);
		if (members == null)
			return;
		
		for(String name : members)
		{
			serverds.send(constructDPByName("[群组" + String.valueOf(groupnum) + "消息]:" + msg, name));
		}
	}

	//服务器接收客户端上传的文件
	public static void receiveFileUDP(String filename,long num) throws IOException
	{
		messege.append("Server receive file.\n");
		String recPath = Server.serverFilePath + "\\" + filename;
		File file = new File(recPath);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		
		for(long i = 1 ; i <= num ; i++)
		{
			serverds.receive(recdp);
			fos.write(recdp.getData(),0,recdp.getLength());
		}
		
		fos.close();
		
		updateFileListUDP();
		informUpdateFileListUDP();
		
		messege.append("Server receive file over.\n");
	}

	//服务器向客户端发文件
	public static void sendFileUDP(String filename) throws IOException, InterruptedException
	{
		messege.append("Server send file to client.\n");
		String localPath = Server.serverFilePath + "\\" + filename;
		File file = new File(localPath);
		FileInputStream fis = new FileInputStream(file);
		int buflen = 8192;
		long filelen = file.length();
		byte[] filebuf = new byte[buflen];
		long num = (filelen%8192==0)?filelen/8192:1+filelen/8192;
		
		serverds.send(constructDPByDp("-sf " + file.getName() + " " + String.valueOf(num), recdp));
		
		for(long i = 1 ; i <= num ; i++)
		{
			int readlen = fis.read(filebuf);
			serverds.send(new DatagramPacket(filebuf, readlen, recdp.getSocketAddress()));
			//控制传输速率,每100毫秒发送一个数据包,防止中途丢包
			TimeUnit.MICROSECONDS.sleep(100);
		}
		
		fis.close();
		messege.append("Server send file over.\n");
		
	}

	// 每个客户相当于是一个线程 服务器就通过run()方法为客户提供服务
	public void run()
	{
		
		String msg = null;
		String[] msgs = null;
		
		while(true)
		{
			try
			{
				serverds.receive(recdp);
				msg = new String(recdp.getData(), 0, recdp.getLength());
				msgs = msg.split("\\s+");
				//登陆:login name
				if(msgs[0].equals("login") && msgs.length == 2)
				{
					login(msgs[1],recdp);
				}
				//私聊:-p self name msg
				else if(msgs[0].equals("-p"))
				{
					sendMsgPriUDP(msgs[2], "["+msgs[1]+"]对你说："+msgs[3]);
				}
				//客户端要下载文件：-df filename
				else if(msgs[0].equals("-df"))
				{
					sendFileUDP(msgs[1]);
				}
				//客户端要创建群组：-cg name1&&name2
				else if(msgs[0].equals("-cg"))
				{
					createGroupUDP(msgs[1]);
					updateOnlineGroupsUDP();
					informUpdateGroupsUDP();
				}
				//群聊：-g groupnum msg
				else if(msgs[0].equals("-g"))
				{
					sendMsgGroupUDP(Integer.parseInt(msgs[1]),msgs[2]);
				}
				//用户下线的处理
				else if(msgs[0].equals("-exit"))
				{
					clientOffLineUDP(msgs[1]);
				}
				//服务器接收客户端发来的文件：-sf filename num
				else if(msgs[0].equals("-sf"))
				{
					receiveFileUDP(msgs[1],Long.parseLong(msgs[2]));
				}
				//服务器接收到客户端发来的下载文件请求：-df filepath
				else if(msgs[0].equals("df"))
				{
					sendFileUDP(msgs[1]);
				}
				else if(msgs[0].equals("-flush"))
				{
					informUpdateFileListUDP();
					serverds.send(constructDPByDp("File list has flushed.", recdp));
				}
				else 
				{
					sendMsgAllUDP(msg);
				}
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}	
	}
}