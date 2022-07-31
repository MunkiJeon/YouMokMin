package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import db.WaitingCustomer;

public class ServerDAO {
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	String sql = null, sql2 = null;
	
	public ServerDAO() {
		
		String url = "jdbc:mariadb://mk0709.iptime.org:3306/youmockmin";
		String username = "admin";
		String password = "1234";
		
		try {
			//1. DB driver 로딩
			Class.forName("org.mariadb.jdbc.Driver");
			
			//2. connection
			con = DriverManager.getConnection(url, username, password);
			
			//3. sql 구문 실행 객체
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
			case "BarberStaffFrame":
				waitingList = "barberwaiting";
				break;
			case "kiosk_sesin":
				waitingList = "sesinwaiting";
				break;
			case "SesinStaffFrame":
				waitingList = "sesinwaiting";
				break;
		}
		
		switch(((WaitingCustomer)data.sendObject).state){
			case "세신진행중":
				bothUpdateDB(data);
				System.out.println("세신진행중으로 간다");
				break;
			case "이발진행중":
				bothUpdateDB(data);
				System.out.println("이발진행중으로 간다");
				break;
			case "완료":
				bothUpdateDB(data);
				break;
			default:
				sql = "update "
						+ waitingList+" set state = '"
						+ ((WaitingCustomer)data.sendObject).state+"' "
						+ "where locker = "
						+ ((WaitingCustomer)data.sendObject).locker+" "
						+ "and menu = '"
						+ ((WaitingCustomer)data.sendObject).menu+"' "
						+ "and id = "+((WaitingCustomer)data.sendObject).id+" "
						+ "and (state = '대기' or state = '세신진행중' or state = '이발진행중' or state = '호출')";
				
				System.out.println(sql);
				
				System.out.println("기본 쿼리문 작성");
				try {
					System.out.println("기본 쿼리문 진입");
					rs = stmt.executeQuery(sql);
					System.out.println("기본 쿼리문 통과");
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
			case "BarberStaffFrame":
				waitingList = "barberwaiting";
				otherList = "sesinwaiting";
				break;
			case "SesinStaffFrame":
				waitingList = "sesinwaiting";
				otherList = "barberwaiting";
				break;
		}
		
		switch(((WaitingCustomer)data.sendObject).state){
		case "완료":
			state = "대기";
			break;
		default:
			state = ((WaitingCustomer)data.sendObject).state;
			break;
		}
		
		
		//락커와 메뉴, id가 같은것만 상태를 수정
		sql = "update "
				+ waitingList+" set state = '"
				+ ((WaitingCustomer)data.sendObject).state+"' "
				+ "where locker = "
				+ ((WaitingCustomer)data.sendObject).locker+" "
				+ "and menu = '"
				+ ((WaitingCustomer)data.sendObject).menu+"' "
				+ "and id = "+((WaitingCustomer)data.sendObject).id+" "
				+ "and (state = '대기' or state = '세신진행중' or state = '이발진행중' or state = '호출')";
		//락커만 같으면 상태를 수정
		sql2 = "update "
				+ otherList+" set state = '"
				+ state+"' "
				+ "where locker = "
				+ ((WaitingCustomer)data.sendObject).locker+" "
				+ "and (state = '대기' or state = '세신진행중' or state = '이발진행중' or state = '호출')";
			
		System.out.println("both 쿼리문 작성");
		System.out.println(sql);
		System.out.println(sql2);
		try {
			System.out.println("both 쿼리문 진입");
			rs = stmt.executeQuery(sql);
			rs = stmt.executeQuery(sql2);
			System.out.println("both 쿼리문 통과");
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