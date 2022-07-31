//레이아웃작업
package customer;

import javax.swing.*;

import db.DAO;
import db.BathDTO;
import db.CustomerDTO;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.awt.event.*;

public class Disgin extends JFrame {
	JFrame enter_F;
	JPanel SelectMain_P, locker_P, oneday_P, regular_P, regularMenuGroup_P;
	JScrollPane oneday_Scroll, regular_Scroll;
	JButton oneday_btn, regular_btn, regular_btn10, regular_btn20, regular_btn30, pwCheck_btn, numberBtn;
	JLabel oneday_lb, regular_lb, main_lb, menu_lb, choiceCheck_lb, memberInfo_lb, Nick_lockerNum_lb;
	String lockerNum, choicelocker;
	String firstPwIn="";
	String SecondPwIn="";
	int chioceMenuID;
	Dialog popup, popUpUserInfo, confirmPopUp, ticketPayPopUp;
	JFormattedTextField setNick;
	int dayChoice, Choiced_Ticket, MaxLocker_num = 30, bound_num = 10;
	JPasswordField pw_Field;
	JPasswordField pwCheck_Field;
	Boolean actionCheck;
	Color mainColor = new Color(118,177,235);
	Color titleColor = new Color(28,61,245);
	Color disableBtnColor = new Color(112,128,144);
	
	String memberInfo, memInfo;
	String [] userInfo = {""};
	String [] payInfo = {""};
	JLabel lockerNumber_Label, leftCount_Label, dDayCount_Label, waringMsg_Label, title_Label, userLockerNum_Label, userLeftCount_Label, userdDay_Label; 
	JButton confirm_Button;
	
	JLabel msG_Label; 
	JButton chk_Button, cancel_Button;
	
	DAO dao = new DAO();
	BathDTO dto = new BathDTO();

	List<JButton> OnedayLocker_arr = new ArrayList(), regularLocker_arr = new ArrayList();
	JButton Oneday_btn00;
	JLabel lblNewLabel;
	JLabel lblNewLabel_1;
	
	JLabel payTitle_Label,useLockerNumm_Label,ticketCount_Label,ticketPrice_Label; 
	JButton pay_Button, payCancel_Button;
	JLabel dDays_Label, userLockerNumb_Label, dDaycounter_Label, seasonTicket_Label, seasonTiPri_Label;
	String price;

	public void makeLocker() {
		for (int i = 1; i <= MaxLocker_num; i++) {
			JButton jb = new JButton(i + "번 라커");
			jb.setBackground(Color.white);
			jb.setFont(new Font("나눔고딕", Font.BOLD, 20));
//			jb.setForeground();
			
			jb.addActionListener(e -> {
				choiceCheck_lb.setText("<html>이용 하실 티켓을<br/>\r\n선택해주세요");
				//pwCheck_btn.setEnabled(true);
				pw_Field.setEditable(true);
				pw_Field.setText("");
				pwCheck_Field.setText("");
				setNick.setText("");
				choicelocker = ((JButton) (e.getSource())).getText();
				lockerNum = choicelocker.substring(0, choicelocker.indexOf("번"));
				Nick_lockerNum_lb.setText("라커 번호: " + choicelocker);
				
				//System.out.println(choicelocker.substring(0,choicelocker.indexOf("번")));
				if (Integer.parseInt(lockerNum) <= bound_num) {
					if (((JButton) (e.getSource())).getBackground().equals(Color.WHITE)) {
						setNick.setText(choicelocker);
						setNick.setEditable(true);
						System.out.println(dayChoice + "정기 흰거 샌택");
						memberInfo_lb.setText("비밀번호 설정");
						regularMenuGroup_P.setVisible(true);
						regular_btn10.setEnabled(true);
						regular_btn20.setEnabled(true);
						regular_btn30.setEnabled(true);
					} else if (((JButton) (e.getSource())).getBackground().equals(Color.DARK_GRAY)) {
						choiceCheck_lb.setText("");
						regularMenuGroup_P.setVisible(false);
						System.out.println(dayChoice + "정기 검은거 샌택");
						memberInfo_lb.setText("비밀번호 입력");
						setNick.setText(choicelocker);
						pw_Field.setEditable(false);
						String info = dao.readinfo(Integer.parseInt(lockerNum));
						pw_Field.setText(info.substring(0, info.indexOf(",")));// read pw
						setNick.setText(info.substring(info.indexOf(",") + 1));// read nick
						popup.setVisible(true);
					}
				} else {
					Oneday_btn00.setVisible(true);
					Oneday_btn00.setEnabled(true);
					setNick.setText(choicelocker);
				}
			});
			if (i <= bound_num) {
				regularLocker_arr.add(jb);
			} else {
				OnedayLocker_arr.add(jb);
			}
		}
		LockerUpdate();
		
	}

