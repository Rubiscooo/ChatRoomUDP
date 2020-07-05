import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

//
@SuppressWarnings("serial")
//�ͻ���������� ��Ϊ�ͻ��˵�½�������ͻ��˵�ý�� 
//��Ҫ���߳�ʵ�� 
//1.�򿪿ͻ���������� 
//2.�����ͻ���֧�ֿͻ�����Ӧ�ķ��� 
//3.�ͻ��˲����ÿͻ�����ͬʱ���պͷ�����Ϣ
//4.���ɿͻ����������ĸ������
public class CreateClientFrame extends JFrame
{
	/**
	 * Launch the application.
	 */
	//�������
	public static CreateClientFrame frame = null;
			
	//�����ı���
	public static JTextArea messege = null;
	public static JTextArea input = null;
	//public static JTextArea clientList = null;
	//public static JTextArea groupList = null;
	
	//�����û��б� ��dlm����ʹ��
	public static JList<String> clientList = null;
	public static DefaultListModel<String> dlmclientList = null;
	
	//����Ⱥ���б� ��dlm����ʹ��
	public static JList<String> groupList = null;
	public static DefaultListModel<String> dlmgroupList = null;
	
	//�ļ��б� ��dlm����ʹ��
	public static JList<String> fileList = null;
	public static DefaultListModel<String> dlmfileList = null;
	
	//ѡȡ��Ⱥ��Ա�б�
	public static JList<String> memberList = null;
	public static DefaultListModel<String> dlmmemberList = null;
	
	//���ְ�ť
	public static JButton sendmsgButton = null;
	public static JButton clearButton = null;
	public static JButton fileButton = null;
	public static JButton exitButton = null;
	public static JButton publicButton = null;
	public static JButton createGroupButton = null;
	public static JButton resetmemberButton = null;
	public static JButton fileRefreshButton = null;
	public static JButton openfolderButton = null;
	
	//����ģʽ��ǩ ���ھ�������ģʽ
	public static JLabel chatModel = null;
	
	//������Ա��Ϣ
	public static String username = null;
	
