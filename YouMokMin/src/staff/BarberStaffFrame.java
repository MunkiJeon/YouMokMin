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
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

import com.sun.tools.javac.Main;

import db.BarberDTO;
import db.DAO;
import db.SesinDTO;
import db.WaitingCustomer;
import server.TCPData;


public class BarberStaffFrame {

	JFrame barberStaff;
	JTable table;
	String[][] tableDB;
	String[] tableColumnName;
	JLabel resWon, barberNow, barberNowStyle1, barberNowStyle2, barberNowStyle3, sesinConnected, managerConnected;
	BarberDTO barberOngoing;
	ArrayList<BarberDTO> barberWaiting;
	ArrayList<WaitingPerson> waitingCustomers;
	JButton barberChattingSend, acceptButton, barberSalesSearchButton, callButton, exitButton;
	JRadioButton allRadioButton;
	JCheckBox cutCheckBox, beautyCheckBox;
	JComboBox yearComboBox, monthComboBox;
	JToggleButton barberSalesDayButton, barberSalesMonthButton;
	CardLayout cl_barberSalesMonitor;
	JPanel barberSalesMonitor, waitingListPanel, waitingPanel;
	String selectedDay;
	JTextField barberChattingTextField;
	JTextArea barberChattingTextArea;
	String name;
	JDialog barberCallCancle, barberNowDone;
	TCPData data;
	WaitingCustomer wc = new WaitingCustomer();


	public BarberStaffFrame(Socket socket) {

		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			System.out.println("oos, ois 연결 성공");
			data = new TCPData(null, "서버", "BarberStaffFrame", "서버");

			oos.writeObject(data);
			oos.flush();
			oos.reset();
			System.out.println("oos 보냄");

		} catch (IOException e1) {

		}

		barberStaff = new JFrame();
		barberStaff.setBounds(100, 20, 960, 800);
		barberStaff.getContentPane().setBackground(new Color(246, 246, 246));

		barberStaff.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		barberStaff.getContentPane().setLayout(null);

		ImageIcon background = new ImageIcon("img/barberBackGround2.png");
		JLabel back = new JLabel(background);
		back.setBounds(0, 5, 946, 763);


		JPanel barberMonitorPanel = new JPanel();
		barberMonitorPanel.setOpaque(false);
		barberMonitorPanel.setBounds(0, 0, 946, 659);
		barberMonitorPanel.setBackground(Color.white);
		barberStaff.getContentPane().add(barberMonitorPanel);
		CardLayout barberMonitorPanelCard = new CardLayout();
		barberMonitorPanel.setLayout(barberMonitorPanelCard);

		JPanel barberButtonPanel = new JPanel();
		barberButtonPanel.setBounds(42, 665, 856, 68);
		barberButtonPanel.setBackground(Color.white);
		barberStaff.getContentPane().add(barberButtonPanel);
		barberButtonPanel.setLayout(new GridLayout(1, 3, 20, 20));

