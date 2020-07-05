import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JTextArea;

//���cmdʵ�ֵ������� ��ȡ�û����������ı�����ȡ Ҳ������CreateServerFrame�������ı�������
//�����ҷ��������� ��ʼΪ�ͻ����ṩ����
class Server extends Thread
{
	
	//���ڴ�����пͻ��ı� ÿ���ͻ�������һ��ClientThread��Ķ���
	public static ArrayList<ClientThread> clientList = new ArrayList<ClientThread>();
	
	//���ڴ洢���пͻ��û����ļ��� ������֤�ظ�
	public static HashSet<String> clientNames = new HashSet<String>();
	
	//���ڴ洢����Ⱥ�� ���̰߳�ȫ��Hashtableʵ�� Ⱥ��->Ⱥ��
	public static Hashtable<Integer, HashSet<String>> groups = new Hashtable<Integer, HashSet<String>>();
	
	//Ⱥ����� �൱��Ⱥ�� Ⱥ�Ŵ�1��ʼ���� ÿ����һ��ȺȺ�ž�+1    ���������������Ⱥ����Ϊ�Ľ�
	public static int groupnum = 0;
	
	// �ļ������ʵ�����û��Ȱ��ļ��ϴ��������� Ȼ��ͻ����ٴӷ����������ļ� �е�����FTP
	// �ļ�Ҫ�ϴ��������� �ٷ������˾���Ҫ��һ��ר�Ŵ���û��ϴ��ļ���·�� ����FilePath
	// ע���޸ģ�����
	public static String serverFilePath = "D:\\server";
	// ����һ��File��Ķ��� ���ļ�����ʱ�������ڴ���Ŀ¼
	public static File root = new File(serverFilePath);
	
	//��Ϣ������
	public static JTextArea messege = null;
	//�����û��ı�������
	public static JTextArea onlineClients = null;
    //�������ļ��ı���
    public static JTextArea onlineFiles = null;
	//��������socket ��CreateServerFrame����
	public static ServerSocket  serverSocket = null;
	
	public Server()
	{
		messege = CreateServerFrame.messege;
		onlineClients = CreateServerFrame.onlineClients;
		onlineFiles = CreateServerFrame.onlineFiles;
		updateFileList();
		messege.append("�������ѳɹ�������\n");
	}

	public static void updateFileList()
	{
		onlineFiles.setText("");
		// ʹ��File��ķ����鿴�ϴ����������������ļ�
		for (String string : Server.root.list())
		{
			onlineFiles.append(string + "\n");
		}
	}
	
	//���ݲ��� ����ʵ������ CS.Server ��Ҫע���޸���λ�ȡ�û���Ϣ
	public void run() 
	{
		try
		{
			ClientThread client = new ClientThread();

			client.start();
		} 
		catch (IOException e)
		{
			messege.append("���յ���½���󵫵�½ʧ����.\n");
			e.printStackTrace();
		}
	}
}

