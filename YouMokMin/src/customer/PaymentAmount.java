package customer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import db.DAO;
import db.CustomerDTO;
import db.PaymentDTO;
import gui_design.Menu_Button;
import gui_design.PayList_Button;

public class PaymentAmount extends JPanel{


	JPanel orderP = new JPanel();
	JPanel fac = new JPanel();
	JPanel total = new JPanel();
	JPanel detail = new JPanel();
	
	JScrollPane scrollP = new JScrollPane(detail);
	
	JLabel title = new JLabel("",JLabel.CENTER);
	
	JTextField locker = new JTextField();
	JPasswordField lockPW = new JPasswordField();
	
	String txtName;
	String lockerNum, pwNum;
	
	JFrame paymentFF;
	
	DecimalFormat formatter = new DecimalFormat("###,###");
	
	int R,G,B;
	public PaymentAmount(int R, int G, int B) {
		
		locker.setFont(new Font("나눔고딕",Font.BOLD,35));
		locker.setForeground(Color.black);
		lockPW.setFont(new Font("나눔고딕",Font.BOLD,35));
		lockPW.setForeground(Color.black);
		
		this.R = R;
		this.G = G;
		this.B = B;
		
		setBounds(20,20,585,630);
		setBackground(Color.white);
		setLayout(null);
		
		fac.setBounds(0,0,585,630);
		fac.setBackground(Color.white);
		fac.setLayout(null);
		
		total.setBounds(0,90,600,100);
		total.setBackground(Color.white);
		total.setLayout(new GridLayout(1,1));
		
		scrollP.setBounds(0,225,585,410);
		detail.setBackground(Color.white);
		detail.setLayout(new GridLayout(50,1,5,5));
		
		login();
		
		add(orderP);
		
		repaint();
		revalidate();
		
	}
	
	void FeeAmoutCheck(String lockerNum) {

		orderP.setVisible(false);
		
		String ttext = (new DAO().customerNickname(Integer.parseInt(lockerNum))).getNickname()+
						"님의 \n결제예정 금액";
		
		title.setFont(new Font("고딕",Font.BOLD,30));
		title.setForeground(Color.black);
		title.setText(ttext);
		title.setBounds(0,10,585,100);
		
		
		PaymentDTO pdto = new DAO().paymentFee(Integer.parseInt(lockerNum));

		String totStr = "총  "+Integer.toString(pdto.getAmount())+"개     "+
				formatter.format(pdto.getPrice())+"원";
		
		detail.removeAll();
		total.removeAll();
		fac.removeAll();//지우기
		
		
		JButton tot = new PayList_Button(totStr,R,G,B,30);
		//tot.setText(totStr);
		tot.setEnabled(false);
		total.add(tot);
		
		int detY = 0;
		for(Object ob :new DAO().paymentFeeDetail(Integer.parseInt(lockerNum))) {
			PaymentDTO ddto = (PaymentDTO)ob;
			
			if(ddto.getAmount()!=0) {
				String detStr = ddto.getMenu()+"  "+Integer.toString(ddto.getAmount())+"개  "+
						formatter.format(ddto.getPrice())+"원";
				JButton det = new PayList_Button(detStr,220,220,220,20);
				det.setBounds(0,detY,600,80);
				System.out.println(detStr);
				det.setEnabled(false);
				detail.add(det);
				detY += 90;	
			}
			
		}
		
		
		fac.add(title);
		fac.add(total);
		fac.add(scrollP);
		add(fac);
		
		fac.repaint();
		fac.revalidate();
	}
	
	void login() { //주문하기를 누르면 나오는 화면

		orderP.setBounds(0,0,585,630);
		orderP.setLayout(null);
		orderP.setBackground(Color.white);
		
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
		numB.setBounds(80,300,300,300);
		numB.setBackground(Color.white);
		
		String[] passButton = {"1","2","3","4","5","6","7","8","9","<","0","지움"};
		for(String str:passButton){
			JButton btnN = new Menu_Button(str,200,200,200,25);
			btnN.addActionListener(new OrderSubmitAction());
			numB.add(btnN);
		}
		
		JPanel sletB = new JPanel();
		sletB.setLayout(new GridLayout(1,1));
		sletB.setBounds(400,300,150,300);
		sletB.setBackground(Color.white);
		
		//JButton backSpace = new menu_Button("지움",230,230,230);
		JButton submit = new Menu_Button("확인",R,G,B);
		//JButton goBack = new menu_Button("뒤로가기",230,230,230,30);
		
		//backSpace.addActionListener(new OrderSubmitAction());
		submit.addActionListener(new OrderSubmitAction());
		//goBack.addActionListener(new OrderSubmitAction());
		
		//sletB.add(backSpace);
		sletB.add(submit);
		//sletB.add(goBack);
		
		orderP.add(locker);
		orderP.add(lockPW);
		orderP.add(numB);
		orderP.add(sletB);
		
		orderP.repaint();
		orderP.revalidate();

	}
	
	void customerCheck() {
		String lockerNum = locker.getText();
		String pwNum = lockPW.getText();
		
		boolean lockPass=false;
		boolean PWPass=false;
		
		ShowMessage showMsg = new ShowMessage();
		showMsg.backP = paymentFF;
		showMsg.time = 1000;
		
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
			showMsg.msg = "락커번호와 비밀번호를 모두 입력해주세요";
			showMsg.start();
		}else {
			if(lockPass&&PWPass) {
				FeeAmoutCheck(lockerNum);
				fac.setVisible(true);
				locker.setText("");
				lockPW.setText("");
				
				
			}else if(!PWPass&&!lockPass) {
				
				showMsg.msg = "락커번호와 비밀번호를 다시 입력해주세요";
				showMsg.start();
				
			}else if(lockPass) {
				
				showMsg.msg = "비밀번호를 다시 입력해주세요";
				showMsg.start();
				
			}

		}
		
	}
	
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
	
	class MyFocusListener implements FocusListener{

		@Override
		public void focusGained(FocusEvent e) {
			txtName = e.getComponent().getName();
		}

		@Override
		public void focusLost(FocusEvent e) {}
		
	}
	
	class OrderSubmitAction implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			String push = e.getActionCommand();
			
			switch(push) {
				case "취소":
					
					break;	
				case "확인":
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
	
	
	public static void main(String[] args) {
		
		

	}

}