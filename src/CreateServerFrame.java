import java.awt.EventQueue;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

//�������˵Ľ��� ������ʾ���������� ��Ϊ��½�����������������ý�� Ϊ����������������׼�� ������
@SuppressWarnings("serial")
class CreateServerFrame extends JFrame
{
	//��ַ
	public static InetAddress add = null;
	//������������
	public static CreateServerFrame frame = null;
	//���
	public static JPanel contentPane = null;
	//�˿�״̬
	public static JLabel portStatus = null;
	//��Ϣ��
	public static JTextArea messege = null;
	//�����û��ı���
	public static JTextArea onlineClients = null;
	//����Ⱥ���ı���
    public static JTextArea onlineGroups = null;
    //�������ļ��ı���
    public static JTextArea onlineFiles = null;
	//��������socket �ɷ�������½���洫��
	public static ServerSocket  serverSocket = null;
	
	//������UDP socket
	public static DatagramSocket serverds = null;

	
	//���ڻ�ȡ����ʾ������������IP

	/**
	 * Launch the application.
	 */
	public static void Create(DatagramSocket ds)
	{
		EventQueue.invokeLater(new Runnable()
		{
			
			public void run()
			{
				try
				{
					serverds = ds;
					//��ʾ����������
					frame = new CreateServerFrame();
					
					//�����ִ��
					frame.setVisible(true);
										
					//����Server����  �ͱ�����Server ����serverSocket �ı��� �����û���
					Server server = new Server();
					//�������������� �÷�������ʼ�����ͻ��˵����� 
					//�ö��߳�ʵ��  �÷����������кʹ��������������߳̽��� �����swing���߳� ���治��ʾ������
					server.start();
					
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws UnknownHostException 
	 */
	//��������ʾ����������
	public CreateServerFrame() throws UnknownHostException
	{
		//���÷���������Ŀ��
		setTitle("�����ҷ�����");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 764, 538);
		
		//���
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//������
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 81, 492, 238);
		contentPane.add(scrollPane);
		
		//������
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(512, 81, 229, 238);
		contentPane.add(scrollPane_1);
		
		//������
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(512, 354, 226, 135);
		contentPane.add(scrollPane_2);
		
		//�����û��ı�������ı�ǩ
		JLabel lblNewLabel = new JLabel("\u5F53\u524D\u5728\u7EBF\u7528\u6237");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(512, 42, 229, 32);
		contentPane.add(lblNewLabel);
		
		//��ʾ������������IP��Ϣ�ı�ǩ
		add = InetAddress.getLocalHost();
		JLabel info = new JLabel("<html><body>"
								+ "���������� : " 
								+ add.getHostName() 
								+ "<br>"+"����IP : " 
								+ add.getHostAddress()
								+ "<br>"+"�������˿� : "+ serverds.getLocalPort()
								+ "<body></html>");
		info.setHorizontalAlignment(SwingConstants.CENTER);
		info.setBounds(10, 10, 194, 72);
		contentPane.add(info);
		
		//��ʾ������״̬�ı�ǩ
		portStatus = new JLabel("portstatus");
		portStatus.setHorizontalAlignment(SwingConstants.CENTER);
		portStatus.setBounds(235, 26, 168, 45);
		portStatus.setText("������������");
		contentPane.add(portStatus);
		
		// *** �����û�������ı���
		onlineClients = new JTextArea();
		onlineClients.setEditable(false);
		scrollPane_1.setViewportView(onlineClients); //��仰�Ѿ����ı���ӽ�ȥ�˲�����add
		
		// *** ��ǰ����Ⱥ���ı���
		onlineGroups = new JTextArea();
		scrollPane_2.setViewportView(onlineGroups);
		onlineGroups.setEditable(false);
		
		// *** ��������Ϣ��
		messege = new JTextArea();
		messege.setEditable(false);
		scrollPane.setViewportView(messege);
		

		
		JLabel lblNewLabel_1 = new JLabel("\u5F53\u524D\u5728\u7EBF\u7FA4\u7EC4");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(515, 319, 226, 25);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("\u4E0A\u4F20\u5230\u670D\u52A1\u5668\u7684\u6587\u4EF6");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(10, 329, 492, 15);
		contentPane.add(lblNewLabel_2);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(10, 354, 492, 135);
		contentPane.add(scrollPane_3);
		
		onlineFiles = new JTextArea();
		onlineFiles.setEditable(false);
		scrollPane_3.setViewportView(onlineFiles);
		


	}
}
