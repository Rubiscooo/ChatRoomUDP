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

//�ͻ����߳� ����ӿͻ��˷�������Ϣ
class Client extends Thread
{
	//�����ı���
	public static JTextArea messege = null;
	public static JTextArea input = null;
	
	//�����û��б� ��dlm����ʹ��
	public static JList<String> clientList = null;
	public static DefaultListModel<String> dlmclientList = null;
	
	//����Ⱥ���б� ��dlm����ʹ��
	public static JList<String> groupList = null;
	public static DefaultListModel<String> dlmgroupList = null;
	
	//�ļ��б� ��dlm����ʹ��
	public static JList<String> fileList = null;
	public static DefaultListModel<String> dlmfileList = null;
	
	//ѡȡ��Ⱥ��Ա�б� ��dlm����ʹ��
	public static JList<String> memberList = null;
	public static DefaultListModel<String> dlmmemberList = null;
	
	//���ְ�ť
	public static JButton sendmsgButton = null;
	public static JButton clearButton = null;
	public static JButton fileButton = null;
	public static JButton publicButton = null;
	public static JButton createGroupButton = null;
	public static JButton resetmemberButton = null;
	public static JButton fileRefreshButton = null;
	public static JButton openfolderButton = null;
	
	//����ģʽ��ǩ
	public static JLabel chatModel = null;
	
	//���ֳ�Ա����
	public static DataOutputStream dout = null;
	public static DataInputStream din = null;
	public static Socket socket = null;
	public static String username = ClientLogin.username;
	
	//UDP
	public static DatagramSocket clientds = ClientLogin.ds;
	public static SocketAddress serveradd = ClientLogin.serveradd;
	
	public static ClientListen ls = null;
	
	//��������������͵�UDP����
	public static DatagramPacket constructdp(String msg)
	{
		return new DatagramPacket(msg.getBytes(), msg.getBytes().length, serveradd);
	}
	
