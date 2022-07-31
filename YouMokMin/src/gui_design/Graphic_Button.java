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

public class Graphic_Button extends JButton {

	
	
		//파랑 39,19,255 or 64,85,163
		//빨강 255,11,40
		//노랑 255,204,0
	
	int R,G,B;
	public Graphic_Button(int R, int G, int B) {
		this.R = R;
		this.G = G;
		this.B = B;
	}
	

	@Override
	protected void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		
		Graphics2D grp = (Graphics2D)g;
		

		grp.setColor(new Color(R,G,B));
		
		grp.fillRect(0, 0, width, height);
		
				
	
	
		grp.setColor(getForeground());
		grp.dispose();


		super.paintComponent(g);
	}
	
}