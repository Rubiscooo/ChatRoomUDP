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


//�ͻ��˼����߳� �������Է���������Ϣ ���ÿͻ����ڷ�����Ϣ��ͬʱ������Ϣ
public class ClientListen extends Thread
{
	//�����ı���
	public static JTextArea messege = null;
	//public static JTextArea clientList = null;
	//public static JTextArea groupList = null;
	//public static JTextArea fileList = null;
	
	//�����û��б� ��dlm����ʹ��
	public static JList<String> clientList = null;
	public static DefaultListModel<String> dlmclientList = null;
	
	//����Ⱥ���б� ��dlm����ʹ��
	public static JList<String> groupList = null;
	public static DefaultListModel<String> dlmgroupList = null;
	
	//�ļ��б� ��dlm����ʹ��
	public static JList<String> fileList = null;
	public static DefaultListModel<String> dlmfileList = null;
	
	
	public static Socket listen = null;
	public static DataOutputStream dout = null;
	public static DataInputStream din = null;
	
	
	public static File recfile = null;
	
	//UDP���ջ�����
	public static byte[] recbuf = new byte[8192];
	public static DatagramPacket recdp = new DatagramPacket(recbuf, recbuf.length);

	//UDP
	public static DatagramSocket clientds = ClientLogin.ds;
	public static SocketAddress serveradd = ClientLogin.serveradd;
	
	public ClientListen() throws IOException
	{
		messege = CreateClientFrame.messege;
		groupList = CreateClientFrame.groupList;
		
		//�����û��б� ��dlm����ʹ��
		clientList = CreateClientFrame.clientList;
		dlmclientList = CreateClientFrame.dlmclientList;
		
		//����Ⱥ���б� ��dlm����ʹ��
		groupList = CreateClientFrame.groupList;
		dlmgroupList = CreateClientFrame.dlmgroupList;
		
		//�ļ��б� ��dlm����ʹ��
		fileList = CreateClientFrame.fileList;
		dlmfileList = CreateClientFrame.dlmfileList;
	}
	//�ͻ������ط������������ļ�
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
				//���������û��б����Ϣ
				if(msgs[0].equals("100"))
				{
					dlmclientList.removeAllElements();
					for(int i = 1 ; i <= msgs.length-1 ; i++)
					{
						dlmclientList.addElement(msgs[i]);
					}
				}
				//��������Ⱥ���б�
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
				//�����ļ��б�
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
				//�ͻ�����˫�̵߳� �����̶߳�Ҫ�ر�
				messege.append("Listen thread interrupt.\nClient disconnect!\n");
				break;
			}
		}
	}
}
