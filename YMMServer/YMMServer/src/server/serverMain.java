package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.LinkedHashMap;

import db.DAO;

public class serverMain {

	LinkedHashMap<String, ObjectOutputStream> oosMap = new LinkedHashMap<String, ObjectOutputStream>();
	ObjectOutputStream oos;
	ObjectInputStream ois;

	public serverMain() {

		try {
			ServerSocket server = new ServerSocket(8888);

			while(true) {
				Socket client = server.accept();
				System.out.println("서버: "+client.getInetAddress()+"접속 성공");

				oos = new ObjectOutputStream(client.getOutputStream());
				ois = new ObjectInputStream(client.getInputStream());

				new Receiver().start();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void saveOos(TCPData data) {
		oosMap.put(data.from, oos);
	}

	void sendObject(TCPData data) {
		try {
			for (String ss : data.to) {
				oosMap.get(ss).writeObject(data);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class Receiver extends Thread {
		@Override
		public void run() {
			try {
				while(ois!=null) {
					TCPData data = new TCPData();
					data = (TCPData) ois.readObject();

					if(data.kind.equals("서버")) {
						saveOos(data);
					}
					else {
						if(data.kind.equals("예약")){
							new DAO().updateDB(data);
							sendObject(data);
						}
						else {
							sendObject(data);
						}
					}
				}
			} catch (Exception e) {
				System.out.println("누군가 퇴장");
			}finally {
				try {
					ois.close();
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
	public static void main(String[] args) {

		new serverMain();

	}
}

