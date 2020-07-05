import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

//客户端线程 处理从客户端发出的消息
class Client extends Thread
{
	//各种文本框
	public static JTextArea messege = null;
	public static JTextArea input = null;
	
	//在线用户列表 和dlm配套使用
	public static JList<String> clientList = null;
	public static DefaultListModel<String> dlmclientList = null;
	
	//在线群组列表 和dlm配套使用
	public static JList<String> groupList = null;
	public static DefaultListModel<String> dlmgroupList = null;
	
	//文件列表 和dlm配套使用
	public static JList<String> fileList = null;
	public static DefaultListModel<String> dlmfileList = null;
	
	//选取的群成员列表 和dlm配套使用
	public static JList<String> memberList = null;
	public static DefaultListModel<String> dlmmemberList = null;
	
	//各种按钮
	public static JButton sendmsgButton = null;
	public static JButton clearButton = null;
	public static JButton fileButton = null;
	public static JButton publicButton = null;
	public static JButton createGroupButton = null;
	public static JButton resetmemberButton = null;
	public static JButton fileRefreshButton = null;
	public static JButton openfolderButton = null;
	
	//聊天模式标签
	public static JLabel chatModel = null;
	
	//各种成员变量
	public static DataOutputStream dout = null;
	public static DataInputStream din = null;
	public static Socket socket = null;
	public static String username = ClientLogin.username;
	
	//UDP
	public static DatagramSocket clientds = ClientLogin.ds;
	public static SocketAddress serveradd = ClientLogin.serveradd;
	
	public static ClientListen ls = null;
	
	//构建向服务器发送的UDP报文
	public static DatagramPacket constructdp(String msg)
	{
		return new DatagramPacket(msg.getBytes(), msg.getBytes().length, serveradd);
	}
	
