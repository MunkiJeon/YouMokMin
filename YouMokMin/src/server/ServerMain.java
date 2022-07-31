package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ServerMain {

	LinkedHashMap<String, ObjectOutputStream> oosMap = new LinkedHashMap<String, ObjectOutputStream>();
	ArrayList<String> connetedMem = new ArrayList<String>();
	FileWriter fw;
	BufferedWriter bw;
	FileReader fr;
	BufferedReader br;
	Socket client;
	
	public ServerMain() {
		
		try {
			ServerSocket server = new ServerSocket(8888);

			while (true) {
				System.out.println("서버접속대기중");
				client = server.accept();

				new Receiver(client).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void sendObject(TCPData data) {
		
		for (String ss : data.to) {
			try {
				System.out.println(ss + "에게 전송");
				oosMap.get(ss).writeObject(data);
				oosMap.get(ss).flush();
				oosMap.get(ss).reset();
				System.out.println(ss + "에게 전송완료");

			} catch (Exception e) {
//				e.printStackTrace();
				System.out.println("접속중이 아니어서 못보낸다!");
			}
		}
	}

	class Receiver extends Thread {
		ObjectInputStream ois;
		ObjectOutputStream oos;

		public Receiver(Socket client) {
			try {
				oos = new ObjectOutputStream(client.getOutputStream());
				ois = new ObjectInputStream(client.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		void saveOos(TCPData data) {
			oosMap.put(data.from, oos);
			connetedMem.add(data.from);
			System.out.println("서버에저장" + oosMap.toString());
		}
		
		void chattingConnected(TCPData data) {

			TCPData chatData = new TCPData();
			chatData.from = "";
			chatData.to = new String[] {"BarberStaffFrame", "SesinStaffFrame", "Manager_Pan"};
			chatData.kind = "채팅";
			
			String str;
			try {
				fr = new FileReader("log/chatlog.txt");
				br = new BufferedReader(fr);
				
				while((str=br.readLine())!=null) {
					chatData.sendObject = str;
					oosMap.get(data.from).writeObject(chatData);
					oosMap.get(data.from).flush();
					oosMap.get(data.from).reset();
				}
				System.out.println("채팅 로그 다 보냄");
				
				chatData.sendObject = "-------------저장된 채팅 로그----------------\n";
				oosMap.get(data.from).writeObject(chatData);
				oosMap.get(data.from).flush();
				oosMap.get(data.from).reset();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			chatData.kind = "접속자목록";
			chatData.sendObject = connetedMem;

			for (int i = 0; i < chatData.to.length; i++) {
				try {
					oosMap.get(chatData.to[i]).writeObject(chatData);
					oosMap.get(chatData.to[i]).flush();
					oosMap.get(chatData.to[i]).reset();
					System.out.println("접속자 목록 전송");
				} catch (Exception e) {
//					e.printStackTrace();
					System.out.println("접속자 목록: 접속중이 아니어서 못보낸다!");
				}
			}

		}

		@Override
		public void run() {
			String name = null;
			try {
				TCPData data = null;
				while (ois != null) {

					data = (TCPData) ois.readObject();
					System.out.println("WaitingCustomer형변환:" + data);
					if (data.to[0].equals("서버")) {
						name = data.from;
						System.out.println(data.from);
						saveOos(data);
						if(data.from.equals("BarberStaffFrame")||
						   data.from.equals("SesinStaffFrame")||
						   data.from.equals("Manager_Pan")) {
							chattingConnected(data);
						}
					}else{
						if (data.kind.equals("예약")) {
							System.out.println("예약실행");
							new ServerDAO().updateDB(data);
							sendObject(data);
						}else if(data.kind.equals("채팅")) {
							fw = new FileWriter("log/chatlog.txt", true);
							bw = new BufferedWriter(fw);
							bw.write((String) data.sendObject+"\n");
							bw.close();
							fw.close();
							
							sendObject(data);
						}
						else {
							sendObject(data);
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					ois.close();
					oos.close();
					oosMap.remove(name);
					connetedMem.remove(name);
					System.out.println("접속이 종료되었습니다");
					
					TCPData chatData = new TCPData();
					chatData.from = "";
					chatData.to = new String[] {"BarberStaffFrame", "SesinStaffFrame", "Manager_Pan"};
					
					chatData.kind = "접속자목록";
					chatData.sendObject = connetedMem;
					
					for (int i = 0; i < chatData.to.length; i++) {
						try {
							oosMap.get(chatData.to[i]).writeObject(chatData);
							oosMap.get(chatData.to[i]).flush();
							oosMap.get(chatData.to[i]).reset();
							System.out.println("접속자 목록 전송");
						} catch (Exception e) {
//							e.printStackTrace();
							System.out.println("퇴장했을때 접속자 목록: 접속중이 아니어서 못보낸다!");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {

		new ServerMain();

	}
}