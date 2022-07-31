package customer;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import db.DAO;
import db.CustomerDTO;
import db.InvenDTO;
import db.MenuDTO;
import db.PaymentDTO;
import gui_design.Graphic_Button;
import gui_design.Menu_Button;
import gui_design.Normal_Button;
import gui_design.Order_Button;
import gui_design.Sep_Button;

public class Kiosk_store{
	
	JFrame kiosk_store_frame = new JFrame();
	
	//키오스크 선택 버튼 패널
	JPanel selectP;
	//키오스크 선택 화면 보여주는 패널
	JPanel showP;
	//매점 배경패널
	JPanel storeP;
	//매점패널 내에 메뉴패널
	JPanel menuP;
	//매점패널 내에 주문제품 리스트 패널
	JPanel ordListMainP;
	JPanel orderListP;
	//사용요금 패널
	JPanel FeeP;
	
	//매점 카테고리 패널
	JPanel storePage;
	JPanel page;

	//매점 상품 패널
	JPanel product;
	
	//매점 orderListP 에 구성요소 패널들
	JPanel ordFeeP;
	JPanel ordListP;
	JPanel ordBtnP;
	
	//카드레이아웃
	CardLayout card = new CardLayout();
	
	//DB에서 가져올 메뉴 정보
	MenuDTO dto;
	
	//매점에서 선택한 상품들 리스트
	HashMap<String,PaymentDTO> paymentList = new HashMap<String,PaymentDTO>();
	String selected; //선택한 상품 id
	
	//주문하기에서 사용되는 멤버
	JPanel orderP;
	
	//제품 주문전 고객 확인용
	JTextField locker = new JTextField();
	JPasswordField lockPW = new JPasswordField();
	
	//사용요금조회
	PaymentAmount pA = new PaymentAmount(255,204,0);
	
	//화면새로고침
	RefreshTimer refresh;
	int refreshTime=0;
	boolean touch = false;
	
	//선택된 소분류
	String selcCt = "과자";
	
	//금액콤마 표시용
	DecimalFormat formatter = new DecimalFormat("###,###");
	
