
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.*;
//如果局域网连接不上就关了防火墙
//客户端登陆聊天室的界面 生成客户端登陆界面 先生成客户端的socket连接服务器 再输入用户名转入聊天界面 并向界面进行传递参数
public class ClientLogin
{
	//客户端登陆界面的框架
	public static JFrame frame = null;
	//客户端登陆界面的面板
	private static JPanel panel;
	//客户端登陆界面的用户名文本框
	public static JTextField userNameField = null;
	//客户端登陆界面的ip地址文本框
	public static JTextField ipField = null;
	//客户端登陆界面的端口号文本框
	public static JTextField portField = null;
	//客户端登陆状态提示标签
	public static JLabel loginStatus = null;
	//连接服务器按钮
	public static JButton connertServerButton = null;
	//登陆按钮
	public static JButton loginButton = null;
	
	public static String username = null;
	public static byte[] ip = new byte[4];
	public static int port = 0;
	
	//UDP 
	//接收缓冲区
	public static byte[] recbuf = new byte[8192];
	public static DatagramPacket recdp = new DatagramPacket(recbuf, recbuf.length);
	//socket
	public static DatagramSocket ds = null;
	public static InetSocketAddress serveradd = null;

	
	//filerootpath 是所有客户端接收文件的根目录
	public static String filerootpath = "D:\\client";
	//clientrootpath 是当前客户端接收的所有文件的根目录
	public static String clientrootpath = null;
	
	public static void main(String[] args)
	{
		// 创建 JFrame 实例
		frame = new JFrame("聊天室客户端登陆");
		// Setting the width and height of frame
		frame.setBounds(300, 400, 454, 238);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JPanel();
		// 添加面板
		frame.getContentPane().add(panel);
		
		//调用用户定义的方法并添加组件到面板
		placeComponents(panel);

		// *** 输入用户名的文本框
		userNameField = new JTextField(20);
		userNameField.setBounds(173, 95, 165, 25);
		
		panel.add(userNameField);
		// *** 输入IP地址的文本框
		ipField = new JTextField();
		ipField.setBounds(173, 25, 165, 25);
		panel.add(ipField);
		ipField.setColumns(10);
		// *** 输入端口号的文本框
		portField = new JTextField();
		portField.setBounds(173, 60, 165, 25);
		panel.add(portField);
		portField.setColumns(10);
		
		//客户端登陆状态提示标签
		loginStatus = new JLabel("");
		loginStatus.setBounds(83, 133, 250, 25);
		loginStatus.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(loginStatus);

		// 设置界面可见
		frame.setVisible(true);
	}
	//建立各种组件
	private static void placeComponents(JPanel panel)
	{
		panel.setLayout(null);

		// 用户名标签
		JLabel userLabel = new JLabel("用户名 : ");
		userLabel.setHorizontalAlignment(SwingConstants.CENTER);
		userLabel.setBounds(69, 95, 80, 25);
		panel.add(userLabel);

		// IP地址标签
		JLabel IP = new JLabel("IP地址:");
		IP.setBounds(83, 25, 80, 25);
		panel.add(IP);

		//端口号标签
		JLabel label = new JLabel("\u7AEF\u53E3\u53F7:");
		label.setBounds(83, 60, 58, 25);
		panel.add(label);
		
		loginButton = new JButton("\u767B\u9646");
		loginButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				login();
			}
		});
		loginButton.setBounds(82, 128, 262, 30);
		panel.add(loginButton);
		
	}

	//登陆
	public static void login() 
	{
		//先判断用户输入的端口是否合法
		try
		{
			port = Integer.parseInt(portField.getText());
			if( port <= 0 || port >= 65536 )
			{
				loginStatus.setText("port = " + port + " 端口不合法重新输入.");
				return;
			}
			
			String ipString = ipField.getText();
			//正则表达式 注意.的转义
			String[] items = ipString.split("\\.");
			
			if( items.length != 4 )
			{
				loginStatus.setText("ip地址不合法重新输入.");
				return;
			}
			//byte的范围是-128~127
			else 
			{
				for(int i = 0 ; i <= 3 ; i++ )
				{

					int ipnum = Integer.parseInt(items[i]);
					if( ipnum >= 0 && ipnum <= 127 )
					{
						ip[i] = (byte) ipnum;
					}
					else if( ipnum >=128 && ipnum <= 255 )
					{
						ip[i] = (byte)(ipnum - 256);
					}
					else 
					{
						loginStatus.setText("ip地址不合法重新输入.");
						return;
					}
				}
			}
			//为了测试方便ip都是本机ip
			//由于ip和端口的不正确输入可能导致无法登陆 要异常处理
			try
			{
				username = userNameField.getText();
				ds = new DatagramSocket();
				byte[] loginmsg = ("login "+username).getBytes();
				serveradd = new InetSocketAddress(InetAddress.getByAddress(ip), port);
				ds.send(new DatagramPacket(loginmsg, loginmsg.length, serveradd));
				ds.receive(recdp);
				String retmsg = new String(recdp.getData(), 0, recdp.getLength());
				if(retmsg.equals("ok"))
				{
					frame.setVisible(false);
					//建立客户端接收文件的目录
					new File(ClientLogin.filerootpath).mkdir();
					//为当前客户端建立接收文件的目录
					clientrootpath = filerootpath + "\\" + username;
					new File(clientrootpath).mkdir();
					//System.out.println("ok");
					CreateClientFrame.Create();
				}
				else
				{
					ds.close();
					loginStatus.setText("连接服务器失败，请检查ip和端口和用户名是否正确.");
				}
			} 
			catch (IOException ioe)
			{
				loginStatus.setText("连接服务器失败，请检查ip和端口是否正确.");
			}
			
		} 
		catch (NumberFormatException nfe)
		{
			loginStatus.setText("端口或ip不合法，重新输入！");
		}
			
	}
	
}
