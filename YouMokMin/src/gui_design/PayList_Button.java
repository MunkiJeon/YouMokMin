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

public class PayList_Button extends JButton {

	
	int round = 10;
	
	//파랑 39,19,255
	//빨강 255,11,40
	//노랑 255,204,0
	
	
	int rR, rG, rB , R,G,B, font=40;
	public PayList_Button(String text,int R, int G, int B) {
		super(text);
		this.rR = R;
		this.rG = G;
		this.rB = B;
		this.R = R;
		this.G = G;
		this.B = B;
		font();
	}
	
	public PayList_Button(String text,int R, int G, int B,int font) {
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
		
		grp.setColor(new Color(R,G,B));
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