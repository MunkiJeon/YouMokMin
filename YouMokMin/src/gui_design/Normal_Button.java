package gui_design;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Normal_Button extends JButton {

	
	int round = 10;
	
			//파랑 39,19,255 or 64,85,163
			//빨강 255,11,40
			//노랑 255,204,0
	Image image;
	
	int rR, rG, rB , R,G,B, font=40;
	public Normal_Button(String text,int R, int G, int B) {
		super(text);
		this.rR = R;
		this.rG = G;
		this.rB = B;
		this.R = R;
		this.G = G;
		this.B = B;
		font();
	}
	
	public Normal_Button(String text,int R, int G, int B,int font) {
		super(text);
		this.rR = R;
		this.rG = G;
		this.rB = B;
		this.R = R;
		this.G = G;
		this.B = B;
		this.font = font;
		font();
	}
	
	public Normal_Button(String text,int R, int G, int B,int nll,int nul) {
		ImageIcon img = new ImageIcon(text);
		image = img.getImage();
		this.rR = R;
		this.rG = G;
		this.rB = B;
		this.R = R;
		this.G = G;
		this.B = B;
		font();
	}
	
	
	void font() {
	
		setFont(new Font("나눔고딕",Font.BOLD,font));
		setForeground(Color.black);
	}

	
	@Override
	protected void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		
		Graphics2D grp = (Graphics2D)g;
		
		grp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
							 RenderingHints.VALUE_ANTIALIAS_ON);

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
		}else { //평소
			R=rR;
			G=rG;
			B=rB;
			grp.setColor(new Color(R,G,B)); 
		}
		
		
		
		grp.fillRoundRect(0, 0, width, height, round, round);
		
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