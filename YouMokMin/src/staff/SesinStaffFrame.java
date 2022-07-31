package staff;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.table.*;

import db.BarberDTO;
import db.DAO;
import db.SesinDTO;
import db.WaitingCustomer;
import server.TCPData;


public class SesinStaffFrame {

	JFrame sesinStaff;
	JTable table;
	String[][] tableDB;
	String[] tableColumnName;
	JLabel resWon, sesinNow, sesinNowStyle1, sesinNowStyle2, sesinNowStyle3, barberConnected, managerConnected;
	SesinDTO sesinOngoing;
	ArrayList<SesinDTO> sesinWaiting;
	ArrayList<WaitingPerson> waitingCustomers;
	JButton sesinChattingSend, acceptButton, sesinSalesSearchButton, callButton, exitButton;
	JRadioButton allRadioButton;
	JCheckBox cutCheckBox, beautyCheckBox;
	JComboBox yearComboBox, monthComboBox;
	JToggleButton sesinSalesDayButton, sesinSalesMonthButton;
	CardLayout cl_sesinSalesMonitor;
	JPanel sesinSalesMonitor, waitingListPanel, waitingPanel;
	String selectedDay;
	JTextField sesinChattingTextField;
	String name;
	JDialog sesinCallCancle, sesinNowDone;
	TCPData data;
	JTextArea sesinChattingTextArea;
	WaitingCustomer wc = new WaitingCustomer();

	public SesinStaffFrame(Socket socket) {

		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			System.out.println("oos, ois 연결 성공");
			data = new TCPData(null, "서버", "SesinStaffFrame", "서버");

			oos.writeObject(data);
			oos.flush();
			oos.reset();
			System.out.println("oos 보냄");

		} catch (IOException e1) {

		}

		sesinStaff = new JFrame();
		sesinStaff.setBounds(100, 20, 960, 800);
		sesinStaff.getContentPane().setBackground(new Color(246, 246, 246));
		sesinStaff.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sesinStaff.getContentPane().setLayout(null);

		ImageIcon background = new ImageIcon("img/sesinBackGround.png");
		JLabel back = new JLabel(background);
		back.setBounds(0, 5, 946, 770);

		JPanel sesinMonitorPanel = new JPanel();
		sesinMonitorPanel.setOpaque(false);
		sesinMonitorPanel.setBounds(0, 0, 946, 659);
		sesinMonitorPanel.setBackground(Color.white);
		sesinStaff.getContentPane().add(sesinMonitorPanel);
		CardLayout sesinMonitorPanelCard = new CardLayout();
		sesinMonitorPanel.setLayout(sesinMonitorPanelCard);

		JPanel sesinButtonPanel = new JPanel();
		sesinButtonPanel.setBounds(42, 665, 856, 68);
		sesinButtonPanel.setBackground(Color.white);
		sesinStaff.getContentPane().add(sesinButtonPanel);
		sesinButtonPanel.setLayout(new GridLayout(1, 3, 20, 20));