	public void LockerUpdate() {
		//락커버튼 갱신해주는곳?????
		ArrayList<CustomerDTO> lockerUpdate_1 = new ArrayList<CustomerDTO>();
		lockerUpdate_1 = new DAO().lockerUpdateArr();
		
		for(int i = 0; i < regularLocker_arr.size(); i++) {
			//정기 싹다 흰색/눌림
			regularLocker_arr.get(i).setBackground(Color.WHITE);
			regularLocker_arr.get(i).setForeground(Color.black);
			regularLocker_arr.get(i).setEnabled(true);
		}
		
		for(int i = 0; i<OnedayLocker_arr.size(); i++) {
			//일일 싹다 흰색/눌림
			OnedayLocker_arr.get(i).setBackground(Color.WHITE);
			OnedayLocker_arr.get(i).setForeground(Color.black);
			OnedayLocker_arr.get(i).setEnabled(true);
		}
		
		for(int i = 0; i<lockerUpdate_1.size(); i++) {
			int useLockerNum = lockerUpdate_1.get(i).locker;
			String usingTime = lockerUpdate_1.get(i).timein+"";
			String usedTime = lockerUpdate_1.get(i).timeout+"";
			
			System.out.println("락커 : "+useLockerNum);
			System.out.println("타임인 : "+usingTime);
			System.out.println("타임아웃 : "+usedTime);
			
			if(usingTime.equals("null")&&usedTime.equals("null")) {
				//정기 신규가입후 집간거 버튼회색/눌림
				//정기
				regularLocker_arr.get(useLockerNum-1).setBackground(Color.DARK_GRAY);
	
			}
			else if((!usingTime.equals("null"))&&usedTime.equals("null")) {
				//정기/신규 입장한거 버튼회색/안눌림
				if(useLockerNum<=10) {
					//정기
					regularLocker_arr.get(useLockerNum-1).setBackground(Color.DARK_GRAY);
					regularLocker_arr.get(useLockerNum-1).setForeground(Color.black);
					regularLocker_arr.get(useLockerNum-1).setEnabled(false);
				}
				else {
					//일일
					OnedayLocker_arr.get(useLockerNum-11).setBackground(Color.DARK_GRAY);
					OnedayLocker_arr.get(useLockerNum-11).setForeground(Color.black);
					OnedayLocker_arr.get(useLockerNum-11).setEnabled(false);
				}
				
			}
			else if((!usingTime.equals("null"))&&(!usedTime.equals("null"))) {
				//정기 나간거 버튼회색/ 눌림
				regularLocker_arr.get(useLockerNum-1).setBackground(Color.DARK_GRAY);
				regularLocker_arr.get(useLockerNum-1).setForeground(Color.white);
			}
			
		}
		
	}

