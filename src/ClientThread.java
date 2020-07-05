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

//�����ҷ����������߳�    ����ʵ��Ϊ�ͻ����ṩ�ĸ��ַ���
public class ClientThread extends Thread
{
	// ����Ҫ�ǵ� �������ʱ����һ�� �����ٴ���һ��
	public Socket socket = null;
	public DataInputStream din = null;
	public DataOutputStream dout = null;
	public String ClientName = null;
	
	//UDP
	//���ջ�����
	public static byte[] recbuf = new byte[8192];
	public static DatagramPacket recdp = new DatagramPacket(recbuf, recbuf.length);
	//UDP socket
	public static DatagramSocket serverds = null;

	// �������û���һ��ӳ���ϵ �û���->�û�
	public static Hashtable<String, ClientThread> nametouser = new Hashtable<String, ClientThread>();

	// �������û���һ��ӳ���ϵUDP �û���->�û���Socket
	public static Hashtable<String, SocketAddress> nametosocket = new Hashtable<String,SocketAddress>();
	
	// ��Ϣ������
	public static JTextArea messege = null;
	// �����û��ı�������
	public static JTextArea onlineClients = null;
	// ����Ⱥ���ı�������
	public static JTextArea onlineGroups = null;
	   //�������ļ��ı���
    public static JTextArea onlineFiles = null;
    
	public ClientThread() throws IOException
	{
		serverds=ServerLogin.serverds;
		messege = CreateServerFrame.messege;
		onlineClients = CreateServerFrame.onlineClients;
		onlineGroups = CreateServerFrame.onlineGroups;
		onlineFiles = CreateServerFrame.onlineFiles;
	}
	
	//��������Դ��dp������Ӧ��
    public static DatagramPacket constructDPByDp(String msg,DatagramPacket dp)
    {
    	return new DatagramPacket(msg.getBytes(), msg.getBytes().length, dp.getSocketAddress());
    }
    
    //�����û����������䷢�͵����ݰ�
    public static DatagramPacket constructDPByName(String msg,String name)
    {
    	return new DatagramPacket(msg.getBytes(), msg.getBytes().length, nametosocket.get(name));
    }

	public static void login(String username,DatagramPacket dp) throws IOException
	{
		// �ͻ��ĵ�¼��֤ Ҫ�����ֲ������ظ�
		if(!"".equals(username) && Server.clientNames.add(username))
		{
			serverds.send(constructDPByDp("ok", dp));
		}
		
		nametosocket.put(username, dp.getSocketAddress());
		
		messege.append(username + " is online.\n");
		
		//�������˸��������û� 
		updateOnlineClientsUDP();
		
		//Ϊ�ͻ��˷��͵�½�ɹ�����ʾ
		serverds.send(constructDPByDp("hello " + username + " you're login.", dp));
		
		//�ÿͻ��˸��������û���Ⱥ����ļ��б�
		informUpdateOnlineClientsUDP();
		informUpdateGroupsUDP();
		informUpdateFileListUDP();
		
		sendMsgAllUDP(username + " online!");

	}
	