	public Client(ClientListen clientListen) throws IOException
	{
		messege = CreateClientFrame.messege;
		input = CreateClientFrame.input;
		groupList = CreateClientFrame.groupList;
		
		//�����û��б�������dlm
		clientList = CreateClientFrame.clientList;
		dlmclientList = CreateClientFrame.dlmclientList;
		
		//����Ⱥ���б� ��dlm����ʹ��
		groupList = CreateClientFrame.groupList;
		dlmgroupList = CreateClientFrame.dlmgroupList;
		
		//�ļ��б�������dlm
		fileList = CreateClientFrame.fileList;
		dlmfileList = CreateClientFrame.dlmfileList;
		
		//��Ա�б�������dlm
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
	//Ϊ���ְ�ť����¼�����ɿͻ��˵Ĳ���
	public void run()
	{
		messege.append("Hello " + username + " you're online now.\n");
		
		//�����Ͱ�ť����¼�
		sendmsgButton.addActionListener(new ActionListener()
		{	//�ͻ�����Ϣ�������е�½
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
		
		//������Ⱥ�鰴ť����¼�
		createGroupButton.addActionListener(new ActionListener()
		{	
			public void actionPerformed(ActionEvent e)
			{
				int size = dlmmemberList.size();
				if( size >= 1 )//ѡȡ������һ���û����ܴ���Ⱥ��
				{
					HashSet<String> set = new HashSet<String>();
					StringBuffer sb = new StringBuffer("-cg ");
					String name = null;
					for(int i = 0 ; i <= size - 1 ; i++ )
					{
						name = dlmmemberList.get(i);
						//��set�ж������Ƿ��ظ� �ظ��˾Ͳ��Ӵ��û�
						if( set.add(name) )
							sb.append(dlmmemberList.get(i) + "&&");
					}
					String cgmsg = sb.toString();
					try
					{
						//����һ������Ⱥ������
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
					messege.append("û��ѡ���κ�Ⱥ��Ա.\n");
				}
			}
		});
		
		//��������ť����¼�
		clearButton.addActionListener(new ActionListener()
		{	//�ͻ�����Ϣ�������е�½
			public void actionPerformed(ActionEvent e)
			{
				clear();
			}
		});
		
		//���򿪰�ť����¼�
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
		
		//��ˢ�°�ť����¼�
		fileRefreshButton.addActionListener(new ActionListener()
		{	//�ͻ�����Ϣ�������е�½
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
		
		//���������찴ť����¼�
		publicButton.addActionListener(new ActionListener()
		{	//�ͻ�����Ϣ�������е�½
			public void actionPerformed(ActionEvent e)
			{
				chatModel.setText("��������");
			}
		});
		
		//����ѡ��Ա��ť����¼� ���Ⱥ��Ա�б�
		resetmemberButton.addActionListener(new ActionListener()
		{	//�ͻ�����Ϣ�������е�½
			public void actionPerformed(ActionEvent e)
			{
				dlmmemberList.removeAllElements();
			}
		});
		
		//�������ļ���ť����¼�
		fileButton.addActionListener(new ActionListener()
		{	
			public void actionPerformed(ActionEvent e)
			{
				// ��ʾ�򿪵��ļ��Ի���
				JFileChooser jfc = new JFileChooser(new File("D:\\client"));
				JFrame frmIpa = new JFrame();
				jfc.showSaveDialog(frmIpa);
				try
				{
					// ʹ���ļ����ȡѡ����ѡ����ļ�
					File file = jfc.getSelectedFile();
					sendFileUDP(file);
				}
				catch (Exception e2)
				{
					JPanel panel3 = new JPanel();
					JOptionPane.showMessageDialog(panel3, "û��ѡ���κ��ļ�", "��ʾ", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		//���ļ��б����˫���¼� ˫���ļ����Ϳ��������ļ�
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
		 
		 //�������û��б����˫���¼� ˫���б��е��û��������л���˽��ģʽ
		MouseListener mouseListenerClient = new MouseAdapter() 
			{
			     public void mouseClicked(MouseEvent e) 
			     {
			         if (e.getClickCount() == 2)
			         {
			        	 //ͨ���޸�����ģʽ��ǩ��������ģʽ
			        	 int index = clientList.locationToIndex(e.getPoint());
			        	 String name = dlmclientList.get(index);
			             chatModel.setText("��[" + name + "]˽��");
			             dlmmemberList.addElement(name);
			         }
			     }
			 };
		clientList.addMouseListener(mouseListenerClient);
		
		//������Ⱥ���б����˫���¼� ˫��Ⱥ���е��û����ͽ���Ⱥ��
		MouseListener mouseListenerGroup = new MouseAdapter() 
		{
		     public void mouseClicked(MouseEvent e) 
		     {
		         if (e.getClickCount() == 2)
		         {
		        	 //ͨ���޸�����ģʽ��ǩ��������ģʽ
		        	 int index = groupList.locationToIndex(e.getPoint());
		        	 String string = null;
		        	 String[] strings = null;
		        	 for(int i = index ; i >= 0 ; i-- )
		        	 {
		        		 string = dlmgroupList.get(i);
		        		 strings = string.split("\\s+");
		        		 if( strings[0].equals("Group:") )
		        		 {
		        			 chatModel.setText("��Ⱥ��[" + Integer.parseInt(strings[1]) + "]Ⱥ��");
		        			 break;
		        		 }
		        	 }
		         }
		     }
		 };
		 groupList.addMouseListener(mouseListenerGroup);
		 
	}
	//������Ϣ��ʵ��
	public void sendMsg(String msg) throws IOException
	{
		String[] msgs = msg.split("\\s+");
		
		String model = chatModel.getText();
		
		//˽��ģʽ��ʵ��
		if( model.endsWith("˽��") && !"".equals(msg)    )
		{
				messege.append("���Լ�˵ : " + msg + "\n");
				int beg = model.indexOf('[');//��ȡ˽�ĵ��û���
				int end = model.indexOf(']');
				String name = model.substring(beg+1, end);
				clientds.send(constructdp("-p "+ ClientLogin.username + " " + name+" "+msg));
		}
		//Ⱥ��ģʽ��ʵ��
		else if( model.endsWith("Ⱥ��") && !"".equals(msg)   )
		{
				int beg = model.indexOf('[');//��ȡȺ��
				int end = model.indexOf(']');
				int groupnum = Integer.parseInt( model.substring(beg+1, end) );
				clientds.send(constructdp("-g " + groupnum + " " + msg));
		}
		else if( model.endsWith("��������") && !"".equals(msg) )
		{
			clientds.send(constructdp("["+username+"]˵"+msg));
		}
		input.setText("");
	}
	//������ʵ��
	public void clear()
	{
		messege.setText("");
	}
	
	//��������ϴ��ļ�
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
		
		//�ļ��ﲻ���пո�debug��
		for(long i = 1 ; i <= num ; i++)
		{
			int readlen = fis.read(filebuf);
			clientds.send(new DatagramPacket(filebuf, readlen, serveradd));
			//���ƴ�������,ÿ100���뷢��һ�����ݰ�,��ֹ��;����
			TimeUnit.MICROSECONDS.sleep(100);
		}
		
		fis.close();
		messege.append("Client send file over.\n");
	}
	
}
