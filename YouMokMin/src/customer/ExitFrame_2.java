package customer;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.table.DefaultTableModel;

import db.BathDTO;
import db.DAO;
import db.WaitingCustomer;
import db.BathDTO;
import server.TCPData;

public class ExitFrame_2 {
	
	JFrame frame;
	JTextField lockerNumTextFeild;
	JPasswordField pwField;
	JTable table;
	String[][] tableDB;
	String[] tableColumnName = {"메뉴", "가격", "수량", "총 금액"};
	JLabel resWon, pwIsWrong, leftCountLabel;
	JDialog payDone;
	JButton	numberBtn;
	Boolean actionCheck;
	String lockerNumIn = "";
	String pwNumIn="";
	DAO dao = new DAO();
	BathDTO dto = new BathDTO();
	ExitFrame_2_server serverConnet;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	TCPData data;
	JPanel showExitCardPanel;

	public static void main(String[] args) {
	      String addr = "192.168.150.63";

	      try {
	         Socket socket = new Socket(addr, 8888);
	         System.out.println("퇴장키오스크: 접속 성공");
	         new ExitFrame_2(socket);
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		//new ExitFrame_2();
	}

	public ExitFrame_2(Socket socket) {
		try {
	         oos = new ObjectOutputStream(socket.getOutputStream());
	         ois = new ObjectInputStream(socket.getInputStream());
	         System.out.println("oos, ois 연결 성공");
	         data = new TCPData(null, "서버", "ExitFrame_2", "서버");

	         oos.writeObject(data);
	         oos.flush();
	         oos.reset();
	         System.out.println("oos 보냄");

	      } catch (Exception e1) {

	      }
		//new ExitFrame_2_server();
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 20, 960, 800);
		
		CardLayout exitFrameCard = new CardLayout();
		frame.getContentPane().setLayout(null);
		
		JLabel maintitle_lb = new JLabel("YOU MOK MIN");
		maintitle_lb.setHorizontalAlignment(SwingConstants.CENTER);
		maintitle_lb.setFont(new Font("나눔고딕", Font.BOLD, 40));
		maintitle_lb.setForeground(Color.BLACK);
		maintitle_lb.setBounds(101, 37, 317, 48);
		frame.getContentPane().add(maintitle_lb);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setIcon(new ImageIcon("ImageCollection/newTitleImg.png"));
		lblNewLabel_1.setBounds(0, 37, 118, 48);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("New label");
		lblNewLabel_2.setIcon(new ImageIcon("ImageCollection/newTitleImg.png"));
		lblNewLabel_2.setBounds(414, 37, 532, 48);
		frame.getContentPane().add(lblNewLabel_2);
		
		//카트이벤트 해주는 판넬
		showExitCardPanel = new JPanel();
		showExitCardPanel.setBounds(12, 123, 922, 630);
		frame.getContentPane().add(showExitCardPanel);
		showExitCardPanel.setLayout(exitFrameCard);
		
		//퇴장버튼이 있는 판넬
		JPanel exitStartPanel = new JPanel();
		exitStartPanel.setBackground(Color.WHITE);
		showExitCardPanel.add(exitStartPanel,"exitStartPanel");
//		frame.getContentPane().add(exitStartPanel, "exitStartPanel");
		exitStartPanel.setLayout(null);
		
		JButton ExitButton = new JButton("퇴장하기");
		ExitButton.setBorderPainted(false);
		ExitButton.setBackground(Color.WHITE);
		ExitButton.setFocusPainted(false);
		ExitButton.setFont(new Font("궁서", Font.BOLD, 80));
		ExitButton.setBounds(50, 210, 439, 153);
		//퇴장을 누르면 다음 화면으로 전환
		ExitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exitFrameCard.show(showExitCardPanel, "infoInputPanel");
				lockerNumTextFeild.setText("");
				pwField.setText("");
				pwIsWrong.setVisible(true);
				pwIsWrong.setText("");
			}
		});
		exitStartPanel.add(ExitButton);
		
		JLabel lblNewLabel = new JLabel("<html><body style='text-align:center;'>저희 사우나를<br/>\r\n<br/>이용 해주셔서 감사합니다.<br/>\r\n<br/>보다 더 나은 서비스로<br/>\r\n<br/>보답 하겠습니다.<br/>\r\n<br/>감사합니다.</body></html>\r\n");
		lblNewLabel.setFont(new Font("나눔고딕", Font.BOLD, 30));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(501, 35, 375, 555);
		exitStartPanel.add(lblNewLabel);
		
		
		//사용자 정보입력하는 판넬
		JPanel infoInputPanel = new JPanel();
		infoInputPanel.setBackground(Color.WHITE);
		showExitCardPanel.add(infoInputPanel,"infoInputPanel");
		infoInputPanel.setLayout(null);
		
		JLabel lockerNumLabel = new JLabel("라커 번호");
		lockerNumLabel.setFont(new Font("굴림", Font.BOLD, 30));
		lockerNumLabel.setBounds(260, 21, 167, 45);
		infoInputPanel.add(lockerNumLabel);
		
		JLabel pwLabel = new JLabel("비밀 번호");
		pwLabel.setFont(new Font("굴림", Font.BOLD, 30));
		pwLabel.setBounds(260, 99, 167, 45);
		infoInputPanel.add(pwLabel);
		
		JButton btnNewButton = new JButton("이전");
		btnNewButton.setBackground(Color.WHITE);
		btnNewButton.setFocusPainted(false);
		btnNewButton.setFont(new Font("나눔고딕", Font.BOLD, 20));
		btnNewButton.setBounds(814, 37, 91, 48);
		
		infoInputPanel.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exitFrameCard.show(showExitCardPanel, "exitStartPanel");
			}
		});
		
		//라커번호 입력 받는 곳
		lockerNumTextFeild = new JTextField();
		lockerNumTextFeild.setBounds(410, 23, 222, 45);
		lockerNumTextFeild.setFont(new Font("나눔고딕", Font.BOLD, 20));
		infoInputPanel.add(lockerNumTextFeild);
		lockerNumTextFeild.setColumns(10);
		lockerNumTextFeild.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				actionCheck=true;
			}
		});
		
		//비밀번호 입력 받는 곳
		pwField = new JPasswordField();
		pwField.setBounds(410, 101, 222, 45);
		pwField.setFont(new Font("나눔고딕", Font.BOLD, 20));
		infoInputPanel.add(pwField);
		pwField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				actionCheck=false;
			}
		});
		
		pwIsWrong = new JLabel("");
		pwIsWrong.setHorizontalAlignment(SwingConstants.CENTER);
		pwIsWrong.setFont(new Font("굴림", Font.BOLD, 25));
		pwIsWrong.setBounds(179, 170, 575, 38);
		pwIsWrong.setForeground(Color.RED);
		infoInputPanel.add(pwIsWrong);
		
		//라커번호/pw 넣고 확인버튼누르기
		JButton okButton = new JButton("확인");
		okButton.setBackground(Color.WHITE);
