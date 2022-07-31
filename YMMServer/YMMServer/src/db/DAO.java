package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import server.TCPData;

public class DAO {
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	String sql = null, sql2 = null;
	
	public DAO() {
		
		String url = "jdbc:mariadb://mk0709.iptime.org:3306/youmockmin";
		String username = "admin";
		String password = "1234";
		
		try {
			//1. DB driver �ε�
			Class.forName("org.mariadb.jdbc.Driver");
			
			//2. connection
			con = DriverManager.getConnection(url, username, password);
			
			//3. sql ���� ���� ��ü
			stmt = con.createStatement();
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void updateDB(TCPData data) {
		String waitingList = "";
		
		switch(data.from) {
			case "kiosk_barber":
				waitingList = "barberwaiting";
				break;
			case "�̹߻�":
				waitingList = "barberwaiting";
				break;
			case "kiosk_sesin":
				waitingList = "sesinwaiting";
				break;
			case "���Ż�":
				waitingList = "sesinwaiting";
				break;
		}
		
		switch(((WaitingCustomer)data.sendObject).state){
			case "����������":
				bothUpdateDB(data);
				break;
			case "�̹�������":
				bothUpdateDB(data);
				break;
			case "�Ϸ�":
				bothUpdateDB(data);
				break;
			default:
				sql = "update "
						+ waitingList+" set state = '"
						+ ((WaitingCustomer)data.sendObject).state+"' "
						+ "where locker = "
						+ ((WaitingCustomer)data.sendObject).locker+" "
						+ "and menu = '"
						+ ((WaitingCustomer)data.sendObject).menu+"' ";
				System.out.println("������ �ۼ�");
				try {
					System.out.println("������ ����");
					rs = stmt.executeQuery(sql);
					System.out.println("������ ���");
				} catch (SQLException e) {
					e.printStackTrace();
				}finally {
					close();
				}
				break;
		}
		
	}
	
	public void bothUpdateDB(TCPData data) {
		String waitingList = "";
		String  otherList= "";
		String state = "";
		switch(data.from) {
			case "�̹߻�":
				waitingList = "barberwaiting";
				otherList = "sesinwaiting";
				break;
			case "���Ż�":
				waitingList = "sesinwaiting";
				otherList = "barberwaiting";
				break;
		}
		
		switch(((WaitingCustomer)data.sendObject).state){
		case "�Ϸ�":
			state = "���";
			break;
		default:
			state = ((WaitingCustomer)data.sendObject).state;
			break;
		}
		
		
		//��Ŀ�� �޴��� �����͸� ���¸� ����
		sql = "update "
				+ waitingList+" set state = '"
				+ ((WaitingCustomer)data.sendObject).state+"' "
				+ "where locker = "
				+ ((WaitingCustomer)data.sendObject).locker+" "
				+ "and menu = '"
				+ ((WaitingCustomer)data.sendObject).menu+"' ";
		//��Ŀ�� ������ ���¸� ����
		sql2 = "update "
				+ otherList+" set state = '"
				+ state+"' "
				+ "where locker = "
				+ ((WaitingCustomer)data.sendObject).locker+" ";
			
		System.out.println("������ �ۼ�");
		try {
			System.out.println("������ ����");
			rs = stmt.executeQuery(sql);
			rs = stmt.executeQuery(sql2);
			System.out.println("������ ���");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
	}
	
	void close() {
		if(rs!=null) try {rs.close();} catch (SQLException e) {}
		if(stmt!=null) try {stmt.close();} catch (SQLException e) {}
		if(con!=null) try {con.close();} catch (SQLException e) {}
	}
	

}