package customer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import db.WaitingCustomer;
import server.TCPData;

public class ExitFrame_2_server {

	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	public ExitFrame_2_server() {
		
		try {
			String addr = "192.168.150.63";
			//addr = "220.72.103.79";
			
			socket = new Socket(addr,8888);
			System.out.println("클라이언트:"+addr+"접속성공");
			
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void apmtSender(WaitingCustomer writeAo) {
		
		try {
			TCPData sendData = new TCPData(writeAo,"퇴장","ExitFrame_2","kiosk_barber","kiosk_sesin","BarberStaffFrame","SesinStaffFrame");
			oos.writeObject(sendData);
			oos.flush();
			oos.reset();
			System.out.println("전송"+Arrays.toString(sendData.to)+"에게");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}