	public Client(ClientListen clientListen) throws IOException
	{
		messege = CreateClientFrame.messege;
		input = CreateClientFrame.input;
		groupList = CreateClientFrame.groupList;
		
		//在线用户列表及其配套dlm
		clientList = CreateClientFrame.clientList;
		dlmclientList = CreateClientFrame.dlmclientList;
		
		//在线群组列表 和dlm配套使用
		groupList = CreateClientFrame.groupList;
		dlmgroupList = CreateClientFrame.dlmgroupList;
		
		//文件列表及其配套dlm
		fileList = CreateClientFrame.fileList;
		dlmfileList = CreateClientFrame.dlmfileList;
		
		//成员列表及其配套dlm
		memberList = CreateClientFrame.memberList;
		dlmmemberList = CreateClientFrame.dlmmemberList;
		
		sendmsgButton = CreateClientFrame.sendmsgButton;
		clearButton = CreateClientFrame.clearButton;
		fileButton = CreateClientFrame.fileButton;
		publicButton = CreateClientFrame.publicButton;
		createGroupButton = CreateClientFrame.createGroupButton;
		resetmemberButton = CreateClientFrame.resetmemberButton;
		fileRefreshButton = CreateClientFrame.fileRefreshButton;
		openfolderButton = CreateClientFrame.openfolderButton;
		
		chatModel = CreateClientFrame.chatModel;
		
		ls = clientListen;
		
		username = CreateClientFrame.username;
		
		
	}
	//为各种按钮添加事件以完成客户端的操作
	public void run()
	{
		messege.append("Hello " + username + " you're online now.\n");
		
		//给发送按钮添加事件
		sendmsgButton.addActionListener(new ActionListener()
		{	//客户端信息输入后进行登陆
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					sendMsg(input.getText());
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		
		//给创建群组按钮添加事件
		createGroupButton.addActionListener(new ActionListener()
		{	
			public void actionPerformed(ActionEvent e)
			{
				int size = dlmmemberList.size();
				if( size >= 1 )//选取了至少一个用户才能创建群组
				{
					HashSet<String> set = new HashSet<String>();
					StringBuffer sb = new StringBuffer("-cg ");
					String name = null;
					for(int i = 0 ; i <= size - 1 ; i++ )
					{
						name = dlmmemberList.get(i);
						//用set判断人名是否重复 重复了就不加此用户
						if( set.add(name) )
							sb.append(dlmmemberList.get(i) + "&&");
					}
					String cgmsg = sb.toString();
					try
					{
						//构建一个创建群组命令
						clientds.send(constructdp(cgmsg));
					} 
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
					dlmmemberList.removeAllElements();
				}
				else 
				{
					messege.append("没有选择任何群成员.\n");
				}
			}
		});
		
		//给清屏按钮添加事件
		clearButton.addActionListener(new ActionListener()
		{	//客户端信息输入后进行登陆
			public void actionPerformed(ActionEvent e)
			{
				clear();
			}
		});
		
		//给打开按钮添加事件
		openfolderButton.addActionListener(new ActionListener()
		{	
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					java.awt.Desktop.getDesktop().open(new File("D:\\client\\" + username));
				} 
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
		});
		
		//给刷新按钮添加事件
		fileRefreshButton.addActionListener(new ActionListener()
		{	//客户端信息输入后进行登陆
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					clientds.send(constructdp("-flush"));
				} 
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		
		//给公屏聊天按钮添加事件
		publicButton.addActionListener(new ActionListener()
		{	//客户端信息输入后进行登陆
			public void actionPerformed(ActionEvent e)
			{
				chatModel.setText("公屏聊天");
			}
		});
		
		//给重选成员按钮添加事件 清空群成员列表
		resetmemberButton.addActionListener(new ActionListener()
		{	//客户端信息输入后进行登陆
			public void actionPerformed(ActionEvent e)
			{
				dlmmemberList.removeAllElements();
			}
		});
		
		//给发送文件按钮添加事件
		fileButton.addActionListener(new ActionListener()
		{	
			public void actionPerformed(ActionEvent e)
			{
				// 显示打开的文件对话框
				JFileChooser jfc = new JFileChooser(new File("D:\\client"));
				JFrame frmIpa = new JFrame();
				jfc.showSaveDialog(frmIpa);
				try
				{
					// 使用文件类获取选择器选择的文件
					File file = jfc.getSelectedFile();
					sendFileUDP(file);
				}
				catch (Exception e2)
				{
					JPanel panel3 = new JPanel();
					JOptionPane.showMessageDialog(panel3, "没有选中任何文件", "提示", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		//给文件列表添加双击事件 双击文件名就可以下载文件
		MouseListener mouseListenerFile = new MouseAdapter() 
		{
		     public void mouseClicked(MouseEvent e) 
		     {
		         if (e.getClickCount() == 2)
		         {
		             int index = fileList.locationToIndex(e.getPoint());
		             try
					 {
						clientds.send(constructdp("-df " + dlmfileList.get(index)));
					 } 
		             catch (IOException e1)
					 {
						e1.printStackTrace();
					 }
		         }
		     }
		 };
		 fileList.addMouseListener(mouseListenerFile);
		 
		 //给在线用户列表添加双击事件 双击列表中的用户名就能切换到私聊模式
		MouseListener mouseListenerClient = new MouseAdapter() 
			{
			     public void mouseClicked(MouseEvent e) 
			     {
			         if (e.getClickCount() == 2)
			         {
			        	 //通过修改聊天模式标签更改聊天模式
			        	 int index = clientList.locationToIndex(e.getPoint());
			        	 String name = dlmclientList.get(index);
			             chatModel.setText("与[" + name + "]私聊");
			             dlmmemberList.addElement(name);
			         }
			     }
			 };
		clientList.addMouseListener(mouseListenerClient);
		
		//给在线群组列表添加双击事件 双击群组中的用户名就进行群聊
		MouseListener mouseListenerGroup = new MouseAdapter() 
		{
		     public void mouseClicked(MouseEvent e) 
		     {
		         if (e.getClickCount() == 2)
		         {
		        	 //通过修改聊天模式标签更改聊天模式
		        	 int index = groupList.locationToIndex(e.getPoint());
		        	 String string = null;
		        	 String[] strings = null;
		        	 for(int i = index ; i >= 0 ; i-- )
		        	 {
		        		 string = dlmgroupList.get(i);
		        		 strings = string.split("\\s+");
		        		 if( strings[0].equals("Group:") )
		        		 {
		        			 chatModel.setText("与群组[" + Integer.parseInt(strings[1]) + "]群聊");
		        			 break;
		        		 }
		        	 }
		         }
		     }
		 };
		 groupList.addMouseListener(mouseListenerGroup);
		 
	}
	//发送消息的实现
	public void sendMsg(String msg) throws IOException
	{
		String[] msgs = msg.split("\\s+");
		
		String model = chatModel.getText();
		
		//私聊模式的实现
		if( model.endsWith("私聊") && !"".equals(msg)    )
		{
				messege.append("你自己说 : " + msg + "\n");
				int beg = model.indexOf('[');//截取私聊的用户名
				int end = model.indexOf(']');
				String name = model.substring(beg+1, end);
				clientds.send(constructdp("-p "+ ClientLogin.username + " " + name+" "+msg));
		}
		//群聊模式的实现
		else if( model.endsWith("群聊") && !"".equals(msg)   )
		{
				int beg = model.indexOf('[');//截取群号
				int end = model.indexOf(']');
				int groupnum = Integer.parseInt( model.substring(beg+1, end) );
				clientds.send(constructdp("-g " + groupnum + " " + msg));
		}
		else if( model.endsWith("公屏聊天") && !"".equals(msg) )
		{
			clientds.send(constructdp("["+username+"]说"+msg));
		}
		input.setText("");
	}
	//清屏的实现
	public void clear()
	{
		messege.setText("");
	}
	
	//向服务器上传文件
	public static void sendFileUDP(File file) throws IOException, InterruptedException
	{
		messege.append("Cliet send file to server.\n");
		String filepath = file.getPath();
		FileInputStream fis = new FileInputStream(filepath);
		int buflen = 8192;
		long filelen = file.length();
		byte[] filebuf = new byte[buflen];
		long num = (filelen%8192==0)?filelen/8192:1+filelen/8192;
		clientds.send(constructdp("-sf " + file.getName() + " " + String.valueOf(num)));
		
		//文件里不能有空格（debug）
		for(long i = 1 ; i <= num ; i++)
		{
			int readlen = fis.read(filebuf);
			clientds.send(new DatagramPacket(filebuf, readlen, serveradd));
			//控制传输速率,每100毫秒发送一个数据包,防止中途丢包
			TimeUnit.MICROSECONDS.sleep(100);
		}
		
		fis.close();
		messege.append("Client send file over.\n");
	}
	
}
