package staff;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import db.DAO;
import db.JungSanDTO;
import db.WaitingCustomer;
import db.ManagerDTO;
import server.TCPData;

import javax.swing.border.LineBorder;

public class Manager_Pan extends JFrame{

	ObjectOutputStream oos;
	ObjectInputStream ois;

	JFrame manager_F;
	JTable userhistory_Salestable,outlayhistory_Salestable, inven_table, itemAdd_table;
	CardLayout userhistoryMonitorCard,outlayhistoryMonitorCard;
	JTextArea ChattingTextArea;
	JTextField ChattingTextField;
	JPanel userhistoryMonitor,outlayhistoryMonitor;
	

	JDialog itemAdd_popup;
	JLabel itemAdd_lb=new JLabel();
	JButton itemAdd_btn=new JButton();

	DAO invendao = new DAO();
	String[][] userhistory_DB, outlayhistory_DB,
	inven_DB = invendao.invenList(),
	itemAdd_DB = invendao.itemAddList();
	String[] userhistorytableColumnName, outlayhistorytableColumnName,
	inventableColumnName = new String[] {"메 뉴","분 류","진열 재고","소비자 가격","창고 재고","도매 가격","상품 선택"},
	itemAddTableColumnName = new String[] {"메 뉴","분 류","판매 가격","입고 수량","도매 가격"};
	String nameS, name, userhistory_selectedDay,outlayhistory_selectedDay;

	DefaultTableModel invenTable = new DefaultTableModel(inven_DB,inventableColumnName);
	DefaultTableModel itemAddModel;
	ArrayList<JCheckBox> itemCheck_arr = new ArrayList<JCheckBox>();

	HashMap<String, String> storeAdd_map = new HashMap<String, String>();

	boolean addToItemOrStorage = true, userORoutlay = true;
	DataOutputStream cdos;
	DataInputStream cdis;

	JButton accesBtn, userhistorySearchButton, outlayhistorySearchButton, chattingSend;
	JRadioButton userhistory_allRadioButton,outlayhistory_allRadioButton;
	JToggleButton userhistoryDayButton, userhistoryMonthButton, outlayhistoryDayButton, outlayhistoryMonthButton;
	JLabel userhistory_resWon,outlayhistory_resWon;

	JCheckBox filInStoreHouse_CheckBox;
	JCheckBox userhistory_oneTicketCheckBox,userhistory_multiUseTicketCheckBox, userhistory_cutCheckBox, 
			  userhistory_beautyCheckBox, userhistory_cleanCheckBox, userhistory_massageCheckBox, 
			  userhistory_sweetsCheckBox, userhistory_drinksCheckBox, userhistory_foodCheckBox, userhistory_toiletriesCheckBox,
			  //---------------------------------------------------------------------------------------------------------------
			  outlayhistory_oneTicketCheckBox,outlayhistory_multiUseTicketCheckBox, outlayhistory_cutCheckBox, 
			  outlayhistory_beautyCheckBox, outlayhistory_cleanCheckBox, outlayhistory_massageCheckBox, 
			  outlayhistory_sweetsCheckBox, outlayhistory_drinksCheckBox, outlayhistory_foodCheckBox, outlayhistory_toiletriesCheckBox;

	JComboBox userhistory_yearComboBox, userhistory_monthComboBox, outlayhistory_yearComboBox, outlayhistory_monthComboBox;
	private JPanel invenTitle_P;
	private JButton refresh_btn;
	private JButton fillTheDisplay_btn;
	private JButton putInWarehouse_btn;

	CardLayout resPanelCard;
	JTable table;
	JLabel detailTitle;
	JComboBox categoryComboBox, subcategoryComboBox, jungsan_yearComboBox, jungsan_monthComboBox;
	int jungsan_selectedMonth, jungsan_selectedDay;
	DefaultTableCellRenderer renderer_right;
	DefaultTableCellRenderer renderer_center;

	JLabel  sesinConnected, barberConnected;
	JButton managerChattingSend;
	JTextField managerChattingTextField;
	JTextArea managerChattingTextArea;
	TCPData data;