	public static void Create()
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					username = ClientLogin.username;
					//�����������ɼ�
					frame = new CreateClientFrame();
					frame.setVisible(true);
					
					
					//�ͻ����ȿ��������߳�
					ClientListen clientListen = new ClientListen();
					clientListen.start();
					
					
					//�ٿ����ͻ����߳�
					Client client = new Client(clientListen);
					client.start();
					
					
					
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CreateClientFrame()
	{
		super("�����ҿͻ��� - " + username);
		//�رմ���ʱ�������ߴ���
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try
				{
					Client.clientds.send(Client.constructdp("-exit " + ClientLogin.username));
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		setBounds(100, 100, 943, 606);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		//���ֹ�����
		JScrollPane messegeScrollPane = new JScrollPane();
		messegeScrollPane.setBounds(10, 39, 533, 322);
		getContentPane().add(messegeScrollPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(553, 40, 168, 145);
		getContentPane().add(scrollPane);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(759, 40, 158, 145);
		getContentPane().add(scrollPane_1);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 391, 533, 108);
		getContentPane().add(scrollPane_2);
		
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(553, 213, 168, 148);
		getContentPane().add(scrollPane_3);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(759, 214, 158, 145);
		getContentPane().add(scrollPane_4);
		
		messege = new JTextArea();
		messegeScrollPane.setViewportView(messege);
		messege.setEditable(false);
		
		input = new JTextArea();
		scrollPane_2.setViewportView(input);
		
		//�����û��б�
		dlmclientList = new DefaultListModel<String>();
		clientList = new JList<String>();
		clientList.setModel(dlmclientList);
		scrollPane.setViewportView(clientList);
		//clientList.setEditable(false);
		
		//����Ⱥ���б�
		dlmgroupList = new DefaultListModel<String>();
		groupList = new JList<String>();
		groupList.setModel(dlmgroupList);
		scrollPane_1.setViewportView(groupList);
		//groupList.setEditable(false);
		
		//�ļ��б�
		dlmfileList = new DefaultListModel<String>();
		fileList = new JList<String>();
		fileList.setModel(dlmfileList);
		scrollPane_3.setViewportView(fileList);
		
		//ѡȡ�����Ա�б�
		dlmmemberList = new DefaultListModel<String>();
		memberList = new JList<String>();
		memberList.setModel(dlmmemberList);
		scrollPane_4.setViewportView(memberList);
		
		//���ְ�ť
		sendmsgButton = new JButton("\u53D1\u9001\u6D88\u606F");
		sendmsgButton.setBounds(10, 509, 91, 48);
		getContentPane().add(sendmsgButton);
		
		clearButton = new JButton("\u6E05\u5C4F");
		clearButton.setBounds(451, 509, 91, 48);
		getContentPane().add(clearButton);
		
		fileButton = new JButton("\u53D1\u9001\u6587\u4EF6");
		fileButton.setBounds(188, 509, 91, 48);
		getContentPane().add(fileButton);
		
		publicButton = new JButton("\u516C\u5C4F\u804A\u5929");
		publicButton.setBounds(99, 509, 91, 48);
		getContentPane().add(publicButton);
		
		createGroupButton = new JButton("\u521B\u5EFA\u7FA4\u7EC4");
		createGroupButton.setBounds(277, 509, 91, 48);
		getContentPane().add(createGroupButton);
		
		resetmemberButton = new JButton("\u91CD\u9009\u6210\u5458");
		resetmemberButton.setBounds(362, 509, 91, 48);
		getContentPane().add(resetmemberButton);
		
		fileRefreshButton = new JButton("\u5237\u65B0");
		fileRefreshButton.setBounds(553, 364, 71, 23);
		getContentPane().add(fileRefreshButton);
		
		openfolderButton = new JButton("\u6253\u5F00");
		openfolderButton.setBounds(634, 364, 80, 23);
		getContentPane().add(openfolderButton);
		
		//���ֱ�ǩ
		chatModel = new JLabel("\u516C\u5C4F\u804A\u5929");
		chatModel.setBounds(10, 358, 533, 34);
		getContentPane().add(chatModel);
		
		JLabel noticeLabel = new JLabel("123");
		noticeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		noticeLabel.setBounds(553, 391, 363, 166);
		noticeLabel.setText("<html><body>����û��� : " + username
				 		    + "<br>˽�� : ˫��Ҫ˽�ĵ��û�"
							+ "<br>����Ⱥ�� : �������û��б���˫����Ҫ��Ⱥ���Ա�ٵ㴴��Ⱥ��" 
							+ "<br>Ⱥ��        : ˫����Ⱥ�ĵ�Ⱥ���û����ٷ���Ϣ" 
							+ "<br>�ϴ��ļ�  : �㷢���ļ���ťѡ���ļ�����"
							+ "<br>�����ļ�  : ˫���ļ��б��е��ļ�" 
							+ "<br>�����û���Ⱥ���б���ڴ���ʱ�Զ�����"
						//	+ "<br>�鿴�ϴ������������ļ� --cf" 
						//	+ "<br>��ʾ���������û� --all" 
						//	+ "<br>��ʾ��������Ⱥ�� --agp"
							+ "<br>���ص��ļ�������D:\\client\\usernameĿ¼��"
							+ "<body></html>");
		getContentPane().add(noticeLabel);
		
		JLabel clientsLabel = new JLabel("\u5F53\u524D\u5728\u7EBF\u7528\u6237");
		clientsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		clientsLabel.setBounds(553, 16, 168, 15);
		getContentPane().add(clientsLabel);
		
		JLabel groupLabel = new JLabel("\u5F53\u524D\u7FA4\u7EC4");
		groupLabel.setHorizontalAlignment(SwingConstants.CENTER);
		groupLabel.setBounds(759, 16, 158, 15);
		getContentPane().add(groupLabel);
		
		JLabel lblNewLabel = new JLabel("\u4E0A\u4F20\u5230\u670D\u52A1\u5668\u7684\u6587\u4EF6");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(553, 195, 168, 15);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("\u5DF2\u9009\u62E9\u7684\u7FA4\u6210\u5458");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(759, 195, 158, 15);
		getContentPane().add(lblNewLabel_1);
		
		
		JLabel lblNewLabel_2 = new JLabel("\u6D88\u606F\u533A");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(10, 16, 533, 15);
		getContentPane().add(lblNewLabel_2);
		
		
		
		
	}
}