		JButton sesinWaitingButton = new JButton("대기 현황");
		sesinWaitingButton.setFont(new Font("나눔고딕", Font.BOLD, 30));
		sesinWaitingButton.setBackground(new Color(53, 53, 53));
		sesinWaitingButton.setForeground(Color.white);
		sesinWaitingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sesinMonitorPanelCard.show(sesinMonitorPanel, "SesinWaiting");
			}
		});
		
		sesinButtonPanel.add(sesinWaitingButton);

		JButton sesinSalesButton = new JButton("이용 내역");
		sesinSalesButton.setFont(new Font("나눔고딕", Font.BOLD, 30));
		sesinSalesButton.setBackground(new Color(53, 53, 53));
		sesinSalesButton.setForeground(Color.white);
		sesinSalesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sesinMonitorPanelCard.show(sesinMonitorPanel, "SesinSales");
			}
		});
		sesinButtonPanel.add(sesinSalesButton);

		JButton sesinChattingButton = new JButton("채팅");
		sesinChattingButton.setFont(new Font("나눔고딕", Font.BOLD, 30));
		sesinChattingButton.setForeground(Color.white);
		sesinChattingButton.setBackground(new Color(53, 53, 53));
		sesinChattingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sesinMonitorPanelCard.show(sesinMonitorPanel, "SesinChatting");
			}
		});
		sesinButtonPanel.add(sesinChattingButton);



		// 대기 현황

		JPanel sesinWaitingList = new JPanel();
		sesinWaitingList.setOpaque(false);
		sesinWaitingList.setBackground(new Color(246, 246, 246));
		sesinMonitorPanel.add(sesinWaitingList, "SesinWaiting");
		sesinWaitingList.setLayout(null);

		waitingPanel = new JPanel();
		waitingPanel.setPreferredSize(new Dimension(700, 1250));
		waitingPanel.setBackground(Color.white);
		waitingPanel.setLayout(null);

		JScrollPane waitingScroll = new JScrollPane(waitingPanel);
		waitingScroll.setBorder(null);
		waitingScroll.setBounds(49, 42, 850, 590);
		waitingScroll.getVerticalScrollBar().setUnitIncrement(20);
		sesinWaitingList.add(waitingScroll);

		sesinWaiting = new DAO().sesinWaitingArr();

		sesinNowStyle1 = new JLabel();
		sesinNowStyle1.setFont(new Font("나눔고딕", Font.BOLD, 20));
		sesinNowStyle1.setHorizontalAlignment(SwingConstants.RIGHT);
		sesinNowStyle1.setForeground(Color.white);
		sesinNowStyle1.setBounds(626, 70, 121, 29);
		waitingPanel.add(sesinNowStyle1);

		sesinNowStyle2 = new JLabel();
		sesinNowStyle2.setHorizontalAlignment(SwingConstants.RIGHT);
		sesinNowStyle2.setForeground(Color.white);
		sesinNowStyle2.setFont(new Font("나눔고딕", Font.BOLD, 20));
		sesinNowStyle2.setBounds(626, 105, 121, 29);
		waitingPanel.add(sesinNowStyle2);

		sesinNowStyle3 = new JLabel();
		sesinNowStyle3.setHorizontalAlignment(SwingConstants.RIGHT);
		sesinNowStyle3.setForeground(Color.white);
		sesinNowStyle3.setFont(new Font("나눔고딕", Font.BOLD, 20));
		sesinNowStyle3.setBounds(626, 140, 121, 29);
		waitingPanel.add(sesinNowStyle3);

		sesinNowDone = new JDialog();
		sesinNowDone.getContentPane().setLayout(new FlowLayout());
		JButton nowDoneButton = new JButton("완료");
		nowDoneButton.setPreferredSize(new Dimension(80, 50));
		nowDoneButton.setFont(new Font("나눔고딕", Font.BOLD, 20));
		nowDoneButton.setBackground(new Color(0, 62, 0));
		nowDoneButton.setForeground(Color.white);
		nowDoneButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				wc.id = sesinOngoing.id;
				wc.locker = Integer.parseInt(sesinNow.getText());

				wc.menu = sesinNowStyle1.getText();

				if(!(sesinNowStyle2.getText().equals(""))) {
					wc.menu += ",";
					wc.menu += sesinNowStyle2.getText();
				}

				if(!(sesinNowStyle3.getText().equals(""))) {
					wc.menu += ",";
					wc.menu += sesinNowStyle3.getText();
				}

				wc.state = "완료";

				System.out.println(wc);

				data = new TCPData();
				data.kind = "예약";
				data.from = "SesinStaffFrame";
				data.to = new String[] {"SesinStaffFrame", "BarberStaffFrame", "kiosk_barber", "kiosk_sesin"};
				data.sendObject = wc;

				try {
					oos.writeObject(data);
					oos.flush();
					oos.reset();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				sesinNowDone.setVisible(false);

				ArrayList<String> nowStyle = new ArrayList<String>();
				nowStyle.add(sesinNowStyle1.getText());
				if(!sesinNowStyle2.getText().equals("")) {
					nowStyle.add(sesinNowStyle2.getText());
				}
				if(!sesinNowStyle3.getText().equals("")) {
					nowStyle.add(sesinNowStyle3.getText());
				}

				new DAO().writeToPayment(Integer.parseInt(sesinNow.getText()), nowStyle);
				
				sesinOngoing = null;
			}
		});
		sesinNowDone.getContentPane().add(nowDoneButton);

		sesinNow = new JLabel();
		sesinNow.setFont(new Font("나눔고딕", Font.BOLD, 50));
		sesinNow.setForeground(Color.white);
		sesinNow.setBackground(new Color(0, 60, 0));
		sesinNow.setOpaque(true);
		sesinNow.setHorizontalAlignment(JLabel.CENTER);
		sesinNow.setBounds(57, 50, 733, 143);
		sesinNow.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sesinCallCancle.setVisible(false);
				sesinNowDone.setVisible(false);
				if(!sesinNow.getText().equals("")) {
					sesinNowDone.setBounds(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y, 100, 100);
					sesinNowDone.setVisible(true);
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}

		});
		waitingPanel.add(sesinNow);

		waitingListPanel = new JPanel();
		waitingListPanel.setBounds(57, 210, 733, 1000);
		waitingListPanel.setBackground(Color.white);
		waitingPanel.add(waitingListPanel);
		waitingListPanel.setLayout(new GridLayout(6, 3, 10, 10));

		waitingCustomers = new ArrayList<WaitingPerson>();

		for (int i = 0; i < 18; i++) {
			WaitingPerson wp = new WaitingPerson();
			wp.setBackground(new Color(242, 226, 209));
			wp.setOpaque(true);
			waitingListPanel.add(wp);
			waitingCustomers.add(wp);
		}


		JDialog requestPassword = new JDialog(sesinStaff, "", true);
		requestPassword.setBounds(500, 300, 200, 160);
		requestPassword.getContentPane().setLayout(new FlowLayout());
		JLabel label = new JLabel("비밀번호를 입력하세요.");
		label.setFont(new Font("나눔고딕", Font.BOLD, 13));
		requestPassword.getContentPane().add(label);
		requestPassword.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				exitButton.doClick();
			}
			@Override
			public void windowClosed(WindowEvent e) {
			}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		JPasswordField pwField = new JPasswordField();
		pwField.setPreferredSize(new Dimension(170, 30));
		pwField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				acceptButton.doClick();
			}
		});
		pwField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();

				if (!Character.isDigit(c)) {
					e.consume();
					return;
				}
			}
		});
		requestPassword.getContentPane().add(pwField);
		JLabel message = new JLabel("비밀번호가 틀렸습니다.");
		message.setForeground(new Color(0, 0, 0, 0));
		requestPassword.getContentPane().add(message);
		acceptButton = new JButton("확인");
		acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (new DAO().pwCheck(wc.locker+"", pwField.getText())) {
					data = new TCPData();
					data.from = "SesinStaffFrame";
					data.to = new String[] {"SesinStaffFrame", "BarberStaffFrame", "kiosk_barber", "kiosk_sesin"};
					data.kind = "예약";

					WaitingCustomer wc2 = new WaitingCustomer();
					
					if(!sesinNow.getText().equals("")) {
						wc2.id = sesinOngoing.id;
						wc2.locker = Integer.parseInt(sesinNow.getText());
						wc2.menu = sesinNowStyle1.getText();

						if(!(sesinNowStyle2.getText().equals(""))) {
							wc2.menu += ",";
							wc2.menu += sesinNowStyle2.getText();
						}

						if(!(sesinNowStyle3.getText().equals(""))) {
							wc2.menu += ",";
							wc2.menu += sesinNowStyle3.getText();
						}

						wc2.state = "완료";
						data.sendObject = wc2;
						System.out.println("2)"+wc2.toString());
						try {
							oos.writeObject(data);
							oos.flush();
							oos.reset();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}


						ArrayList<String> nowStyle = new ArrayList<String>();
						nowStyle.add(sesinNowStyle1.getText());
						if(!sesinNowStyle2.getText().equals("")) {
							nowStyle.add(sesinNowStyle2.getText());
						}
						if(!sesinNowStyle3.getText().equals("")) {
							nowStyle.add(sesinNowStyle3.getText());
						}

						new DAO().writeToPayment(Integer.parseInt(sesinNow.getText()), nowStyle);
					}
					
					wc.state = "세신진행중";
					data.sendObject = wc;
					System.out.println("2)"+wc2.toString());
					try {
						oos.writeObject(data);
						oos.flush();
						oos.reset();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					requestPassword.setVisible(false);

				} else {
					message.setForeground(Color.red);
				}
			}
		});
		requestPassword.getContentPane().add(acceptButton);

		exitButton = new JButton("닫기");
		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				wc.state = "대기";
				data.sendObject = wc;
				try {
					oos.writeObject(data);
					oos.flush();
					oos.reset();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				requestPassword.setVisible(false);
			}
		});
		requestPassword.getContentPane().add(exitButton);



		sesinCallCancle = new JDialog();
		sesinCallCancle.setResizable(false);
		sesinCallCancle.getContentPane().setLayout(new FlowLayout());
		callButton = new JButton("호출");
		callButton.setFont(new Font("나눔고딕", Font.BOLD, 20));
		callButton.setPreferredSize(new Dimension(80, 50));
		callButton.setBackground(new Color(0, 62, 0));
		callButton.setForeground(Color.white);
		callButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {

					System.out.println("1)"+wc.toString());
					data.sendObject = wc;
					oos.writeObject(data);
					oos.flush();
					oos.reset();

					sesinCallCancle.setVisible(false);
					requestPassword.setLocation(sesinCallCancle.getX(), sesinCallCancle.getY());
					pwField.setText("");
					message.setForeground(new Color(0, 0, 0, 0));
					requestPassword.setVisible(true);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton cancleButton = new JButton("취소");
		cancleButton.setFont(new Font("나눔고딕", Font.BOLD, 20));
		cancleButton.setPreferredSize(new Dimension(80, 50));
		cancleButton.setBackground(new Color(162, 51, 41));
		cancleButton.setForeground(Color.white);
		cancleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {

					wc.state = "취소";
					data.sendObject = wc;
					System.out.println("3)"+wc.toString());
					oos.writeObject(data);
					oos.flush();
					oos.reset();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				sesinCallCancle.setVisible(false);
			}
		});
		sesinCallCancle.getContentPane().add(callButton);
		sesinCallCancle.getContentPane().add(cancleButton);

		refreshWaitingPanel();


		// 매출
		JPanel sesinSales = new JPanel();
		sesinSales.setOpaque(false);
		sesinSales.setBackground(new Color(246, 246, 246));
		sesinMonitorPanel.add(sesinSales, "SesinSales");
		sesinSales.setLayout(null);

		sesinSalesMonitor = new JPanel();
		sesinSalesMonitor.setBounds(43, 180, 854, 465);
		sesinSales.add(sesinSalesMonitor);
		cl_sesinSalesMonitor = new CardLayout();
		sesinSalesMonitor.setLayout(cl_sesinSalesMonitor);

		sesinSalesDayButton = new JToggleButton("일별");
		sesinSalesDayButton.setFont(new Font("나눔고딕", Font.BOLD, 30));
		sesinSalesDayButton.setBackground(new Color(0, 62, 0));
		sesinSalesDayButton.setForeground(Color.white);
		sesinSalesDayButton.setBounds(43, 48, 423, 62);
		sesinSalesDayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sesinSalesSearchButton.setEnabled(true);
				cl_sesinSalesMonitor.show(sesinSalesMonitor, "SesinSalesBlankPanel");
			}
		});
		sesinSales.add(sesinSalesDayButton);

		sesinSalesMonthButton = new JToggleButton("월별");
		sesinSalesMonthButton.setFont(new Font("나눔고딕", Font.BOLD, 30));
		sesinSalesMonthButton.setBackground(new Color(0, 62, 0));

		sesinSalesMonthButton.setForeground(Color.white);
		sesinSalesMonthButton.setBounds(479, 48, 418, 62);
		sesinSalesMonthButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sesinSalesSearchButton.setEnabled(true);
				cl_sesinSalesMonitor.show(sesinSalesMonitor, "SesinSalesBlankPanel");
			}
		});
		sesinSales.add(sesinSalesMonthButton);

		ButtonGroup bg = new ButtonGroup();
		bg.add(sesinSalesDayButton);
		bg.add(sesinSalesMonthButton);

		Vector<String> year = new Vector<String>();
		year.add("2020");
		year.add("2021");
		year.add("2022");

		yearComboBox = new JComboBox(year);
		yearComboBox.setFont(new Font("나눔고딕", Font.BOLD, 15));
		yearComboBox.setBounds(50, 122, 122, 40);
		yearComboBox.setBackground(Color.white);
		yearComboBox.setSelectedItem("2022");
		sesinSales.add(yearComboBox);

		Vector<String> month = new Vector<String>();
		for (String ss : "01,02,03,04,05,06,07,08,09,10,11,12".split(",")) {
			month.add(ss);
		}

		monthComboBox = new JComboBox(month);
		monthComboBox.setFont(new Font("나눔고딕", Font.BOLD, 15));
		monthComboBox.setBounds(190, 122, 122, 40);
		monthComboBox.setBackground(Color.white);
		monthComboBox.setSelectedItem("04");
		sesinSales.add(monthComboBox);

		JPanel sesinSalesBlankPanel = new JPanel();
		sesinSalesBlankPanel.setBackground(new Color(246, 246, 246));
		sesinSalesMonitor.add(sesinSalesBlankPanel, "SesinSalesBlankPanel");

		JPanel sesinSalesDayPanel = new JPanel();
		sesinSalesMonitor.add(sesinSalesDayPanel, "SesinSalesDayPanel");
		sesinSalesDayPanel.setLayout(null);

		JPanel sesinSalesDayPanelUp = new JPanel();
		sesinSalesDayPanelUp.setBounds(0, 0, 854, 37);
		sesinSalesDayPanelUp.setBackground(new Color(246, 246, 246));
		sesinSalesDayPanel.add(sesinSalesDayPanelUp);
		sesinSalesDayPanelUp.setLayout(new GridLayout(1, 7, 5, 5));

		for (int i = 0; i < 7; i++) {
			JLabel jl = new JLabel();
			jl.setBackground(Color.white);
			jl.setOpaque(false);
			jl.setHorizontalAlignment(JLabel.CENTER);
			jl.setText("일월화수목금토".charAt(i) + "");
			jl.setFont(new Font("나눔고딕", Font.BOLD, 15));
			sesinSalesDayPanelUp.add(jl);
			if (i == 0) {
				jl.setForeground(Color.red);
			}
			if (i == 6) {
				jl.setForeground(Color.blue);
			}
		}

		JPanel sesinSalesDayPanelDown = new JPanel();
		sesinSalesDayPanelDown.setBackground(new Color(246, 246, 246));
		sesinSalesDayPanelDown.setBounds(0, 37, 854, 428);
		sesinSalesDayPanel.add(sesinSalesDayPanelDown);
		sesinSalesDayPanelDown.setLayout(new GridLayout(6, 7, 5, 5));

		ArrayList<JButton> calendarButton = new ArrayList<JButton>();

		for (int i = 0; i < 42; i++) {
			JButton jb = new JButton();
			jb.setBackground(Color.white);
			jb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					cl_sesinSalesMonitor.show(sesinSalesMonitor, "SesinSalesResPanel");

					selectedDay = ((JButton)(e.getSource())).getText();

					calcDailySales(selectedDay);
				}
			});
			jb.setFont(new Font("나눔고딕", Font.BOLD, 20));
			calendarButton.add(jb);
			sesinSalesDayPanelDown.add(jb);
			if (i % 7 == 6) {
				jb.setForeground(Color.blue);
			}
			if (i % 7 == 0) {
				jb.setForeground(Color.red);
			}
		}

		sesinSalesSearchButton = new JButton("조회");
		sesinSalesSearchButton.setFont(new Font("나눔고딕", Font.BOLD, 20));
		sesinSalesSearchButton.setBackground(new Color(242, 226, 209));
		sesinSalesSearchButton.setBounds(350, 124, 75, 35);
		sesinSalesSearchButton.setEnabled(false);
		sesinSalesSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				allRadioButton.setSelected(true);
				cutCheckBox.setSelected(true);
				beautyCheckBox.setSelected(true);

				int selectedYear = Integer.parseInt((String) yearComboBox.getSelectedItem());
				int selectedMonth = Integer.parseInt((String) monthComboBox.getSelectedItem());

				if (sesinSalesDayButton.isSelected()) {

					cl_sesinSalesMonitor.show(sesinSalesMonitor, "SesinSalesDayPanel");

					Calendar selectedDay = Calendar.getInstance();
					selectedDay.set(selectedYear, selectedMonth - 1, 1);

					int first = selectedDay.get(Calendar.DAY_OF_WEEK) - 1;
					int last = selectedDay.getActualMaximum(Calendar.DATE);

					int day = 1;
					for (JButton jb : calendarButton) {
						jb.setText(null);
						jb.setEnabled(true);
						jb.setBackground(Color.white);
					}
					for (int i = first; i < last + first; i++) {
						calendarButton.get(i).setText(day + "");
						day++;
					}
					for (JButton jb : calendarButton) {
						if (jb.getText() == null) {
							jb.setEnabled(false);
							jb.setBackground(Color.LIGHT_GRAY);
						}
					}
				}

				else if (sesinSalesMonthButton.isSelected()) {
					cl_sesinSalesMonitor.show(sesinSalesMonitor, "SesinSalesResPanel");

					calcMonthlySales();
				}
			}
		});
		sesinSales.add(sesinSalesSearchButton);

		JPanel sesinSalesResPanel = new JPanel();
		sesinSalesMonitor.add(sesinSalesResPanel, "SesinSalesResPanel");
		sesinSalesResPanel.setBackground(new Color(246, 246, 246));
		sesinSalesResPanel.setLayout(null);

		tableColumnName = new String[] { "ID", "Locker", "Menu", "Price", "Time", "State" };
		table = new JTable();

		JScrollPane resScroll = new JScrollPane(table);
		resScroll.setPreferredSize(new Dimension(450, 400));
		resScroll.setBounds(134, 5, 708, 407);
		sesinSalesResPanel.add(resScroll);

		JLabel resTotal = new JLabel("Total: ");
		resTotal.setFont(new Font("나눔고딕", Font.BOLD, 25));
		resTotal.setBounds(537, 420, 90, 45);
		sesinSalesResPanel.add(resTotal);

		resWon = new JLabel("원");
		resWon.setFont(new Font("나눔고딕", Font.BOLD, 25));
		resWon.setBounds(639, 420, 188, 45);
		resWon.setHorizontalAlignment(JLabel.RIGHT);
		sesinSalesResPanel.add(resWon);

		cutCheckBox = new JCheckBox("세신");
		cutCheckBox.setFont(new Font("나눔고딕", Font.BOLD, 17));
		cutCheckBox.setBackground(new Color(246, 246, 246));
		cutCheckBox.setBounds(20, 67, 68, 26);
		cutCheckBox.setSelected(true);
		cutCheckBox.addActionListener(new ServiceSelect());
		sesinSalesResPanel.add(cutCheckBox);

		beautyCheckBox = new JCheckBox("마사지");
		beautyCheckBox.setFont(new Font("나눔고딕", Font.BOLD, 17));
		beautyCheckBox.setBackground(new Color(246, 246, 246));
		beautyCheckBox.setBounds(20, 112, 100, 26);
		beautyCheckBox.setSelected(true);
		beautyCheckBox.addActionListener(new ServiceSelect());
		sesinSalesResPanel.add(beautyCheckBox);

		allRadioButton = new JRadioButton("ALL");
		allRadioButton.setFont(new Font("나눔고딕", Font.BOLD, 20));
		allRadioButton.setBackground(new Color(246, 246, 246));
		allRadioButton.setBounds(10, 18, 78, 45);
		allRadioButton.setSelected(true);
		allRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (allRadioButton.isSelected()) {
					cutCheckBox.setSelected(true);
					beautyCheckBox.setSelected(true);
				} else {
					cutCheckBox.setSelected(false);
					beautyCheckBox.setSelected(false);
				}
				if(!cutCheckBox.isSelected() && !beautyCheckBox.isSelected()) {
					tableDB = null;
					setTable();
					resWon.setText("0 원");
				}
				else if(sesinSalesMonthButton.isSelected()) {
					calcMonthlySales();
				}
				else if(sesinSalesDayButton.isSelected()) {
					calcDailySales(selectedDay);
				}
			}
		});
		sesinSalesResPanel.add(allRadioButton);

		// 채팅
		JPanel sesinChatting = new JPanel();
		sesinChatting.setOpaque(false);
		sesinMonitorPanel.add(sesinChatting, "SesinChatting");
		sesinChatting.setLayout(null);

		sesinChattingTextField = new JTextField();
		sesinChattingTextField.setBounds(49, 610, 730, 45);
		sesinChatting.add(sesinChattingTextField);
		sesinChattingTextField.setColumns(10);
		sesinChattingTextField.setFont(new Font("나눔고딕", Font.PLAIN, 13));
		sesinChattingTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sesinChattingSend.doClick();
			}
		});

		//채팅입력후 보내는 곳
		sesinChattingSend = new JButton("전송");
		sesinChattingSend.setBounds(783, 611, 116, 43);
		sesinChattingSend.setFont(new Font("나눔고딕", Font.BOLD, 20));
		sesinChattingSend.setBackground(new Color(0, 62, 0));
		sesinChattingSend.setForeground(Color.white);
		sesinChatting.add(sesinChattingSend);
		sesinChattingSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = sesinChattingTextField.getText();

				sesinChattingTextArea.setCaretPosition(sesinChattingTextArea.getDocument().getLength());
				if(!msg.equals("")) {
					try {
						data = new TCPData();
						data.kind = "채팅";
						data.from = "SesinStaffFrame";
						data.to = new String[] {"SesinStaffFrame", "BarberStaffFrame",  "Manager_Pan"};
						data.sendObject = "[세신사] "+sesinChattingTextField.getText();
						oos.writeObject(data);
						oos.flush();
						oos.reset();
						System.out.println(data+": 전송 완료");
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					sesinChattingTextField.setText("");
				}
				sesinChattingTextField.requestFocus();
			}
		});

		barberConnected = new JLabel("이발사 OFF");
		barberConnected.setForeground(Color.WHITE);
		barberConnected.setOpaque(true);
		barberConnected.setFont(new Font("나눔고딕", Font.BOLD, 20));
		barberConnected.setHorizontalAlignment(SwingConstants.CENTER);
		barberConnected.setBounds(49, 37, 140, 34);
		barberConnected.setBackground(new Color(38, 83, 113));
		sesinChatting.add(barberConnected);

		managerConnected = new JLabel("관리자 OFF");
		managerConnected.setForeground(Color.WHITE);
		managerConnected.setOpaque(true);
		managerConnected.setHorizontalAlignment(SwingConstants.CENTER);
		managerConnected.setFont(new Font("나눔고딕", Font.BOLD, 20));
		managerConnected.setBounds(201, 37, 140, 34);
		managerConnected.setBackground(new Color(38, 83, 113));
		sesinChatting.add(managerConnected);
		
		sesinChattingTextArea = new JTextArea();
		sesinChattingTextArea.setForeground(Color.BLACK);
		sesinChattingTextArea.setFont(new Font("나눔고딕", Font.PLAIN, 15));
		sesinChattingTextArea.setEnabled(false);
		sesinChattingTextArea.setEditable(false);
		sesinChattingTextArea.setDisabledTextColor(Color.BLACK);
		sesinChattingTextArea.setBounds(0, 0, 850, 519);
		
		JScrollPane scrollPane = new JScrollPane(sesinChattingTextArea);
		scrollPane.setBounds(49, 83, 850, 517);
		sesinChatting.add(scrollPane);

		sesinStaff.getContentPane().add(back);

		sesinStaff.setVisible(true);
		sesinStaff.setResizable(false);
		sesinStaff.setDefaultCloseOperation(sesinStaff.EXIT_ON_CLOSE);

		new Receiver().start();
	}

	class WaitingPerson extends JPanel implements MouseListener{

		JLabel lockerNum;
		JLabel style1;
		JLabel style2;
		JLabel style3;

		public WaitingPerson() {

			setLayout(null);
			lockerNum = new JLabel();
			lockerNum.setBounds(30, 35, 100, 80);
			lockerNum.setHorizontalAlignment(JLabel.CENTER);
			lockerNum.setFont(new Font("나눔고딕", Font.BOLD, 50));
			style1 = new JLabel();
			style1.setBounds(120, 20, 100, 30);
			style1.setHorizontalAlignment(JLabel.RIGHT);
			style1.setFont(new Font("나눔고딕", Font.BOLD, 17));
			style2 = new JLabel();
			style2.setBounds(120, 60, 100, 30);
			style2.setHorizontalAlignment(JLabel.RIGHT);
			style2.setFont(new Font("나눔고딕", Font.BOLD, 17));
			style3 = new JLabel();
			style3.setBounds(120, 100, 100, 30);
			style3.setHorizontalAlignment(JLabel.RIGHT);
			style3.setFont(new Font("나눔고딕", Font.BOLD, 17));

			add(lockerNum);
			add(style1);
			add(style2);
			add(style3);

			addMouseListener(this);
		}

		@Override
		public void mouseClicked(MouseEvent e) {

			if(!((WaitingPerson)e.getSource()).lockerNum.getText().equals("")) {

				callButton.setEnabled(true);
				
				wc.id = sesinWaiting.get(waitingCustomers.indexOf((JPanel)(e.getSource()))).id;

				wc.locker = Integer.parseInt(((WaitingPerson)e.getSource()).lockerNum.getText());
				wc.menu = ((WaitingPerson)e.getSource()).style1.getText();

				if(!((WaitingPerson)e.getSource()).style2.getText().equals("")) {
					wc.menu += ",";
					wc.menu += ((WaitingPerson)e.getSource()).style2.getText();
				}

				if(!((WaitingPerson)e.getSource()).style3.getText().equals("")) {
					wc.menu += ",";
					wc.menu += ((WaitingPerson)e.getSource()).style3.getText();
				}

				wc.state = "호출";

				if(!new DAO().checkBarberWait(wc.id)) {
					callButton.setEnabled(false);
				}
				data.to = new String[] {"SesinStaffFrame", "BarberStaffFrame", "kiosk_barber", "kiosk_sesin"};
				data.kind = "예약";

				System.out.println(wc.toString());

				sesinNowDone.setVisible(false);
				sesinCallCancle.setVisible(false);
				sesinCallCancle.setBounds(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y, 200, 100);
				sesinCallCancle.setVisible(true);
			}

		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}

	void refreshWaitingPanel() {
		sesinOngoing = new DAO().sesinNow();
		if(sesinOngoing!=null) {
			sesinNow.setText(sesinOngoing.locker+"");
			sesinNowStyle1.setText(sesinOngoing.menu.split(",")[0]);

			if(sesinOngoing.menu.split(",").length >=2) {
				sesinNowStyle2.setText(sesinOngoing.menu.split(",")[1]);
			}
			else {
				sesinNowStyle2.setText("");
			}

			if(sesinOngoing.menu.split(",").length >=3) {
				sesinNowStyle3.setText(sesinOngoing.menu.split(",")[2]);
			}
			else {
				sesinNowStyle3.setText("");
			}
		}else {
			sesinNow.setText("");
			sesinNowStyle1.setText("");
			sesinNowStyle2.setText("");
			sesinNowStyle3.setText("");
		}

		sesinWaiting = new DAO().sesinWaitingArr();
		for (int i = 0; i < sesinWaiting.size(); i++) {
			waitingCustomers.get(i).lockerNum.setText(sesinWaiting.get(i).locker+"");
			waitingCustomers.get(i).style1.setText(sesinWaiting.get(i).menu.split(",")[0]);

			if(sesinWaiting.get(i).menu.split(",").length >=2) {
				waitingCustomers.get(i).style2.setText(sesinWaiting.get(i).menu.split(",")[1]);
			}
			else {
				waitingCustomers.get(i).style2.setText("");
			}

			if(sesinWaiting.get(i).menu.split(",").length >=3) {
				waitingCustomers.get(i).style3.setText(sesinWaiting.get(i).menu.split(",")[2]);
			}
			else {
				waitingCustomers.get(i).style3.setText("");
			}
			waitingCustomers.get(i).setBackground(new Color(255, 242, 230));
		}

		for (int i = sesinWaiting.size(); i < waitingCustomers.size(); i++) {
			waitingCustomers.get(i).lockerNum.setText("");
			waitingCustomers.get(i).style1.setText("");
			waitingCustomers.get(i).style2.setText("");
			waitingCustomers.get(i).style3.setText("");
			waitingCustomers.get(i).setBackground(new Color(0,0,0,0));
		}

		waitingPanel.repaint();
		waitingPanel.revalidate();
	}

	class ServiceSelect implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!((JCheckBox)(e.getSource())).isSelected()) {
				allRadioButton.setSelected(false);
			}
			if (cutCheckBox.isSelected() && beautyCheckBox.isSelected()) {
				allRadioButton.setSelected(true);
			}

			if(!cutCheckBox.isSelected() && !beautyCheckBox.isSelected()) {
				tableDB = null;
				setTable();
				resWon.setText("0 원");
			}
			else if(sesinSalesMonthButton.isSelected()) {
				calcMonthlySales();
			}
			else if(sesinSalesDayButton.isSelected()) {
				calcDailySales(selectedDay);
			}
		}
	}

	void setTable() {
		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(JLabel.RIGHT);
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(JLabel.CENTER);


		table.setModel(new DefaultTableModel(tableDB, tableColumnName));

		table.setEnabled(false);
		
		table.setRowHeight(40);

		table.getColumnModel().getColumn(0).setPreferredWidth(85);
		table.getColumnModel().getColumn(0).setCellRenderer(center);
		table.getColumnModel().getColumn(1).setPreferredWidth(85);
		table.getColumnModel().getColumn(1).setCellRenderer(center);
		table.getColumnModel().getColumn(2).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setCellRenderer(center);
		table.getColumnModel().getColumn(3).setPreferredWidth(144);
		table.getColumnModel().getColumn(3).setCellRenderer(right);
		table.getColumnModel().getColumn(4).setPreferredWidth(160);
		table.getColumnModel().getColumn(4).setCellRenderer(center);
		table.getColumnModel().getColumn(5).setPreferredWidth(100);
		table.getColumnModel().getColumn(5).setCellRenderer(center);


	}

	void calcMonthlySales() {

		String rangeSql = "";
		if(cutCheckBox.isSelected()) {
			rangeSql+="menu.sep = '세신' or ";
		}
		if(beautyCheckBox.isSelected()) {
			rangeSql+="menu.sep = '마사지' or ";
		}

		tableDB = new DAO().paymentList(new DAO().paymentListSql(
				(String) yearComboBox.getSelectedItem(),
				(String) monthComboBox.getSelectedItem(),
				rangeSql.substring(0, rangeSql.lastIndexOf(" or"))
				));

		setTable();

		resWon.setText(new DecimalFormat().format(new DAO().paymentCalcTotal(new DAO().paymentCalcSql(
				(String) yearComboBox.getSelectedItem(),
				(String) monthComboBox.getSelectedItem(),
				rangeSql.substring(0, rangeSql.lastIndexOf(" or")))))
				+ " 원");
	}

	void calcDailySales(String selectedDay) {

		String rangeSql = "";
		if(cutCheckBox.isSelected()) {
			rangeSql+="menu.sep = '세신' or ";
		}
		if(beautyCheckBox.isSelected()) {
			rangeSql+="menu.sep = '마사지' or ";
		}

		tableDB = new DAO().paymentList(new DAO().paymentListSql(
				(String) yearComboBox.getSelectedItem(),
				(String) monthComboBox.getSelectedItem(),
				selectedDay,
				rangeSql.substring(0, rangeSql.lastIndexOf(" or"))
				));


		setTable();

		resWon.setText(new DecimalFormat().format(new DAO().paymentCalcTotal(
				new DAO().paymentCalcSql(
						(String) yearComboBox.getSelectedItem(),
						(String) monthComboBox.getSelectedItem(),
						selectedDay,
						rangeSql.substring(0, rangeSql.lastIndexOf(" or")))))
				+ " 원");
	}

	ObjectOutputStream oos;
	ObjectInputStream ois;

	class Receiver extends Thread{
		@Override
		public void run() {
			try {

				while(ois!=null) {
					TCPData data = (TCPData) ois.readObject();
					if(data.kind.equals("예약") || data.kind.equals("퇴장")) {
						refreshWaitingPanel();
					}
					else if(data.kind.equals("채팅")) {
						sesinChattingTextArea.append((String) data.sendObject+"\n");
						sesinChattingTextArea.setCaretPosition(sesinChattingTextArea.getDocument().getLength());
						System.out.println("채팅 받음");
					}
					else if(data.kind.equals("접속자목록")) {
						if(!((ArrayList)data.sendObject).contains("BarberStaffFrame")){
							barberConnected.setBackground(Color.LIGHT_GRAY);
							barberConnected.setText("이발사 OFF");
						}else {
							barberConnected.setBackground(new Color(0, 62, 0));
							barberConnected.setText("이발사 ON");
						}
						if(!((ArrayList)data.sendObject).contains("Manager_Pan")){
							managerConnected.setBackground(Color.LIGHT_GRAY);
							managerConnected.setText("관리자 OFF");
						}
						else {
							managerConnected.setBackground(new Color(0, 62, 0));
							managerConnected.setText("관리자 ON");
						}
						System.out.println("채팅접속중 받음");
					}
					else {
						System.out.println("아무데도 못들어가");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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

		String addr = "192.168.150.63";

		try {
			Socket socket = new Socket(addr, 8888);
			System.out.println("세신사: 접속 성공");
			new SesinStaffFrame(socket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}