	class Receiver extends Thread{
		@Override
		public void run() {
			try {

				while(ois!=null) {
					TCPData data = (TCPData) ois.readObject();
					if(data.kind.equals("채팅")) {
						managerChattingTextArea.append((String) data.sendObject+"\n");
						managerChattingTextArea.setCaretPosition(managerChattingTextArea.getDocument().getLength());
						//						System.out.println("채팅 받음");
					}
					else if(data.kind.equals("접속자목록")) {
						if(!((ArrayList)data.sendObject).contains("SesinStaffFrame")){
							sesinConnected.setBackground(Color.LIGHT_GRAY);
							sesinConnected.setText("세신사 OFF");
						}else {
							sesinConnected.setBackground(new Color(38, 83, 113));
							sesinConnected.setText("세신사 ON");
						}
						if(!((ArrayList)data.sendObject).contains("BarberStaffFrame")){
							barberConnected.setBackground(Color.LIGHT_GRAY);
							barberConnected.setText("이발사 OFF");
						}
						else {
							barberConnected.setBackground(new Color(38, 83, 113));
							barberConnected.setText("이발사 ON");
						}
						//						System.out.println("채팅접속중 받음");
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

	class TCPmultireceiver extends Thread{
		@Override
		public void run() {
			try {
				while(cdis!=null) {
					ChattingTextArea.append(cdis.readUTF()+"\n");
					ChattingTextArea.setCaretPosition(ChattingTextArea.getDocument().getLength());
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		}

	}

	void chBoxArr() {
		for (int i = 0; i < inven_DB.length; i++) {
			JCheckBox filInStore_CheckBox = new JCheckBox("상품 선택");
			filInStore_CheckBox.setFont(new Font("나눔고딕", Font.BOLD, 15));
			itemCheck_arr.add(filInStore_CheckBox);
		}
	}
	class TableCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer{
		int row; 
		boolean isSelected = false;

		public TableCell() {
		}
		public TableCell(JTableHeader tableHeader) {
			filInStoreHouse_CheckBox = new JCheckBox("전체 선택");
			filInStoreHouse_CheckBox.setFont(new Font("나눔고딕", Font.BOLD, 15));
			tableHeader.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					
					if(filInStoreHouse_CheckBox.isSelected()) {
						for (int i = 0; i < inven_DB.length; i++) {
							itemCheck_arr.get(i).setSelected(false);
						}
					}else if(!filInStoreHouse_CheckBox.isSelected()){
						for (int i = 0; i < inven_DB.length; i++) {
							itemCheck_arr.get(i).setSelected(true);
						}
					}
					filInStoreHouse_CheckBox.setSelected(!filInStoreHouse_CheckBox.isSelected());
				}
			});
		}
		@Override public Object getCellEditorValue() { return null;}

		@Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if(row<0) {return filInStoreHouse_CheckBox;}
			return itemCheck_arr.get(row);
		}
		@Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			if(row<0) {return filInStoreHouse_CheckBox;}
			return itemCheck_arr.get(row);
		}
	}

	public Manager_Pan(Socket socket) {
		//	public Manager_Pan_B() {	
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			System.out.println("oos, ois 연결 성공");

			data = new TCPData(new WaitingCustomer(), "서버", "Manager_Pan", "서버");

			oos.writeObject(data);
			oos.flush();
			oos.reset();
			System.out.println("oos 보냄");
		} catch (IOException e1) {
		}

		manager_F = new JFrame("관리자");
		manager_F.getContentPane().setBackground(new Color(0, 0, 0));
		manager_F.setBounds(0, 0, 960, 800);
		manager_F.getContentPane().setLayout(null);

		JPanel MonitorPanel = new JPanel();
		MonitorPanel.setBounds(0, 0, 946, 672);
		manager_F.getContentPane().add(MonitorPanel);
		CardLayout MonitorCard = new CardLayout();
		MonitorPanel.setLayout(MonitorCard);

		JPanel Title = new JPanel();
		Title.setBackground(new Color(0, 0, 0));
		Title.setBounds(10, 682, 922, 69);
		manager_F.getContentPane().add(Title);
		Title.setLayout(new GridLayout(1, 0, 20, 0));

		JButton inven_btn = new JButton("재 고");
		inven_btn.setBackground(new Color(152, 251, 152));
		inven_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setinvenTable();
				MonitorCard.show(MonitorPanel, "inven");
			}
		});
		inven_btn.setFont(new Font("나눔고딕", Font.BOLD, 24));
		Title.add(inven_btn);

		JButton userhistory_btn = new JButton("이용 내역");
		userhistory_btn.setBackground(new Color(152, 251, 152));
		userhistory_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userORoutlay = true;
				MonitorCard.show(MonitorPanel, "userhistory");
			}
		});
		userhistory_btn.setFont(new Font("나눔고딕", Font.BOLD, 24));
		Title.add(userhistory_btn);
		
		JButton outlayhistory_btn = new JButton("지출 내역");
		outlayhistory_btn.setBackground(new Color(152, 251, 152));
		outlayhistory_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userORoutlay = false;
				MonitorCard.show(MonitorPanel, "outlayhistory");
			}
		});
		outlayhistory_btn.setFont(new Font("나눔고딕", Font.BOLD, 24));
		Title.add(outlayhistory_btn);

		JButton settle_btn = new JButton("정 산");
		settle_btn.setBackground(new Color(152, 251, 152));
		settle_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MonitorCard.show(MonitorPanel, "jungsan");
			}
		});
		settle_btn.setFont(new Font("나눔고딕", Font.BOLD, 24));
		Title.add(settle_btn);

		JButton chat_btn = new JButton("채 팅");
		chat_btn.setBackground(new Color(152, 251, 152));
		chat_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MonitorCard.show(MonitorPanel, "Chatting");
				//				new letsgo();
			}
		});
		chat_btn.setFont(new Font("나눔고딕", Font.BOLD, 24));
		Title.add(chat_btn);

		//------------------------------------inventy
		JPanel inven_P = new JPanel();
		inven_P.setBackground(new Color(0, 0, 0));
		inven_P.setBounds(12, 10, 920, 651);
		MonitorPanel.add(inven_P,"inven");

		inven_table = new JTable(invenTable);
		inven_table.getTableHeader().setFont(new Font("나눔고딕", Font.BOLD, 20));
		inven_table.setSelectionBackground(Color.GRAY);
		inven_table.setForeground(Color.WHITE);
		inven_table.setCellSelectionEnabled(true);
		inven_table.setGridColor(Color.WHITE);
		inven_table.setBorder(new LineBorder(Color.DARK_GRAY));
		inven_table.setBackground(Color.DARK_GRAY);
		inven_table.setAutoscrolls(false);
		inven_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		inven_table.setFont(new Font("나눔고딕", Font.BOLD, 20));
		inven_table.setRowHeight(30);
		inven_table.setFillsViewportHeight(true);
		JScrollPane inven_list = new JScrollPane(inven_table);
		inven_list.setBackground(Color.DARK_GRAY);
		inven_list.setBounds(12, 68, 922, 604);

		chBoxArr();
		setinvenTable();

		inven_P.setLayout(null);
		
		invenTitle_P = new JPanel();
		invenTitle_P.setBackground(new Color(0, 0, 0));
		invenTitle_P.setBounds(12, 7, 922, 60);
		inven_P.add(invenTitle_P);
		invenTitle_P.setLayout(new GridLayout(1, 0, 50, 0));
		
		JDialog e_message = new JDialog(itemAdd_popup,"", true);
		e_message.getContentPane().setLayout(new FlowLayout());
		e_message.setUndecorated(true);
		e_message.setBounds(0,0, 300, 45);
		JLabel e_lb = new JLabel("숫자만 입력해 주세요");
		e_lb.setHorizontalAlignment(SwingConstants.CENTER);
		e_lb.setForeground(Color.RED);
		e_lb.setFont(new Font("나눔고딕", Font.PLAIN, 24));
		JButton e_btn = new JButton("확인");
		e_btn.addActionListener(e->{
			 e_message.dispose();
		});
		e_btn.setBackground(new Color(152, 251, 152));
		e_message.getContentPane().add(e_lb); 
		e_message.getContentPane().add(e_btn);
		
		e_message.setLocationRelativeTo(null);

		putInWarehouse_btn = new JButton("창고 상품 입고");
		putInWarehouse_btn.setBackground(new Color(152, 251, 152));
		putInWarehouse_btn.setFont(new Font("나눔고딕", Font.BOLD, 24));
		putInWarehouse_btn.addActionListener(e->{
			itemAdd_lb.setText("상품 입고"); 
			itemAdd_btn.setText("입고");
			
			addToItemOrStorage = false;
			if (!addToItemOrStorage) {
				itemAdd_DB = invendao.itemAddList();
				itemAdd_table.setModel(new DefaultTableModel(itemAdd_DB,itemAddTableColumnName) {
					@Override
					public boolean isCellEditable(int row, int column) {//해당 컬럼만 막고 열음
						//all cells false
						if (column<=1) {
							return false;  
						}
						return true;
					}
				});
				itemAdd_table.getColumnModel().getColumn(0).setPreferredWidth(195);
				itemAdd_table.getColumnModel().getColumn(1).setPreferredWidth(100);
				itemAdd_table.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						int row = itemAdd_table.getSelectedRow();
						int column = itemAdd_table.getSelectedColumn();
						//						System.out.println(row+", "+column);
						if(column==0) {
							char c = e.getKeyChar();
							if (!Character.isDigit(c)) {
								e.consume();
								return;
							}
						}
						if(column==1) {
							char c = e.getKeyChar();

							if (Character.isDigit(c)) {
								e.consume();
								return;
							}
						}
					}
				});
				itemAdd_table.getModel().addTableModelListener(new TableModelListener() {
					
					@Override
					public void tableChanged(TableModelEvent e) {
						try {
							String menu = (String) itemAdd_table.getValueAt(itemAdd_table.getSelectedRow(), 0);
							String category = (String) itemAdd_table.getValueAt(itemAdd_table.getSelectedRow(), 1);
							Integer retailPrice  = Integer.parseInt((String) itemAdd_table.getValueAt(itemAdd_table.getSelectedRow(), 2));
							Integer addStorage = Integer.parseInt((String) itemAdd_table.getValueAt(itemAdd_table.getSelectedRow(), 3));
							Integer wholesalePrice = Integer.parseInt((String) itemAdd_table.getValueAt(itemAdd_table.getSelectedRow(), 4));
							System.out.println("["+menu+"]["+category+"]["+retailPrice+"]["+addStorage+"]["+wholesalePrice+"]");
							storeAdd_map.put(menu,category+","+retailPrice+","+addStorage+","+wholesalePrice);
						} catch (Exception e1) {
							e_message.setVisible(true);
							
						}

					}
				});
			}
			itemAdd_popup.setVisible(true);

		});
		invenTitle_P.add(putInWarehouse_btn);

		fillTheDisplay_btn = new JButton("진열 상품 채우기");
		fillTheDisplay_btn.setBackground(new Color(152, 251, 152));
		fillTheDisplay_btn.setFont(new Font("나눔고딕", Font.BOLD, 24));
		fillTheDisplay_btn.addActionListener(e->{

			for (int i = 0; i < inven_DB.length; i++) {
				if(itemCheck_arr.get(i).isSelected()) {
					String menu = (String) inven_table.getValueAt(i, 0);
					Integer storage = Integer.parseInt((String) inven_table.getValueAt(i, 4));
					ManagerDTO dto = new ManagerDTO(menu,storage);
					invendao.invenFilInTheStore(dto);
				}
			}
			for (int i = 0; i < inven_DB.length; i++) {
				itemCheck_arr.get(i).setSelected(false);
			}
			setinvenTable();
//			refreshSender(new WaitingCustomer());
		});
		invenTitle_P.add(fillTheDisplay_btn);

		refresh_btn = new JButton("새로 고침");
		refresh_btn.setBackground(new Color(152, 251, 152));
		refresh_btn.setFont(new Font("나눔고딕", Font.BOLD, 24));
		refresh_btn.addActionListener(e->{
			setinvenTable();//재고 화면 새로 고침
			System.out.println("새로고침 완료");
		});
		invenTitle_P.add(refresh_btn);

		inven_P.add(inven_list);

		//-----------------입고용 팝업
		itemAdd_popup = new JDialog(manager_F,"", true);//윈도우 끄지않고는 뒤에꺼 안눌리게함
		
		itemAdd_popup.setBounds(0, 0, 540, 530);
		itemAdd_popup.getContentPane().setLayout(null);
		itemAdd_popup.setBackground(Color.DARK_GRAY);
		itemAdd_popup.setVisible(false);

		JPanel itemAdd_P = new JPanel();
		itemAdd_P.setBackground(Color.WHITE);
		itemAdd_P.setBounds(new Rectangle(0, 0, 484, 25));
		itemAdd_popup.getContentPane().add(itemAdd_P);
		itemAdd_P.setLayout(null);

		itemAdd_lb.setBounds(167, 0, 150, 25);
		itemAdd_lb.setFont(new Font("나눔고딕", Font.BOLD, 24));
		itemAdd_P.add(itemAdd_lb);
		itemAdd_lb.setHorizontalAlignment(SwingConstants.CENTER);

		itemAdd_btn.setBounds(393, 0, 89, 25);
		itemAdd_btn.setBackground(new Color(152, 251, 152));
		itemAdd_btn.setFont(new Font("나눔고딕", Font.BOLD, 15));
		itemAdd_btn.addActionListener(e->{
			if(itemAdd_btn.getText().equals("입고")) {
				storeHouseAdd(itemAdd_btn.getText());
			}else if(itemAdd_btn.getText().equals("추가")){
				storeHouseAdd(itemAdd_btn.getText());
			}
		});
		itemAdd_P.add(itemAdd_btn);

		JScrollPane itemAdd_scroll = new JScrollPane();
		itemAdd_scroll.setBounds(new Rectangle(0, 0, 650, 40));
		itemAdd_scroll.setBounds(0, 30, 540, 464);
		itemAdd_scroll.setBackground(Color.LIGHT_GRAY);
		itemAdd_popup.add(itemAdd_scroll);
		
		itemAddModel = new DefaultTableModel(itemAdd_DB,itemAddTableColumnName);

		itemAdd_table = new JTable(itemAddModel);
		itemAdd_table.getTableHeader().setFont(new Font("나눔고딕", Font.BOLD, 15));
		itemAdd_table.setSelectionBackground(Color.GRAY);
		itemAdd_table.setForeground(Color.BLACK);
		itemAdd_table.setCellSelectionEnabled(true);
		itemAdd_table.setGridColor(Color.WHITE);
		itemAdd_table.setAutoscrolls(false);
		itemAdd_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		itemAdd_table.setFont(new Font("나눔고딕", Font.BOLD, 20));
		itemAdd_table.setRowHeight(30);
		itemAdd_table.setFillsViewportHeight(true);
		itemAdd_table.setBorder(new LineBorder(Color.LIGHT_GRAY));
		itemAdd_table.setBackground(Color.LIGHT_GRAY);
		itemAdd_scroll.setViewportView(itemAdd_table);
		

		itemAdd_popup.setResizable(false);
		itemAdd_popup.setLocationRelativeTo(null);
		itemAdd_popup.setDefaultCloseOperation(itemAdd_popup.DISPOSE_ON_CLOSE);
		
		//--------------------------------------------------------------------사용자 이용 내역
		JPanel userhistory = new JPanel();
		userhistory.setBackground(new Color(0, 0, 0));
		MonitorPanel.add(userhistory, "userhistory");
		userhistory.setLayout(null);

		userhistoryMonitor = new JPanel();
		userhistoryMonitor.setBounds(5, 150, 934, 510);
		userhistory.add(userhistoryMonitor);
		userhistoryMonitorCard = new CardLayout();
		userhistoryMonitor.setLayout(userhistoryMonitorCard);

		userhistoryDayButton = new JToggleButton("일별");
		userhistoryDayButton.setFont(new Font("돋움", Font.BOLD, 30));
		userhistoryDayButton.setBackground(Color.WHITE);
		userhistoryDayButton.setBounds(12, 5, 455, 86);
		userhistoryDayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userhistorySearchButton.setEnabled(true);
				userhistoryMonitorCard.show(userhistoryMonitor, "userhistoryBlankPanel");
			}
		});
		userhistory.add(userhistoryDayButton);

		userhistoryMonthButton = new JToggleButton("월별");
		userhistoryMonthButton.setFont(new Font("돋움", Font.BOLD, 30));
		userhistoryMonthButton.setBackground(Color.WHITE);
		userhistoryMonthButton.setBounds(479, 5, 455, 86);
		userhistoryMonthButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userhistorySearchButton.setEnabled(true);
				userhistoryMonitorCard.show(userhistoryMonitor, "userhistoryBlankPanel");
			}
		});
		userhistory.add(userhistoryMonthButton);

		ButtonGroup userhistory_bg = new ButtonGroup();
		userhistory_bg.add(userhistoryDayButton);
		userhistory_bg.add(userhistoryMonthButton);

		Vector<String> userhistory_year = new Vector<String>();
		userhistory_year.add("2020");
		userhistory_year.add("2021");
		userhistory_year.add("2022");

		userhistory_yearComboBox = new JComboBox(userhistory_year);
		userhistory_yearComboBox.setFont(new Font("돋움", Font.BOLD, 15));
		userhistory_yearComboBox.setBounds(10, 102, 122, 40);
		userhistory_yearComboBox.setSelectedItem("2022");
		userhistory.add(userhistory_yearComboBox);

		Vector<String> userhistory_month = new Vector<String>();
		for (String ss : "01,02,03,04,05,06,07,08,09,10,11,12".split(",")) {
			userhistory_month.add(ss);
		}

		userhistory_monthComboBox = new JComboBox(userhistory_month);
		userhistory_monthComboBox.setFont(new Font("돋움", Font.BOLD, 15));
		userhistory_monthComboBox.setBounds(144, 102, 122, 40);
		userhistory_monthComboBox.setSelectedItem("04");
		userhistory.add(userhistory_monthComboBox);

		JPanel userhistoryBlankPanel = new JPanel();
		userhistoryMonitor.add(userhistoryBlankPanel, "userhistoryBlankPanel");

		JPanel userhistoryDayPanel = new JPanel();
		userhistoryMonitor.add(userhistoryDayPanel, "userhistoryDayPanel");
		userhistoryDayPanel.setLayout(null);

		JPanel userhistoryDayPanelUp = new JPanel();
		userhistoryDayPanelUp.setBounds(0, 2, 934, 35);
		userhistoryDayPanel.add(userhistoryDayPanelUp);
		userhistoryDayPanelUp.setLayout(new GridLayout(1, 7, 5, 5));

		for(int i = 0; i < 7; i++) {
			JLabel userhistory_jl = new JLabel();
			userhistory_jl.setBackground(Color.white);
			userhistory_jl.setOpaque(false);
			userhistory_jl.setHorizontalAlignment(JLabel.CENTER);
			userhistory_jl.setFont(new Font("돋움", Font.BOLD, 20));
			userhistory_jl.setText("일월화수목금토".charAt(i)+"");
			userhistoryDayPanelUp.add(userhistory_jl);
			if(i==0) {
				userhistory_jl.setForeground(Color.red);
			}
			if(i==6) {
				userhistory_jl.setForeground(Color.blue);
			}
		}

		JPanel userhistoryDayPanelDown = new JPanel();
		userhistoryDayPanelDown.setBounds(0, 37, 934, 473);
		userhistoryDayPanel.add(userhistoryDayPanelDown);
		userhistoryDayPanelDown.setLayout(new GridLayout(6, 7, 5, 5));

		ArrayList<JButton> userhistory_calendarButton = new ArrayList<JButton>();

		for (int i = 0; i < 42; i++) {
			JButton userhistory_jb = new JButton();
			userhistory_jb.setBackground(Color.white);
			userhistory_jb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					userhistoryMonitorCard.show(userhistoryMonitor, "userhistoryResPanel");
					userhistory_selectedDay = ((JButton)(e.getSource())).getText();
					calcDailySales(userhistory_selectedDay);
				}
			});
			userhistory_calendarButton.add(userhistory_jb);
			userhistoryDayPanelDown.add(userhistory_jb);
			if(i%7==6) {
				userhistory_jb.setForeground(Color.blue);
			}
			if(i%7==0) {
				userhistory_jb.setForeground(Color.red);
			}
		}

		userhistorySearchButton = new JButton("조회");
		userhistorySearchButton.setBackground(new Color(152, 251, 152));
		userhistorySearchButton.setFont(new Font("돋움", Font.BOLD, 20));
		userhistorySearchButton.setBounds(278, 107, 75, 28);
		userhistorySearchButton.setEnabled(false);
		userhistorySearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				userhistory_allRadioButton.setSelected(true);
				userhistory_oneTicketCheckBox.setSelected(true);
				userhistory_multiUseTicketCheckBox.setSelected(true);
				userhistory_cutCheckBox.setSelected(true);
				userhistory_beautyCheckBox.setSelected(true);
				userhistory_cleanCheckBox.setSelected(true);
				userhistory_massageCheckBox.setSelected(true);
				userhistory_sweetsCheckBox.setSelected(true);
				userhistory_drinksCheckBox.setSelected(true);
				userhistory_foodCheckBox.setSelected(true);
				userhistory_toiletriesCheckBox.setSelected(true);

				int userhistory_selectedYear = Integer.parseInt((String)userhistory_yearComboBox.getSelectedItem());
				int userhistory_selectedMonth = Integer.parseInt((String)userhistory_monthComboBox.getSelectedItem());

				if(userhistoryDayButton.isSelected()) {

					userhistoryMonitorCard.show(userhistoryMonitor, "userhistoryDayPanel");

					Calendar userhistory_selectedDay = Calendar.getInstance();
					userhistory_selectedDay.set(userhistory_selectedYear, userhistory_selectedMonth-1, 1);

					int userhistory_first = userhistory_selectedDay.get(Calendar.DAY_OF_WEEK)-1;
					int userhistory_last = userhistory_selectedDay.getActualMaximum(Calendar.DATE);

					int userhistory_day = 1;
					for (JButton userhistory_jb : userhistory_calendarButton) {
						userhistory_jb.setText(null);
						userhistory_jb.setFont(new Font("돋움", Font.BOLD, 20));
						userhistory_jb.setEnabled(true);
						userhistory_jb.setBackground(Color.white);
					}
					for (int i = userhistory_first; i < userhistory_last+userhistory_first; i++) {
						userhistory_calendarButton.get(i).setText(userhistory_day+"");
						userhistory_day++;
					}
					for (JButton userhistory_jb : userhistory_calendarButton) {
						if(userhistory_jb.getText()==null) {
							userhistory_jb.setEnabled(false);
							userhistory_jb.setBackground(Color.LIGHT_GRAY);
						}
					}
				}
				else if(userhistoryMonthButton.isSelected()) {
					userhistoryMonitorCard.show(userhistoryMonitor, "userhistoryResPanel");

					calcMonthlySales();
				}
			}
		});
		userhistory.add(userhistorySearchButton);

		JPanel userhistoryResPanel = new JPanel();
		userhistoryMonitor.add(userhistoryResPanel, "userhistoryResPanel");
		userhistoryResPanel.setLayout(null);

		userhistorytableColumnName = new String[] {"ID", "Locker", "Menu", "Price", "Time", "State"};

		userhistory_Salestable = new JTable();
		userhistory_Salestable.setFont(new Font("나눔고딕", Font.BOLD, 15));
		userhistory_Salestable.getTableHeader().setFont(new Font("나눔고딕", Font.BOLD, 20));

		JScrollPane userhistory_resScroll = new JScrollPane(userhistory_Salestable);
		userhistory_resScroll.setPreferredSize(new Dimension(450, 400));
		userhistory_resScroll.setBounds(134, 5, 792, 436);
		userhistoryResPanel.add(userhistory_resScroll);

		JLabel userhistory_resTotal = new JLabel("Total: ");
		userhistory_resTotal.setFont(new Font("돋움", Font.BOLD, 25));
		userhistory_resTotal.setBounds(500, 453, 90, 45);
		userhistoryResPanel.add(userhistory_resTotal);

		userhistory_resWon = new JLabel("원");
		userhistory_resWon.setFont(new Font("돋움", Font.BOLD, 25));
		userhistory_resWon.setBounds(722, 453, 188, 45);
		userhistory_resWon.setHorizontalAlignment(JLabel.RIGHT);
		userhistoryResPanel.add(userhistory_resWon);

		userhistory_allRadioButton = new JRadioButton("ALL");
		userhistory_allRadioButton.setFont(new Font("돋움", Font.BOLD, 20));
		userhistory_allRadioButton.setBounds(10, 0, 70, 50);
		userhistory_allRadioButton.setSelected(true);
		userhistory_allRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				boolean userhistory_allCheck= userhistory_oneTicketCheckBox.isSelected()&&userhistory_multiUseTicketCheckBox.isSelected()&&
						userhistory_cutCheckBox.isSelected()&&userhistory_beautyCheckBox.isSelected()&&userhistory_cleanCheckBox.isSelected()&&
						userhistory_massageCheckBox.isSelected()&&userhistory_sweetsCheckBox.isSelected()&&
						userhistory_drinksCheckBox.isSelected()&&userhistory_foodCheckBox.isSelected()&&userhistory_toiletriesCheckBox.isSelected();

				if (userhistory_allRadioButton.isSelected()) {
					userhistory_oneTicketCheckBox.setSelected(true);
					userhistory_multiUseTicketCheckBox.setSelected(true);
					userhistory_cutCheckBox.setSelected(true);
					userhistory_beautyCheckBox.setSelected(true);
					userhistory_cleanCheckBox.setSelected(true);
					userhistory_massageCheckBox.setSelected(true);
					userhistory_sweetsCheckBox.setSelected(true);
					userhistory_drinksCheckBox.setSelected(true);
					userhistory_foodCheckBox.setSelected(true);
					userhistory_toiletriesCheckBox.setSelected(true);
				} else {
					userhistory_oneTicketCheckBox.setSelected(false);
					userhistory_multiUseTicketCheckBox.setSelected(false);
					userhistory_cutCheckBox.setSelected(false);
					userhistory_beautyCheckBox.setSelected(false);
					userhistory_cleanCheckBox.setSelected(false);
					userhistory_massageCheckBox.setSelected(false);
					userhistory_sweetsCheckBox.setSelected(false);
					userhistory_drinksCheckBox.setSelected(false);
					userhistory_foodCheckBox.setSelected(false);
					userhistory_toiletriesCheckBox.setSelected(false);
				}
				if(userhistory_allCheck) {
					userhistory_DB = null;
					setuserhistoryTable();
					userhistory_resWon.setText("0 원");
				}
				else if(userhistoryMonthButton.isSelected()) {
					calcMonthlySales();
				}
				else if(userhistoryDayButton.isSelected()) {
					calcDailySales(userhistory_selectedDay);
				}
			}
		});
		userhistoryResPanel.add(userhistory_allRadioButton);
		//티켓 이발 미용 세신 마사지 과자 음료 식품 세면도구
		//Ticket haircut beauty clean massage sweets drinks food toiletries
		int userhistory_spacingItems = 40;
		//일일권
		userhistory_oneTicketCheckBox = new JCheckBox("일일권");
		userhistory_oneTicketCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		userhistory_oneTicketCheckBox.setBounds(20, 40, 90, 40);
		userhistory_oneTicketCheckBox.setSelected(true);
		userhistory_oneTicketCheckBox.addActionListener(new serviceSelect());
		userhistoryResPanel.add(userhistory_oneTicketCheckBox);
		//정기권
		userhistory_multiUseTicketCheckBox = new JCheckBox("정기권");
		userhistory_multiUseTicketCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		userhistory_multiUseTicketCheckBox.setBounds(20, 40+(userhistory_spacingItems*1), 90, 40);
		userhistory_multiUseTicketCheckBox.setSelected(true);
		userhistory_multiUseTicketCheckBox.addActionListener(new serviceSelect());
		userhistoryResPanel.add(userhistory_multiUseTicketCheckBox);
		//이발
		userhistory_cutCheckBox = new JCheckBox("이발");
		userhistory_cutCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		userhistory_cutCheckBox.setBounds(20, 40+(userhistory_spacingItems*2), 90, 40);
		userhistory_cutCheckBox.setSelected(true);
		userhistory_cutCheckBox.addActionListener(new serviceSelect());
		userhistoryResPanel.add(userhistory_cutCheckBox);
		//미용
		userhistory_beautyCheckBox = new JCheckBox("미용");
		userhistory_beautyCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		userhistory_beautyCheckBox.setBounds(20, 40+(userhistory_spacingItems*3), 90, 40);
		userhistory_beautyCheckBox.setSelected(true);
		userhistory_beautyCheckBox.addActionListener(new serviceSelect());
		userhistoryResPanel.add(userhistory_beautyCheckBox);
		//세신
		userhistory_cleanCheckBox = new JCheckBox("세신");
		userhistory_cleanCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		userhistory_cleanCheckBox.setBounds(20, 40+(userhistory_spacingItems*4), 90, 40);
		userhistory_cleanCheckBox.setSelected(true);
		userhistory_cleanCheckBox.addActionListener(new serviceSelect());
		userhistoryResPanel.add(userhistory_cleanCheckBox);
		//마사지
		userhistory_massageCheckBox = new JCheckBox("마사지");
		userhistory_massageCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		userhistory_massageCheckBox.setBounds(20, 40+(userhistory_spacingItems*5), 90, 40);
		userhistory_massageCheckBox.setSelected(true);
		userhistory_massageCheckBox.addActionListener(new serviceSelect());
		userhistoryResPanel.add(userhistory_massageCheckBox);
		//과자
		userhistory_sweetsCheckBox = new JCheckBox("과자");
		userhistory_sweetsCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		userhistory_sweetsCheckBox.setBounds(20, 40+(userhistory_spacingItems*6), 90, 40);
		userhistory_sweetsCheckBox.setSelected(true);
		userhistory_sweetsCheckBox.addActionListener(new serviceSelect());
		userhistoryResPanel.add(userhistory_sweetsCheckBox);
		//음료
		userhistory_drinksCheckBox = new JCheckBox("음료");
		userhistory_drinksCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		userhistory_drinksCheckBox.setBounds(20, 40+(userhistory_spacingItems*7), 90, 40);
		userhistory_drinksCheckBox.setSelected(true);
		userhistory_drinksCheckBox.addActionListener(new serviceSelect());
		userhistoryResPanel.add(userhistory_drinksCheckBox);
		//식품
		userhistory_foodCheckBox = new JCheckBox("식품");
		userhistory_foodCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		userhistory_foodCheckBox.setBounds(20, 40+(userhistory_spacingItems*8), 90, 40);
		userhistory_foodCheckBox.setSelected(true);
		userhistory_foodCheckBox.addActionListener(new serviceSelect());
		userhistoryResPanel.add(userhistory_foodCheckBox);
		//세면도구
		userhistory_toiletriesCheckBox = new JCheckBox("세면도구");
		userhistory_toiletriesCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		userhistory_toiletriesCheckBox.setBounds(20, 40+(userhistory_spacingItems*9), 90, 40);
		userhistory_toiletriesCheckBox.setSelected(true);
		userhistory_toiletriesCheckBox.addActionListener(new serviceSelect());
		userhistoryResPanel.add(userhistory_toiletriesCheckBox);
		
		//---------------------------지출내역-----------------------------
		
		JPanel outlayhistory = new JPanel();
		outlayhistory.setBackground(new Color(0, 0, 0));
		MonitorPanel.add(outlayhistory, "outlayhistory");
		outlayhistory.setLayout(null);

		outlayhistoryMonitor = new JPanel();
		outlayhistoryMonitor.setBounds(5, 150, 934, 510);
		outlayhistory.add(outlayhistoryMonitor);
		outlayhistoryMonitorCard = new CardLayout();
		outlayhistoryMonitor.setLayout(outlayhistoryMonitorCard);

		outlayhistoryDayButton = new JToggleButton("일별");
		outlayhistoryDayButton.setFont(new Font("돋움", Font.BOLD, 30));
		outlayhistoryDayButton.setBackground(Color.WHITE);
		outlayhistoryDayButton.setBounds(12, 5, 455, 86);
		outlayhistoryDayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				outlayhistorySearchButton.setEnabled(true);
				outlayhistoryMonitorCard.show(outlayhistoryMonitor, "outlayhistoryBlankPanel");
			}
		});
		outlayhistory.add(outlayhistoryDayButton);

		outlayhistoryMonthButton = new JToggleButton("월별");
		outlayhistoryMonthButton.setFont(new Font("돋움", Font.BOLD, 30));
		outlayhistoryMonthButton.setBackground(Color.WHITE);
		outlayhistoryMonthButton.setBounds(479, 5, 455, 86);
		outlayhistoryMonthButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				outlayhistorySearchButton.setEnabled(true);
				outlayhistoryMonitorCard.show(outlayhistoryMonitor, "outlayhistoryBlankPanel");
			}
		});
		outlayhistory.add(outlayhistoryMonthButton);

		ButtonGroup outlayhistory_bg = new ButtonGroup();
		outlayhistory_bg.add(outlayhistoryDayButton);
		outlayhistory_bg.add(outlayhistoryMonthButton);

		Vector<String> outlayhistory_year = new Vector<String>();
		outlayhistory_year.add("2020");
		outlayhistory_year.add("2021");
		outlayhistory_year.add("2022");

		outlayhistory_yearComboBox = new JComboBox(outlayhistory_year);
		outlayhistory_yearComboBox.setFont(new Font("돋움", Font.BOLD, 15));
		outlayhistory_yearComboBox.setBounds(10, 102, 122, 40);
		outlayhistory_yearComboBox.setSelectedItem("2022");
		outlayhistory.add(outlayhistory_yearComboBox);

		Vector<String> outlayhistory_month = new Vector<String>();
		for (String ss : "01,02,03,04,05,06,07,08,09,10,11,12".split(",")) {
			outlayhistory_month.add(ss);
		}

		outlayhistory_monthComboBox = new JComboBox(outlayhistory_month);
		outlayhistory_monthComboBox.setFont(new Font("돋움", Font.BOLD, 15));
		outlayhistory_monthComboBox.setBounds(144, 102, 122, 40);
		outlayhistory_monthComboBox.setSelectedItem("04");
		outlayhistory.add(outlayhistory_monthComboBox);

		JPanel outlayhistoryBlankPanel = new JPanel();
		outlayhistoryMonitor.add(outlayhistoryBlankPanel, "outlayhistoryBlankPanel");

		JPanel outlayhistoryDayPanel = new JPanel();
		outlayhistoryMonitor.add(outlayhistoryDayPanel, "outlayhistoryDayPanel");
		outlayhistoryDayPanel.setLayout(null);

		JPanel outlayhistoryDayPanelUp = new JPanel();
		outlayhistoryDayPanelUp.setBounds(0, 2, 934, 35);
		outlayhistoryDayPanel.add(outlayhistoryDayPanelUp);
		outlayhistoryDayPanelUp.setLayout(new GridLayout(1, 7, 5, 5));

		for(int i = 0; i < 7; i++) {
			JLabel outlayhistory_jl = new JLabel();
			outlayhistory_jl.setBackground(Color.white);
			outlayhistory_jl.setOpaque(false);
			outlayhistory_jl.setHorizontalAlignment(JLabel.CENTER);
			outlayhistory_jl.setFont(new Font("돋움", Font.BOLD, 20));
			outlayhistory_jl.setText("일월화수목금토".charAt(i)+"");
			outlayhistoryDayPanelUp.add(outlayhistory_jl);
			if(i==0) {
				outlayhistory_jl.setForeground(Color.red);
			}
			if(i==6) {
				outlayhistory_jl.setForeground(Color.blue);
			}
		}

		JPanel outlayhistoryDayPanelDown = new JPanel();
		outlayhistoryDayPanelDown.setBounds(0, 37, 934, 473);
		outlayhistoryDayPanel.add(outlayhistoryDayPanelDown);
		outlayhistoryDayPanelDown.setLayout(new GridLayout(6, 7, 5, 5));

		ArrayList<JButton> outlayhistory_calendarButton = new ArrayList<JButton>();

		for (int i = 0; i < 42; i++) {
			JButton outlayhistory_jb = new JButton();
			outlayhistory_jb.setBackground(Color.white);
			outlayhistory_jb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					outlayhistoryMonitorCard.show(outlayhistoryMonitor, "outlayhistoryResPanel");
					outlayhistory_selectedDay = ((JButton)(e.getSource())).getText();
					calcDailySales(outlayhistory_selectedDay);
				}
			});
			outlayhistory_calendarButton.add(outlayhistory_jb);
			outlayhistoryDayPanelDown.add(outlayhistory_jb);
			if(i%7==6) {
				outlayhistory_jb.setForeground(Color.blue);
			}
			if(i%7==0) {
				outlayhistory_jb.setForeground(Color.red);
			}
		}

		outlayhistorySearchButton = new JButton("조회");
		outlayhistorySearchButton.setBackground(new Color(152, 251, 152));
		outlayhistorySearchButton.setFont(new Font("돋움", Font.BOLD, 20));
		outlayhistorySearchButton.setBounds(278, 107, 75, 28);
		outlayhistorySearchButton.setEnabled(false);
		outlayhistorySearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				outlayhistory_sweetsCheckBox.setSelected(true);
				outlayhistory_drinksCheckBox.setSelected(true);
				outlayhistory_foodCheckBox.setSelected(true);
				outlayhistory_toiletriesCheckBox.setSelected(true);

				int outlayhistory_selectedYear = Integer.parseInt((String)outlayhistory_yearComboBox.getSelectedItem());
				int outlayhistory_selectedMonth = Integer.parseInt((String)outlayhistory_monthComboBox.getSelectedItem());

				if(outlayhistoryDayButton.isSelected()) {

					outlayhistoryMonitorCard.show(outlayhistoryMonitor, "outlayhistoryDayPanel");

					Calendar outlayhistory_selectedDay = Calendar.getInstance();
					outlayhistory_selectedDay.set(outlayhistory_selectedYear, outlayhistory_selectedMonth-1, 1);

					int outlayhistory_first = outlayhistory_selectedDay.get(Calendar.DAY_OF_WEEK)-1;
					int outlayhistory_last = outlayhistory_selectedDay.getActualMaximum(Calendar.DATE);

					int outlayhistory_day = 1;
					for (JButton outlayhistory_jb : outlayhistory_calendarButton) {
						outlayhistory_jb.setText(null);
						outlayhistory_jb.setFont(new Font("돋움", Font.BOLD, 20));
						outlayhistory_jb.setEnabled(true);
						outlayhistory_jb.setBackground(Color.white);
					}
					for (int i = outlayhistory_first; i < outlayhistory_last+outlayhistory_first; i++) {
						outlayhistory_calendarButton.get(i).setText(outlayhistory_day+"");
						outlayhistory_day++;
					}
					for (JButton outlayhistory_jb : outlayhistory_calendarButton) {
						if(outlayhistory_jb.getText()==null) {
							outlayhistory_jb.setEnabled(false);
							outlayhistory_jb.setBackground(Color.LIGHT_GRAY);
						}
					}
				}
				else if(outlayhistoryMonthButton.isSelected()) {
					outlayhistoryMonitorCard.show(outlayhistoryMonitor, "outlayhistoryResPanel");

					calcMonthlySales();//수정 필요
				}
			}
		});
		outlayhistory.add(outlayhistorySearchButton);

		JPanel outlayhistoryResPanel = new JPanel();
		outlayhistoryMonitor.add(outlayhistoryResPanel, "outlayhistoryResPanel");
		outlayhistoryResPanel.setLayout(null);
		//intime(입고 시간) sep(상세 항목)  menu(제품명) quantity(수량) wholesaleprice(도매가)
		outlayhistorytableColumnName = new String[] {"입고 시간", "종 류", "메 뉴", "수 량", "도매가"};//바꿔야됨

		outlayhistory_Salestable = new JTable();
		
		outlayhistory_Salestable.setFont(new Font("나눔고딕", Font.BOLD, 15));
		outlayhistory_Salestable.getTableHeader().setFont(new Font("나눔고딕", Font.BOLD, 20));

		JScrollPane outlayhistory_resScroll = new JScrollPane(outlayhistory_Salestable);
		outlayhistory_resScroll.setPreferredSize(new Dimension(450, 400));
		outlayhistory_resScroll.setBounds(134, 5, 792, 436);
		outlayhistoryResPanel.add(outlayhistory_resScroll);

		JLabel outlayhistory_resTotal = new JLabel("Total: ");
		outlayhistory_resTotal.setFont(new Font("돋움", Font.BOLD, 25));
		outlayhistory_resTotal.setBounds(500, 453, 90, 45);
		outlayhistoryResPanel.add(outlayhistory_resTotal);

		outlayhistory_resWon = new JLabel("원");
		outlayhistory_resWon.setFont(new Font("돋움", Font.BOLD, 25));
		outlayhistory_resWon.setBounds(722, 453, 188, 45);
		outlayhistory_resWon.setHorizontalAlignment(JLabel.RIGHT);
		outlayhistoryResPanel.add(outlayhistory_resWon);

		outlayhistory_allRadioButton = new JRadioButton("ALL");
		outlayhistory_allRadioButton.setFont(new Font("돋움", Font.BOLD, 20));
		outlayhistory_allRadioButton.setBounds(10, 0, 70, 50);
		outlayhistory_allRadioButton.setSelected(true);
		outlayhistory_allRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				boolean outlayhistory_allCheck= outlayhistory_sweetsCheckBox.isSelected()&&outlayhistory_drinksCheckBox.isSelected()
											  &&outlayhistory_foodCheckBox.isSelected()&&outlayhistory_toiletriesCheckBox.isSelected();

				if (outlayhistory_allRadioButton.isSelected()) {
					outlayhistory_sweetsCheckBox.setSelected(true);
					outlayhistory_drinksCheckBox.setSelected(true);
					outlayhistory_foodCheckBox.setSelected(true);
					outlayhistory_toiletriesCheckBox.setSelected(true);
				} else {
					outlayhistory_sweetsCheckBox.setSelected(false);
					outlayhistory_drinksCheckBox.setSelected(false);
					outlayhistory_foodCheckBox.setSelected(false);
					outlayhistory_toiletriesCheckBox.setSelected(false);
				}
				if(outlayhistory_allCheck) {
					outlayhistory_DB = null;
					setoutlayhistoryTable();
					outlayhistory_resWon.setText("0 원");
				}
				else if(outlayhistoryMonthButton.isSelected()) {
					calcMonthlySales();
				}
				else if(outlayhistoryDayButton.isSelected()) {
					calcDailySales(outlayhistory_selectedDay);
				}
			}
		});
		outlayhistoryResPanel.add(outlayhistory_allRadioButton);
		//과자 음료 식품 세면도구
		int outlayhistory_spacingItems = 40;
		
		//과자
		outlayhistory_sweetsCheckBox = new JCheckBox("과자");
		outlayhistory_sweetsCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		outlayhistory_sweetsCheckBox.setBounds(20, 40+(outlayhistory_spacingItems*0), 90, 40);
		outlayhistory_sweetsCheckBox.setSelected(true);
		outlayhistory_sweetsCheckBox.addActionListener(new serviceSelect());
		outlayhistoryResPanel.add(outlayhistory_sweetsCheckBox);
		//음료
		outlayhistory_drinksCheckBox = new JCheckBox("음료");
		outlayhistory_drinksCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		outlayhistory_drinksCheckBox.setBounds(20, 40+(outlayhistory_spacingItems*1), 90, 40);
		outlayhistory_drinksCheckBox.setSelected(true);
		outlayhistory_drinksCheckBox.addActionListener(new serviceSelect());
		outlayhistoryResPanel.add(outlayhistory_drinksCheckBox);
		//식품
		outlayhistory_foodCheckBox = new JCheckBox("식품");
		outlayhistory_foodCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		outlayhistory_foodCheckBox.setBounds(20, 40+(outlayhistory_spacingItems*2), 90, 40);
		outlayhistory_foodCheckBox.setSelected(true);
		outlayhistory_foodCheckBox.addActionListener(new serviceSelect());
		outlayhistoryResPanel.add(outlayhistory_foodCheckBox);
		//세면도구
		outlayhistory_toiletriesCheckBox = new JCheckBox("세면도구");
		outlayhistory_toiletriesCheckBox.setFont(new Font("돋움", Font.BOLD, 15));
		outlayhistory_toiletriesCheckBox.setBounds(20, 40+(outlayhistory_spacingItems*3), 90, 40);
		outlayhistory_toiletriesCheckBox.setSelected(true);
		outlayhistory_toiletriesCheckBox.addActionListener(new serviceSelect());
		outlayhistoryResPanel.add(outlayhistory_toiletriesCheckBox);
		
		//--------------------------------------------------------------(진격의) 정어엉사아안화아며어언
		JPanel jungsanPanel = new JPanel();
		jungsanPanel.setBackground(new Color(0, 0, 0));
		jungsanPanel.setBounds(0, 0, 950, 651);
		MonitorCard.show(MonitorPanel, "jungsan");
		jungsanPanel.setLayout(null);

		jungsan_yearComboBox = new JComboBox();
		jungsan_yearComboBox.setModel(new DefaultComboBoxModel(new String[] {"2020", "2021", "2022"}));
		jungsan_yearComboBox.setBounds(45, 24, 160, 47);
		jungsan_yearComboBox.setSelectedItem("2022");
		jungsanPanel.add(jungsan_yearComboBox);

		jungsan_monthComboBox = new JComboBox();
		jungsan_monthComboBox.setModel(new DefaultComboBoxModel(new String[] {"ALL", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"}));
		jungsan_monthComboBox.setBounds(217, 24, 160, 47);
		jungsanPanel.add(jungsan_monthComboBox);

		JLabel totGain = new JLabel("");
		totGain.setOpaque(true);
		totGain.setBackground(Color.LIGHT_GRAY);
		
		totGain.setHorizontalAlignment(SwingConstants.RIGHT);
		totGain.setFont(new Font("나눔고딕", Font.BOLD, 23));
		totGain.setBounds(734, 24, 181, 47);
		jungsanPanel.add(totGain);

		JLabel totMinus = new JLabel("");
		totMinus.setForeground(new Color(255, 255, 255));
		totMinus.setHorizontalAlignment(SwingConstants.RIGHT);
		totMinus.setFont(new Font("나눔고딕", Font.PLAIN, 20));
		totMinus.setBounds(591, 13, 131, 23);
		jungsanPanel.add(totMinus);

		JLabel totPlus = new JLabel("");
		totPlus.setForeground(new Color(255, 255, 255));
		totPlus.setHorizontalAlignment(SwingConstants.RIGHT);
		totPlus.setFont(new Font("나눔고딕", Font.PLAIN, 20));
		totPlus.setBounds(591, 52, 131, 23);
		jungsanPanel.add(totPlus);

		JPanel resPanel = new JPanel();
		resPanel.setBounds(45, 93, 870, 558);
		resPanelCard = new CardLayout();
		resPanel.setLayout(resPanelCard);

		JPanel yearTotRes = new JPanel();
		yearTotRes.setBackground(Color.WHITE);

		ArrayList<JPanel> yearEachPanel = new ArrayList<JPanel>();
		ArrayList<JLabel> yearEachMinus = new ArrayList<JLabel>();
		ArrayList<JLabel> yearEachPlus = new ArrayList<JLabel>();
		ArrayList<JLabel> yearEachRealGain = new ArrayList<JLabel>();

		for (int i = 0; i < 12; i++) {
			int x = 5;
			if(i>5) {
				x = 440;
			}
			JPanel eachResPanel = new JPanel();
			eachResPanel.setBounds(x, 60+80*(i%6), 420, 50);
			eachResPanel.setBackground(Color.white);
			eachResPanel.setLayout(null);
			eachResPanel.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {}
				@Override
				public void mousePressed(MouseEvent e) {}
				@Override
				public void mouseExited(MouseEvent e) {}
				@Override
				public void mouseEntered(MouseEvent e) {}
				@Override
				public void mouseClicked(MouseEvent e) {
					jungsan_selectedMonth = (yearEachPanel.indexOf(e.getSource())+1);
					jungsan_selectedDay = 0;

					categoryComboBox.setSelectedItem("ALL");
					detailTitle.setText(jungsan_yearComboBox.getSelectedItem() + "년 " + jungsan_selectedMonth + "월");

					resPanelCard.show(resPanel, "detailRes");
				}
			});
			yearEachPanel.add(eachResPanel);

			JLabel jungsan_month = new JLabel((i+1)+"월");
			jungsan_month.setFont(new Font("나눔고딕", Font.BOLD, 20));
			jungsan_month.setBounds(0, 0, 70, 50);
			jungsan_month.setHorizontalAlignment(JLabel.CENTER);
			eachResPanel.add(jungsan_month);

			JLabel jungsan_minus = new JLabel("");
			jungsan_minus.setFont(new Font("나눔고딕", Font.PLAIN, 15));
			jungsan_minus.setBounds(60, 0, 100, 50);
			jungsan_minus.setHorizontalAlignment(JLabel.RIGHT);
			eachResPanel.add(jungsan_minus);

			JLabel plus = new JLabel("");
			plus.setFont(new Font("나눔고딕", Font.PLAIN, 15));
			plus.setBounds(180, 0, 100, 50);
			plus.setHorizontalAlignment(JLabel.RIGHT);
			eachResPanel.add(plus);

			JLabel realGain = new JLabel("");
			realGain.setFont(new Font("나눔고딕", Font.BOLD, 15));
			realGain.setBounds(300, 0, 100, 50);
			eachResPanel.add(realGain);
			realGain.setHorizontalAlignment(JLabel.RIGHT);
			yearTotRes.add(eachResPanel);		

			yearEachMinus.add(jungsan_minus);
			yearEachPlus.add(plus);
			yearEachRealGain.add(realGain);
		}

		resPanel.add(yearTotRes, "yearTotRes");
		yearTotRes.setLayout(null);

		JLabel seperateLine = new JLabel("");
		seperateLine.setBackground(Color.LIGHT_GRAY);
		seperateLine.setOpaque(true);
		seperateLine.setBounds(433, 72, 1, 446);
		yearTotRes.add(seperateLine);

		JLabel minusTitle1 = new JLabel("총 지출");
		minusTitle1.setHorizontalAlignment(SwingConstants.CENTER);
		minusTitle1.setFont(new Font("나눔고딕", Font.PLAIN, 17));
		minusTitle1.setBounds(88, 21, 79, 29);
		yearTotRes.add(minusTitle1);

		JLabel plusTitle1 = new JLabel("총 매출");
		plusTitle1.setHorizontalAlignment(SwingConstants.CENTER);
		plusTitle1.setFont(new Font("나눔고딕", Font.PLAIN, 17));
		plusTitle1.setBounds(199, 21, 79, 29);
		yearTotRes.add(plusTitle1);

		JLabel gainTitle1 = new JLabel("순 수익");
		gainTitle1.setHorizontalAlignment(SwingConstants.CENTER);
		gainTitle1.setFont(new Font("나눔고딕", Font.PLAIN, 17));
		gainTitle1.setBounds(313, 21, 79, 29);
		yearTotRes.add(gainTitle1);

		JLabel minusTitle2 = new JLabel("총 지출");
		minusTitle2.setHorizontalAlignment(SwingConstants.CENTER);
		minusTitle2.setFont(new Font("나눔고딕", Font.PLAIN, 17));
		minusTitle2.setBounds(527, 21, 79, 29);
		yearTotRes.add(minusTitle2);

		JLabel plusTitle2 = new JLabel("총 매출");
		plusTitle2.setHorizontalAlignment(SwingConstants.CENTER);
		plusTitle2.setFont(new Font("나눔고딕", Font.PLAIN, 17));
		plusTitle2.setBounds(638, 21, 79, 29);
		yearTotRes.add(plusTitle2);

		JLabel gainTitle2 = new JLabel("순 수익");
		gainTitle2.setHorizontalAlignment(SwingConstants.CENTER);
		gainTitle2.setFont(new Font("나눔고딕", Font.PLAIN, 17));
		gainTitle2.setBounds(752, 21, 79, 29);
		yearTotRes.add(gainTitle2);

		jungsanPanel.add(resPanel);

		JPanel monthTotRes = new JPanel();
		monthTotRes.setBackground(Color.WHITE);
		monthTotRes.setLayout(null);

		JLabel minusTitle3 = new JLabel("총 지출");
		minusTitle3.setHorizontalAlignment(SwingConstants.CENTER);
		minusTitle3.setFont(new Font("나눔고딕", Font.PLAIN, 17));
		minusTitle3.setBounds(88, 21, 79, 29);
		monthTotRes.add(minusTitle3);

		JLabel plusTitle3 = new JLabel("총 매출");
		plusTitle3.setHorizontalAlignment(SwingConstants.CENTER);
		plusTitle3.setFont(new Font("나눔고딕", Font.PLAIN, 17));
		plusTitle3.setBounds(199, 21, 79, 29);
		monthTotRes.add(plusTitle3);

		JLabel gainTitle3 = new JLabel("순 수익");
		gainTitle3.setHorizontalAlignment(SwingConstants.CENTER);
		gainTitle3.setFont(new Font("나눔고딕", Font.PLAIN, 17));
		gainTitle3.setBounds(313, 21, 79, 29);
		monthTotRes.add(gainTitle3);

		JLabel minusTitle4 = new JLabel("총 지출");
		minusTitle4.setHorizontalAlignment(SwingConstants.CENTER);
		minusTitle4.setFont(new Font("나눔고딕", Font.PLAIN, 17));
		minusTitle4.setBounds(527, 21, 79, 29);
		monthTotRes.add(minusTitle4);

		JLabel plusTitle4 = new JLabel("총 매출");
		plusTitle4.setHorizontalAlignment(SwingConstants.CENTER);
		plusTitle4.setFont(new Font("나눔고딕", Font.PLAIN, 17));
		plusTitle4.setBounds(638, 21, 79, 29);
		monthTotRes.add(plusTitle4);

		JLabel gainTitle4 = new JLabel("순 수익");
		gainTitle4.setHorizontalAlignment(SwingConstants.CENTER);
		gainTitle4.setFont(new Font("나눔고딕", Font.PLAIN, 17));
		gainTitle4.setBounds(752, 21, 79, 29);
		monthTotRes.add(gainTitle4);

		ArrayList<JPanel> monthEachPanel = new ArrayList<JPanel>();
		ArrayList<JLabel> monthEachMinus = new ArrayList<JLabel>();
		ArrayList<JLabel> monthEachPlus = new ArrayList<JLabel>();
		ArrayList<JLabel> monthEachRealGain = new ArrayList<JLabel>();
		ArrayList<JLabel> dayarr = new ArrayList<JLabel>();

		for (int i = 0; i < 31; i++) {
			int x = 5;
			if(i>14) {
				x = 450;
			}
			JPanel eachResPanel = new JPanel();
			eachResPanel.setBounds(x, 60+30*(i%15), 420, 25);
			if(i==30) {
				eachResPanel.setBounds(x, 60+30*15, 420, 25);
			}
			eachResPanel.setBackground(Color.white);
			eachResPanel.setLayout(null);
			eachResPanel.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {}
				@Override
				public void mousePressed(MouseEvent e) {}
				@Override
				public void mouseExited(MouseEvent e) {}
				@Override
				public void mouseEntered(MouseEvent e) {}
				@Override
				public void mouseClicked(MouseEvent e) {

					jungsan_selectedMonth = Integer.parseInt((String) jungsan_monthComboBox.getSelectedItem());
					jungsan_selectedDay = (monthEachPanel.indexOf(e.getSource())+1);
					categoryComboBox.setSelectedItem("ALL");
					detailTitle.setText(jungsan_yearComboBox.getSelectedItem() + "년 " + jungsan_monthComboBox.getSelectedItem() + "월 " + jungsan_selectedDay +"일");
					resPanelCard.show(resPanel, "detailRes");
				}
			});
			monthEachPanel.add(eachResPanel);

			JLabel day = new JLabel();
			day.setFont(new Font("나눔고딕", Font.BOLD, 15));
			day.setBounds(0, 2, 70, 20);
			day.setHorizontalAlignment(JLabel.CENTER);
			eachResPanel.add(day);
			dayarr.add(day);

			JLabel minus = new JLabel("");
			minus.setFont(new Font("나눔고딕", Font.PLAIN, 13));
			minus.setBounds(60, 2, 100, 20);
			minus.setHorizontalAlignment(JLabel.RIGHT);
			eachResPanel.add(minus);

			JLabel plus = new JLabel("");
			plus.setFont(new Font("나눔고딕", Font.PLAIN, 13));
			plus.setBounds(180, 2, 100, 20);
			plus.setHorizontalAlignment(JLabel.RIGHT);
			eachResPanel.add(plus);

			JLabel realGain = new JLabel("");
			realGain.setFont(new Font("나눔고딕", Font.BOLD, 13));
			realGain.setBounds(300, 2, 100, 20);
			realGain.setHorizontalAlignment(JLabel.RIGHT);
			eachResPanel.add(realGain);
			monthTotRes.add(eachResPanel);		

			monthEachMinus.add(minus);
			monthEachPlus.add(plus);
			monthEachRealGain.add(realGain);
		}

		JLabel seperateLine2 = new JLabel("");
		seperateLine2.setBackground(Color.LIGHT_GRAY);
		seperateLine2.setOpaque(true);
		seperateLine2.setBounds(433, 20, 1, 508);
		monthTotRes.add(seperateLine2);

		resPanel.add(monthTotRes, "monthTotRes");



		JPanel detailRes = new JPanel();
		detailRes.setLayout(null);


		categoryComboBox = new JComboBox();
		categoryComboBox.setBounds(183, 27, 80, 29);
		categoryComboBox.setModel(new DefaultComboBoxModel(new String[] {"ALL", "이용권", "이발", "세신", "매점"}));
		categoryComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(((String) categoryComboBox.getSelectedItem()).equals("ALL")) {
					subcategoryComboBox.setSelectedItem("ALL");
					subcategoryComboBox.setEnabled(false);
				}
				else if(((String) categoryComboBox.getSelectedItem()).equals("이용권")) {
					subcategoryComboBox.setEnabled(true);
					subcategoryComboBox.setModel(new DefaultComboBoxModel(new String[] {"ALL", "일일권", "정기권"}));
					subcategoryComboBox.setSelectedItem("ALL");
				}
				else if(((String) categoryComboBox.getSelectedItem()).equals("이발")) {
					subcategoryComboBox.setEnabled(true);
					subcategoryComboBox.setModel(new DefaultComboBoxModel(new String[] {"ALL", "이발", "미용"}));
					subcategoryComboBox.setSelectedItem("ALL");
				}
				else if(((String) categoryComboBox.getSelectedItem()).equals("세신")) {
					subcategoryComboBox.setEnabled(true);
					subcategoryComboBox.setModel(new DefaultComboBoxModel(new String[] {"ALL", "세신", "마사지"}));
					subcategoryComboBox.setSelectedItem("ALL");
				}
				else if(((String) categoryComboBox.getSelectedItem()).equals("매점")) {
					subcategoryComboBox.setEnabled(true);
					subcategoryComboBox.setModel(new DefaultComboBoxModel(new String[] {"ALL", "과자", "음료", "식품", "세면도구"}));
					subcategoryComboBox.setSelectedItem("ALL");
				}
			}
		});
		detailRes.add(categoryComboBox);

		subcategoryComboBox = new JComboBox();
		subcategoryComboBox.setBounds(271, 27, 80, 29);
		subcategoryComboBox.setEnabled(false);
		subcategoryComboBox.setModel(new DefaultComboBoxModel(new String[] {"ALL"}));
		subcategoryComboBox.setSelectedItem("ALL");
		subcategoryComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(jungsan_selectedDay==0) {
					monthDetail();
				}
				else {
					dayDetail();
				}
				table.setRowHeight(30);
			}
		});
		detailRes.add(subcategoryComboBox);

		resPanel.add(detailRes, "detailRes");

		detailTitle = new JLabel();
		detailTitle.setFont(new Font("나눔고딕", Font.BOLD, 20));
		detailTitle.setBounds(12, 17, 200, 50);
		detailRes.add(detailTitle);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 66, 846, 466);
		detailRes.add(scrollPane);

		table = new JTable();
		table.setEnabled(false);

		table.setFont(new Font("나눔고딕", Font.PLAIN, 12));
		table.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
						"분류", "메뉴명", "지출액", "매출액", "순수익"
				}
				));

		renderer_right = new DefaultTableCellRenderer();
		renderer_right.setHorizontalAlignment(JLabel.RIGHT);
		renderer_center = new DefaultTableCellRenderer();
		renderer_center.setHorizontalAlignment(JLabel.CENTER);
		table.getColumn("지출액").setCellRenderer(renderer_right);

		scrollPane.setViewportView(table);


		JButton goButton = new JButton("조회");
		goButton.setBackground(new Color(152, 251, 152));
		goButton.setFont(new Font("돋움", Font.BOLD, 20));
		goButton.setBounds(400, 30, 91, 35);
		goButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String selectedYear = (String) jungsan_yearComboBox.getSelectedItem();
				String selectedMonth = (String) jungsan_monthComboBox.getSelectedItem();

				if(((String)(jungsan_monthComboBox.getSelectedItem())).equals("ALL")) {

					ArrayList<JungSanDTO> arr = new DAO().yearJungsan(selectedYear);
					int yearTotMinus = 0;
					int yearTotPlus = 0;
					int yearTotRealGain = 0;
					for (int i = 0; i < arr.size(); i++) {
						yearEachMinus.get(i).setText(new DecimalFormat().format(arr.get(i).minus));
						yearTotMinus += arr.get(i).minus;
						yearEachPlus.get(i).setText(new DecimalFormat().format(arr.get(i).plus));
						yearTotPlus += arr.get(i).plus;
						yearEachRealGain.get(i).setText(new DecimalFormat().format(arr.get(i).realGain));

						if(arr.get(i).realGain>0){
							yearEachRealGain.get(i).setForeground(Color.blue);
						}
						else if(arr.get(i).realGain==0) {
							yearEachRealGain.get(i).setForeground(Color.black);
						}
						else {
							yearEachRealGain.get(i).setForeground(Color.red);
						}
						yearTotRealGain += arr.get(i).realGain;


					}

					for (JPanel jp : yearEachPanel) {
						jp.setBorder(null);
					}

					int maxIdx = 0;
					int minIdx = 0;
					for (int i = 0; i < yearEachRealGain.size(); i++) {
						try {
							if((new DecimalFormat().parse(yearEachRealGain.get(i).getText())).intValue()
									< (new DecimalFormat().parse(yearEachRealGain.get(minIdx).getText())).intValue()){
								minIdx = i;
							}
							if((new DecimalFormat().parse(yearEachRealGain.get(i).getText())).intValue()
									> (new DecimalFormat().parse(yearEachRealGain.get(maxIdx).getText())).intValue()){
								maxIdx = i;
							}

						} catch (Exception e1) {
							e1.printStackTrace();
						}

					}

					yearEachPanel.get(minIdx).setBorder(new LineBorder(Color.red));
					yearEachPanel.get(maxIdx).setBorder(new LineBorder(Color.blue));

					totMinus.setText(new DecimalFormat().format(yearTotMinus));
					totPlus.setText(new DecimalFormat().format(yearTotPlus));
					totGain.setText(new DecimalFormat().format(yearTotRealGain));

					if(yearTotRealGain>0){
						totGain.setForeground(Color.blue);
					}
					else if(yearTotRealGain==0) {
						totGain.setForeground(Color.black);
					}
					else {
						totGain.setForeground(Color.red);
					}

					resPanel.revalidate();
					resPanel.repaint();

					resPanelCard.show(resPanel, "yearTotRes");
				}
				else {
					ArrayList<JungSanDTO> arr = new DAO().monthJungsan(selectedYear, selectedMonth);
					int monthTotMinus = 0;
					int monthTotPlus = 0;
					int monthTotRealGain = 0;
					for (int i = 0; i < arr.size(); i++) {
						dayarr.get(i).setText((i+1)+"일");
						monthEachMinus.get(i).setText(new DecimalFormat().format(arr.get(i).minus));
						monthTotMinus += arr.get(i).minus;
						monthEachPlus.get(i).setText(new DecimalFormat().format(arr.get(i).plus));
						monthTotPlus += arr.get(i).plus;
						monthEachRealGain.get(i).setText(new DecimalFormat().format(arr.get(i).realGain));
						monthTotRealGain += arr.get(i).realGain;

						if(arr.get(i).realGain>0){
							monthEachRealGain.get(i).setForeground(Color.blue);
						}
						else if(arr.get(i).realGain==0) {
							monthEachRealGain.get(i).setForeground(Color.black);
						}
						else {
							monthEachRealGain.get(i).setForeground(Color.red);
						}
					}
					for (int i = arr.size(); i < dayarr.size(); i++) {
						dayarr.get(i).setText("");
						monthEachMinus.get(i).setText("");
						monthEachPlus.get(i).setText("");
						monthEachRealGain.get(i).setText("");
					}

					for (JPanel jp : monthEachPanel) {
						jp.setBorder(null);
					}

					int maxIdx = 0;
					int minIdx = 0;
					for (int i = 0; i < arr.size(); i++) {
						try {
							if((new DecimalFormat().parse(monthEachRealGain.get(i).getText())).intValue()
									< (new DecimalFormat().parse(monthEachRealGain.get(minIdx).getText())).intValue()){
								minIdx = i;
							}
							if((new DecimalFormat().parse(monthEachRealGain.get(i).getText())).intValue()
									> (new DecimalFormat().parse(monthEachRealGain.get(maxIdx).getText())).intValue()){
								maxIdx = i;
							}

						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}

					monthEachPanel.get(minIdx).setBorder(new LineBorder(Color.red));
					monthEachPanel.get(maxIdx).setBorder(new LineBorder(Color.blue));

					totMinus.setText(new DecimalFormat().format(monthTotMinus));
					totPlus.setText(new DecimalFormat().format(monthTotPlus));
					totGain.setText(new DecimalFormat().format(monthTotRealGain));

					if(monthTotRealGain>0){
						totGain.setForeground(Color.blue);
					}
					else if(monthTotRealGain==0) {
						totGain.setForeground(Color.black);
					}
					else {
						totGain.setForeground(Color.red);
					}

					resPanel.revalidate();
					resPanel.repaint();

					resPanelCard.show(resPanel, "monthTotRes");

				}
			}
		});
		jungsanPanel.add(goButton);

		JLabel totMinusTitle = new JLabel("총 지출");
		totMinusTitle.setForeground(new Color(255, 255, 255));
		totMinusTitle.setHorizontalAlignment(SwingConstants.CENTER);
		totMinusTitle.setFont(new Font("나눔고딕", Font.BOLD, 20));
		totMinusTitle.setBounds(503, 10, 79, 29);
		jungsanPanel.add(totMinusTitle);

		JLabel totPlusTitle = new JLabel("총 매출");
		totPlusTitle.setForeground(new Color(255, 255, 255));
		totPlusTitle.setHorizontalAlignment(SwingConstants.CENTER);
		totPlusTitle.setFont(new Font("나눔고딕", Font.BOLD, 20));
		totPlusTitle.setBounds(503, 49, 79, 29);
		jungsanPanel.add(totPlusTitle);

		goButton.doClick();
		MonitorPanel.add(jungsanPanel,"jungsan");

		//채팅@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

		JPanel managerChatting = new JPanel();
		managerChatting.setBackground(new Color(0, 0, 0));
		MonitorPanel.add(managerChatting, "Chatting"); //여기 바버모니터패널 대신 관리자화면에 있는 패널 이름 넣기
		managerChatting.setLayout(null);

		managerChattingTextField = new JTextField();
		managerChattingTextField.setBounds(49, 610, 730, 45);
		managerChatting.add(managerChattingTextField);
		managerChattingTextField.setColumns(10);
		managerChattingTextField.setFont(new Font("나눔고딕", Font.PLAIN, 13));
		managerChattingTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				managerChattingSend.doClick();
			}
		});

		//채팅입력후 보내는 곳
		managerChattingSend = new JButton("전송");
		managerChattingSend.setBounds(783, 611, 116, 43);
		managerChattingSend.setFont(new Font("나눔고딕", Font.BOLD, 20));
		managerChattingSend.setBackground(new Color(162, 51, 41));
		managerChattingSend.setForeground(Color.white);
		managerChatting.add(managerChattingSend);
		managerChattingSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = managerChattingTextField.getText();

				managerChattingTextArea.setCaretPosition(managerChattingTextArea.getDocument().getLength());

				if(!msg.equals("")) {
					try {
						data = new TCPData();
						data.kind = "채팅";
						data.from = "Manager_Pan";
						data.to = new String[] {"BarberStaffFrame", "SesinStaffFrame", "Manager_Pan"};
						data.sendObject = "[관리자] "+managerChattingTextField.getText();
						oos.writeObject(data);
						oos.flush();
						oos.reset();
						System.out.println(data+": 전송 완료");
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					managerChattingTextField.setText("");
				}
				managerChattingTextField.requestFocus();
			}
		});

		managerChattingTextArea = new JTextArea();
		managerChattingTextArea.setDisabledTextColor(Color.BLACK);
		managerChattingTextArea.setForeground(Color.BLACK);
		managerChattingTextArea.setEnabled(false);
		managerChattingTextArea.setEditable(false);
		managerChattingTextArea.setBounds(49, 83, 850, 519);
		managerChattingTextArea.setFont(new Font("나눔고딕", Font.PLAIN, 15));

		JScrollPane ChattingscrollPane = new JScrollPane(managerChattingTextArea);
		ChattingscrollPane.setBounds(49, 83, 850, 517);
		managerChatting.add(ChattingscrollPane);

		sesinConnected = new JLabel("세신사");
		sesinConnected.setForeground(Color.WHITE);
		sesinConnected.setOpaque(true);
		sesinConnected.setFont(new Font("나눔고딕", Font.BOLD, 20));
		sesinConnected.setHorizontalAlignment(SwingConstants.CENTER);
		sesinConnected.setBounds(49, 37, 140, 34);
		sesinConnected.setBackground(new Color(38, 83, 113));
		managerChatting.add(sesinConnected);

		barberConnected = new JLabel("이발사");
		barberConnected .setForeground(Color.WHITE);
		barberConnected .setOpaque(true);
		barberConnected .setHorizontalAlignment(SwingConstants.CENTER);
		barberConnected .setFont(new Font("나눔고딕", Font.BOLD, 20));
		barberConnected .setBounds(201, 37, 140, 34);
		barberConnected .setBackground(new Color(38, 83, 113));
		managerChatting.add(barberConnected);

		manager_F.setVisible(true);
		manager_F.setResizable(false);
		manager_F.setLocationRelativeTo(null);
		manager_F.setDefaultCloseOperation(manager_F.EXIT_ON_CLOSE);
		new Receiver().start();
	}

	void monthDetail() {
		System.out.println("monthDetail 실행");
		table.setModel(new DefaultTableModel(
				new DAO().detailJungsan(
						new DAO().monthDetailSqlMake(
								(String) jungsan_yearComboBox.getSelectedItem(),
								jungsan_selectedMonth+"", 
								(String) categoryComboBox.getSelectedItem(),
								(String) subcategoryComboBox.getSelectedItem()
								)
						),
				new String[] {
						"분류", "메뉴명", "지출액", "매출액", "순수익"
				}
				));
		table.getColumn("분류").setCellRenderer(renderer_center);
		table.getColumn("메뉴명").setCellRenderer(renderer_center);
		table.getColumn("지출액").setCellRenderer(renderer_right);
		table.getColumn("매출액").setCellRenderer(renderer_right);
		table.getColumn("순수익").setCellRenderer(renderer_right);
		table.setRowHeight(30);
		table.getColumn("분류").setPreferredWidth(125);
		table.getColumn("메뉴명").setPreferredWidth(250);
		table.getColumn("지출액").setPreferredWidth(150);
		table.getColumn("매출액").setPreferredWidth(150);
		table.getColumn("순수익").setPreferredWidth(150);
	}

	void dayDetail() {
		table.setModel(new DefaultTableModel(
				new DAO().detailJungsan(new DAO().dayDetailSqlMake(
						(String) jungsan_yearComboBox.getSelectedItem(),
						(String) jungsan_monthComboBox.getSelectedItem(),
						jungsan_selectedDay+"",
						(String) categoryComboBox.getSelectedItem(),
						(String) subcategoryComboBox.getSelectedItem()
						)),
				new String[] {
						"분류", "메뉴명", "지출액", "매출액", "순수익"
				}
				));
		table.getColumn("분류").setCellRenderer(renderer_center);
		table.getColumn("메뉴명").setCellRenderer(renderer_center);
		table.getColumn("지출액").setCellRenderer(renderer_right);
		table.getColumn("매출액").setCellRenderer(renderer_right);
		table.getColumn("순수익").setCellRenderer(renderer_right);
		table.setRowHeight(30);
		table.getColumn("분류").setPreferredWidth(125);
		table.getColumn("메뉴명").setPreferredWidth(250);
		table.getColumn("지출액").setPreferredWidth(150);
		table.getColumn("매출액").setPreferredWidth(150);
		table.getColumn("순수익").setPreferredWidth(150);
	}


	//-------------------------------------------------------
	void storeHouseAdd(String work) {
		if(work.equals("입고")&&!addToItemOrStorage) {

			Set item =storeAdd_map.entrySet();
			for (Object itemKV: item) {
				Map.Entry storeitem = (Map.Entry)itemKV;
				ManagerDTO dto = new ManagerDTO(storeitem.getKey()+"",storeitem.getValue()+"");
				System.out.println("key:"+storeitem.getKey()+"val"+storeitem.getValue()+"");
				invendao.invenFilInTheStorehouse(dto);
			}
			storeAdd_map.clear();
			setinvenTable();
			itemAdd_popup.dispose();
		}else if(work.equals("추가")&&addToItemOrStorage){
			System.out.println("추가 dao");
			itemAdd_popup.dispose();
		}
	}
	//-------------------------------------------------------
	void setinvenTable() {
		inven_DB = new DAO().invenList();
		inven_table.setModel(new DefaultTableModel(inven_DB,inventableColumnName) {
			@Override
			public boolean isCellEditable(int row, int column) {//해당 컬럼만 막고 열음
				//all cells false
				if (column<6&&column>=0) {
					return false; 
				}
				else {
					return true;
				}
			}
		});
		inven_table.setFont(new Font("나눔고딕", Font.BOLD, 20));
		inven_table.setRowHeight(30);
		inven_table.setFillsViewportHeight(true);

		inven_table.getColumnModel().getColumn(0).setPreferredWidth(230);
		inven_table.getColumnModel().getColumn(1).setPreferredWidth(135);
		inven_table.getColumnModel().getColumn(2).setPreferredWidth(90);
		inven_table.getColumnModel().getColumn(3).setPreferredWidth(120);
		inven_table.getColumnModel().getColumn(4).setPreferredWidth(110);
		inven_table.getColumnModel().getColumn(5).setPreferredWidth(122);
		inven_table.getColumnModel().getColumn(6).setPreferredWidth(97);
		inven_table.getColumnModel().getColumn(6).setHeaderRenderer(new TableCell(inven_table.getTableHeader()));
		inven_table.getColumnModel().getColumn(6).setCellRenderer(new TableCell());
		inven_table.getColumnModel().getColumn(6).setCellEditor(new TableCell());
	}
	//-------------------------------------------------------
	class serviceSelect implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(userORoutlay) {
				boolean allCheck = userhistory_oneTicketCheckBox.isSelected()&&userhistory_multiUseTicketCheckBox.isSelected()&&
						userhistory_cutCheckBox.isSelected()&&userhistory_beautyCheckBox.isSelected()&&userhistory_cleanCheckBox.isSelected()&&
						userhistory_massageCheckBox.isSelected()&&userhistory_sweetsCheckBox.isSelected()&&
						userhistory_drinksCheckBox.isSelected()&&userhistory_foodCheckBox.isSelected()&&userhistory_toiletriesCheckBox.isSelected();

				boolean	Onecheck = userhistory_oneTicketCheckBox.isSelected()||userhistory_multiUseTicketCheckBox.isSelected()||
						userhistory_cutCheckBox.isSelected()||userhistory_beautyCheckBox.isSelected()||userhistory_cleanCheckBox.isSelected()||
						userhistory_massageCheckBox.isSelected()||userhistory_sweetsCheckBox.isSelected()||
						userhistory_drinksCheckBox.isSelected()||userhistory_foodCheckBox.isSelected()||userhistory_toiletriesCheckBox.isSelected();

				if (!((JCheckBox)(e.getSource())).isSelected()) {
					userhistory_allRadioButton.setSelected(false);
				}
				if (allCheck) {
					userhistory_allRadioButton.setSelected(true);
				}
				if(!allCheck&&!Onecheck) {
					userhistory_DB = null;
					setuserhistoryTable();
					userhistory_resWon.setText("0 원");
				}
				else if(userhistoryMonthButton.isSelected()) {
					//뭐라도 선택된 상태에서 월별
					calcMonthlySales();
				}
				else if(userhistoryDayButton.isSelected()) {
					//뭐라도 선택된 상태에서 일별
					calcDailySales(userhistory_selectedDay);
				}
			}else {
				boolean outlayhistory_allCheck= outlayhistory_sweetsCheckBox.isSelected()&&outlayhistory_drinksCheckBox.isSelected()
						  &&outlayhistory_foodCheckBox.isSelected()&&outlayhistory_toiletriesCheckBox.isSelected();

				boolean	outlayhistory_Onecheck = outlayhistory_sweetsCheckBox.isSelected()||outlayhistory_drinksCheckBox.isSelected()
						  ||outlayhistory_foodCheckBox.isSelected()||outlayhistory_toiletriesCheckBox.isSelected();

				if (!((JCheckBox)(e.getSource())).isSelected()) {
					outlayhistory_allRadioButton.setSelected(false);
				}
				if (outlayhistory_allCheck) {
					outlayhistory_allRadioButton.setSelected(true);
				}
				if(!outlayhistory_allCheck&&!outlayhistory_Onecheck) {
					outlayhistory_DB = null;
					setuserhistoryTable();
					outlayhistory_resWon.setText("0 원");
				}
				else if(outlayhistoryMonthButton.isSelected()) {
					//뭐라도 선택된 상태에서 월별
					calcMonthlySales();
				}
				else if(outlayhistoryDayButton.isSelected()) {
					//뭐라도 선택된 상태에서 일별
					calcDailySales(outlayhistory_selectedDay);
				}
			}
		}
	}

	void setuserhistoryTable() {
		userhistory_Salestable.setModel(new DefaultTableModel(userhistory_DB, userhistorytableColumnName));

		userhistory_Salestable.setEnabled(false);
		userhistory_Salestable.setRowHeight(40);
		userhistory_Salestable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		userhistory_Salestable.getColumnModel().getColumn(0).setPreferredWidth(85);
		userhistory_Salestable.getColumnModel().getColumn(1).setPreferredWidth(85);
		userhistory_Salestable.getColumnModel().getColumn(2).setPreferredWidth(150);
		userhistory_Salestable.getColumnModel().getColumn(3).setPreferredWidth(144);
		userhistory_Salestable.getColumnModel().getColumn(4).setPreferredWidth(210);
		userhistory_Salestable.getColumnModel().getColumn(5).setPreferredWidth(100);
	}
	
	void setoutlayhistoryTable() {
		outlayhistory_Salestable.setModel(new DefaultTableModel(outlayhistory_DB, outlayhistorytableColumnName));

		outlayhistory_Salestable.setEnabled(false);
		outlayhistory_Salestable.setRowHeight(40);
		outlayhistory_Salestable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//intime(입고 시간) sep(상세 항목)  menu(제품명) quantity(수량) wholesaleprice(도매가)
		outlayhistory_Salestable.getColumnModel().getColumn(0).setPreferredWidth(200);
		outlayhistory_Salestable.getColumnModel().getColumn(1).setPreferredWidth(120);
		outlayhistory_Salestable.getColumnModel().getColumn(2).setPreferredWidth(200);
		outlayhistory_Salestable.getColumnModel().getColumn(3).setPreferredWidth(85);
		outlayhistory_Salestable.getColumnModel().getColumn(4).setPreferredWidth(144);
	}

	void calcMonthlySales() {

		String rangeSql = "";

		if(userORoutlay) {
			if(userhistory_oneTicketCheckBox.isSelected()) {
				rangeSql+="menu.sep = '일일권' or ";
			}
			if(userhistory_multiUseTicketCheckBox.isSelected()) {
				rangeSql+="menu.sep = '정기권' or ";
			}
			if(userhistory_cutCheckBox.isSelected()) {
				rangeSql+="menu.sep = '이발' or ";
			}
			if(userhistory_beautyCheckBox.isSelected()) {
				rangeSql+="menu.sep = '미용' or ";
			}
			if(userhistory_cleanCheckBox.isSelected()) {
				rangeSql+="menu.sep = '세신' or ";
			}
			if(userhistory_massageCheckBox.isSelected()) {
				rangeSql+="menu.sep = '마사지' or ";
			}
			if(userhistory_sweetsCheckBox.isSelected()) {
				rangeSql+="menu.sep = '과자' or ";
			}
			if(userhistory_drinksCheckBox.isSelected()) {
				rangeSql+="menu.sep = '음료' or ";
			}
			if(userhistory_foodCheckBox.isSelected()) {
				rangeSql+="menu.sep = '식품' or ";
			}
			if(userhistory_toiletriesCheckBox.isSelected()) {
				rangeSql+="menu.sep = '세면도구' or ";
			}
			
			userhistory_DB = new DAO().paymentList(new DAO().paymentListSql(
					(String) userhistory_yearComboBox.getSelectedItem(),
					(String) userhistory_monthComboBox.getSelectedItem(),
					rangeSql.substring(0, rangeSql.lastIndexOf(" or"))
					));

			setuserhistoryTable();

			userhistory_resWon.setText(new DecimalFormat().format(new DAO().paymentCalcTotal(new DAO().paymentCalcSql(
					(String) userhistory_yearComboBox.getSelectedItem(),
					(String) userhistory_monthComboBox.getSelectedItem(),
					rangeSql.substring(0, rangeSql.lastIndexOf(" or")))))
					+ " 원");
		}else {

			if(outlayhistory_sweetsCheckBox.isSelected()) {
				rangeSql+="outlay.sep = '과자' or ";
			}
			if(outlayhistory_drinksCheckBox.isSelected()) {
				rangeSql+="outlay.sep = '음료' or ";
			}
			if(outlayhistory_foodCheckBox.isSelected()) {
				rangeSql+="outlay.sep = '식품' or ";
			}
			if(outlayhistory_toiletriesCheckBox.isSelected()) {
				rangeSql+="outlay.sep = '세면도구' or ";
			}

			
			outlayhistory_DB = new DAO().outlayList(new DAO().outlayListSql(
					(String) outlayhistory_yearComboBox.getSelectedItem(),
					(String) outlayhistory_monthComboBox.getSelectedItem(),
					rangeSql.substring(0, rangeSql.lastIndexOf(" or"))
					));

			setoutlayhistoryTable();

			outlayhistory_resWon.setText(new DecimalFormat().format(new DAO().outlayCalcTotal(new DAO().outlayCalcSql(
					(String) outlayhistory_yearComboBox.getSelectedItem(),
					(String) outlayhistory_monthComboBox.getSelectedItem(),
					rangeSql.substring(0, rangeSql.lastIndexOf(" or")))))
					+ " 원");
		}
		
	}

	void calcDailySales(String selectedDay) {

		String rangeSql = "";
		
		
		if(userORoutlay) {
			if(userhistory_oneTicketCheckBox.isSelected()) {
				rangeSql+="menu.sep = '일일권' or ";
			}
			if(userhistory_multiUseTicketCheckBox.isSelected()) {
				rangeSql+="menu.sep = '정기권' or ";
			}
			if(userhistory_cutCheckBox.isSelected()) {
				rangeSql+="menu.sep = '이발' or ";
			}
			if(userhistory_beautyCheckBox.isSelected()) {
				rangeSql+="menu.sep = '미용' or ";
			}
			if(userhistory_cleanCheckBox.isSelected()) {
				rangeSql+="menu.sep = '세신' or ";
			}
			if(userhistory_massageCheckBox.isSelected()) {
				rangeSql+="menu.sep = '마사지' or ";
			}
			if(userhistory_sweetsCheckBox.isSelected()) {
				rangeSql+="menu.sep = '과자' or ";
			}
			if(userhistory_drinksCheckBox.isSelected()) {
				rangeSql+="menu.sep = '음료' or ";
			}
			if(userhistory_foodCheckBox.isSelected()) {
				rangeSql+="menu.sep = '식품' or ";
			}
			if(userhistory_toiletriesCheckBox.isSelected()) {
				rangeSql+="menu.sep = '세면도구' or ";
			}
			
			userhistory_DB = new DAO().paymentList(new DAO().paymentListSql(
					(String) userhistory_yearComboBox.getSelectedItem(),
					(String) userhistory_monthComboBox.getSelectedItem(),
					selectedDay,
					rangeSql.substring(0, rangeSql.lastIndexOf(" or"))
					));

			setuserhistoryTable();

			userhistory_resWon.setText(new DecimalFormat().format(new DAO().paymentCalcTotal(
					new DAO().paymentCalcSql(
							(String) userhistory_yearComboBox.getSelectedItem(),
							(String) userhistory_monthComboBox.getSelectedItem(),
							selectedDay,
							rangeSql.substring(0, rangeSql.lastIndexOf(" or")))))
					+ " 원");
		}else {
			if(outlayhistory_sweetsCheckBox.isSelected()) {
				rangeSql+="outlay.sep = '과자' or ";
			}
			if(outlayhistory_drinksCheckBox.isSelected()) {
				rangeSql+="outlay.sep = '음료' or ";
			}
			if(outlayhistory_foodCheckBox.isSelected()) {
				rangeSql+="outlay.sep = '식품' or ";
			}
			if(outlayhistory_toiletriesCheckBox.isSelected()) {
				rangeSql+="outlay.sep = '세면도구' or ";
			}
			
			outlayhistory_DB = new DAO().outlayList(new DAO().outlayListSql(
					(String) outlayhistory_yearComboBox.getSelectedItem(),
					(String) outlayhistory_monthComboBox.getSelectedItem(),
					selectedDay,
					rangeSql.substring(0, rangeSql.lastIndexOf(" or"))
					));

			setoutlayhistoryTable();

			outlayhistory_resWon.setText(new DecimalFormat().format(new DAO().outlayCalcTotal(
					new DAO().outlayCalcSql(
							(String) outlayhistory_yearComboBox.getSelectedItem(),
							(String) outlayhistory_monthComboBox.getSelectedItem(),
							selectedDay,
							rangeSql.substring(0, rangeSql.lastIndexOf(" or")))))
					+ " 원");
		}
		
	}
	
//	public void refreshSender(WaitingCustomer write) {
//		
//		try {
//
//			TCPData sendData = new TCPData(new WaitingCustomer(),"재고반영","Manager_Pan","kiosk_store");
//			oos.writeObject(sendData);
//			oos.flush();
//			oos.reset();
//			
//			System.out.println("전송 "+Arrays.toString(sendData.to)+"에게");
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//	}

	public static void main(String[] args) {

		String addr = "192.168.20.29";
		
		//			String addr = "192.168.20.44";
					 addr = "192.168.150.63";
		//			String addr = "121.161.160.79";

		try {
			Socket socket = new Socket(addr, 8888);
			System.out.println("관리자: 접속 성공");

			new Manager_Pan(socket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}