	public Kiosk_store(){
		
		locker.setFont(new Font("나눔고딕",Font.BOLD,35));
		locker.setForeground(Color.black);
		lockPW.setFont(new Font("나눔고딕",Font.BOLD,35));
		lockPW.setForeground(Color.black);
		
		kiosk_store_frame.setTitle("매점");
		kiosk_store_frame.setBounds(50,50,640,800);
		kiosk_store_frame.setLayout(null);
		kiosk_store_frame.addMouseListener(new FrameRefresh());
		
		pA.paymentFF=kiosk_store_frame;
		
		selectP = new JPanel();
		showP = new JPanel();
		storeP = new JPanel();
		menuP = new JPanel();
		FeeP = new JPanel();
		storePage = new JPanel();
		orderListP = new JPanel();
		orderP = new JPanel();
		
		selectP.setBounds(0,671,624,90); //메뉴,요금,불만 선택버튼패널
		selectP.setBackground(Color.white);
		selectP.setLayout(new GridLayout(1,3,0,0));
		
		showP.setBounds(0,0,640,671); //메인 배경
		showP.setBackground(Color.white);
		showP.setLayout(card);
		
		storeP.setBounds(0,0,640,671); //매점 배경
		storeP.setBackground(Color.white);
		storeP.setLayout(null);
		
		orderListP.setBounds(0,471,624,200); //매점 주문리스트 패널
		orderListP.setBackground(Color.white);
		orderListP.setLayout(null);
		
		storePage.setBounds(0,60,640,411); //매점 메뉴별화면 배경
		storePage.setBackground(Color.white);
		storePage.setLayout(null);
		
		menuP.setBounds(0,0,624,60); //매점의 메뉴바
		menuP.setBackground(Color.white);
		menuP.setLayout(new GridLayout(1,4,3,0));
		
		FeeP.setBounds(0,0,640,671); //사용요금 배경
		FeeP.setBackground(Color.white);
		FeeP.setLayout(null);
		
		Graphic_Button gb1 = new Graphic_Button(39,19,255); //파
		Graphic_Button gb2 = new Graphic_Button(255,11,40); //빨
		Graphic_Button gb3 = new Graphic_Button(255,204,0); //노
		Graphic_Button gb4 = new Graphic_Button(44,141,23);	//초
		Graphic_Button gb5 = new Graphic_Button(0,0,0);     //검
		gb1.setEnabled(false);
		gb2.setEnabled(false);
		gb3.setEnabled(false);
		gb4.setEnabled(false);
		gb5.setEnabled(false);
		gb1.setBounds(0,0,20,401);//왼쪽
		gb2.setBounds(0,401,20,270);//왼쪽아래
		gb3.setBounds(0,651,640,20);//아래
		gb4.setBounds(605,0,20,671);//오른쪽
		gb5.setBounds(0,0,640,20);//위
		FeeP.add(gb1);
		FeeP.add(gb2);
		FeeP.add(gb3);
		FeeP.add(gb4);
		FeeP.add(gb5);

		FeeP.add(pA);
		FeeP.repaint();
		FeeP.revalidate();
		
		//주문하기 화면패널
		orderP.setBounds(0,0,640,800);
		orderP.setBackground(Color.white);
		orderP.setLayout(null);
		orderP.setVisible(false);
		
		
		//매점 내 카테고리별 배경 패널들
		page = new JPanel();
		
		page.setBackground(Color.white);
		page.setLayout(null);
		page.addMouseMotionListener(new MenuMouseListener());
		page.addMouseListener(new MenuMouseListener());
		
		
		//매점 내 카테고리 버튼 넣기
		String[] arr = {"과자","음료","식품","세면도구"};
		
		for(int i=0; i<arr.length; i++) {
			Sep_Button menuB = new Sep_Button(arr[i],255,204,0);
			menuB.addActionListener(new MyActionListener());
			menuP.add(menuB);
		}
		
		storePage.add(page);
		
	
		//orderListP (주문예정선택상품패널)에 구성요소 넣기
		orderComp();
		
		//매점 내 화면에 카테고리, 제품리스트, 주문상품화면 넣기
		storeP.add(menuP);
		storeP.add(storePage);
		storeP.add(orderListP);
		
		//키오스크 선택화면 보여주는 패널에 각 패널 넣기
		showP.add("매점",storeP);
		showP.add("사용요금",FeeP);
		
		String[] arr2 = {"매점","사용요금"};
		
		ButtonGroup bg2 = new ButtonGroup();
		
		for(int i=0; i<arr2.length; i++) {
			Order_Button selectB = new Order_Button(arr2[i],255,255,255);
			selectB.addActionListener(new MyActionListener());
			bg2.add(selectB);
			selectP.add(selectB);
		}
		
		kiosk_store_frame.add(orderP); //주문하기 눌렀을때 보여지는 주문패널
		kiosk_store_frame.add(showP);  //기본화면에서 매점,사용요금,불만접수화면을 모두 넣은 showP
		kiosk_store_frame.add(selectP);//매점,사용요금,불만접수화면 버튼을 넣은 하단패널
		
		kiosk_store_frame.setResizable(false);
		kiosk_store_frame.setVisible(true);
		kiosk_store_frame.setDefaultCloseOperation(3);
		
		makeProductPane(selcCt);
		
		refresh = new RefreshTimer(); //화면 새로고침
		refresh.start();
		
		//new store_refresh(this);
		
	}
	
	
	//////////////////////////////////////////////////////////////////////
	void menuComp(String str) { // 매점의 메뉴를 넣을 패널을 만들어 넣어주는 메소드
		
		switch (str) {
			case "과자":
				makeProductPane(str);
				
				break;
			case "음료":
				makeProductPane(str);
				
				break;
			case "식품":
				makeProductPane(str);
				
				break;
			case "세면도구":
				makeProductPane(str);
				
				break;
		}		
		
	}
	
