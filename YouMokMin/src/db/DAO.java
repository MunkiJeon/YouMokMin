package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import db.BarberDTO;
import db.SesinDTO;

public class DAO {

	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	String url, username, password;
	String sql = null;

	public DAO() {
		url = "jdbc:mariadb://mk0709.iptime.org:3306/youmockmin";
		username = "admin";
		password = "1234";
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//----------------------------------------------------------------------------------------입장
	
	ArrayList<CustomerDTO> lockerUpdate;
	
	public ArrayList<CustomerDTO> lockerUpdateArr() {
		lockerUpdate = new ArrayList<CustomerDTO>();

		sql = "SELECT customer.locker, customer.timein, customer.timeout FROM customer";

		try {
			rs = stmt.executeQuery(sql);

			while (rs.next()) {

				CustomerDTO dto_1 = new CustomerDTO();
				dto_1.locker = rs.getInt("locker");
				dto_1.timein = rs.getDate("timein");
				dto_1.timeout = rs.getDate("timeout");

				lockerUpdate.add(dto_1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return lockerUpdate;
	}
	
	//customer.timeout를 확인하는거=0419에 추가한 메소드========================================
	public int timeOutCheck(int locker) {
		int res = 0;
		sql = "select customer.timein, customer.timeout from customer where locker = " + locker;
		
		try {
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				//rs.next();
				if(rs.getDate("timeout") == null&&rs.getDate("timein")==null) {
					//정기 회원인데 신규가입하고 집에 간 경우 -> 버튼 눌려도 욈
					return res = 1;
				}
				else if(rs.getDate("timeout") == null&&rs.getDate("timein")!=null) {
					//정기회원 이면서 입장을 한 경우 -> 버튼이 눌리면 안됨
					return res = 2;
				}
				else {//timeIn/Out 둘다 NULL or timeIn=NULL timeOut!=null
					//빈락커인 경우 -> 버튼이 눌려도 됨
					return res=0;
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return res;
	}

/*============0412에 추가한 메소드============*/
public void subscribeTF(String locker) { //기존 정기이용자가 입장시 입장시간만 갱신 시켜주고 paymentDB에는 추가가 안됨
	
	sql = "update customer set timein = sysdate(), timeout = null where locker = "+locker;

	try {
		con = DriverManager.getConnection(url, username, password);
		stmt = con.createStatement();
		rs = stmt.executeQuery(sql);
	} catch (SQLException e) {
		e.printStackTrace();
	}
	finally {
		close();
	}
}
/*====================================*/

public String readinfo(int locker) {
	String res = null;
	sql = "select * from customer where locker = " + locker;
	try {
		con = DriverManager.getConnection(url, username, password);
		stmt = con.createStatement();
		rs = stmt.executeQuery(sql);
		rs.next();
		System.out.println(locker + "번 라커 -> pw: " + rs.getString("password") + "nick: " + rs.getString("nickname"));
		res = rs.getString("password");
		res += "," + rs.getString("nickname");
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		close();
	}
	return res;
}

public void write(BathDTO dto) {
	//일일사용자, 정기(신규)사용자를 customer table에 넣을때
	//0418에 정기회원 만기일까지 넣는 sql문으로 변경
	sql = "insert into customer (locker, nickname, password, subscribe,timein,timeout,leftcount,dday)"
			+ "values (" + dto.locker + ",'" + dto.nickname + "','" + dto.password + "'," + dto.subscribe + ","
			+ "sysdate()" + "," + dto.timeout + "," + dto.leftcount + ","
			+ "adddate(sysdate(),interval "+(dto.leftcount*9)+" day))";
	try {
		con = DriverManager.getConnection(url, username, password);
		stmt = con.createStatement();
		rs = stmt.executeQuery(sql);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {
		close();
	}

}

public void writeFreshCutomer(BathDTO dto) {//일일사용자, 정기(신규)사용자를 customer table에 넣을때
	//0418에 정기회원 만기일까지 넣는 sql문으로 변경

	sql = "insert into customer (locker, nickname, password, subscribe ,timein,timeout,leftcount,dday)"
			+ "values (" + dto.locker + ",'" + dto.nickname + "','" + dto.password + "'," + dto.subscribe + ","
			+ "NULL" + "," + dto.timeout + "," + dto.leftcount + ","
			+ "adddate(sysdate(),interval "+(dto.leftcount*9)+" day))";
	System.out.println(sql);
	try {
		con = DriverManager.getConnection(url, username, password);
		stmt = con.createStatement();
		rs = stmt.executeQuery(sql);
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {
		close();
	}

}

public void entrancePay(int menuID, String lockerNum) {

	sql = "insert into payment(id, locker, category, menuid, menu, price, amount, paytime, state) values"
			+ "((select max(p.id) from payment p)+1,"
			+ lockerNum+","
			+ "(select menu.category from menu where menu.id = "+menuID+"),"
			+ menuID+","
			+ "(select menu.menu from menu where menu.id = "+menuID+"),"
			+ "(select menu.price from menu where menu.id = "+menuID+")*1, "
			+ "1, sysdate(), '대기')";
	try {
		con = DriverManager.getConnection(url, username, password);
		stmt = con.createStatement();
		rs = stmt.executeQuery(sql);
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		close();
	}

}

//0419 밤에 추가한거=================================================================
	public String seasonTickeyPay(int locker) {
		//신규정기가입후 바로 퇴장할때 결제정보 불러오는거
		String info = null;
		sql = "SELECT customer.locker, customer.leftcount, customer.dday FROM customer where customer.locker = " + locker;
		try {
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
	
			info = rs.getString("locker");
			info += "," + rs.getString("leftcount");
			info += "," + rs.getDate("dday");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return info;
	}
	
	public void changeTicketPayment(String locker) {
		//신규가입하고 바로 입장 안하고 결제만 할때
		sql = "update payment set payment.state = '완료' where locker = "+locker;

		try {
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			close();
		}
	}
	
	public void removeClientInfo(String locker) {
		//신규가입하고 바로 입장 안하고 결제만 할때
		sql = "delete from customer where customer.locker = " + locker;

		try {
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sql = "DELETE FROM payment WHERE payment.state = '대기' AND payment.locker = " + locker;
		
		try {
//			con = DriverManager.getConnection(url, username, password);
//			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		finally {
			close();
		}
	}
	//============================================================================
	//0418에 추가=====================================================
	 
	public void ddayCheck() {
		//Dday 시간이 지나면 날려버리는 DAO
		sql = "delete from customer where customer.dday < SYSDATE() and subscribe = 1";
		
		try {
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		finally {
			close();
		}
	}
	
	//정기회원 정보 불러와서 보여주는 DAO
	public String memberinfoList(int locker) {
		String res = null;
		sql = "select  customer.nickname, customer.leftcount, customer.dday from customer where locker = " + locker;
		try {
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				res = rs.getString("nickname");
				res += "," + rs.getString("leftcount");
				res += ","+rs.getDate("dday");	
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return res;
		
	}
	
	
	//--------------------------------------------------------------------------------------------퇴장
	
	public  int userCheck(String locker, String password) { // 퇴장할 때 락커번호와 비번을 불러서 확인하는 메소드
		int res = 0;

		sql = "select * from customer where locker = '" + locker + "'";

		try {
			rs = stmt.executeQuery(sql);
			rs.next();
			if (rs.getString("locker").equals(locker) && rs.getString("password").equals(password)) {
				System.out.println(rs.getString("locker") + "의 결제내역 입니다.");
				res = 1;
				System.out.println(res);

			} else if (rs.getString("password") != password) {
				res=2;
				System.out.println("비밀번호를 확인해주세요.");
			}
		} catch (SQLException e) {
			res=3;
			System.out.println("아이디를 확인해주세요");
		} finally {
			close();
		}

		return res;
	}
	
	public boolean pwCheck(String locker, String password) { // 퇴장할 때 락커번호와 비밀번호 체크

		String realPw = null;

		sql = "select password from customer where customer.locker = " + locker;

		try {
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				realPw = rs.getString("password");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return password.equals(realPw);
	}

	public String[][] exitPaymentList(String locker) { // 퇴장할 때 전체 이용 내역 보여주기

		ArrayList<String[]> res = new ArrayList<String[]>();

		String[][] DB = null;

		sql = "select payment.menu, (select menu.price from menu where menu.menu = payment.menu) as price, payment.amount, "
				+ "payment.price as tot from payment " + "where payment.locker = " + locker
				+ " and payment.state = '대기'";

		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				res.add(new String[] { rs.getString("menu"), rs.getInt("price") + "", rs.getInt("amount") + "",
						rs.getInt("tot") + "" });
			}

			DB = new String[res.size()][4];

			for (int i = 0; i < DB.length; i++) {
				for (int j = 0; j < DB[0].length; j++) {
					DB[i][j] = res.get(i)[j];
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return DB;
	}

	public String calcTot(String locker) { // 퇴장할 때 총 이용 금액 정산

		String tot = null;

		sql = "select sum(payment.price) as res from payment " + "where payment.locker = " + locker
				+ " and payment.state = '대기'";

		try {
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				tot = rs.getString("res");
			}
		} catch (SQLException e) {

		} finally {
			close();
		}
		return tot;
	}
	/*===0414에 추가한 메소드====================================================*/
	public void sesinWaitingDelete(int locker) { //세신대기목록에서 '대기'중인 서비스가 있는데 퇴장하면 그 내역들도 삭제
		sql = "delete from sesinwaiting where sesinwaiting.locker = " + locker + " and sesinwaiting.state = '대기'";
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			close();
		}
	}
	
	public void barbarWaitingDelete(int locker) { //이발대기목록에서 '대기'중인 서비스가 있는데 퇴장하면 그 내역들도 삭제
		sql = "delete from barberwaiting where barberwaiting.locker = " + locker + " and barberwaiting.state = '대기'";
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		finally {
			close();
		}
	}
	
	public void subscribeDelete(int locker) { //횟수가 1남은 정기회원은 퇴장때 회원목록에서 제거
		sql = "delete from customer where customer.leftcount = 1 and locker = " + locker + " and subscribe = 1";
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		finally {
			close();
		}
	}
	
	public void customerSplit(int lockerNum) { // 정기회원인지 일일회원인지 구분
		CustomerDTO dto = null;
		sql = "select subscribe, leftcount from customer where locker = "+lockerNum;
		
		System.out.println(sql);
		try {
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				dto = new CustomerDTO();
				dto.subscribe = rs.getInt("subscribe");
				dto.leftcount = rs.getInt("leftcount");
				dto.locker = lockerNum;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		
		new DAO().customerHourCheck(dto);
		
	}
	
	public void customerHourCheck(CustomerDTO cdto) { //이용시간 24시간을 넘겼는지 확인
		CustomerDTO dto = cdto;
		int overuse = 0;
		sql = "select (timestampdiff(minute,timein,sysdate())/60)/24 as overuse "+
			  "from customer where locker = "+dto.locker;

		System.out.println(sql);
		try {
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				overuse = rs.getInt("overuse");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		
		if(overuse>=1 && (dto.subscribe==0||(dto.subscribe==1&&(dto.leftcount-overuse)<0))) {
			new DAO().insertTicket(dto,overuse);
		}else if(overuse>=1 && (dto.subscribe==1&&(dto.leftcount-overuse)>=0)){
			new DAO().updateLeftcount(dto,overuse);
		}
		
	}
	
	public void insertTicket(CustomerDTO cdto,int overuse) { //24시간을 넘기고 일일이용권or 정기이용권(회원권추가결제필요할때)
		CustomerDTO dto = cdto;
		sql = "insert into payment (id, locker, category, menuid, "+
			  "menu, amount, price, paytime, state) values "+
			  "((select max(p1.id)+1 from payment p1), "+dto.locker+", 1, 100, '일일권', "+overuse+", "+overuse+"*7000, sysdate(), '대기')";
			 
		System.out.println(sql);
		try {
			rs = stmt.executeQuery(sql);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		
	}
	
	public void updateLeftcount(CustomerDTO cdto,int overuse) { // 정기회원이 24시간 이상 사용한 경우(회원권추가결제필요없을때)
		CustomerDTO dto = cdto;
		sql = "update customer set leftcount = "+(dto.leftcount-overuse)+
			  " where locker = "+dto.locker;
			 
		System.out.println(sql);
		try {
			rs = stmt.executeQuery(sql);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		
	}
	
	public void paymentDone(String locker) { // payment 테이블 상태 완료로 바꾸고 회원정보 수정

		sql = "update payment set state = '완료' where locker = " + locker;
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sql = "update customer set leftcount = leftcount - 1 where locker = " + locker + " and subscribe = 1";
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sql = "update customer set timeout = sysdate() where locker = " + locker + " and subscribe = 1";

		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sql = "delete from customer where locker = " + locker + " and subscribe = 0";

		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	
	//----------------------------------------------------------------------------------------- 손님 키오스크
	
	
	public ArrayList<MenuDTO> menuList(){
		ArrayList<MenuDTO> res = new ArrayList<MenuDTO>();
		sql = "select * from menu";
		
		try {
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				MenuDTO dto = new MenuDTO();
				dto.id = rs.getInt("id");
				dto.menu = rs.getString("menu");
				dto.img = rs.getString("img");
				dto.price = rs.getInt("price");
				dto.category = rs.getString("category");
				dto.sep = rs.getString("sep");
				
				res.add(dto);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		
		return res;
	}
	
	
	public MenuDTO menuSelected(int id){
		MenuDTO dto = null;
		sql = "select * from menu where id = "+id;
		
		try {
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				dto = new MenuDTO();
				dto.id = rs.getInt("id");
				dto.menu = rs.getString("menu");
				dto.img = rs.getString("img");
				dto.price = rs.getInt("price");
				dto.category = rs.getString("category");
				dto.sep = rs.getString("sep");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		
		return dto;
	}
	
	
	public ArrayList<PaymentDTO> paymentList(){
		ArrayList<PaymentDTO> res = new ArrayList<PaymentDTO>();
		sql = "select * from payment";
		
		try {
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				PaymentDTO dto = new PaymentDTO();
				dto.id = rs.getInt("id");
				dto.locker = rs.getInt("locker");
				dto.category = rs.getString("category");
				dto.menuid = rs.getInt("menuid");
				dto.amount = rs.getInt("amount");
				dto.price = rs.getInt("price");
				dto.paytime = rs.getTimestamp("paytime");
				dto.state = rs.getString("state");
				
				res.add(dto);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		
		return res;
	}
	
	
	public void paymentInsert(PaymentDTO dto){
		
		sql = "insert into payment (price, locker, category, "+
			  "menuid, menu, amount, paytime, state,id) values "+
			  "( "+dto.price+","+dto.locker+
			  " , '"+dto.category+
			  "' , "+dto.menuid+
			  " , '"+dto.menu+
			  "' , "+dto.amount+
			  " ,sysdate(), '대기',(select max(p.id) from payment p)+1)";
		
		try {
			rs = stmt.executeQuery(sql);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	
	public PaymentDTO paymentFee(int locker){
		PaymentDTO dto = null;
		sql = "select sum(price), sum(amount) from payment "+
			  " where date_format(paytime,'%Y-%m-%d %H:%i:%s') "+
			  " >= (select date_format(timein,'%Y-%m-%d %H:%i:%s') from customer "+
			  " where locker = "+locker+") and state = '대기' and locker = "+locker;
		//System.out.println(sql);
		try {
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				dto = new PaymentDTO();
				
				dto.amount = rs.getInt("sum(amount)");
				dto.price = rs.getInt("sum(price)");

			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		
		return dto;
		
	}
	
	
	public ArrayList<PaymentDTO> paymentFeeDetail(int locker){
		ArrayList<PaymentDTO> res = new ArrayList<PaymentDTO>();
		sql = "select price, amount, menu from payment"+
				  " where date_format(paytime,'%Y-%m-%d %H:%i:%s')"+
				  " >= (select date_format(timein,'%Y-%m-%d %H:%i:%s') from customer"+
				  " where locker = "+locker+") and state = '대기' and locker = "+locker;
		//System.out.println(sql);
		try {
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				PaymentDTO dto = new PaymentDTO();
				
				dto.menu = rs.getString("menu");
				dto.amount = rs.getInt("amount");
				dto.price = rs.getInt("price");
				
				res.add(dto);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		
		return res;
	}
	
	
	
	
	public ArrayList<CustomerDTO> customerList(){
		ArrayList<CustomerDTO> res = new ArrayList<CustomerDTO>();
		sql = "select * from customer";
		
		try {
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				CustomerDTO dto = new CustomerDTO();
				dto.id = rs.getInt("id");
				dto.locker = rs.getInt("locker");
				dto.nickname = rs.getString("nickname");
				dto.password = rs.getInt("password");
				dto.subscribe = rs.getInt("subscribe");
				dto.timein = rs.getTimestamp("timein");
				dto.timeout = rs.getTimestamp("timeout");
				dto.leftcount = rs.getInt("leftcount");
				res.add(dto);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		
		return res;
	}
	
	
	public CustomerDTO customerNickname(int locker){
		
		sql = "select nickname from customer where locker = "+ locker;
		
		CustomerDTO cusDto = null;
		try {
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				cusDto = new CustomerDTO();
				cusDto.nickname = rs.getString("nickname");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		return cusDto;
	}
	
	
	public ArrayList<WaitingCustomer> barberwaitingList(){
		ArrayList<WaitingCustomer> res = new ArrayList<WaitingCustomer>();
		sql = "select * from barberwaiting where state = '대기'or state = '세신진행중' or state = '호출'";
		
		try {
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				WaitingCustomer dto = new WaitingCustomer();
				dto.id = rs.getInt("id");
				dto.locker = rs.getInt("locker");
				dto.menu = rs.getString("menu");
				dto.state = rs.getString("state");
				
				res.add(dto);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		
		return res;
	}
	
	
	public void barberwaitingInsert(WaitingCustomer wc){
		
		sql = "insert into barberwaiting (locker, menu, state) values "+
		"("+wc.locker+", '"+wc.menu+"', '"+wc.state+"')";
		
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}

	}
	
	
	public ArrayList<WaitingCustomer> sesinwaitingList(){
		ArrayList<WaitingCustomer> res = new ArrayList<WaitingCustomer>();
		sql = "select * from sesinwaiting where state = '대기'or state = '이발진행중' or state = '호출'";
		
		try {
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				WaitingCustomer dto = new WaitingCustomer();
				dto.id = rs.getInt("id");
				dto.locker = rs.getInt("locker");
				dto.menu = rs.getString("menu");
				dto.state = rs.getString("state");
				
				res.add(dto);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		
		return res;
	}
	
	
	public void sesinwaitingInsert(WaitingCustomer wc){
		
		sql = "insert into sesinwaiting (locker, menu, state) values "+
		"("+wc.locker+", '"+wc.menu+"', '"+wc.state+"')";
		
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}

	}
	

	
	public void inventoryModify(InvenDTO info){
		
		sql = "update inventory set display = "+info.getDisplay()+
			  " where menu = '"+info.getMenu()+"'";
		
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		
	}
	
	public InvenDTO inventoryAmount(String menu){
		
		InvenDTO dto=null;
		
		sql = "select display, storage from inventory where menu = '"+menu+"'";
		
		try {
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				dto = new InvenDTO();
				dto.display = rs.getInt("display");
				dto.storage = rs.getInt("storage");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		return dto;
	}

	public String billsTimeCheck(String locker) {
		sql = "select payment.menu, payment.price, payment.amount, payment.paytime, "
				+ "payment.price*payment.amount as tot from payment " + "where payment.locker = " + locker
				+ " and payment.state = '대기';";

		return sql;
	}

	public String[][] billsList(String sqlmade) {

		ArrayList<String[]> res = new ArrayList<String[]>();

		String[][] DB = null;

		try {
			rs = stmt.executeQuery(sqlmade);

			while (rs.next()) {
				res.add(new String[] { rs.getString("menu"), rs.getInt("price") + "", rs.getInt("amount") + "",
						rs.getInt("tot") + "", rs.getTimestamp("paytime") + "" });
			}

			DB = new String[res.size()][5];

			for (int i = 0; i < DB.length; i++) {
				for (int j = 0; j < DB[0].length; j++) {
					DB[i][j] = res.get(i)[j];
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return DB;
	}
	
	//----------------------------------------------------------------------------------------- 이발사/세신사
	
	public String paymentListSql(String year, String month, String day, String range) { // 일 결제내역 조회 sql 생성

		sql = "select payment.*, menu.sep from payment join menu" + " on payment.menu = menu.menu" + " where (" + range
				+ ") and paytime >= '" + year + ":" + month + ":" + day + "' and paytime < '" + year + ":" + month + ":"
				+ (Integer.parseInt(day) + 1) + "' and (payment.state = '완료' or payment.state = '취소')"
				+ " order by paytime";

		return sql;
	}

	public String paymentListSql(String year, String month, String range) { // 월 결제내역 조회 sql 생성

		sql = "select payment.*, menu.sep from payment join menu" + " on payment.menu = menu.menu" + " where (" + range
				+ ") and paytime >= '" + year + ":" + month + ":01' and paytime < '" + year + ":" + month
				+ ":31' and (payment.state = '완료' or payment.state = '취소') " + "order by paytime";

		return sql;
	}

	public String[][] paymentList(String sqlmade) { // 결제 내역 조회

		ArrayList<String[]> res = new ArrayList<String[]>();

		String[][] DB = null;

		try {
			rs = stmt.executeQuery(sqlmade);

			while (rs.next()) {
				res.add(new String[] { rs.getInt("id") + "", rs.getInt("locker") + "", rs.getString("menu"),
						rs.getInt("price") + "", rs.getTimestamp("paytime") + "", rs.getString("state") });
			}

			DB = new String[res.size()][6];

			for (int i = 0; i < DB.length; i++) {
				for (int j = 0; j < DB[0].length; j++) {
					if(j==3) {
						DB[i][j] = new DecimalFormat().format(Integer.parseInt((String) res.get(i)[j]))+" ";
					}else {
						DB[i][j] = res.get(i)[j];
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return DB;
	}

	public String paymentCalcSql(String year, String month, String day, String range) { // 월 매출 금액 sql 생성

		sql = "select sum(payment.price) from payment join menu" + " on payment.menu = menu.menu" + " where (" + range
				+ ") and paytime >= '" + year + ":" + month + ":" + day + "' and paytime < '" + year + ":" + month + ":"
				+ (Integer.parseInt(day) + 1) + "' and payment.state = '완료' " + "order by paytime";

		return sql;
	}

	public String paymentCalcSql(String year, String month, String range) { // 일 매출 금액 sql 생성

		sql = "select sum(payment.price) from payment join menu" + " on payment.menu = menu.menu" + " where (" + range
				+ ") and paytime >= '" + year + ":" + month + ":01' and paytime < '" + year + ":" + month
				+ ":31' and payment.state = '완료' " + "order by paytime";

		return sql;
	}

	public int paymentCalcTotal(String sqlmade) { // 총 매출 금액 계산

		int res = 0;

		try {
			rs = stmt.executeQuery(sqlmade);

			while (rs.next()) {
				res = rs.getInt("sum(payment.price)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return res;
	}

	public BarberDTO barberNow() {
		sql = "select barberwaiting.*, customer.password" + " from barberwaiting join customer"
				+ " on barberwaiting.locker = customer.locker " + " where barberwaiting.state = '이발진행중'";
		BarberDTO dto = null;
		try {
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				dto = new BarberDTO();
				dto.id = rs.getInt("id");
				dto.locker = rs.getInt("locker");
				dto.password = rs.getInt("password");
				dto.menu = rs.getString("menu");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return dto;
	}

	ArrayList<BarberDTO> barberWaiting;
	public ArrayList<BarberDTO> barberWaitingArr() { // 현재 대기중인 목록 불러오기
		barberWaiting = new ArrayList<BarberDTO>();

		sql = "select barberwaiting.*, customer.password" + " from barberwaiting join customer"
				+ " on barberwaiting.locker = customer.locker "
				+ " where barberwaiting.state = '대기' or barberwaiting.state = '호출' or barberwaiting.state = '세신진행중'"
				+ "order by barberwaiting.id";

		try {
			rs = stmt.executeQuery(sql);

			while (rs.next()) {

				BarberDTO dto = new BarberDTO();
				dto.id = rs.getInt("id");
				dto.locker = rs.getInt("locker");
				dto.password = rs.getInt("password");
				dto.menu = rs.getString("menu");

				barberWaiting.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return barberWaiting;
	}

	public boolean checkSesinWait(int id){

		boolean res = true;
		String state = "";

		sql = "select state from barberwaiting where id = "+id;

		try {
			rs = stmt.executeQuery(sql);

			while(rs.next()) {
				state = rs.getString("state");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}

		if(state.equals("세신진행중")) {
			res = false;
		}

		return res;
	}

	public void writeToPayment(int locker, ArrayList menu) {

		for (int i = 0; i < menu.size(); i++) {
			sql = "insert into payment(id, locker, category, menuid, menu, price, amount, paytime, state) values "
					+ "((select max(p.id) from payment p)+1, "
					+ locker
					+ ", (select menu.category from menu where menu.menu = '"+menu.get(i)+"') "
					+ ", (select menu.id from menu where menu.menu = '"+menu.get(i)+"'), '"
					+ menu.get(i)
					+ "', (select menu.price from menu where menu.menu = '"+menu.get(i)+"'), "
					+ 1
					+ ", sysdate(), '대기')";

			System.out.println(sql);
			try {
				rs = stmt.executeQuery(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		close();
	}
	
	public SesinDTO sesinNow() {

		sql = "select sesinwaiting.*, customer.password" + " from sesinwaiting join customer"
				+ " on sesinwaiting.locker = customer.locker " + " where sesinwaiting.state = '세신진행중'";

		SesinDTO dto = null;

		try {
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				dto = new SesinDTO();
				dto.id = rs.getInt("id");
				dto.locker = rs.getInt("locker");
				dto.password = rs.getInt("password");
				dto.menu = rs.getString("menu");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return dto;
	}

	ArrayList<SesinDTO> sesinWaiting;

	public ArrayList<SesinDTO> sesinWaitingArr() {
		sesinWaiting = new ArrayList<SesinDTO>();

		sql = "select sesinwaiting.*, customer.password" + " from sesinwaiting join customer"
				+ " on sesinwaiting.locker = customer.locker "
				+ " where sesinwaiting.state = '대기' or sesinwaiting.state = '호출' or sesinwaiting.state = '이발진행중'"
				+ "order by sesinwaiting.id";

		try {
			rs = stmt.executeQuery(sql);

			while (rs.next()) {

				SesinDTO dto = new SesinDTO();
				dto.id = rs.getInt("id");
				dto.locker = rs.getInt("locker");
				dto.password = rs.getInt("password");
				dto.menu = rs.getString("menu");

				sesinWaiting.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return sesinWaiting;
	}

	public boolean checkBarberWait(int id){

		boolean res = true;
		String state = "";

		sql = "select state from sesinwaiting where id = "+id;

		try {
			rs = stmt.executeQuery(sql);

			while(rs.next()) {
				state = rs.getString("state");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}

		if(state.equals("이발진행중")) {
			res = false;
		}

		return res;
	}
	
	
	//------------------------------------------------------------------------------------관리자
	
	public String manager_ListSql(String year, String month, String day, String range) {

		sql = "select payment.*, menu.sep from payment join menu" + " on payment.menu = menu.menu" + " where (" + range
				+ ") and paytime >= '" + year + ":" + month + ":" + day + "' and paytime < '" + year + ":" + month + ":"
				+ (Integer.parseInt(day) + 1) + "'" + " order by paytime";

		return sql;
	}

	public String manager_ListSql(String year, String month, String range) {

		sql = "select payment.*, menu.sep from payment join menu" + " on payment.menu = menu.menu" + " where (" + range
				+ ") and paytime >= '" + year + ":" + month + ":01' and paytime < '" + year + ":" + month + ":31' "
				+ "order by paytime";

		return sql;
	}
	
	public String[][] invenList() {

		ArrayList<String[]> res = new ArrayList<String[]>();
		sql = "select * from inventory";
		String[][] DB = null;
		try {
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				res.add(new String[] { rs.getString("menu") + "", rs.getString("sep") + "",
									   rs.getInt("display") + "", rs.getInt("retailPrice") + "",
									   rs.getInt("storage") + "", rs.getInt("wholesalePrice") + ""});
			}
			DB = new String[res.size()][6];

			for (int i = 0; i < DB.length; i++) {
				for (int j = 0; j < DB[0].length; j++) {
					DB[i][j] = res.get(i)[j];
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return DB;
	}
	public String[][] itemAddList() {

		ArrayList<String[]> res = new ArrayList<String[]>();
		sql = "select * from inventory";
		String[][] DB = null;
		try {
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				res.add(new String[] { rs.getString("menu") + "", rs.getString("sep") + "",
									   rs.getInt("retailPrice") + "",
									  "0", rs.getInt("wholesalePrice") + ""});
			}
			DB = new String[res.size()][5];

			for (int i = 0; i < DB.length; i++) {
				for (int j = 0; j < DB[0].length; j++) {
					DB[i][j] = res.get(i)[j];
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return DB;
	}
	public void invenFilInTheStore(ManagerDTO dto) {
		
		int res = 0;
		String detail_sql = "select * from inventory WHERE menu = '" + dto.menu + "'";
		try {

			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(detail_sql);
			rs.next();
			System.out.println(dto.storage+"//"+rs.getInt("displayMax")+"//"+ rs.getInt("display"));
			if(dto.storage >= (rs.getInt("displayMax") - rs.getInt("display"))) {
				sql = "update inventory set "
						+ " display = " + rs.getInt("displayMax")
						+ ", storage = " + (dto.storage - (rs.getInt("displayMax") - rs.getInt("display"))) 
						+ " where menu = '" + dto.menu + "'";
			}else if(dto.storage < (rs.getInt("displayMax") - rs.getInt("display"))) {
				sql = "update inventory set "
						+ " display = " + (rs.getInt("display")+dto.storage)
						+ ", storage = " + 0 
						+ " where menu = '" + dto.menu + "'";
			}
			res = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public void invenFilInTheStorehouse(ManagerDTO dto) {
		int res = 0;
		String detail_sql = "select * from inventory WHERE menu = '" + dto.menu + "'";
		try {
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(detail_sql);
			rs.next();
			sql = "update inventory set " 
					+ " sep = '" + rs.getString("sep")
					+ "', retailPrice  = " + dto.retailPrice
					+ ", storage  = " + (rs.getInt("storage")+dto.storage)
					+ ", wholesalePrice  = " + dto.wholesalePrice 
					+ " where menu = '" + dto.menu + "'";
			res = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String inserting_sql = "select m1.menu, m2.category, m1.storage,m1.wholesalePrice,SYSDATE() as intime"
				+" from inventory m1 join menu m2 on m1.menu = m2.menu WHERE m1.menu = '"+ dto.menu +"'";
		try {
			rs = stmt.executeQuery(inserting_sql);
			System.out.println(rs.getRow());
			sql = "insert into outlay (menu,category,sep,quantity,wholesalePrice,intime)values ('" 
					+ dto.menu
					+ "', '" + "4"//rs.getString("category")
					+ "', '" + dto.sep
					+ "', " + dto.storage 
					+ ", " + ( dto.wholesalePrice * dto.storage )//추가
					+ ",sysdate())";
			res = stmt.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			close();
		}
	}
	
	public String outlayListSql(String year, String month, String day, String range) { // 일 지줄내역 조회 sql 생성

		sql = "select outlay.* from outlay where (" + range
				+ ") and intime >= '" + year + ":" + month + ":" + day + "' and intime < '" + year + ":" + month + ":"
				+ (Integer.parseInt(day) + 1) + "' order by intime";

		return sql;
	}

	public String outlayListSql(String year, String month, String range) { // 월 지출내역 조회 sql 생성

		sql = "select outlay.* from outlay where (" + range
				+ ") and intime >= '" + year + ":" + month + ":01' and intime < '" + year + ":" + month
				+ ":31'" + " order by intime";

		return sql;
	}

	public String[][] outlayList(String sqlmade) { // 결제 내역 조회

		ArrayList<String[]> res = new ArrayList<String[]>();

		String[][] DB = null;

		try {
			rs = stmt.executeQuery(sqlmade);

			while (rs.next()) {

				//intime(입고 시간) sep(상세 항목)  menu(제품명) quantity(수량) wholesaleprice(도매가)
				res.add(new String[] {rs.getTimestamp("intime") + "",rs.getString("sep"), rs.getString("menu") + "",
									rs.getInt("quantity") + "",rs.getInt("wholesaleprice") + ""});
			}

			DB = new String[res.size()][5];

			for (int i = 0; i < DB.length; i++) {
				for (int j = 0; j < DB[0].length; j++) {
					if(j==3) {
						DB[i][j] = new DecimalFormat().format(Integer.parseInt((String) res.get(i)[j]))+" ";
					}else {
						DB[i][j] = res.get(i)[j];
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return DB;
	}

	public String outlayCalcSql(String year, String month, String day, String range) { // 월 지출 금액 sql 생성

		sql = "select sum(outlay.wholesaleprice) from outlay " + " where (" + range
				+ ") and intime >= '" + year + ":" + month + ":" + day + "' and intime < '" + year + ":" + month + ":"
				+ (Integer.parseInt(day) + 1) + "' order by intime";

		return sql;
	}

	public String outlayCalcSql(String year, String month, String range) { // 일 지출 금액 sql 생성

		sql = "select sum(outlay.wholesaleprice) from outlay " + " where (" + range
				+ ") and intime >= '" + year + ":" + month + ":01' and intime < '" + year + ":" + month
				+ ":31'" + "order by intime";
		
		return sql;
	}

	public int outlayCalcTotal(String sqlmade) { // 총 지출 금액 계산

		int res = 0;

		try {
			rs = stmt.executeQuery(sqlmade);

			while (rs.next()) {
				res = rs.getInt("sum(outlay.wholesaleprice)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return res;
	}
	
	public ArrayList<JungSanDTO> yearJungsan(String year){

		ArrayList<JungSanDTO> arr = new ArrayList<JungSanDTO>();

		for (int i = 0; i < 12; i++) {
			JungSanDTO dto = new JungSanDTO();

			sql = "select sum(outlay.wholesaleprice) as minus from outlay where intime >= '"
					+ year+ "-"+(i+1)+"-01' and intime <= '"
					+ year+ "-"+(i+1)+"-31'";
			try {
				rs = stmt.executeQuery(sql);
				System.out.println(sql);
				while(rs.next()) {
					dto.minus = rs.getInt("minus");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			sql = "select sum(payment.price) as plus from payment where paytime >= '"
					+ year+"-"+ (i+1)+"-01' and paytime <= '"
					+ year+ "-"+(i+1)+"-31'";
			try {
				rs = stmt.executeQuery(sql);
				while(rs.next()) {
					dto.plus = rs.getInt("plus");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			dto.realGain = dto.plus - dto.minus;

			arr.add(dto);
		}

		close();
		return arr;

	}

	public ArrayList<JungSanDTO> monthJungsan(String year, String month){

		ArrayList<JungSanDTO> arr = new ArrayList<JungSanDTO>();

		Calendar selectedDay = Calendar.getInstance();
		selectedDay.set(Integer.parseInt(year), Integer.parseInt(month)-1, 1);		

		for (int i = 0; i < selectedDay.getActualMaximum(Calendar.DATE); i++) {

			JungSanDTO dto = new JungSanDTO();

			sql = "select sum(outlay.wholesaleprice) as minus from outlay where outlay.intime >= '"
					+ year+"-"+month+ "-"+(i+1)
					+ "' and outlay.intime < '"
					+ year+"-"+month+"-"+(i+2)+ "'";

			System.out.println("1)"+sql);

			try {
				rs = stmt.executeQuery(sql);

				while(rs.next()) {
					dto.minus = rs.getInt("minus");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			sql = "select sum(payment.price) as plus from payment where payment.paytime >= '"
					+ year+"-"+month+ "-"+(i+1)
					+ "' and payment.paytime < '"
					+ year+"-"+month+"-"+(i+2)+ "'";

			System.out.println("2)"+sql);

			try {
				rs = stmt.executeQuery(sql);

				while(rs.next()) {
					dto.plus = rs.getInt("plus");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			dto.realGain = dto.plus - dto.minus;

			arr.add(dto);
		}

		return arr;
	}

	public String monthDetailSqlMake(String year, String month, String category, String sep) {

		String range = null;

		switch(category) {
		case "ALL": 
			range = "";
			break;
		case "이용권":
			range = "where category = 1 ";
			break;
		case "이발":
			range = "where category = 2 ";
			break;
		case "세신":
			range = "where category = 3 ";
			break;
		case "매점":
			range = "where category = 4 ";
			break;
		}

		String sepRange = "";

		if(!sep.equals("ALL")) {
			sepRange = "and a.sep = '"+sep+"' ";
		}

		sql = "select a.category as 분류, a.menu as 메뉴명, b.지출, c.매출, ifnull(매출, 0) - ifnull(지출, 0) as 순수익 from "
				+ "(select menu.category, menu.sep, menu.menu from menu) a join "
				+ "(select menu.menu, sum(x.wholesaleprice) as 지출 from menu left outer join "
				+ "(select * from outlay where intime >= '"
				+ year+"-"+month+"-01' and intime <= '"+year+"-"+month+"-31') x "
				+ "on menu.menu = x.menu "
				+ "group by menu.menu) b "
				+ "on a.menu = b.menu "
				+ "join (select menu.menu, sum(y.price) as 매출 from menu left outer join "
				+ "(select * from payment where payment.paytime >= '"
				+ year+"-"+month+"-01' and payment.paytime <= '"+year+"-"+month+"-31') y "
				+ "on menu.menu = y.menu "
				+ "group by menu.menu) c "
				+ "on a.menu = c.menu "
				+ range + sepRange
				+ "order by 순수익 desc";

		return sql;
	}

	public String dayDetailSqlMake(String year, String month, String day, String category, String sep) {

		String range = null;

		switch(category) {
		case "ALL": 
			range = "";
			break;
		case "이용권":
			range = "where category = 1 ";
			break;
		case "이발":
			range = "where category = 2 ";
			break;
		case "세신":
			range = "where category = 3 ";
			break;
		case "매점":
			range = "where category = 4 ";
			break;
		}

		String sepRange = "";

		if(!sep.equals("ALL")) {
			sepRange = "and a.sep = '"+sep+"' ";
		}

		sql = "select a.category as 분류, a.menu as 메뉴명, b.지출, c.매출, ifnull(매출, 0) - ifnull(지출, 0) as 순수익 from "
				+ "(select menu.category, menu.sep , menu.menu from menu) a join "
				+ "(select menu.menu, sum(x.wholesaleprice) as 지출 from menu left outer join "
				+ "(select * from outlay where intime >= '"
				+ year+"-"+month+"-"+day+"' and intime < '"+year+"-"+month+"-"+(Integer.parseInt(day)+1)+"') x "
				+ "on menu.menu = x.menu "
				+ "group by menu.menu) b "
				+ "on a.menu = b.menu "
				+ "join (select menu.menu, sum(y.price) as 매출 from menu left outer join "
				+ "(select * from payment where payment.paytime >= '"
				+ year+"-"+month+"-"+day+"' and payment.paytime < '"+year+"-"+month+"-"+(Integer.parseInt(day)+1)+"') y "
				+ "on menu.menu = y.menu "
				+ "group by menu.menu) c "
				+ "on a.menu = c.menu "
				+ range + sepRange
				+ "order by 순수익 desc";

		return sql;
	}

	public String[][] detailJungsan(String sql){

		ArrayList<String[]> res = new ArrayList<String[]>();
		String[][] tableDB = null;

		System.out.println("3)"+sql);

		try {
			rs = stmt.executeQuery(sql);

			while(rs.next()) {
				res.add(new String[] {rs.getString("분류"), rs.getString("메뉴명"), rs.getInt("지출")+"", rs.getInt("매출")+"", rs.getInt("순수익")+""});
			}

			tableDB = new String[res.size()+1][5];

			for (int i = 0; i < tableDB.length-1; i++) {
				for (int j = 0; j < tableDB[0].length; j++) {
					if(j<2) {
						tableDB[i][j] = res.get(i)[j];
					}
					else {
						tableDB[i][j] = new DecimalFormat().format(Integer.parseInt(res.get(i)[j]));
					}
				}
			}
			tableDB[res.size()][0] = "Total";
			tableDB[res.size()][1] = "";

			for (int i = 2; i < 5; i++) {
				int tot = 0;
				for(int j = 0; j < tableDB.length-1; j++) {
					tot += Integer.parseInt(res.get(j)[i]);
					tableDB[res.size()][i] = new DecimalFormat().format(tot);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		close();

		return tableDB;
	}
	
	
	
	void close() {
		if (rs != null) try {rs.close();} catch (SQLException e) {}
		if (stmt != null) try {stmt.close();} catch (SQLException e) {}
		if (con != null) try {con.close();} catch (SQLException e) {}
	}
}
