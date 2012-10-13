package lanex.one;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;

public class Room {

	public final static float ROOM_X_SIZE = 720f;
	public final static float ROOM_Y_SIZE = 720f;
	public int indX, indY;
	protected ArrayList<Enemy> mobs;
	protected ArrayList<StaticEntity> items;

	public Room(int x, int y) {
		indX = x;
		indY = y;
		mobs = new ArrayList<Enemy>();
		items = new ArrayList<StaticEntity>();
	}

	public void update(Player p) {
		for (Enemy e : mobs) {
			e.update(p);
		}
		for (StaticEntity i : items) {
			i.update(p);
		}
		for (int i = 0; i < mobs.size(); i++) {
			if (mobs.get(i).health <= 0)
				mobs.remove(mobs.get(i));
		}
		if (p.health <= 0)
			;
		// System.exit (0);
	}

	public void render(Graphics g) {
		g.drawImage(ERM.getImage("room.png"), 0, 0);

		for (Enemy e : mobs) {
			e.render(g);
		}
		for (StaticEntity i : items) {
			i.render(g);
		}
	}

	public void addMob() {
		int str = (int) (Math.random() * 3);
		if (str == 0)
			mobs.add(new Enemy(MobTypes.LARGE_MOB));
		else if (str == 1)
			mobs.add(new Enemy(MobTypes.MEDIUM_MOB));
		else
			mobs.add(new Enemy(MobTypes.SMALL_MOB));
	}

}
