package lanex.one;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class OneGame extends ScreenPage {
	static Player player;
	static Vector2f mousePosition;
	public static int level = 1;
	boolean mouseON = false;
	public static final float WALL_THICKNESS = 40;
	public static final float leftBound = WALL_THICKNESS,
			rightBound = 720 - WALL_THICKNESS, upBound = WALL_THICKNESS,
			lowBound = 720 - WALL_THICKNESS;
	public static final Rectangle topDoor = new Rectangle(335, 0, 50, 50),
			bottomDoor = new Rectangle(335, 670, 50, 50),
			leftDoor = new Rectangle(0, 335, 50, 50),
			rightDoor = new Rectangle(670, 335, 50, 50);

	// GAME LOGIC
	static Floor currentFloor;
	static Room currentRoom;

	public static void initNew() {
		level = 1;
		player = new Player(100, 100);
		mousePosition = new Vector2f(player.position.x, player.position.y);
		makeFloor();
	}
	public static void makeFloor() {
		currentFloor = new Floor(level);
		currentFloor.generate();
		currentRoom = currentFloor.getRoom(0, 0);
		level++;
	}

	public OneGame() {
		player = new Player(100, 100);
		mousePosition = new Vector2f(0, 0);
	}

	@Override
	public void render(GameContainer container, Graphics g) {
		// PROCESSING

		if (mouseON) {
			Vector2f acceleration = mousePosition.copy().sub(player.position)
					.normalise().scale(1f);
			player.accelerate(acceleration);
			// ERM.getImage("character.png").setRotation((float)
			// acceleration.getTheta());
			// / SET DIRECTION OF PLAYER
		}

		// UPDATE ALL
		{
			player.update();
			currentRoom.update(player);
			checkDoors();
		}

		// OneApp.THC.append("mouse position: " + mousePosition +
		// " Player velocity: " + player.velocity);

		g.clear();
		g.setColor(Color.white);
		g.drawString("IN GAME!!!", 100, 100);

		// ERM.listRes();
		// System.out.println("IMAGE: " + ERM.getImage("room.png"));

		// ERM.getImage("character.png").setRotation(ERM.getImage("character.png").getRotation()+1);
		currentRoom.render(g);

//		// DEBUG DRAW DOORS TO CHECK
//		g.draw(topDoor);
//		g.draw(bottomDoor);
//		g.draw(leftDoor);
//		g.draw(rightDoor);

		player.render(g);

		
		//STATS
		g.setColor (Color.white);
		g.drawString ("Score: " + player.score,740,20);
		g.drawString ("Health: " + player.health,740,40);
		g.drawString ("Damage: " + player.damage,740,60);
		g.drawString ("Strength: " + player.mass,740,80);
		g.drawString ("Speed: " + player.MAX_SPEED,740,100);
		
		
		//DRAW MAP
		g.setColor(Color.red);
		g.drawRect(980, 0, 300, 300);
		for(int x = 0; x < currentFloor.size; x++){
			for(int y = 0; y < currentFloor.size; y++){
				g.drawRect(980 + x*300/currentFloor.size, y*300/currentFloor.size, 300/currentFloor.size, 300/currentFloor.size);
			}
		}
		
		g.fillRect(980 + currentRoom.indX*300/currentFloor.size, currentRoom.indY*300/currentFloor.size, 300/currentFloor.size, 300/currentFloor.size);
		
	}

	public void checkDoors() {
		int x = 0, y = 0;

		if (topDoor.contains(player.position.x, player.position.y)) {
			x = currentRoom.indX;
			y = currentRoom.indY - 1;
			player.position.y = 670;
		} else if (bottomDoor.contains(player.position.x, player.position.y)) {
			x = currentRoom.indX;
			y = currentRoom.indY + 1;
			player.position.y = 50;

		} else if (leftDoor.contains(player.position.x, player.position.y)) {
			x = currentRoom.indX - 1;
			y = currentRoom.indY;
			player.position.x = 670;

		} else if (rightDoor.contains(player.position.x, player.position.y)) {
			x = currentRoom.indX + 1;
			y = currentRoom.indY;
			player.position.x = 50;
		} else {
			return;
		}
		if (x < 0) x = currentFloor.size-1;
		if (y < 0) y = currentFloor.size-1;
		if (x > currentFloor.size-1) x = 0;
		if (y > currentFloor.size-1) y = 0;
		currentRoom = currentFloor.getRoom(x, y);
		ERM.sfx("door.se.ogg");
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
		mousePosition.x = newx;
		mousePosition.y = newy;
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		if (button == Input.MOUSE_LEFT_BUTTON)
		{
			float theta = (float) Math.atan((y-player.position.y)/(x-player.position.x)); 
			player.velocity = new Vector2f((float)Math.cos(theta)*player.MAX_SPEED/2,(float)Math.sin(theta)*player.MAX_SPEED/2);
		}
		mouseDragged(x, y, x, y);
		mouseON = true;
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		mouseON = false;
	}

}