		JButton barberWaitingButton = new JButton("대기 현황");
		barberWaitingButton.setFont(new Font("나눔고딕", Font.BOLD, 30));
		barberWaitingButton.setBackground(new Color(38, 83, 113));
		//		barberWaitingButton.setBackground(new Color(0, 0, 64));
		barberWaitingButton.setForeground(Color.white);
		barberWaitingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				barberMonitorPanelCard.show(barberMonitorPanel, "BarberWaiting");
			}
		});
		barberButtonPanel.add(barberWaitingButton);

		JButton barberSalesButton = new JButton("이용 내역");
		barberSalesButton.setFont(new Font("나눔고딕", Font.BOLD, 30));
		barberSalesButton.setBackground(new Color(38, 83, 113));
		//		barberSalesButton.setBackground(new Color(0, 0, 64));
		barberSalesButton.setForeground(Color.white);
		barberSalesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				barberMonitorPanelCard.show(barberMonitorPanel, "BarberSales");
			}
		});
		barberButtonPanel.add(barberSalesButton);

		JButton barberChattingButton = new JButton("채팅");
		barberChattingButton.setFont(new Font("나눔고딕", Font.BOLD, 30));
		barberChattingButton.setForeground(Color.white);
		barberChattingButton.setBackground(new Color(38, 83, 113));
		//		barberChattingButton.setBackground(new Color(0, 0, 64));
		barberChattingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				barberMonitorPanelCard.show(barberMonitorPanel, "BarberChatting");
			}
		});
		barberButtonPanel.add(barberChattingButton);



		// 대기 현황

		JPanel barberWaitingList = new JPanel();
		barberWaitingList.setOpaque(false);
		barberWaitingList.setBackground(new Color(246, 246, 246));
		barberMonitorPanel.add(barberWaitingList, "BarberWaiting");
		barberWaitingList.setLayout(null);

		waitingPanel = new JPanel();
		waitingPanel.setPreferredSize(new Dimension(700, 1250));
		waitingPanel.setBackground(Color.white);
		waitingPanel.setLayout(null);

		JScrollPane waitingScroll = new JScrollPane(waitingPanel);
		waitingScroll.setBorder(null);
		waitingScroll.setBounds(49, 42, 850, 590);
		waitingScroll.getVerticalScrollBar().setUnitIncrement(20);
		barberWaitingList.add(waitingScroll);

		barberWaiting = new DAO().barberWaitingArr();

		barberNowStyle1 = new JLabel();
		barberNowStyle1.setFont(new Font("나눔고딕", Font.BOLD, 20));
		barberNowStyle1.setHorizontalAlignment(SwingConstants.RIGHT);
		barberNowStyle1.setForeground(Color.white);
		barberNowStyle1.setBounds(626, 70, 121, 29);
		waitingPanel.add(barberNowStyle1);

		barberNowStyle2 = new JLabel();
		barberNowStyle2.setHorizontalAlignment(SwingConstants.RIGHT);
		barberNowStyle2.setForeground(Color.white);
		barberNowStyle2.setFont(new Font("나눔고딕", Font.BOLD, 20));
		barberNowStyle2.setBounds(626, 105, 121, 29);
		waitingPanel.add(barberNowStyle2);

		barberNowStyle3 = new JLabel();
		barberNowStyle3.setHorizontalAlignment(SwingConstants.RIGHT);
		barberNowStyle3.setForeground(Color.white);
		barberNowStyle3.setFont(new Font("나눔고딕", Font.BOLD, 20));
		barberNowStyle3.setBounds(626, 140, 121, 29);
		waitingPanel.add(barberNowStyle3);

		barberNowDone = new JDialog();
		barberNowDone.getContentPane().setLayout(new FlowLayout());
		JButton nowDoneButton = new JButton("완료");
		nowDoneButton.setPreferredSize(new Dimension(80, 50));
		nowDoneButton.setFont(new Font("나눔고딕", Font.BOLD, 20));
		nowDoneButton.setBackground(new Color(38, 83, 113));
		nowDoneButton.setForeground(Color.white);
		nowDoneButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				wc.id = barberOngoing.id;
				wc.locker = Integer.parseInt(barberNow.getText());

				wc.menu = barberNowStyle1.getText();

				if(!(barberNowStyle2.getText().equals(""))) {
					wc.menu += ",";
					wc.menu += barberNowStyle2.getText();
				}

				if(!(barberNowStyle3.getText().equals(""))) {
					wc.menu += ",";
					wc.menu += barberNowStyle3.getText();
				}

				wc.state = "완료";

				System.out.println(wc);

				data = new TCPData();
				data.kind = "예약";
				data.from = "BarberStaffFrame";
				data.to = new String[] {"BarberStaffFrame", "SesinStaffFrame", "kiosk_barber", "kiosk_sesin"};
				data.sendObject = wc;

				try {
					oos.writeObject(data);
					oos.flush();
					oos.reset();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				barberNowDone.setVisible(false);

				ArrayList<String> nowStyle = new ArrayList<String>();
				nowStyle.add(barberNowStyle1.getText());
				if(!barberNowStyle2.getText().equals("")) {
					nowStyle.add(barberNowStyle2.getText());
				}
				if(!barberNowStyle3.getText().equals("")) {
					nowStyle.add(barberNowStyle3.getText());
				}

				new DAO().writeToPayment(Integer.parseInt(barberNow.getText()), nowStyle);
				
				barberOngoing = null;
			}
		});
		barberNowDone.getContentPane().add(nowDoneButton);

		barberNow = new JLabel();
		barberNow.setFont(new Font("나눔고딕", Font.BOLD, 50));
		barberNow.setForeground(Color.white);
		barberNow.setBackground(new Color(162, 51, 41));
		barberNow.setOpaque(true);
		barberNow.setHorizontalAlignment(JLabel.CENTER);
		barberNow.setBounds(57, 50, 733, 143);
		barberNow.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				barberCallCancle.setVisible(false);
				barberNowDone.setVisible(false);
				if(!barberNow.getText().equals("")) {
					barberNowDone.setBounds(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y, 100, 100);
					barberNowDone.setVisible(true);
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
		waitingPanel.add(barberNow);

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


		JDialog requestPassword = new JDialog(barberStaff, "", true);
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
		requestPassword.setBounds(500, 300, 200, 160);
		requestPassword.getContentPane().setLayout(new FlowLayout());
		JLabel label = new JLabel("비밀번호를 입력하세요.");
		label.setFont(new Font("나눔고딕", Font.BOLD, 13));
		requestPassword.getContentPane().add(label);
		
		
		JPasswordField pwField = new JPasswordField();
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
		pwField.setPreferredSize(new Dimension(170, 30));
		pwField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				acceptButton.doClick();
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
					data.from = "BarberStaffFrame";
					data.to = new String[] {"BarberStaffFrame", "SesinStaffFrame", "kiosk_barber", "kiosk_sesin"};
					data.kind = "예약";

					WaitingCustomer wc2 = new WaitingCustomer();

					if(!barberNow.getText().equals("")) {
						wc2.id = barberOngoing.id;
						wc2.locker = Integer.parseInt(barberNow.getText());
						wc2.menu = barberNowStyle1.getText();

						if(!(barberNowStyle2.getText().equals(""))) {
							wc2.menu += ",";
							wc2.menu += barberNowStyle2.getText();
						}

						if(!(barberNowStyle3.getText().equals(""))) {
							wc2.menu += ",";
							wc2.menu += barberNowStyle3.getText();
						}

						wc2.state = "완료";
						data.sendObject = wc2;
						System.out.println("2)"+wc2.toString());
						try {
							oos.writeObject(data);
							oos.flush();
							oos.reset();
						} catch (IOException e1) {
							e1.printStackTrace();
						}


						ArrayList<String> nowStyle = new ArrayList<String>();
						nowStyle.add(barberNowStyle1.getText());
						if(!barberNowStyle2.getText().equals("")) {
							nowStyle.add(barberNowStyle2.getText());
						}
						if(!barberNowStyle3.getText().equals("")) {
							nowStyle.add(barberNowStyle3.getText());
						}

						new DAO().writeToPayment(Integer.parseInt(barberNow.getText()), nowStyle);
					}
					
					wc.state = "이발진행중";
					data.sendObject = wc;
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



		barberCallCancle = new JDialog();
		barberCallCancle.setResizable(false);
		barberCallCancle.getContentPane().setLayout(new FlowLayout());
		callButton = new JButton("호출");
		callButton.setFont(new Font("나눔고딕", Font.BOLD, 20));
		callButton.setPreferredSize(new Dimension(80, 50));
		callButton.setBackground(new Color(38, 83, 113));
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

					barberCallCancle.setVisible(false);
					requestPassword.setLocation(barberCallCancle.getX(), barberCallCancle.getY());
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
				barberCallCancle.setVisible(false);
			}
		});
		barberCallCancle.getContentPane().add(callButton);
		barberCallCancle.getContentPane().add(cancleButton);

		refreshWaitingPanel();


		// 매출
		JPanel barberSales = new JPanel();
		barberSales.setOpaque(false);
		barberSales.setBackground(new Color(246, 246, 246));
		barberMonitorPanel.add(barberSales, "BarberSales");
		barberSales.setLayout(null);

		barberSalesMonitor = new JPanel();
		barberSalesMonitor.setBounds(43, 180, 854, 465);
		barberSales.add(barberSalesMonitor);
		cl_barberSalesMonitor = new CardLayout();
		barberSalesMonitor.setLayout(cl_barberSalesMonitor);

		barberSalesDayButton = new JToggleButton("일별");
		barberSalesDayButton.setFont(new Font("나눔고딕", Font.BOLD, 30));
		barberSalesDayButton.setBackground(new Color(162, 51, 41));
		barberSalesDayButton.setForeground(Color.white);
		barberSalesDayButton.setBounds(43, 48, 423, 62);
		barberSalesDayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				barberSalesSearchButton.setEnabled(true);
				cl_barberSalesMonitor.show(barberSalesMonitor, "BarberSalesBlankPanel");
			}
		});
		barberSales.add(barberSalesDayButton);

		barberSalesMonthButton = new JToggleButton("월별");
		barberSalesMonthButton.setFont(new Font("나눔고딕", Font.BOLD, 30));
		barberSalesMonthButton.setBackground(new Color(162, 51, 41));

		barberSalesMonthButton.setForeground(Color.white);
		barberSalesMonthButton.setBounds(479, 48, 418, 62);
		barberSalesMonthButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				barberSalesSearchButton.setEnabled(true);
				cl_barberSalesMonitor.show(barberSalesMonitor, "BarberSalesBlankPanel");
			}
		});
		barberSales.add(barberSalesMonthButton);

		ButtonGroup bg = new ButtonGroup();
		bg.add(barberSalesDayButton);
		bg.add(barberSalesMonthButton);

		Vector<String> year = new Vector<String>();
		year.add("2020");
		year.add("2021");
		year.add("2022");

		yearComboBox = new JComboBox(year);
		yearComboBox.setFont(new Font("나눔고딕", Font.BOLD, 15));
		yearComboBox.setBounds(50, 122, 122, 40);
		yearComboBox.setBackground(Color.white);
		yearComboBox.setSelectedItem("2022");
		barberSales.add(yearComboBox);

		Vector<String> month = new Vector<String>();
		for (String ss : "01,02,03,04,05,06,07,08,09,10,11,12".split(",")) {
			month.add(ss);
		}

		monthComboBox = new JComboBox(month);
		monthComboBox.setFont(new Font("나눔고딕", Font.BOLD, 15));
		monthComboBox.setBounds(190, 122, 122, 40);
		monthComboBox.setBackground(Color.white);
		monthComboBox.setSelectedItem("04");
		barberSales.add(monthComboBox);

		JPanel barberSalesBlankPanel = new JPanel();
		barberSalesBlankPanel.setBackground(new Color(246, 246, 246));
		barberSalesMonitor.add(barberSalesBlankPanel, "BarberSalesBlankPanel");

		JPanel barberSalesDayPanel = new JPanel();
		barberSalesMonitor.add(barberSalesDayPanel, "BarberSalesDayPanel");
		barberSalesDayPanel.setLayout(null);

		JPanel barberSalesDayPanelUp = new JPanel();
		barberSalesDayPanelUp.setBounds(0, 0, 854, 37);
		barberSalesDayPanelUp.setBackground(new Color(246, 246, 246));
		barberSalesDayPanel.add(barberSalesDayPanelUp);
		barberSalesDayPanelUp.setLayout(new GridLayout(1, 7, 5, 5));

		for (int i = 0; i < 7; i++) {
			JLabel jl = new JLabel();
			jl.setBackground(Color.white);
			jl.setOpaque(false);
			jl.setHorizontalAlignment(JLabel.CENTER);
			jl.setText("일월화수목금토".charAt(i) + "");
			jl.setFont(new Font("나눔고딕", Font.BOLD, 15));
			barberSalesDayPanelUp.add(jl);
			if (i == 0) {
				jl.setForeground(Color.red);
			}
			if (i == 6) {
				jl.setForeground(Color.blue);
			}
		}

		JPanel barberSalesDayPanelDown = new JPanel();
		barberSalesDayPanelDown.setBackground(new Color(246, 246, 246));
		barberSalesDayPanelDown.setBounds(0, 37, 854, 428);
		barberSalesDayPanel.add(barberSalesDayPanelDown);
		barberSalesDayPanelDown.setLayout(new GridLayout(6, 7, 5, 5));

		ArrayList<JButton> calendarButton = new ArrayList<JButton>();

		for (int i = 0; i < 42; i++) {
			JButton jb = new JButton();
			jb.setBackground(Color.white);
			jb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					cl_barberSalesMonitor.show(barberSalesMonitor, "BarberSalesResPanel");

					selectedDay = ((JButton)(e.getSource())).getText();

					calcDailySales(selectedDay);
				}
			});
			jb.setFont(new Font("나눔고딕", Font.BOLD, 20));
			calendarButton.add(jb);
			barberSalesDayPanelDown.add(jb);
			if (i % 7 == 6) {
				jb.setForeground(Color.blue);
			}
			if (i % 7 == 0) {
				jb.setForeground(Color.red);
			}
		}

		barberSalesSearchButton = new JButton("조회");
		barberSalesSearchButton.setFont(new Font("나눔고딕", Font.BOLD, 20));
		barberSalesSearchButton.setBackground(new Color(242, 226, 209));
		barberSalesSearchButton.setBounds(350, 124, 75, 35);
		barberSalesSearchButton.setEnabled(false);
		barberSalesSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				allRadioButton.setSelected(true);
				cutCheckBox.setSelected(true);
				beautyCheckBox.setSelected(true);

				int selectedYear = Integer.parseInt((String) yearComboBox.getSelectedItem());
				int selectedMonth = Integer.parseInt((String) monthComboBox.getSelectedItem());

				if (barberSalesDayButton.isSelected()) {

					cl_barberSalesMonitor.show(barberSalesMonitor, "BarberSalesDayPanel");

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

				else if (barberSalesMonthButton.isSelected()) {
					cl_barberSalesMonitor.show(barberSalesMonitor, "BarberSalesResPanel");

					calcMonthlySales();
				}
			}
		});
		barberSales.add(barberSalesSearchButton);

		JPanel barberSalesResPanel = new JPanel();
		barberSalesMonitor.add(barberSalesResPanel, "BarberSalesResPanel");
		barberSalesResPanel.setBackground(new Color(246, 246, 246));
		barberSalesResPanel.setLayout(null);

		tableColumnName = new String[] { "ID", "Locker", "Menu", "Price", "Time", "State" };
		table = new JTable();

		JScrollPane resScroll = new JScrollPane(table);
		resScroll.setPreferredSize(new Dimension(450, 400));
		resScroll.setBounds(134, 5, 708, 407);
		barberSalesResPanel.add(resScroll);

		JLabel resTotal = new JLabel("Total: ");
		resTotal.setFont(new Font("나눔고딕", Font.BOLD, 25));
		resTotal.setBounds(537, 420, 90, 45);
		barberSalesResPanel.add(resTotal);

		resWon = new JLabel("원");
		resWon.setFont(new Font("나눔고딕", Font.BOLD, 25));
		resWon.setBounds(639, 420, 188, 45);
		resWon.setHorizontalAlignment(JLabel.RIGHT);
		barberSalesResPanel.add(resWon);

		cutCheckBox = new JCheckBox("이발");
		cutCheckBox.setFont(new Font("나눔고딕", Font.BOLD, 20));
		cutCheckBox.setBackground(new Color(246, 246, 246));
		cutCheckBox.setBounds(20, 67, 68, 26);
		cutCheckBox.setSelected(true);
		cutCheckBox.addActionListener(new ServiceSelect());
		barberSalesResPanel.add(cutCheckBox);

		beautyCheckBox = new JCheckBox("미용");
		beautyCheckBox.setFont(new Font("나눔고딕", Font.BOLD, 20));
		beautyCheckBox.setBackground(new Color(246, 246, 246));
		beautyCheckBox.setBounds(20, 112, 68, 26);
		beautyCheckBox.setSelected(true);
		beautyCheckBox.addActionListener(new ServiceSelect());
		barberSalesResPanel.add(beautyCheckBox);

		allRadioButton = new JRadioButton("ALL");
		allRadioButton.setFont(new Font("나눔고딕", Font.BOLD, 22));
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
				else if(barberSalesMonthButton.isSelected()) {
					calcMonthlySales();
				}
				else if(barberSalesDayButton.isSelected()) {
					calcDailySales(selectedDay);
				}
			}
		});
		barberSalesResPanel.add(allRadioButton);

		// 채팅
		JPanel barberChatting = new JPanel();
		barberChatting.setOpaque(false);
		barberMonitorPanel.add(barberChatting, "BarberChatting");
		barberChatting.setLayout(null);

		barberChattingTextField = new JTextField();
		barberChattingTextField.setBounds(49, 610, 730, 45);
		barberChatting.add(barberChattingTextField);
		barberChattingTextField.setColumns(10);
		barberChattingTextField.setFont(new Font("나눔고딕", Font.PLAIN, 13));
		barberChattingTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				barberChattingSend.doClick();
			}
		});

		//채팅입력후 보내는 곳
		barberChattingSend = new JButton("전송");
		barberChattingSend.setBounds(783, 611, 116, 43);
		barberChattingSend.setFont(new Font("나눔고딕", Font.BOLD, 20));
		barberChattingSend.setBackground(new Color(162, 51, 41));
		barberChattingSend.setForeground(Color.white);
		barberChatting.add(barberChattingSend);
		barberChattingSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = barberChattingTextField.getText();

				barberChattingTextArea.setCaretPosition(barberChattingTextArea.getDocument().getLength());

				if(!msg.equals("")) {
					try {
						data = new TCPData();
						data.kind = "채팅";
						data.from = "BarberStaffFrame";
						data.to = new String[] {"BarberStaffFrame", "SesinStaffFrame", "Manager_Pan"};
						data.sendObject = "[이발사] "+barberChattingTextField.getText();
						oos.writeObject(data);
						oos.flush();
						oos.reset();
						System.out.println(data+": 전송 완료");
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					barberChattingTextField.setText("");
				}
				barberChattingTextField.requestFocus();
			}
		});


		barberChattingTextArea = new JTextArea();
		barberChattingTextArea.setDisabledTextColor(Color.BLACK);
		barberChattingTextArea.setForeground(Color.BLACK);
		barberChattingTextArea.setEnabled(false);
		barberChattingTextArea.setEditable(false);
		barberChattingTextArea.setBounds(49, 83, 850, 519);
		barberChattingTextArea.setFont(new Font("나눔고딕", Font.PLAIN, 15));
		
		
		JScrollPane scrollPane = new JScrollPane(barberChattingTextArea);
		scrollPane.setBounds(49, 83, 850, 517);
		barberChatting.add(scrollPane);

		sesinConnected = new JLabel("세신사");
		sesinConnected.setForeground(Color.WHITE);
		sesinConnected.setOpaque(true);
		sesinConnected.setFont(new Font("나눔고딕", Font.BOLD, 20));
		sesinConnected.setHorizontalAlignment(SwingConstants.CENTER);
		sesinConnected.setBounds(49, 37, 140, 34);
		sesinConnected.setBackground(new Color(38, 83, 113));
		barberChatting.add(sesinConnected);

		managerConnected = new JLabel("관리자");
		managerConnected.setForeground(Color.WHITE);
		managerConnected.setOpaque(true);
		managerConnected.setHorizontalAlignment(SwingConstants.CENTER);
		managerConnected.setFont(new Font("나눔고딕", Font.BOLD, 20));
		managerConnected.setBounds(201, 37, 140, 34);
		managerConnected.setBackground(new Color(38, 83, 113));
		barberChatting.add(managerConnected);

		barberStaff.getContentPane().add(back);

		barberStaff.setVisible(true);
		barberStaff.setResizable(false);
		barberStaff.setDefaultCloseOperation(barberStaff.EXIT_ON_CLOSE);

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

				wc.id = barberWaiting.get(waitingCustomers.indexOf((JPanel)(e.getSource()))).id;
				
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

				if(!new DAO().checkSesinWait(wc.id)) {
					callButton.setEnabled(false);
				}
				data.to = new String[] {"BarberStaffFrame", "SesinStaffFrame", "kiosk_barber", "kiosk_sesin"};
				data.kind = "예약";

				System.out.println(wc.toString());

				barberNowDone.setVisible(false);
				barberCallCancle.setVisible(false);
				barberCallCancle.setBounds(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y, 200, 100);
				barberCallCancle.setVisible(true);
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
		
		System.out.println("새로고침");
		barberOngoing = new DAO().barberNow();
		if(barberOngoing!=null) {
			barberNow.setText(barberOngoing.locker+"");
			barberNowStyle1.setText(barberOngoing.menu.split(",")[0]);

			if(barberOngoing.menu.split(",").length >=2) {
				barberNowStyle2.setText(barberOngoing.menu.split(",")[1]);
			}
			else {
				barberNowStyle2.setText("");
			}

			if(barberOngoing.menu.split(",").length >=3) {
				barberNowStyle3.setText(barberOngoing.menu.split(",")[2]);
			}
			else {
				barberNowStyle3.setText("");
			}
		}else {
			barberNow.setText("");
			barberNowStyle1.setText("");
			barberNowStyle2.setText("");
			barberNowStyle3.setText("");
		}

		barberWaiting = new DAO().barberWaitingArr();
		for (int i = 0; i < barberWaiting.size(); i++) {
			waitingCustomers.get(i).lockerNum.setText(barberWaiting.get(i).locker+"");
			waitingCustomers.get(i).style1.setText(barberWaiting.get(i).menu.split(",")[0]);

			if(barberWaiting.get(i).menu.split(",").length >=2) {
				waitingCustomers.get(i).style2.setText(barberWaiting.get(i).menu.split(",")[1]);
			}
			else {
				waitingCustomers.get(i).style2.setText("");
			}

			if(barberWaiting.get(i).menu.split(",").length >=3) {
				waitingCustomers.get(i).style3.setText(barberWaiting.get(i).menu.split(",")[2]);
			}
			else {
				waitingCustomers.get(i).style3.setText("");
			}
			waitingCustomers.get(i).setBackground(new Color(255, 242, 230));
		}

		for (int i = barberWaiting.size(); i < waitingCustomers.size(); i++) {
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
			else if(barberSalesMonthButton.isSelected()) {
				calcMonthlySales();
			}
			else if(barberSalesDayButton.isSelected()) {
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
			rangeSql+="menu.sep = '이발' or ";
		}
		if(beautyCheckBox.isSelected()) {
			rangeSql+="menu.sep = '미용' or ";
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
			rangeSql+="menu.sep = '이발' or ";
		}
		if(beautyCheckBox.isSelected()) {
			rangeSql+="menu.sep = '미용' or ";
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
						barberChattingTextArea.append((String) data.sendObject+"\n");
						barberChattingTextArea.setCaretPosition(barberChattingTextArea.getDocument().getLength());
						System.out.println("채팅 받음");
					}
					else if(data.kind.equals("접속자목록")) {
						if(!((ArrayList)data.sendObject).contains("SesinStaffFrame")){
							sesinConnected.setBackground(Color.LIGHT_GRAY);
							sesinConnected.setText("세신사 OFF");
						}else {
							sesinConnected.setBackground(new Color(38, 83, 113));
							sesinConnected.setText("세신사 ON");
						}
						if(!((ArrayList)data.sendObject).contains("Manager_Pan")){
							managerConnected.setBackground(Color.LIGHT_GRAY);
							managerConnected.setText("관리자 OFF");
						}
						else {
							managerConnected.setBackground(new Color(38, 83, 113));
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

		String addr = "192.168.219.102";
//		addr = "192.168.20.35";
		//		addr = "mk0709.iptime.org";
		addr = "192.168.150.63";
		//		addr = "106.248.235.10";
		//		addr = "192.168.20.44";
		//		addr = "121.161.160.79";

		try {
			Socket socket = new Socket(addr, 8888);
			System.out.println("이발사: 접속 성공");
			new BarberStaffFrame(socket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}