package gui_design;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JToggleButton;

public class Sep_Button extends JButton {
	
	
	//파랑 39,19,255
	//빨강 255,11,40
	//노랑 255,204,0
	
	 int R, G, B,rR, rG, rB ;
	public Sep_Button(String text,int R, int G, int B) {
		super(text);
		this.R = R;
		this.G = G;
		this.B = B;
		this.rR = R;
		this.rG = G;
		this.rB = B;
		setBackground(new Color(R,G,B));
		font();
	}
	
	void font() {
		setFont(new Font("고딕",Font.BOLD,30));
		setForeground(Color.black);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		
		Graphics2D grp = (Graphics2D)g;
		
		grp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
							RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(R!=240&&G!=240&&B!=240) {
			if (getModel().isArmed()) {//버튼이 눌리는동안)
				R=R-15;
				G=G-15;
				B=B-15;
				if(R<0) {R=0;}
				if(G<0) {G=0;}
				if(B<0) {B=0;}
				grp.setColor(new Color(R,G,B));
				R=rR;
				G=rG;
				B=rB;
			}else if (getModel().isRollover()) { //마우스 올린동안
				R=R+10;
				G=G+10;
				B=B+10;
				if(R>255) {R=255;}
				if(G>255) {G=255;}
				if(B>255) {B=255;}
				grp.setColor(new Color(R,G,B)); 
				R=rR;
				G=rG;
				B=rB;
			}else if(!getModel().isEnabled()){
				grp.setColor(new Color(200,200,200)); 
			}else { //평소
				grp.setColor(new Color(R,G,B));
				R=rR;
				G=rG;
				B=rB;
			}
		}

		
		
		grp.fillRoundRect(0, 0, width, height, 7, 7);
		
		FontMetrics fm = grp.getFontMetrics();
		Rectangle strBounds = fm.getStringBounds(this.getText(),grp).getBounds();
		
		int textX = (width - strBounds.width) / 2;
		int textY = (height - strBounds.height) / 2 + fm.getAscent();
		
		grp.setColor(getForeground());
		grp.setFont(getFont());
		grp.drawString(getText(), textX, textY);
		grp.dispose();

		
		super.paintComponent(g);
	}
		
}