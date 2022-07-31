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
			case "이발사":
				waitingList = "barberwaiting";
				break;
			case "kiosk_sesin":
				waitingList = "sesinwaiting";
				break;
			case "세신사":
				waitingList = "sesinwaiting";
				break;
		}
		
		switch(((WaitingCustomer)data.sendObject).state){
			case "세신진행중":
				bothUpdateDB(data);
				break;
			case "이발진행중":
				bothUpdateDB(data);
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
						+ ((WaitingCustomer)data.sendObject).menu+"' ";
				System.out.println("쿼리문 작성");
				try {
					System.out.println("쿼리문 진입");
					rs = stmt.executeQuery(sql);
					System.out.println("쿼리문 통과");
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
			case "이발사":
				waitingList = "barberwaiting";
				otherList = "sesinwaiting";
				break;
			case "세신사":
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
		
		
		//락커와 메뉴가 같은것만 상태를 수정
		sql = "update "
				+ waitingList+" set state = '"
				+ ((WaitingCustomer)data.sendObject).state+"' "
				+ "where locker = "
				+ ((WaitingCustomer)data.sendObject).locker+" "
				+ "and menu = '"
				+ ((WaitingCustomer)data.sendObject).menu+"' ";
		//락커만 같으면 상태를 수정
		sql2 = "update "
				+ otherList+" set state = '"
				+ state+"' "
				+ "where locker = "
				+ ((WaitingCustomer)data.sendObject).locker+" ";
			
		System.out.println("쿼리문 작성");
		try {
			System.out.println("쿼리문 진입");
			rs = stmt.executeQuery(sql);
			rs = stmt.executeQuery(sql2);
			System.out.println("쿼리문 통과");
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