	// �������� �����ӵ������������пͻ��˷���Ϣmsg UDP
	public static void sendMsgAllUDP(String msg) throws IOException
	{
		// ���������û��б� �ҵ������û���SocketAddress����dp������
		for (String name : Server.clientNames)
		{
			serverds.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, nametosocket.get(name)));
		}
	}
	
	//˽�� ��һ���ض��û�����Ϣ
	public static void sendMsgPriUDP(String name,String msg) throws IOException
	{
		serverds.send(constructDPByName(msg, name));
	}
	
	//֪ͨ���пͻ��˸����û��б� UDP
	public static void informUpdateOnlineClientsUDP() throws IOException
	{
		String msg = new String("100");
		for(String name : Server.clientNames)
		{
			msg = msg + " " + name;
		}
		sendMsgAllUDP(msg);
	}
	
	//���������û��б� ���û����ߺ�����ʱ�����
	public static void updateOnlineClientsUDP()
	{
		onlineClients.setText("");
		for( String username : Server.clientNames )
		{
			onlineClients.append(username + "\n");
			onlineClients.append(nametosocket.get(username).toString()+"\n");
		}
	}
	
	//����������Ⱥ���б�
	public static void updateOnlineGroupsUDP()
	{
		onlineGroups.setText("");
		
		HashSet<String> members = null;
		
		Iterator<Integer> iterator = Server.groups.keySet().iterator();
		
		while( iterator.hasNext() )
		{
			int groupnum = iterator.next();
			//��ȡ��ǰȺ��
			members = Server.groups.get(groupnum);
			//��������0�������ʾ
			if( members.size() > 0 )
			{
				onlineGroups.append("Group : " + groupnum + "\n");
				//�ٷ�ÿ����ĳ�Ա
				for( String name : members )
				{
					onlineGroups.append(name + "\n");
				}
			}
			else
			{
				//һ�߱���һ���޸������ڵ�ֵ�˻ᱨ�쳣 �õ������Ͳ�����쳣��
				iterator.remove();
			}
		}
	}
		
	public static void updateFileListUDP()
	{
		onlineFiles.setText("");
		// ʹ��File��ķ����鿴�ϴ����������������ļ�
		for (String string : Server.root.list())
		{
			onlineFiles.append(string + "\n");
		}
	}
	//֪ͨ�ͻ��˸�������Ⱥ���б�
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

	//�ڷ���˴���Ⱥ��
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

	//��Ⱥ
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
	//Ⱥ�ĵ�ʵ��
	public static void sendMsgGroupUDP(int groupnum,String msg) throws IOException
	{
		// �ж������治����
		HashSet<String> members = Server.groups.get(groupnum);
		if (members == null)
			return;
		
		for(String name : members)
		{
			serverds.send(constructDPByName("[Ⱥ��" + String.valueOf(groupnum) + "��Ϣ]:" + msg, name));
		}
	}

	//���������տͻ����ϴ����ļ�
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

	//��������ͻ��˷��ļ�
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
			//���ƴ�������,ÿ100���뷢��һ�����ݰ�,��ֹ��;����
			TimeUnit.MICROSECONDS.sleep(100);
		}
		
		fis.close();
		messege.append("Server send file over.\n");
		
	}

	// ÿ���ͻ��൱����һ���߳� ��������ͨ��run()����Ϊ�ͻ��ṩ����
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
				//��½:login name
				if(msgs[0].equals("login") && msgs.length == 2)
				{
					login(msgs[1],recdp);
				}
				//˽��:-p self name msg
				else if(msgs[0].equals("-p"))
				{
					sendMsgPriUDP(msgs[2], "["+msgs[1]+"]����˵��"+msgs[3]);
				}
				//�ͻ���Ҫ�����ļ���-df filename
				else if(msgs[0].equals("-df"))
				{
					sendFileUDP(msgs[1]);
				}
				//�ͻ���Ҫ����Ⱥ�飺-cg name1&&name2
				else if(msgs[0].equals("-cg"))
				{
					createGroupUDP(msgs[1]);
					updateOnlineGroupsUDP();
					informUpdateGroupsUDP();
				}
				//Ⱥ�ģ�-g groupnum msg
				else if(msgs[0].equals("-g"))
				{
					sendMsgGroupUDP(Integer.parseInt(msgs[1]),msgs[2]);
				}
				//�û����ߵĴ���
				else if(msgs[0].equals("-exit"))
				{
					clientOffLineUDP(msgs[1]);
				}
				//���������տͻ��˷������ļ���-sf filename num
				else if(msgs[0].equals("-sf"))
				{
					receiveFileUDP(msgs[1],Long.parseLong(msgs[2]));
				}
				//���������յ��ͻ��˷����������ļ�����-df filepath
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