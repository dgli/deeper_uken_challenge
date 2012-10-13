package lanex.one;

public class Button {
	protected float x;
	protected float y;
	protected float width;
	protected float height;
	
	public Button (float x, float y, float width, float height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean ifOnButton(float mx, float my)
	{
		return mx > x && mx < x + width && my > y && my < y + height;
	}
}