//		okButton.setBorderPainted(false);
		okButton.setFocusPainted(false);
		okButton.setBounds(402, 544, 127, 57);
		okButton.setFont(new Font("나눔고딕", Font.PLAIN, 20));
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DAO dao = new DAO();
				String uLocker = lockerNumTextFeild.getText();
				String uLockerPw = pwField.getText();
				
				int userCheck = dao.userCheck(uLocker, uLockerPw);
				
					if(userCheck==1) { //ID와  PW가 맞을때
						exitFrameCard.show(showExitCardPanel, "payList");
						
						new DAO().customerSplit(Integer.parseInt(lockerNumTextFeild.getText()));
						
						tableDB = new DAO().exitPaymentList(lockerNumTextFeild.getText());
						
						table.setModel(new DefaultTableModel(tableDB, tableColumnName));
						table.getTableHeader().setFont(new Font("나눔고딕", Font.PLAIN, 20));
						table.setFont(new Font("나눔고딕", Font.PLAIN, 20));

						table.setEnabled(false);
						table.setRowHeight(40);

						table.getColumnModel().getColumn(0).setPreferredWidth(85);
						table.getColumnModel().getColumn(1).setPreferredWidth(85);
						table.getColumnModel().getColumn(2).setPreferredWidth(200);
						table.getColumnModel().getColumn(3).setPreferredWidth(144);
						table.setBounds(138, 169, 686, 425);
						
						String res = new DAO().calcTot(lockerNumTextFeild.getText());
						
						if(res==null) {
							res = "0";
						}
						
						resWon.setText(res+" 원");
					}
					else if(uLocker.equals("")){
						pwIsWrong.setText("락커번호를 입력해주세요!");
						pwIsWrong.setForeground(Color.red);
					}
					else if(uLockerPw.equals("")) {
						pwIsWrong.setText("비밀번호를 입력해주세요!");
						pwIsWrong.setForeground(Color.red);
					}
					else if(userCheck==2) {
						pwIsWrong.setText("락커번호 혹은 비밀번호가 옳지 않습니다!");
						pwIsWrong.setForeground(Color.red);
					}
					else if(userCheck==3) {
						pwIsWrong.setText("락커번호 혹은 비밀번호가 옳지 않습니다!");
						pwIsWrong.setForeground(Color.red);
					}
					lockerNumIn = "";
					pwNumIn="";
				
			}
		});
		infoInputPanel.add(okButton);
		
		//키패드 넣을 판넬
		JPanel keyPadPanel = new JPanel();
		keyPadPanel.setBounds(242, 226, 461, 304);
		infoInputPanel.add(keyPadPanel);
		keyPadPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		//키패드 생성
		String [] numArr = {"1","2","3","4","5","6","7","8","9","<-","0","전체삭제"};
		for(int i=0; i<numArr.length; i++) {
			numberBtn = new JButton(numArr[i]);
			keyPadPanel.add(numberBtn);
			numberBtn.setBackground(Color.white);
			numberBtn.setFont(new Font("나눔고딕", Font.BOLD, 20));
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
							lockerNumTextFeild.setText(lockerNumTextFeild.getText()+choiceNum);
							lockerNumIn += choiceNum;
							System.out.println("1차 결과 : "+lockerNumIn);
						}
						else if(choiceNum.equals("<-")) {
							System.out.println("한칸 지움");
							lockerNumIn = lockerNumIn.substring(0, lockerNumIn.length()-1);
							lockerNumTextFeild.setText(lockerNumIn);
							System.out.println("1차 결과 : "+lockerNumIn);
						}
						else if(choiceNum.equals("전체삭제")) { //전체 삭제
							System.out.println("전체 지움");
							lockerNumTextFeild.setText("");
							lockerNumIn=lockerNumTextFeild.getText();
							System.out.println("1차 결과 : "+lockerNumIn);
						}
					}

					//두번째 비밀번호 입력칸이 활성화 됬을때--------------------------------------------------------------
					else if(actionCheck==false) {
						if(choiceNum.equals("1")||choiceNum.equals("2")||choiceNum.equals("3")
								||choiceNum.equals("4")||choiceNum.equals("5")||choiceNum.equals("6")
								||choiceNum.equals("7")||choiceNum.equals("8")||choiceNum.equals("9")||choiceNum.equals("0")) {
							System.out.println("======두번째칸 활성화======");
							System.out.println(choiceNum);
							pwField.setText(pwField.getText()+choiceNum);
							pwNumIn += choiceNum;
							System.out.println("2차 결과 : "+pwNumIn);
						}
						else if(choiceNum.equals("<-")) {
							System.out.println("한칸 지움");
							pwNumIn = pwNumIn.substring(0, pwNumIn.length()-1);
							pwField.setText(pwNumIn);
							System.out.println("2차 결과 : "+pwNumIn);
						}
						else if(choiceNum.equals("전체삭제")) { //전체 삭제
							System.out.println("전체 지움");
							pwField.setText("");
							pwNumIn=pwField.getText();
							System.out.println("2차 결과 : "+pwNumIn);
						}
					}

				}
			});
		}
		
		//매출내역 보여주는 판넬
		JPanel payList = new JPanel();
		payList.setBackground(Color.WHITE);
		showExitCardPanel.add(payList, "payList");
		payList.setLayout(null);
		
		btnNewButton = new JButton("이전");
		btnNewButton.setBackground(Color.WHITE);
		btnNewButton.setFocusPainted(false);
		btnNewButton.setFont(new Font("나눔고딕", Font.BOLD, 20));
		btnNewButton.setBounds(814, 37, 91, 48);
		
		payList.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exitFrameCard.show(showExitCardPanel, "exitStartPanel");
			}
		});
		
		table = new JTable();

		JScrollPane resScroll = new JScrollPane(table);
		resScroll.setPreferredSize(new Dimension(450, 400));
		resScroll.setBounds(79, 121, 792, 334);
		payList.add(resScroll);
		
		JLabel totLabel = new JLabel("총 액 :  ");
		totLabel.setFont(new Font("굴림", Font.PLAIN, 40));
		totLabel.setBounds(179, 515, 168, 63);
		payList.add(totLabel);
		
		resWon = new JLabel(" 원");
		resWon.setFont(new Font("굴림", Font.PLAIN, 40));
		resWon.setBounds(305, 515, 274, 63);
		resWon.setHorizontalAlignment(JLabel.RIGHT);
		payList.add(resWon);
		
		JButton letsPay = new JButton("결제하기");
		letsPay.setBorderPainted(false);
		letsPay.setFocusPainted(false);
		letsPay.setBackground(Color.WHITE);
		letsPay.setFont(new Font("굴림", Font.PLAIN, 30));
		letsPay.setBounds(680, 506, 173, 87);
		letsPay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int lockerNum = Integer.parseInt(lockerNumTextFeild.getText());
				new DAO().subscribeDelete(lockerNum);
				new DAO().sesinWaitingDelete(lockerNum);
				new DAO().barbarWaitingDelete(lockerNum);
				new DAO().paymentDone(Integer.toString(lockerNum));
				payDone.setVisible(true);
				pwIsWrong.setVisible(false);
				System.out.println("결제가 완료되었습니다.");
			}
		});
		payList.add(letsPay);
		
		payDone = new JDialog(frame, "", false);
		payDone.setBounds(500, 300, 200, 100);
		payDone.getContentPane().setLayout(new FlowLayout());
		payDone.getContentPane().add(new JLabel("결제가 완료되었습니다."));
		
		JButton done = new JButton("확인");
		
		done.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				payDone.setVisible(false);
				exitFrameCard.show(showExitCardPanel, "exitStartPanel");
				serverConnet.apmtSender(new WaitingCustomer());
			}
		});
		payDone.getContentPane().add(done);
		
		serverConnet = new ExitFrame_2_server(); //서버통신에 필요한 클래스 호출
		
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}