
import java.awt.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
//��������½���� �ñ�����Ϊ�����ҷ����� ��� ServerSocket����Ľ���
//Ҫ������˿ڽ��� ServerSocket���� �ɹ���ת�����������
public class ServerLogin extends JFrame
{
	//��������½�����������
	public static ServerLogin frame = null;
	//��������½��������
	private static JPanel contentPane = null;
	//����˿ڵ��ı��� ע���ڵ�½���治��Ҫ�ж�����Ķ˿��Ƿ�Ϸ�ҲҪ�ж϶˿��Ƿ�ռ��
	private static JTextField port = null;
	//��ʾ������״̬�ı�ǩ
	public static JLabel status = null;
	//���ڻ�ȡ����ʾ������������IP
	public static InetAddress add = null;
	//�ļ������ʵ�����û��Ȱ��ļ��ϴ��������� Ȼ��ͻ����ٴӷ����������ļ� �е�����FTP
	//�ļ�Ҫ�ϴ��������� �ٷ������˾���Ҫ��һ��ר�Ŵ���û��ϴ��ļ���·�� ����FilePath
	//ע���޸ģ�����
	public static String serverFilePath = "D:\\server";
	//����һ��File��Ķ��� ���ļ�����ʱ�������ڴ���Ŀ¼ 
	
	//������UDP socket
	public static DatagramSocket serverds = null;
	
	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			//������ڵ� ʵ�ַ���������Ĵ�������ʾ ����˿��ñ���ͨ���ö˿��ṩ�����ҷ���
			public void run()
			{
				try
				{
					frame = new ServerLogin();

					frame.setVisible(true);
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
	//������������½�ĸ������
	public ServerLogin() throws UnknownHostException
	{
		//��������½����������
		setTitle("�����ҷ�������½");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 454, 326);
		
		//���
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//�˿ںű�ǩ
		JLabel lblNewLabel = new JLabel("�˿ں�:");
		lblNewLabel.setFont(new Font("����", Font.PLAIN, 16));
		lblNewLabel.setBounds(50, 107, 75, 33);
		contentPane.add(lblNewLabel);
		
		// *** ����˿ڵ��ı���
		port = new JTextField();
		port.setBounds(135, 108, 240, 33);
		contentPane.add(port);
		port.setColumns(10);
		
		// *** ��½��ť  ���°�ť���¼��ǿ������������� ��ʼ֮ǰ���ж϶˿��Ƿ�ռ��
		JButton btnNewButton = new JButton("���������ҷ�����");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				turnToServerFrame(port.getText());
				port.setText("");
			}
		});
		btnNewButton.setFont(new Font("����", Font.PLAIN, 21));
		btnNewButton.setBounds(103, 192, 245, 48);
		contentPane.add(btnNewButton);
		
		add = InetAddress.getLocalHost();
		// *** ��Ϣ��ǩ ��ʾ������������IP
		JLabel info = new JLabel( "<html><body>���������� : " 
									+ add.getHostName() 
									+ "<br>" + "����IP : " 
									+ add.getHostAddress()
									+ "<body></html>");
		info.setFont(new Font("΢���ź�", Font.PLAIN, 18));
		info.setHorizontalAlignment(SwingConstants.CENTER);
		info.setBounds(42, 10, 353, 87);
		contentPane.add(info);
		
	}
	
	//�رյ�½���� ��������������
	public static void turnToServerFrame(String port)
	{
		//���÷�����״̬��ǩ 
		setStatusLabel();
		//�ж϶˿��Ƿ�Ϸ� 
		try
		{	//�������Ķ˿��Ƿ�Ϸ�
			if( Integer.parseInt(port) >= 1 && Integer.parseInt(port) <= 65535)
			{
				try
				{
					//������������DatagramSocket���� �������ɹ�˵���������ѽ���
					serverds = new DatagramSocket(Integer.parseInt(port));
					
					//���������������ļ���Ŀ¼
					new File(serverFilePath).mkdir();
					
					frame.setVisible(false);
					
					//�ͻ���δ����ǰ�������� 
					CreateServerFrame.Create(serverds);
					
				}
				catch (IOException ioe)
				{
					status.setText("�˿ڱ�ռ�ã��������룡");
				}

			}
		}
		catch( NumberFormatException nfe )
		{
			status.setText("�˿ڲ��Ϸ����������� ��");
		}
	}
	//��������½�����״̬��ǩ ��ʾ�˿��Ƿ�Ϸ����Ƿ�ռ��
	public static void setStatusLabel()
	{
		if( status == null)
		{
			status = new JLabel();
			status.setBounds(50, 150, 332, 32);
			status.setHorizontalAlignment(SwingConstants.CENTER);
			contentPane.add(status);
		}
	}
}

