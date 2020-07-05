import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextArea;


//客户端监听线程 处理来自服务器的信息 能让客户端在发送消息的同时接收消息
public class ClientListen extends Thread
{
	//各种文本框
	public static JTextArea messege = null;
	//public static JTextArea clientList = null;
	//public static JTextArea groupList = null;
	//public static JTextArea fileList = null;
	
	//在线用户列表 和dlm配套使用
	public static JList<String> clientList = null;
	public static DefaultListModel<String> dlmclientList = null;
	
	//在线群组列表 和dlm配套使用
	public static JList<String> groupList = null;
	public static DefaultListModel<String> dlmgroupList = null;
	
	//文件列表 和dlm配套使用
	public static JList<String> fileList = null;
	public static DefaultListModel<String> dlmfileList = null;
	
	
	public static Socket listen = null;
	public static DataOutputStream dout = null;
	public static DataInputStream din = null;
	
	
	public static File recfile = null;
	
	//UDP接收缓冲区
	public static byte[] recbuf = new byte[8192];
	public static DatagramPacket recdp = new DatagramPacket(recbuf, recbuf.length);

	//UDP
	public static DatagramSocket clientds = ClientLogin.ds;
	public static SocketAddress serveradd = ClientLogin.serveradd;
	
	public ClientListen() throws IOException
	{
		messege = CreateClientFrame.messege;
		groupList = CreateClientFrame.groupList;
		
		//在线用户列表 和dlm配套使用
		clientList = CreateClientFrame.clientList;
		dlmclientList = CreateClientFrame.dlmclientList;
		
		//在线群组列表 和dlm配套使用
		groupList = CreateClientFrame.groupList;
		dlmgroupList = CreateClientFrame.dlmgroupList;
		
		//文件列表 和dlm配套使用
		fileList = CreateClientFrame.fileList;
		dlmfileList = CreateClientFrame.dlmfileList;
	}
	//客户端下载服务器发来的文件
	public static void receiveFileUDP(String filename,long num) throws IOException
	{
		messege.append("Client receive file.\n");
		String recPath = ClientLogin.clientrootpath + "\\" + filename;
		File file = new File(recPath);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		
		for(long i = 1 ; i <= num ; i++)
		{
			clientds.receive(recdp);
			fos.write(recdp.getData(),0,recdp.getLength());
		}
		
		fos.close();
				
		messege.append("Client receive file over.\n");
	}
	
	public void run()
	{

		while (true)
		{
			try
			{
				clientds.receive(recdp);
				String msg = new String(recdp.getData(), 0, recdp.getLength());
				String[] msgs = msg.split("\\s+");
				//更新在线用户列表的消息
				if(msgs[0].equals("100"))
				{
					dlmclientList.removeAllElements();
					for(int i = 1 ; i <= msgs.length-1 ; i++)
					{
						dlmclientList.addElement(msgs[i]);
					}
				}
				//更新在线群组列表
				else if(msgs[0].equals("101"))
				{
					dlmgroupList.removeAllElements();
					for(int i = 1 ; i <= msgs.length-1 ; i++)
					{
						if(msgs[i].charAt(0) == '!')
						{
							dlmgroupList.addElement("Group: " + msgs[i].substring(1) + " \n");
						}
						else 
						{
							dlmgroupList.addElement(msgs[i]);
						}
					}
				}
				//更新文件列表
				else if(msgs[0].equals("102"))
				{
					dlmfileList.removeAllElements();
					for(int i = 1 ; i <= msgs.length-1 ; i++)
					{
						dlmfileList.addElement(msgs[i]+"\n");
					}
				}
				else if(msgs[0].equals("-sf"))
				{
					receiveFileUDP(msgs[1],Long.parseLong(msgs[2]));
				}
				else
				{
					messege.append(msg + "\n");
				}
			} 
			catch (IOException e)
			{
				//客户端是双线程的 两个线程都要关闭
				messege.append("Listen thread interrupt.\nClient disconnect!\n");
				break;
			}
		}
	}
}