	public Disgin() {
		
		// 메인 프레임
		enter_F = new JFrame("입장 키오스크");
		enter_F.getContentPane().setBackground(Color.WHITE);
		enter_F.setResizable(false);
		enter_F.setBounds(100, 0, 960, 800);
		enter_F.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		enter_F.getContentPane().setLayout(null);

		// 프레임안에 라벨////////////////
		main_lb = new JLabel("YOU MOK MIN");
		main_lb.setHorizontalTextPosition(SwingConstants.CENTER);
		main_lb.setHorizontalAlignment(SwingConstants.CENTER);
		main_lb.setAlignmentX(Component.CENTER_ALIGNMENT);
		main_lb.setFont(new Font("나눔고딕", Font.BOLD, 40));
		main_lb.setForeground(Color.BLACK);
		main_lb.setBounds(86, 10, 309, 65);
		enter_F.getContentPane().add(main_lb);
		// --------------라커생성
		makeLocker();
		// 판넬 깔아둘 모니터(카드 레이아웃) 판넬
		JPanel Monitor_P = new JPanel();
		Monitor_P.setBounds(12, 85, 922, 668);
		enter_F.getContentPane().add(Monitor_P);
		CardLayout MonitorCard = new CardLayout();
		Monitor_P.setLayout(MonitorCard);

		JButton Previous_btn = new JButton("이전 화면");
		Previous_btn.setBorderPainted(false);
		Previous_btn.setFont(new Font("나눔고딕", Font.BOLD, 20));
		Previous_btn.setBackground(Color.WHITE);
		Previous_btn.addActionListener(e -> {
			popup.setVisible(false);
			pw_Field.setText("");
			pwCheck_Field.setText("");
			setNick.setText("");
			Choiced_Ticket = 0;
			dayChoice = 0;
			MonitorCard.show(Monitor_P, "메인");
			Previous_btn.setVisible(false);
			oneday_Scroll.setVisible(false);
			Oneday_btn00.setVisible(false);
			Oneday_btn00.setEnabled(false);
			setNick.setEditable(true);
			regular_btn10.setEnabled(false);
			regular_btn20.setEnabled(false);
			regular_btn30.setEnabled(false);
			choiceCheck_lb.setVisible(false);
			choiceCheck_lb.setText(null);
			regular_Scroll.setVisible(false);
			regularMenuGroup_P.setVisible(false);
		});
		Previous_btn.setVisible(false);
		Previous_btn.setBounds(762, 20, 140, 48);
		enter_F.getContentPane().add(Previous_btn);

		// ------------------메인 선택 화면
		SelectMain_P = new JPanel();
		SelectMain_P.setLayout(null);
		SelectMain_P.setBackground(Color.WHITE);
		Monitor_P.add(SelectMain_P, "메인");
		
		//일일락커 선택 버튼=================================
		oneday_btn = new JButton("일일");
		oneday_btn.setBorderPainted(false);
		oneday_btn.setFocusPainted(false);
		oneday_btn.setForeground(Color.BLACK);
		oneday_btn.setHorizontalTextPosition(SwingConstants.CENTER);
		oneday_btn.setBackground(Color.WHITE);
		oneday_btn.setFont(new Font("궁서", Font.BOLD, 80));
		oneday_btn.addActionListener(e -> {
			dao.ddayCheck();//기간지나면 락커번호 날리는 dao문
			LockerUpdate();
			MonitorCard.show(Monitor_P, "라커룸");
			menu_lb.setText("<html>----가격표----<br/>\r\n" + "<br/>[   일일 이용권 가격   ]\r\n" + "<br/> 	7000원  ");
			menu_lb.setForeground(Color.black);
			menu_lb.setFont(new Font("나눔고딕", Font.BOLD, 30));
			Previous_btn.setVisible(true);
			choiceCheck_lb.setVisible(true);
			oneday_Scroll.setVisible(true);
			memberInfo_lb.setText("비밀번호 설정");
			setNick.setEditable(false);
			for (JButton jButton : OnedayLocker_arr) {
				oneday_P.add(jButton);
				dayChoice = 0;
			}

		});
		oneday_btn.setBounds(29, 26, 273, 155);
		SelectMain_P.add(oneday_btn);
		
		//정기락커 선택 버튼=================================
		regular_btn = new JButton("정기");
		regular_btn.setBorderPainted(false);
		regular_btn.setFocusPainted(false);
		regular_btn.setForeground(Color.BLACK);
		regular_btn.setBackground(Color.WHITE);
		regular_btn.setFont(new Font("궁서", Font.BOLD, 80));
		regular_btn.addActionListener(e -> {
			dao.ddayCheck();//기간지나면 락커번호 날리는 dao문
			LockerUpdate();
			MonitorCard.show(Monitor_P, "라커룸");
			menu_lb.setText("<html>-------------가격표-------------<br/>\r\n" + "<br/> [   정기 이용권 가격 및 유의사항   ]\r\n"
					+ "<br/> ※ 10회권 :  63000원 - 3개월 <\r\n" + "<br/> ※ 20회권 : 126000원 - 6개월<\r\n" + "<br/> ※ 30회권 : 189000원 - 9개월 <"
					+"<br/>"
					+"<br/>※만기일에 소유하신 정기권은"+"<br/> 보유 횟수 상관없이 자동 소멸 됩니다.");
			menu_lb.setForeground(Color.black);
			menu_lb.setFont(new Font("나눔고딕", Font.BOLD, 25));
		
			Previous_btn.setVisible(true);
			regular_Scroll.setVisible(true);
			choiceCheck_lb.setVisible(true);
			regularMenuGroup_P.setVisible(true);

			dayChoice = 1;
			for (JButton jButton : regularLocker_arr) {
				regular_P.add(jButton);
			}

		});
		regular_btn.setBounds(29, 291, 273, 165);
		SelectMain_P.add(regular_btn);

		oneday_lb = new JLabel("<html>하루 이용권<br/>가격 : 7,000원");
		oneday_lb.setHorizontalAlignment(SwingConstants.LEFT);
		oneday_lb.setForeground(Color.BLACK);
		oneday_lb.setFont(new Font("한컴 말랑말랑 Bold", Font.PLAIN, 40));
		oneday_lb.setBounds(353, 52, 536, 102);
		SelectMain_P.add(oneday_lb);

		regular_lb = new JLabel("<html>10회/20회/30회권으로\r\n<br/>구성된 정기 이용권 입니다.");
		regular_lb.setHorizontalAlignment(SwingConstants.LEFT);
		regular_lb.setForeground(Color.BLACK);
		regular_lb.setFont(new Font("한컴 말랑말랑 Bold", Font.PLAIN, 40));
		regular_lb.setBounds(353, 213, 536, 148);
		SelectMain_P.add(regular_lb);
		
		JLabel waringMsg_lb = new JLabel("<html>※ 입장시간으로 부터 24시간이 경과할 경우 추가요금이 발생할 수 있습니다.\r\n<br/>* 일일권 : 추가 결제\r\n<br/>* 정기권 : 횟수 차감");
		waringMsg_lb.setHorizontalAlignment(SwingConstants.CENTER);
		waringMsg_lb.setFont(new Font("굴림", Font.BOLD, 20));
		waringMsg_lb.setForeground(Color.RED);
		waringMsg_lb.setBounds(29, 553, 860, 105);
		SelectMain_P.add(waringMsg_lb);
		
		JLabel lblNewLabel_2 = new JLabel("<html>※정기권 주의사항※\r\n<br/>\r\n<br/>-개인락커로 사용하실 수 있습니다.\r\n"
				+ "<br/>-각 정기권에는 만기일이 있습니다.\r\n"
				+ "<br/>-만기일에 소유하신 정기권은 보유 횟수 상관없이 자동 소멸 됩니다.\r\n"
				+ "<br/>=================================================================\r\n"
				+ "<br/>10회권 - 3개월   \r"
				+ "20회권 - 6개월   \r"
				+ "30회권 - 9개월");
		lblNewLabel_2.setFont(new Font("나눔고딕", Font.BOLD, 18));
		lblNewLabel_2.setBounds(353, 369, 536, 174);
		SelectMain_P.add(lblNewLabel_2);

		locker_P = new JPanel();
		locker_P.setBackground(Color.WHITE);
		locker_P.setLayout(null);
		locker_P.setBounds(0, 0, 786, 477);
		Monitor_P.add(locker_P, "라커룸");

		// ----------목욕 티켓가격
		menu_lb = new JLabel();
		menu_lb.setOpaque(true);
		menu_lb.setBackground(Color.WHITE);
		menu_lb.setHorizontalAlignment(SwingConstants.CENTER);
		menu_lb.setBounds(22, 322, 469, 313);
		locker_P.add(menu_lb);
		menu_lb.setFont(new Font("굴림", Font.PLAIN, 30));

		// -----------정기권 선택 여부 확인 라벨
		choiceCheck_lb = new JLabel("");
		choiceCheck_lb.setHorizontalAlignment(SwingConstants.CENTER);
		choiceCheck_lb.setForeground(Color.RED);
		choiceCheck_lb.setFont(new Font("굴림", Font.BOLD, 25));
		choiceCheck_lb.setBounds(542, 367, 324, 57);
		choiceCheck_lb.setVisible(false);
		locker_P.add(choiceCheck_lb);

		// -----------일일권 선택 버튼
		Oneday_btn00 = new JButton("일일 이용권");
		Oneday_btn00.setFont(new Font("나눔고딕", Font.BOLD, 25));
		Oneday_btn00.setBackground(Color.WHITE);
		Oneday_btn00.setVisible(false);
		Oneday_btn00.setBounds(542, 509, 332, 57);
		Oneday_btn00.addActionListener(e -> {
			popup.setVisible(true);
			choiceCheck_lb.setText("");
			Oneday_btn00.setVisible(false);
			Choiced_Ticket = 0;
			chioceMenuID = 100;
		});
		locker_P.add(Oneday_btn00);

		// -----------정기권 버튼 모음
		regularMenuGroup_P = new JPanel();
		regularMenuGroup_P.setBounds(542, 453, 332, 173);
		locker_P.add(regularMenuGroup_P);
		regularMenuGroup_P.setLayout(new GridLayout(0, 1, 0, 0));
		regularMenuGroup_P.setVisible(false);
		// --------------------------------------------
		regular_btn10 = new JButton("10일 정기이용권");
		regular_btn10.setLocation(611, 1);
		regular_btn10.setFont(new Font("나눔고딕", Font.BOLD, 25));
		regular_btn10.setBackground(Color.WHITE);
		regular_btn10.setEnabled(false);
		regular_btn10.addActionListener(e -> {//여기서 D-day설정 해주면 될꺼 같은데
			Choiced_Ticket = 10; //n권
			System.out.println(Choiced_Ticket);
			popup.setVisible(true);
			choiceCheck_lb.setText("");
			regularMenuGroup_P.setVisible(false);
			chioceMenuID = 101; //menuDB의 id이다.
			price = "63000"; 
		});
		regularMenuGroup_P.add(regular_btn10);
		// --------------------------------------------
		regular_btn20 = new JButton("20일 정기이용권");
		regular_btn20.setFont(new Font("나눔고딕", Font.BOLD, 25));
		regular_btn20.setBackground(Color.WHITE);
		regular_btn20.setLocation(611, 31);
		regular_btn20.setEnabled(false);
		regular_btn20.addActionListener(e -> {
			Choiced_Ticket = 20;
			System.out.println(Choiced_Ticket);
			popup.setVisible(true);
			choiceCheck_lb.setText("");
			regularMenuGroup_P.setVisible(false);
			chioceMenuID = 102;
			price = "126000";
		});
		regularMenuGroup_P.add(regular_btn20);
		// --------------------------------------------
		regular_btn30 = new JButton("30일 정기이용권");
		regular_btn30.setLocation(611, 61);
		regular_btn30.setFont(new Font("나눔고딕", Font.BOLD, 25));
		regular_btn30.setBackground(Color.WHITE);
		regular_btn30.setEnabled(false);
		regular_btn30.addActionListener(e -> {
			Choiced_Ticket = 30;
			System.out.println(Choiced_Ticket);
			popup.setVisible(true);
			choiceCheck_lb.setText("");
			regularMenuGroup_P.setVisible(false);
			chioceMenuID = 103;
			price = "189000";
		});
		regularMenuGroup_P.add(regular_btn30);

		// -------------라커 판넬
		oneday_Scroll = new JScrollPane();
		oneday_Scroll.setBounds(0, 58, 922, 240);
		locker_P.add(oneday_Scroll);
		oneday_P = new JPanel();
		oneday_P.setBackground(Color.WHITE);
		oneday_Scroll.setViewportView(oneday_P);
		oneday_Scroll.setVisible(false);
		oneday_P.setLayout(new GridLayout(3, 5, 5, 5));

		regular_Scroll = new JScrollPane();
		regular_Scroll.setBounds(0, 58, 922, 195);
		locker_P.add(regular_Scroll);
		regular_P = new JPanel();
		regular_P.setBackground(Color.WHITE);
		regular_Scroll.setViewportView(regular_P);
		regular_Scroll.setVisible(false);
		regular_P.setLayout(new GridLayout(2, 5, 5, 5));
		
		lblNewLabel = new JLabel("New label");
		lblNewLabel.setIcon(new ImageIcon("ImageCollection/newTitleImg.png"));
		lblNewLabel.setBounds(383, 18, 563, 48);
		enter_F.getContentPane().add(lblNewLabel);
		
		lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setIcon(new ImageIcon("ImageCollection/newTitleImg.png"));
		lblNewLabel_1.setBounds(0, 16, 100, 48);
		enter_F.getContentPane().add(lblNewLabel_1);

		// ----------------비번설정+닉설정 / 비번 입력
		popup = new JDialog();
		popup.setBounds(850, 0, 450, 600);
		popup.setResizable(false);
		popup.setLocationRelativeTo(null);
		CardLayout popupCard = new CardLayout();
		popup.setLayout(popupCard);
		
		// 일일 팝업 : 비번설정 
		// 정기 팝업 : 비번설정+닉설정 / 비번 입력
		JPanel memberInfo_P = new JPanel();
		memberInfo_P.setLayout(null);
		memberInfo_P.setBounds(0, 0, 486, 363);
		popup.add(memberInfo_P, "회원 팝업");

		memberInfo_lb = new JLabel("");
		memberInfo_lb.setFont(new Font("나눔고딕", Font.BOLD, 40));
		memberInfo_lb.setBounds(100, 10, 300, 58);
		memberInfo_P.add(memberInfo_lb);
		//--------------------
		Nick_lockerNum_lb = new JLabel("");
		Nick_lockerNum_lb.setFont(new Font("나눔고딕", Font.BOLD, 30));
		Nick_lockerNum_lb.setHorizontalAlignment(SwingConstants.LEFT);
		Nick_lockerNum_lb.setBounds(50, 75, 300, 28);
		memberInfo_P.add(Nick_lockerNum_lb);
		//--------------------
		JPanel pw_p = new JPanel();
		pw_p.setBounds(50, 120, 400, 40);
		memberInfo_P.add(pw_p);
		pw_p.setLayout(null);

		JLabel pw_lb = new JLabel("PW");
		pw_lb.setFont(new Font("나눔고딕", Font.BOLD, 30));
		pw_lb.setBounds(0, 0, 150, 38);
		pw_p.add(pw_lb);

		pw_Field = new JPasswordField();
		pw_Field.setFont(new Font("나눔고딕", Font.BOLD, 25));
		pw_Field.setHorizontalAlignment(SwingConstants.CENTER);
		pw_Field.setBounds(170, 0, 159, 38);
		pw_p.add(pw_Field);
		pw_Field.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				actionCheck=true;
			}
		});
		//--------------------	
		JPanel pwCheck_p = new JPanel();
		pwCheck_p.setLayout(null);
		pwCheck_p.setBounds(50, 165, 400, 40);
		memberInfo_P.add(pwCheck_p);

		JLabel pwCheck_lb = new JLabel("PW Check");
		pwCheck_lb.setFont(new Font("나눔고딕", Font.BOLD, 30));
		pwCheck_lb.setBounds(0, 0, 150, 38);
		pwCheck_p.add(pwCheck_lb);

		pwCheck_Field = new JPasswordField();
		pwCheck_Field.setFont(new Font("나눔고딕", Font.BOLD, 25));
		pwCheck_Field.setHorizontalAlignment(SwingConstants.CENTER);
		pwCheck_Field.setBounds(170, 0, 159, 38);
		pwCheck_p.add(pwCheck_Field);
		pwCheck_Field.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				actionCheck=false;
			}
		});
		//--------------------
		JPanel setNick_p = new JPanel();
		setNick_p.setLayout(null);
		setNick_p.setBounds(50, 210, 400, 40);
		memberInfo_P.add(setNick_p);

		JLabel setNick_lb = new JLabel("NickName");
		setNick_lb.setFont(new Font("나눔고딕", Font.BOLD, 30));
		setNick_lb.setBounds(0, 0, 150, 38);
		setNick_p.add(setNick_lb);

		setNick = new JFormattedTextField();
		setNick.setFont(new Font("나눔고딕", Font.BOLD, 25));
		setNick.setBounds(170, 0, 159, 38);
		setNick_p.add(setNick);
		//------각종 비밀번호 오류처리 메세지----------------------
		JLabel errorMessage_lb = new JLabel("");
		errorMessage_lb.setFont(new Font("굴림", Font.BOLD, 15));
		errorMessage_lb.setForeground(Color.RED);
		errorMessage_lb.setHorizontalTextPosition(SwingConstants.CENTER);
		errorMessage_lb.setHorizontalAlignment(SwingConstants.CENTER);
		errorMessage_lb.setBounds(10, 480, 300, 60);
		memberInfo_P.add(errorMessage_lb);
		
		//---키패드추가-----------------------------------------
		JPanel keyPad_p = new JPanel();
		keyPad_p.setLayout(new GridLayout(0, 3, 0, 0));
		
		keyPad_p.setBounds(65, 270, 300, 200);
		memberInfo_P.add(keyPad_p);
		String [] numArr = {"1","2","3","4","5","6","7","8","9","<-","0","삭제"};
		for(int i=0; i<numArr.length; i++) {
			numberBtn = new JButton(numArr[i]);
			numberBtn.setFont(new Font("나눔고딕", Font.BOLD, 20));
			numberBtn.setBackground(Color.WHITE);
			keyPad_p.add(numberBtn);
			numberBtn.addActionListener(new ActionListener() {
				
				//키패트버튼 액션리스너@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@Override
				public void actionPerformed(ActionEvent e) {
					String choiceNum = ((JButton) (e.getSource())).getText();
					
					//첫번째 비밀번호 입력칸이 활성화 됬을때---------------------------------------------------------------
					if(actionCheck==true) {
						if(choiceNum.equals("1")||choiceNum.equals("2")||choiceNum.equals("3")
								||choiceNum.equals("4")||choiceNum.equals("5")||choiceNum.equals("6")
								||choiceNum.equals("7")||choiceNum.equals("8")||choiceNum.equals("9")||choiceNum.equals("0")) {
							System.out.println("======첫번째칸 활성화======");
							System.out.println(choiceNum);
							pw_Field.setText(pw_Field.getText()+choiceNum);
							firstPwIn += choiceNum;
							System.out.println("1차 결과 : "+firstPwIn);
						}
						else if(choiceNum.equals("<-")) {
							System.out.println("한칸 지움");
							firstPwIn = firstPwIn.substring(0, firstPwIn.length()-1);
							pw_Field.setText(firstPwIn);
							System.out.println("1차 결과 : "+firstPwIn);
						}
						else if(choiceNum.equals("전체삭제")) { //전체 삭제
							System.out.println("전체 지움");
							pw_Field.setText("");
							firstPwIn=pw_Field.getText();
							System.out.println("1차 결과 : "+firstPwIn);
						}
					}
					
					//두번째 비밀번호 입력칸이 활성화 됬을때--------------------------------------------------------------
					else if(actionCheck==false) {
						if(choiceNum.equals("1")||choiceNum.equals("2")||choiceNum.equals("3")
								||choiceNum.equals("4")||choiceNum.equals("5")||choiceNum.equals("6")
								||choiceNum.equals("7")||choiceNum.equals("8")||choiceNum.equals("9")||choiceNum.equals("0")) {
							System.out.println("======두번째칸 활성화======");
							System.out.println(choiceNum);
							pwCheck_Field.setText(pwCheck_Field.getText()+choiceNum);
							SecondPwIn += choiceNum;
							System.out.println("2차 결과 : "+SecondPwIn);
						}
						else if(choiceNum.equals("<-")) {
							System.out.println("한칸 지움");
							SecondPwIn = SecondPwIn.substring(0, SecondPwIn.length()-1);
							pwCheck_Field.setText(SecondPwIn);
							System.out.println("2차 결과 : "+SecondPwIn);
						}
						else if(choiceNum.equals("전체삭제")) { //전체 삭제
							System.out.println("전체 지움");
							pwCheck_Field.setText("");
							SecondPwIn=pwCheck_Field.getText();
							System.out.println("2차 결과 : "+SecondPwIn);
						}
					}
					
				}
			});
		}
		
		//--비밀번호 누르고 입장/가입할때---------------------------------------------------
		JButton Confirm_btn = new JButton("확인");
		Confirm_btn.setFont(new Font("나눔고딕", Font.BOLD, 20));
		Confirm_btn.addActionListener(e -> {
			if (Pattern.matches("^[0-9]{4,6}$", pw_Field.getText())&& Pattern.matches("^[0-9]{4,6}$", pwCheck_Field.getText())) {
				if (pw_Field.getText().equals(pwCheck_Field.getText())) {
					System.out.println("pw----------->" + pwCheck_Field.getText());
					dto.setLocker(Integer.parseInt(lockerNum));
					dto.setPassword("" + pwCheck_Field.getText());
					dto.setLeftcount(Choiced_Ticket);
					System.out.println(Choiced_Ticket);
					dto.setSubscribe(dayChoice);
					System.out.println(dayChoice);
					dto.setNickname(setNick.getText());
					//일반회원일때
					if(dto.getSubscribe()==0 && dto.getLeftcount()==0) {
						//바로 customerDB에 보내기
						dao.write(dto);//->여기서 D-day도 설정해줘야 할 듯
						dao.entrancePay(chioceMenuID, lockerNum);
					}
					//정기회원일때
					else {
						System.out.println(dto.getSubscribe()+"/$$$/"+dto.getLeftcount());
						//정기회원이면서 기존회원입장일때
						if(dto.getSubscribe()==1 && dto.getLeftcount()==0) {
							//기존정기고객 정보 갱신
							System.out.println("기존정기회원입니다. / 추가안함");
							System.out.println(lockerNum);
							dao.subscribeTF(lockerNum);
							popUpUserInfo.setVisible(true);
							memberInfo = new DAO().memberinfoList(Integer.parseInt(lockerNum));
							userInfo = memberInfo.split(",");
							userLockerNum_Label.setText(userInfo[0]);//락커번호 불러오기
							userLeftCount_Label.setText((Integer.parseInt(userInfo[1])-1)+""+"회");//남은 정기권횟수 불러오기
							userdDay_Label.setText(userInfo[2]);//만기일 불러오기
						}
						//정기회원인데 신규가입이라서 customerDB에 timein을 NULL로 입력해줘야 할때
						else {
							System.out.println("진입");
							confirmPopUp.setVisible(true);
						}
					}
					popup.setVisible(false);
					pw_Field.setText("");
					pwCheck_Field.setText("");
					setNick.setText("");
					Choiced_Ticket = 0;
					dayChoice = 0;
					errorMessage_lb.setText("");
					Confirm_btn.setEnabled(false);
					MonitorCard.show(Monitor_P, "메인");
					Previous_btn.setVisible(false);
					oneday_Scroll.setVisible(false);
					Oneday_btn00.setVisible(false);
					Oneday_btn00.setEnabled(false);
					regular_btn10.setEnabled(false);
					regular_btn20.setEnabled(false);
					regular_btn30.setEnabled(false);
					choiceCheck_lb.setVisible(false);
					choiceCheck_lb.setText(null);
					setNick.setEditable(true);
					regular_Scroll.setVisible(false);
					regularMenuGroup_P.setVisible(false);
					Confirm_btn.setEnabled(true);
					pw_Field.setText("");
					pwCheck_Field.setText("");
					firstPwIn="";
					SecondPwIn="";
				}
				else {
					errorMessage_lb.setText("PW가 서로 다릅니다.");
				}
			}
			else {
				errorMessage_lb.setText("<html>PW는 숫자만 가능합니다.<br/> 4자리~6자리만가능합니다.");
			}
		});
		
		//=기존회원입장시 알림메세지===============================================
		popUpUserInfo = new JDialog();
		popUpUserInfo.setBounds(0, 0, 400, 600);
		popUpUserInfo.setResizable(false);
		popUpUserInfo.setLocationRelativeTo(null);
		popUpUserInfo.setLayout(null);
		
		lockerNumber_Label = new JLabel("락커번호 : ");
		lockerNumber_Label.setHorizontalAlignment(SwingConstants.CENTER);
		lockerNumber_Label.setFont(new Font("굴림", Font.BOLD, 20));
		lockerNumber_Label.setBounds(12, 145, 112, 37);
		popUpUserInfo.add(lockerNumber_Label);
		
		leftCount_Label = new JLabel("남은 횟수 : ");
		leftCount_Label.setFont(new Font("굴림", Font.BOLD, 20));
		leftCount_Label.setHorizontalAlignment(SwingConstants.CENTER);
		leftCount_Label.setBounds(12, 192, 112, 37);
		popUpUserInfo.add(leftCount_Label);
		
		dDayCount_Label = new JLabel("회원권 만기일 : ");
		dDayCount_Label.setFont(new Font("굴림", Font.BOLD, 20));
		dDayCount_Label.setHorizontalAlignment(SwingConstants.CENTER);
		dDayCount_Label.setBounds(12, 239, 150, 37);
		popUpUserInfo.add(dDayCount_Label);
		
		waringMsg_Label = new JLabel("<html><body style='text-align:center;'>정기 회원권은 만기일이 지나면 자동 소멸 됩니다.<br/>\r\n<br/>만기일에 주의해주세요.</body></html>");
		waringMsg_Label.setForeground(Color.RED);
		waringMsg_Label.setFont(new Font("굴림", Font.PLAIN, 14));
		waringMsg_Label.setBounds(12, 326, 362, 82);
		waringMsg_Label.setHorizontalAlignment(JLabel.CENTER);
		popUpUserInfo.add(waringMsg_Label);
	
		title_Label = new JLabel("회원정보");
		title_Label.setFont(new Font("나눔고딕", Font.BOLD, 30));
		title_Label.setHorizontalAlignment(SwingConstants.CENTER);
		title_Label.setBounds(12, 24, 362, 75);
		popUpUserInfo.add(title_Label);
		
		userLockerNum_Label = new JLabel("");
		userLockerNum_Label.setFont(new Font("나눔고딕", Font.BOLD, 20));
		userLockerNum_Label.setBounds(136, 145, 93, 37);
		popUpUserInfo.add(userLockerNum_Label);
		
		userLeftCount_Label = new JLabel("");
		userLeftCount_Label.setBounds(136, 192, 93, 37);
		userLeftCount_Label.setFont(new Font("나눔고딕", Font.BOLD, 20));
		popUpUserInfo.add(userLeftCount_Label);
		
		userdDay_Label = new JLabel("");
		userdDay_Label.setBounds(174, 239, 150, 37);
		userdDay_Label.setFont(new Font("나눔고딕", Font.BOLD, 20));
		popUpUserInfo.add(userdDay_Label);
		
		confirm_Button = new JButton("확인");
		confirm_Button.setBounds(138, 499, 91, 50);
		confirm_Button.setFont(new Font("나눔고딕", Font.BOLD, 20));
		popUpUserInfo.add(confirm_Button);
		confirm_Button.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				popUpUserInfo.setVisible(false);
			}
		});
		
		//=신규가입후 입장물어보는=========================================================
		confirmPopUp = new JDialog();
		confirmPopUp.setBounds(0, 0, 400, 240);
		confirmPopUp.setResizable(false);
		confirmPopUp.setLocationRelativeTo(null);
		confirmPopUp.setLayout(null);
		
		msG_Label = new JLabel("입장하시겠습니까?");
		msG_Label.setFont(new Font("나눔고딕", Font.BOLD, 30));
		msG_Label.setHorizontalAlignment(SwingConstants.CENTER);
		msG_Label.setBounds(12, 10, 362, 75);
		confirmPopUp.add(msG_Label);
		
		chk_Button = new JButton("확인");
		chk_Button.setBounds(38, 141, 121, 46);
		confirmPopUp.add(chk_Button);
		chk_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dao.write(dto);
				dao.entrancePay(chioceMenuID, lockerNum);
				confirmPopUp.setVisible(false);
			}
		});
		
		cancel_Button = new JButton("아니오");
		cancel_Button.setBounds(224, 141, 121, 46);
		confirmPopUp.add(cancel_Button);
		cancel_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {	
//				dao.정기신규회원 cutomerDB에 timein null로 올리는 sql
				dao.writeFreshCutomer(dto);
				dao.entrancePay(chioceMenuID, lockerNum);
				
				confirmPopUp.setVisible(false);
				
				ticketPayPopUp.setVisible(true);
				memInfo = new DAO().seasonTickeyPay(Integer.parseInt(lockerNum));
				payInfo = memInfo.split(",");
				
				userLockerNumb_Label.setText(payInfo[0]);//락커번호 불러오기
				seasonTicket_Label.setText(payInfo[1]+"회권"); //입력받은 정기권
				dDaycounter_Label.setText(payInfo[2]+"까지");//만기일 불러오기
				seasonTiPri_Label.setText(price+"원"); //입력받은 정기권 가격
					
			}
		});
		
		//=신규정기락커가입시=======================================
		ticketPayPopUp = new JDialog();
		ticketPayPopUp.setBounds(0, 0, 400, 500);
		ticketPayPopUp.setResizable(false);
		ticketPayPopUp.setLocationRelativeTo(null);
		ticketPayPopUp.setLayout(null);
		
		payTitle_Label = new JLabel("정기권 결제");
		payTitle_Label.setFont(new Font("나눔고딕", Font.BOLD, 30));
		payTitle_Label.setHorizontalAlignment(SwingConstants.CENTER);
		payTitle_Label.setBounds(12, 10, 362, 75);
		ticketPayPopUp.add(payTitle_Label);
		
		useLockerNumm_Label = new JLabel("락커번호 : ");
		useLockerNumm_Label.setHorizontalAlignment(SwingConstants.CENTER);
		useLockerNumm_Label.setFont(new Font("나눔고딕", Font.BOLD, 20));
		useLockerNumm_Label.setBounds(46, 110, 94, 46);
		ticketPayPopUp.add(useLockerNumm_Label);
		
		ticketCount_Label = new JLabel("정기권 : ");
		ticketCount_Label.setHorizontalAlignment(SwingConstants.CENTER);
		ticketCount_Label.setFont(new Font("나눔고딕", Font.BOLD, 20));
		ticketCount_Label.setBounds(65, 163, 75, 46);
		ticketPayPopUp.add(ticketCount_Label);
		
		ticketPrice_Label = new JLabel("가   격 : ");
		ticketPrice_Label.setHorizontalAlignment(SwingConstants.CENTER);
		ticketPrice_Label.setFont(new Font("나눔고딕", Font.BOLD, 20));
		ticketPrice_Label.setBounds(65, 275, 75, 46);
		ticketPayPopUp.add(ticketPrice_Label);
		
		dDays_Label = new JLabel("만기일 : ");
		dDays_Label.setHorizontalAlignment(SwingConstants.CENTER);
		dDays_Label.setFont(new Font("나눔고딕", Font.BOLD, 20));
		dDays_Label.setBounds(65, 219, 75, 46);
		ticketPayPopUp.add(dDays_Label);
		
		userLockerNumb_Label = new JLabel("");
		userLockerNumb_Label.setFont(new Font("나눔고딕", Font.BOLD, 20));
		userLockerNumb_Label.setBounds(152, 110, 157, 46);
		ticketPayPopUp.add(userLockerNumb_Label);
		
		seasonTicket_Label = new JLabel("");
		seasonTicket_Label.setFont(new Font("나눔고딕", Font.BOLD, 20));
		seasonTicket_Label.setBounds(152, 163, 157, 46);
		ticketPayPopUp.add(seasonTicket_Label);
		
		dDaycounter_Label = new JLabel("");
		dDaycounter_Label.setFont(new Font("나눔고딕", Font.BOLD, 20));
		dDaycounter_Label.setBounds(152, 219, 157, 46);
		ticketPayPopUp.add(dDaycounter_Label);
		
		seasonTiPri_Label = new JLabel("");
		seasonTiPri_Label.setFont(new Font("나눔고딕", Font.BOLD, 20));
		seasonTiPri_Label.setBounds(152, 275, 157, 46);
		ticketPayPopUp.add(seasonTiPri_Label);
		
		pay_Button = new JButton("결제하기");
		pay_Button.setBounds(46, 376, 121, 46);
		ticketPayPopUp.add(pay_Button);
		pay_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//paymentDB에서 결제정보 "완료로 바꿔주기"
				new DAO().changeTicketPayment(lockerNum);
				ticketPayPopUp.setVisible(false);
				
			}
		});
		
		payCancel_Button = new JButton("취소");
		payCancel_Button.setBounds(203, 376, 121, 46);
		ticketPayPopUp.add(payCancel_Button);
		payCancel_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new DAO().removeClientInfo(lockerNum);
				ticketPayPopUp.setVisible(false);
			}
		});
				
		Confirm_btn.setBounds(270, 490, 95, 36);
		Confirm_btn.setEnabled(true);
		memberInfo_P.add(Confirm_btn);
		
		//--------------------		
		enter_F.setVisible(true);
		enter_F.setLocationRelativeTo(null);
		enter_F.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		new Disgin();
	}
}