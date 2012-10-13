package lanex.one;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.MouseOverArea;

public class OneMenu extends ScreenPage{
	
	public OneMenu(){
	}

	@Override
	public void render(GameContainer container, Graphics g) {
		// PROCESSING
		
		g.clear();
		g.setColor(Color.white);
		g.drawString("IN GAME!!!", 100, 100);
		
		//ERM.listRes();
		//System.out.println("IMAGE: " + ERM.getImage("room.png"));

		
		g.drawImage(ERM.getImage("menu_back.png"), 0, 0);
		
		
		// WIDTH SHOULD BE 400
		// HEIGHT SHOULD BE 100
		g.setColor(Color.green);
		g.drawRect(OneApp.RENDER_WIDTH/2 - 200,  500, 400, 100);
		g.drawRect(OneApp.RENDER_WIDTH/2 - 200,  350, 400, 100);
		
	}

	@Override
	public void keyPressed(int key, char c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(int key, char c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		OneGame.initNew();
		OneApp.splash.reset();
		OneSplash.setRedirect("game");		
		OneApp.setPage("splash");
		
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
