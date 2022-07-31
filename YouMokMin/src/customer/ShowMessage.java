package customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui_design.Graphic_Button;


public class ShowMessage extends Thread{
	
	JFrame backP;
	String msg;
	int time;
	
	public ShowMessage(JFrame backP, String msg, int time){
		
		this.backP = backP;
		this.msg = msg;
		this.time = time;
		this.start();
	}
	
	public ShowMessage(){
	}
	
	@Override
	public void run() {
		try{
			System.out.println("스레드 실행중");

			
			popUpMsg pu = new popUpMsg(backP,msg);
			
			Thread.sleep(time);
		
			pu.closePopUpMsg();
			
			this.interrupt();
			System.out.println("스레드 종료");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	class popUpMsg extends JFrame{
		
		JFrame backP;
		JPanel textPanel;
		Graphic_Button gb1 = new Graphic_Button(39,19,255); //파
		Graphic_Button gb2 = new Graphic_Button(255,11,40); //빨
		Graphic_Button gb3 = new Graphic_Button(255,204,0); //노
		Graphic_Button gb4 = new Graphic_Button(44,141,23);//초
		Graphic_Button gb5 = new Graphic_Button(0,0,0);      //검
		
		
		public popUpMsg(JFrame backP,String str) {
			
			setSize(350,250);
			setLocation(backP.getX()+145, backP.getY()+200);
			setUndecorated(true);
			setBackground(new Color(255,255,255,0));
			setLayout(null);
			setVisible(true);
			setAlwaysOnTop(true);
			
			gb1.setEnabled(false);
			gb2.setEnabled(false);
			gb3.setEnabled(false);
			gb4.setEnabled(false);
			gb5.setEnabled(false);
			gb1.setBounds(0,0,20,150);//왼쪽
			gb2.setBounds(0,150,20,170);//왼쪽아래
			gb3.setBounds(0,230,350,20);//아래
			gb4.setBounds(330,0,20,250);//오른쪽
			gb5.setBounds(0,0,350,20);//위
			
			
			textPanel = new JPanel();
			textPanel.setBounds(0,0,350,250);
			textPanel.setBackground(Color.white);
			textPanel.setLayout(new BorderLayout());
			
			JLabel msg = new JLabel(str,JLabel.CENTER);
			msg.setFont(new Font("나눔고딕",Font.BOLD,17));
			msg.setForeground(Color.black);
			msg.setVisible(true);
			
			textPanel.add(gb1);
			textPanel.add(gb2);
			textPanel.add(gb3);
			textPanel.add(gb4);
			textPanel.add(gb5);
			
			textPanel.add(msg,BorderLayout.CENTER);
			
			setContentPane(textPanel);
			
		}
		
		public void closePopUpMsg() {
			dispose();
		}
	}
}