	//////////////////////////////////////////////////////////////////////
	int menuX , menuY , pageX;
	public void listingProdPane(int pcnt) {//화면에 제품 수평리스팅 해주는 메소드
		if(pcnt==1){
			//System.out.println("컴포넌트 1개다");
			product.setLocation(menuX,menuY);
		}else if(pcnt%2==0) {
			//System.out.println("컴포넌트 2배수개다");
			menuY = 205;
			product.setLocation(menuX*185,menuY);
			menuX++;
		}else if(pcnt%2!=0) {//여기서 x축 길이 증가 해야함
			//System.out.println("컴포넌트 2배수아니다");
			pageX = ((pcnt+1)/2)*185;
			menuY = 0;
			product.setLocation(menuX*185,menuY);
		}
	}
	
	//////////////////////////////////////////////////////////////////////	
	public void makeProductPane(String str) {//DB에서 카테고리,종류 맞는 메뉴가져오는 메소드
		ArrayList<MenuDTO> DBList = callDB();
		page.removeAll();
		menuX = 0;
		menuY = 0;
		pageX = 0;
		page.setBounds(0,0,640,411);
				
		for(Object ob : DBList) {
			dto = (MenuDTO)ob;
			try {
				if(dto.getSep().equals(str)&&dto.getCategory().equals("4")) {
					
					if(page.getWidth()<((panelCount(page)/2)+1)*185) {
						page.setBounds(0,0,page.getWidth()+185,411);
					}
					
					
					
					product = new JPanel();
					product.setSize(180,200);
					product.setBackground(Color.white);
					product.setLayout(null);
					
				
					
					String pText = 
							"<html><body><center>"+
							dto.getMenu()+"<br>"+formatter.format(dto.getPrice())+
							"원</center></body></html>";
					
					Menu_Button pb;
					
					////////이미지 사이즈 바꿔주는 부분////////
					if(dto.getImg()!=null) {
//						ImageIcon icon = new ImageIcon(dto.getImg());
//						Image img = icon.getImage();
//						Image changeImg = img.getScaledInstance(130, 130, Image.SCALE_SMOOTH);
//						ImageIcon producImg = new ImageIcon(changeImg);
						//pb = new menu_Button(new ImageIcon(dto.getImg()),255,204,0);
						pb = new Menu_Button(dto.getImg(),255,204,0,0,0);
						//이미지 안나오는데 이유를 모르겠음;;
						//이유는 그래픽으로 다시 그려줘야 해서(둥근버튼을 그래픽으로 그린것이기 때문에 위에 이미지를 다시 그려줘야했음)
					}else {
						pb = new Menu_Button("No Image",255,204,0);
					}
					
					pb.setBounds(15,10,150,150);
					pb.setName(Integer.toString(dto.getId()));
					pb.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							refreshTime=0;
							touch = true;
							String str = e.toString();
							selected = str.substring(str.lastIndexOf("on")+3);
							orderList(selected);
						}
					});
					//제품이름,가격 버튼 아래에 가운데 정렬하는 부분
					JPanel lbPan = new JPanel();
					lbPan.setBounds(0,160,180,40);
					lbPan.setLayout(new GridLayout(1,1));
					lbPan.setBackground(Color.white);
					JLabel plName = new JLabel(pText,JLabel.CENTER);
					plName.setFont(new Font("나눔고딕",Font.BOLD,16));
					plName.setForeground(Color.black);
					lbPan.add(plName);
					
					try {
						int soldout = new DAO().inventoryAmount(dto.getMenu()).getDisplay();
						if(soldout<=0) {//품절시 버튼 비활성화 해주는 부분
							
							pText = "<html><body><center>"+
									dto.getMenu()+"<br>"+
									"품절</center></body></html>";
							plName.setText(pText);
							plName.setFont(new Font("나눔고딕",Font.BOLD,16));
							plName.setForeground(Color.gray);
							pb.setEnabled(false);
						}
					}catch (Exception e) {
						// TODO: handle exception
					}
					
					product.add(pb);
					product.add(lbPan);
					
					//System.out.println("패널의 컴포넌트수"+menuPanelCount());
					
					listingProdPane(panelCount(page));
					page.add(product);
					page.repaint();
					page.revalidate();
					
				}
			}catch (Exception e) {
				
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////
	public ArrayList<MenuDTO> callDB() {//DB의 등록해놓은 모든 제품을 가져오는 메소드
		ArrayList<MenuDTO> arr = new ArrayList<MenuDTO>();
		
		for(MenuDTO dto: new DAO().menuList()) {
			
			arr.add(dto);
		}
		return arr;
	}
	
	//////////////////////////////////////////////////////////////////////
	void orderList(String selected) {//메뉴에서 선택한 제품의 DB에 접근해 값을 가져오는 메소드
		MenuDTO dto = new DAO().menuSelected(Integer.parseInt(selected));
		selectedP(dto);
	}
	
	//////////////////////////////////////////////////////////////////////
	void orderComp() { // orderListP 에 구성요소 패널들 넣어주는 메소드
		ordFeeP = new JPanel();
		ordListP = new JPanel();
		ordListMainP = new JPanel();
		
		ordBtnP = new JPanel();
		
		ordFeeP.setBounds(1,0,497,50);
		ordFeeP.setBackground(new Color(255,204,0));
		
		
		ordListMainP.setBounds(0,50,500,150);
		ordListMainP.setBackground(Color.white);
		ordListMainP.setLayout(null);
		
		ordListP.setBounds(0,0,500,150);
		ordListP.setBackground(Color.white);
		ordListP.setLayout(null);
		ordListP.addMouseMotionListener(new MyMouseListener());
		ordListP.addMouseListener(new MyMouseListener());
		
		
		ordBtnP.setBounds(500,0,123,200);
		ordBtnP.setBackground(Color.white);
		ordBtnP.setLayout(new GridLayout(2,1));
		
		JButton order = new Order_Button("주문하기",245,194,0,25,0);
		JButton cancel = new Order_Button("취소하기",245,194,0,25,0);
		
		order.addActionListener(new MyActionListener());
		cancel.addActionListener(new MyActionListener());
		
		ordBtnP.add(order);
		ordBtnP.add(cancel);
		
		ordListMainP.add(ordListP);
		orderListP.add(ordFeeP);
		orderListP.add(ordListMainP);
		orderListP.add(ordBtnP);
		
	}
	
	
	//////////////////////////////////////////////////////////////////////
	JLabel totAmount = new JLabel("",JLabel.CENTER);
	JLabel totPrice = new JLabel("",JLabel.CENTER);
	
	int totalAmount=0,totalPrice=0;
	
	void totalFee() { //총 개수와 금액을 설정해주는 메소드
		
		
		ordFeeP.repaint();
		ordFeeP.revalidate();
		
		ordFeeP.setLayout(null);

		paymentList.forEach((key, value) -> {
			totalAmount+=value.getAmount();
			totalPrice+= (value.getAmount()*value.getPrice());
			});
		
		totAmount.setFont(new Font("나눔고딕",Font.BOLD,20));
		totAmount.setForeground(Color.black);
		
		totPrice.setFont(new Font("나눔고딕",Font.BOLD,20));
		totPrice.setForeground(Color.black);
		
		totAmount.setText("총 "+totalAmount+" 개");
		totPrice.setText(formatter.format(totalPrice)+" 원");
		
		totAmount.setBounds(170,0,100,50);
		totPrice.setBounds(300,0,200,50);
		
		ordFeeP.add(totAmount);
		ordFeeP.add(totPrice);
		
		totalAmount=0;
		totalPrice=0;
		
	}
	
	
	int nextLine=0,separate=0;
	
	void selectedP(MenuDTO _dto) { //제품선택시 선택리스트에 패널 추가하는 메소드
		
		JLabel pAmount = new JLabel("",JLabel.CENTER);
		pAmount.setFont(new Font("나눔고딕",Font.BOLD,16));
		pAmount.setForeground(Color.black);
		pAmount.setBounds(197, 0, 40, 40);
		JLabel pPrice = new JLabel();
		pPrice.setFont(new Font("나눔고딕",Font.BOLD,16));
		pPrice.setForeground(Color.black);
		pPrice.setBounds(360, 0, 200, 40);
		
		try {
			if(paymentList.get(_dto.getMenu()).getMenu()!=null) {
				separate=1;
			}
		}catch (Exception e) {
			
		}
		
		if(separate==0) { //처음 선택한 제품일때
			if(ordListP.getHeight()<panelCount(ordListP)*50) {
				ordListP.setBounds(0,ordListMainP.getHeight()-ordListP.getHeight()-50,500,ordListP.getHeight()+50);
			}
			
			ordListP.repaint();
			ordListP.revalidate();	
			
			PaymentDTO pdto = new PaymentDTO(_dto.getCategory(),
											 _dto.getId(),
											 _dto.getMenu(),
											 1,_dto.getPrice());
			
			paymentList.put(pdto.getMenu(),	pdto);
			
			JPanel panel = new JPanel();
			panel.setBounds(5,5+nextLine,490,40);
			panel.setBackground(new Color(230,230,230));
			panel.setLayout(null);
			
			JLabel pMenu = new JLabel(paymentList.get(_dto.getMenu()).getMenu(),
									  JLabel.CENTER);
			
			pMenu.setBounds(0,0,150,40);
			pMenu.setFont(new Font("나눔고딕",Font.BOLD,15));
			pMenu.setForeground(Color.black);
			
			pPrice.setText(formatter.format(pdto.getPrice()));
			
			JButton plus = new Normal_Button("+",255,204,0,15);
			JButton minus = new Normal_Button("-",255,204,0,15);
			JButton delete = new Normal_Button("X",255,204,0,15);
			
			minus.setBounds(160,3,34,34);
			plus.setBounds(240,3,34,34);
			delete.setBounds(280,3,34,34);
			pAmount.setText(Integer.toString(
					paymentList.get(_dto.getMenu()).getAmount()
					));
			
			plus.addActionListener(new ActionListener() { //수량 증가
			
				@Override
				public void actionPerformed(ActionEvent e) {
					refreshTime=0;
					touch = true;
					
					if(paymentList.get(_dto.getMenu())==null) {
						paymentList.put(pdto.getMenu(),	pdto);
					}
					
					if(new DAO().inventoryAmount(pMenu.getText()).getDisplay()
							    > paymentList.get(_dto.getMenu()).getAmount()) {
						
						int amut = paymentList.get(_dto.getMenu()).getAmount()+1;
						paymentList.get(_dto.getMenu()).setAmount(amut);
						pAmount.setText(Integer.toString(amut));
						pPrice.setText(formatter.format(pdto.getPrice()*amut));
						totalFee();
					}
				}
			});
			
			minus.addActionListener(new ActionListener() { //수량 감소
			
				@Override
				public void actionPerformed(ActionEvent e) {
					refreshTime=0;
					touch = true;
					
					if(paymentList.get(_dto.getMenu()).getAmount()!=1) {
						int amut = paymentList.get(_dto.getMenu()).getAmount()-1;
						
						paymentList.get(_dto.getMenu()).setAmount(amut);
						pAmount.setText(Integer.toString(amut));
						pPrice.setText(formatter.format(pdto.getPrice()*amut));
						totalFee();
					}
					if(paymentList.get(_dto.getMenu()).getAmount()<=0) {
						paymentList.remove(_dto.getMenu());
					}
				}
			});
			
			delete.addActionListener(new ActionListener() { //주문예정 내역 삭제
				
				@Override
				public void actionPerformed(ActionEvent e) {
					refreshTime=0;
					touch = true;
					
					paymentList.remove(_dto.getMenu());
					panel.setEnabled(false);
					pMenu.setEnabled(false);
					minus.setEnabled(false);
					minus.setForeground(Color.gray);
					pAmount.setEnabled(false);
					pAmount.setText("0");
					plus.setEnabled(false);
					plus.setForeground(Color.gray);
					delete.setEnabled(false);
					delete.setForeground(Color.gray);
					pPrice.setEnabled(false);
					pPrice.setText("삭제됨");
					totalFee();
				}
			});
			
			
			panel.add(pMenu);
			panel.add(minus);
			panel.add(pAmount);
			panel.add(plus);
			panel.add(delete);
			
			panel.add(pPrice);
			
			ordListP.add(panel);
			
			nextLine +=50;
			
					
		}else if(separate==1) { //중복선택한 제품일때
			separate=0;
		}
		
		totalFee();
	}
	
	int panelCount(JPanel jb) { //선택한 제품을 리스팅 해줄때 추가된 패널의 개수를 구해주는 메소드
		
		int cntP=0;
		for(int i=0; i<100; i++) {
			try {
				if(jb.getComponent(i)==null) {
					
				}
			}catch (Exception e) {
				break;
			}
			cntP+=1;
		}
		
		return cntP+1;
	}
	
	
	

	//////////////////////////////////////////////////////////////////////

	void order() { //주문하기를 누르면 나오는 화면
		showP.setVisible(false);
		selectP.setVisible(false);
		
		locker.setName("락커번호");
		lockPW.setName("비밀번호");
		
		locker.setText("");
		lockPW.setText("");
		
		locker.addFocusListener(new MyFocusListener());
		lockPW.addFocusListener(new MyFocusListener());
		
		locker.setBounds(150,50,300,100);
		lockPW.setBounds(150,180,300,100);
		
		JPanel numB = new JPanel();
		numB.setLayout(new GridLayout(4,3));
		numB.setBounds(80,300,300,400);
		numB.setBackground(Color.white);
		
		String[] passButton = {"1","2","3","4","5","6","7","8","9","<","0","지움"};
		for(String str:passButton){
			JButton btnN = new Menu_Button(str,200,200,200,25);
			btnN.addActionListener(new OrderSubmitAction());
			numB.add(btnN);
		}
		
		JPanel sletB = new JPanel();
		sletB.setLayout(new GridLayout(2,1));
		sletB.setBounds(400,300,150,400);
		sletB.setBackground(Color.white);
		
		//JButton backSpace = new menu_Button("지움",230,230,230);
		JButton submit = new Menu_Button("주문",255,204,0);
		JButton goBack = new Menu_Button("뒤로가기",0,0,0,30);
		
		//backSpace.addActionListener(new OrderSubmitAction());
		submit.addActionListener(new OrderSubmitAction());
		goBack.addActionListener(new OrderSubmitAction());
		
		//sletB.add(backSpace);
		sletB.add(submit);
		sletB.add(goBack);
		
		
		orderP.add(locker);
		orderP.add(lockPW);
		orderP.add(numB);
		orderP.add(sletB);
		
		orderP.setVisible(true);

	}
	
	void cancel() {
		locker.setText("");
		lockPW.setText("");
		
		paymentList.clear();
		
		ordFeeP.removeAll();
		ordFeeP.repaint();
		ordFeeP.revalidate();
		
		ordListP.removeAll();
		nextLine=0;
		ordListP.setBounds(0,0,500,150);
		ordListP.repaint();
		ordListP.revalidate();
		makeProductPane("과자");
	}
	
	class MyActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshTime=0;
			touch = true;
			String push = e.getActionCommand();

			if(push.equals("과자")) {
				
				menuComp(push);
				selcCt = push;
			}else if(push.equals("음료")) {
				
				menuComp(push);
				selcCt = push;
			}else if(push.equals("식품")) {
				
				menuComp(push);
				selcCt = push;
			}else if(push.equals("세면도구")) {
				
				menuComp(push);
				selcCt = push;
			}else if(push.equals("주문하기")) {
				
				if(paymentList.size()!=0) {
					order();
				}else {
					new ShowMessage(kiosk_store_frame,"주문하실 제품을 선택해 주세요",1500);
				}
				
			}else if(push.equals("취소하기")) {
				cancel();
				
			}
			
			if(push.equals("매점")) {
				card.show(showP,push);
				storeP.repaint();
				storeP.revalidate();
				
				pA.orderP.setVisible(false);
				pA.fac.setVisible(false);
			}else if(push.equals("사용요금")) {
				card.show(showP,push);
				FeeP.repaint();
				FeeP.revalidate();
				
				pA.orderP.setVisible(true);
				storeP.setVisible(false);
			}
		}
	}
	
	
	String txtName;
	
	void inputNum(String num) {
		if(num.equals("지움")) {
			if(txtName.equals("락커번호")) {
				locker.setText("");
				
			}else if(txtName.equals("비밀번호")) {
				lockPW.setText("");
			}
		}else if(num.equals("<")){
			try {
				if(txtName.equals("락커번호")) {
					String txt = locker.getText();
					int last = txt.length()-1;
					locker.setText(txt.substring(0,last));
					
				}else if(txtName.equals("비밀번호")) {
					String txt = lockPW.getText();
					int last = txt.length()-1;
					lockPW.setText(txt.substring(0,last));
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}else {
			if(txtName.equals("락커번호")) {
				String txt = locker.getText();
				locker.setText(txt+num);
			}else if(txtName.equals("비밀번호")) {
				String txt = lockPW.getText();
				lockPW.setText(txt+num);
			}
		}
	}
	
	
	void customerCheck() {
		String lockerNum = locker.getText();
		String pwNum = lockPW.getText();
		
		boolean lockPass=false;
		boolean PWPass=false;
		
		try {
			for(CustomerDTO dto: new DAO().customerList()) {
				if(Integer.parseInt(lockerNum) == dto.getLocker()&&dto.getTimein()!=null&&dto.getTimeout()==null) {
					lockPass = true;
					if(Integer.parseInt(pwNum) == dto.getPassword()&&dto.getTimein()!=null&&dto.getTimeout()==null) {
						PWPass = true;
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		if(lockerNum.equals("")||pwNum.equals("")){
			new ShowMessage(kiosk_store_frame,"락커번호와 비밀번호를 모두 입력해주세요",1000);
		}else {
			if(lockPass&&PWPass) {
				new ShowMessage(kiosk_store_frame,"주문되었습니다. 감사합니다",1000);
				submitToDB(lockerNum, pwNum);
				cancel();
			}else if(!PWPass&&!lockPass) {
				new ShowMessage(kiosk_store_frame,"락커번호와 비밀번호를 다시 입력해주세요",1000);
			}else if(lockPass) {
				new ShowMessage(kiosk_store_frame,"비밀번호를 다시 입력해주세요",1000);
			}
		}
	}
	
	void submitToDB(String lockerNum, String pwNum) {
		paymentList.forEach((key, value) -> {
			if(value.getAmount()!=0) {
				PaymentDTO payDTO = new PaymentDTO();
				payDTO.setPrice(
						value.getAmount()*value.getPrice()
						);
				payDTO.setLocker(Integer.parseInt(lockerNum));
				payDTO.setCategory(value.getCategory());
				payDTO.setMenuid(value.getMenuid());
				payDTO.setMenu(value.getMenu());
				payDTO.setAmount(value.getAmount());
				new DAO().paymentInsert(payDTO);
			}
		});
		submitToInvenDB();
		goBack();
		
	}
	
	void submitToInvenDB() {//주문시 재고차감 메소드
		try {
			paymentList.forEach((key, value) -> {
				//주문시 재고차감 -> 필요한것 어떤 메뉴인지, 몇개 주문했는지(몇개로 수정할지)
				DAO dao = new DAO();
				InvenDTO invenAmount = dao.inventoryAmount(value.getMenu());
				InvenDTO invenDTO = new InvenDTO();
				invenDTO.setMenu(value.getMenu());
				invenDTO.setDisplay(invenAmount.getDisplay()-value.getAmount());
				invenDTO.setStorage(invenAmount.getStorage()-value.getAmount());
				new DAO().inventoryModify(invenDTO);
			});
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	void goBack() {
		locker.setText("");
		lockPW.setText("");
		orderP.setVisible(false);
		showP.setVisible(true);
		selectP.setVisible(true);
		
	}
	
	
	class OrderSubmitAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			refreshTime=0;
			touch = true;
			String push = e.getActionCommand();
			switch(push) {
				case "뒤로가기":
					goBack();
					break;	
				case "주문":
					customerCheck();
					break;
				case "지움":
					inputNum(push);
					break;
				case "<":
					inputNum(push);
					break;
				case "0":
					inputNum(push);
					break;
				case "1":
					inputNum(push);
					break;
				case "2":
					inputNum(push);
					break;
				case "3":
					inputNum(push);
					break;
				case "4":
					inputNum(push);
					break;
				case "5":
					inputNum(push);
					break;
				case "6":
					inputNum(push);
					break;
				case "7":
					inputNum(push);
					break;
				case "8":
					inputNum(push);
					break;
				case "9":
					inputNum(push);
					break;
			}
		}
	}
	
	
	
	int fcY=0,mdY=0,fcX=0,mdX=0;
	int border;
	class MyMouseListener implements MouseListener, MouseMotionListener{
		void moveP() {
			int point = ordListP.getY()-fcY;
			int move=point+mdY;
			ordListP.setBounds(0,move,ordListP.getWidth(),ordListP.getHeight());
		}
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {
			refreshTime=0;
			touch = true;
			fcY=e.getY();
		}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mouseDragged(MouseEvent e) {
			mdY=e.getY();
			border = ordListMainP.getHeight()-ordListP.getHeight();
			if(ordListP.getY()<=0&&ordListP.getY()>=border) {			
				moveP();
			}else if(ordListP.getY()>0) {
				ordListP.setBounds(0,0,ordListP.getWidth(),ordListP.getHeight());
			}else if(ordListP.getY()<border) {
				ordListP.setBounds(0,border,ordListP.getWidth(),ordListP.getHeight());
			}
		}
		@Override
		public void mouseMoved(MouseEvent e) {}
	}
	
	
	
	class MenuMouseListener implements MouseListener, MouseMotionListener{
		void moveP() {
			int point = page.getX()-fcX;
			int move=point+mdX;
			page.setBounds(move,0,page.getWidth(),page.getHeight());
		}
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {
			refreshTime=0;
			touch = true;
			fcX=e.getX();
		}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mouseDragged(MouseEvent e) {
			mdX=e.getX();
			border = storePage.getWidth()-page.getWidth();
			if(storePage.getX()<=0&&page.getX()>=border) {	//if(ordListP.getY()<=0&&ordListP.getY()>=border) {			
				moveP();
			}else if(page.getX()>0) {
				page.setBounds(0,0,page.getWidth(),page.getHeight());
			}else if(page.getX()<border) {
				page.setBounds(border,0,page.getWidth(),page.getHeight());
			}
		}
		@Override
		public void mouseMoved(MouseEvent e) {}
	}
	
	
	class FrameRefresh implements MouseListener{
		@Override
		public void mouseClicked(MouseEvent e) { }
		@Override
		public void mousePressed(MouseEvent e) {
			refreshTime=0;		
			touch = true;
		}
		@Override
		public void mouseReleased(MouseEvent e) { }
		@Override
		public void mouseEntered(MouseEvent e) { }
		@Override
		public void mouseExited(MouseEvent e) { }
		
	}
	
	
	class RefreshTimer extends Thread { //새로고침 스레드 클래스
		@Override
		public void run() {
			try {
				while(true) {
					sleep(5000);
					while(touch) {
						refreshTime++;
						sleep(1000);
						if(refreshTime>15) {//15초마다 새로고침
							pA.orderP.setVisible(false);
							pA.fac.setVisible(false);
							pA.locker.setText("");
							pA.lockPW.setText("");
							card.show(showP,"매점");
							storeP.repaint();
							storeP.revalidate();
							refreshTime=0;
							System.out.println("화면 새로고침");
							cancel();
							touch = false;
						}
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	class MyFocusListener implements FocusListener{
		@Override
		public void focusGained(FocusEvent e) {
			txtName = e.getComponent().getName();
		}
		@Override
		public void focusLost(FocusEvent e) {}
	}
	
	public static void main(String[] args) {
		new Kiosk_store();
		
	}

}