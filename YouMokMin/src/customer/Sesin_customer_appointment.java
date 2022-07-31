package customer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import db.DAO;
import db.WaitingCustomer;
import gui_design.Normal_Button;
import server.TCPData;

public class Sesin_customer_appointment extends JPanel {
	
	ArrayList<WaitingCustomer> apmt;
	
	ObjectOutputStream oos;
	ObjectInputStream ois;
	WaitingCustomer rao = null;
	Socket socket = null;
	
	JPanel apmtPanel;
	JPanel calledP;
	
	JLabel wait;
	JLabel call;
	JLabel waitingLocker;
	Normal_Button calledLocker;
	int apmtCnt=0;
	
	void getSocket(Socket socket) {
		this.socket = socket;
	}
	class apmtReceiver extends Thread{
		@Override
		public void run() {
			try {
				TCPData sendData = new TCPData(new WaitingCustomer(),"서버","kiosk_sesin","서버");
				oos.writeObject(sendData);
				oos.flush();
				oos.reset();
				System.out.println("최초연결데이터전송완료");
				while(ois!=null) {
					System.out.println("데이터 받는다");
					TCPData readData = (TCPData)ois.readObject();
					makeList();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	void apmtSender(WaitingCustomer writeAo) {
		
		try {
			TCPData sendData = new TCPData(writeAo,"예약","kiosk_sesin","kiosk_sesin","kiosk_barber","BarberStaffFrame","SesinStaffFrame");
			oos.writeObject(sendData);
			oos.flush();
			oos.reset();
			System.out.println("전송"+Arrays.toString(sendData.to)+"에게");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	
	
	int custCnt = 0;
	public void makeList() {
		apmtPanel.removeAll();
		calledLocker.setText("");
		apmt = new DAO().sesinwaitingList();
		System.out.println(apmt.toString());
		for(Object ob :apmt) {
			WaitingCustomer wc = (WaitingCustomer)ob;
			if(wc.state.equals("대기")||wc.state.equals("이발진행중")) {
				custCnt++;
				makePanel(wc);
			}else {
				calledLocker.setText(Integer.toString(wc.locker));
			}
		}
		wait.setText("대기 "+custCnt+"명");
		custCnt = 0;
		panelCnt = 0;
		apmtPanel.repaint();
		apmtPanel.revalidate();
	}
	int panelCnt=0;
	public void makePanel(WaitingCustomer wc) {
		Normal_Button apmt = new Normal_Button(Integer.toString(wc.locker),220,220,220,30);
		apmt.setEnabled(false);
		if(panelCnt==0) {
			apmt.setBounds(0,0,115,62);
			apmt.setBackground(new Color(240,240,240));
		}else {
			apmt.setBounds(0,panelCnt*68,115,62);
			apmt.setBackground(new Color(240,240,240));
		}
		apmtPanel.add(apmt);
		panelCnt ++;
	}
	public Sesin_customer_appointment() {
		try {
			String addr = "192.168.150.63";
			socket = new Socket(addr,8888);
			System.out.println("클라이언트:"+addr+"접속성공");
			ObjectOutputStream noos = new ObjectOutputStream(socket.getOutputStream());
			oos = noos;
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		setBounds(500,0,125,471);
		setBackground(Color.white);
		setLayout(null);
		
		calledLocker = new Normal_Button(" ",64,85,163,30);
		calledLocker.setForeground(Color.white);
		calledLocker.setBounds(5,20,115,70);
		calledLocker.setEnabled(false);
		
		call = new JLabel("호출",JLabel.LEFT);
		call.setFont(new Font("나눔고딕",Font.BOLD,16));
		call.setBounds(5,0,115,20);
		wait = new JLabel("대기 "+apmtCnt+"명",JLabel.LEFT);
		wait.setFont(new Font("나눔고딕",Font.BOLD,16));
		wait.setBounds(5,90,115,40);
		
		apmtPanel = new JPanel(); // 대기중인 락커가 보일 패널
		apmtPanel.setLayout(null);
		apmtPanel.setBounds(5,130,115,336);
		apmtPanel.setBackground(Color.white);
		

		add(call);
		add(wait);
		add(calledLocker);
		add(apmtPanel);
	
		makeList();	
		new apmtReceiver().start();
		
	}
}