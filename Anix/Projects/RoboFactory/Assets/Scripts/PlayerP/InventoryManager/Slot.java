package PlayerP.InventoryManager;

import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;

public class Slot {
	private int number;

	private boolean isEmpty = true;

	public Inventory inventory;
	private Item item;

	private Texture texture;
	private Vector2f position;
	
	public Slot() {
		
	}
	
	public Slot(int number, Vector2f position, Inventory inventory) {
		this.number = number;
		this.position = position;
		this.inventory = inventory;

		texture = UI.loadTexture("resources/textures/Inventory/slotImage.png");
	}
	
	public void render() {
		if(isEmpty)
			return;
		
		if(item.getAmount() <= 0) {
			setItem(null);
			
			return;
		}
		
		int x = (int) (inventory.slotWidth * 0.5f);
		
		if(item.getAmount() >= 10) {
			x -= 4;
		} else if(item.getAmount() == 100) {
			x -= 10;
		}
		
		UI.drawString("x" + item.getAmount(), inventory.position.x + position.x + x, inventory.position.y + position.y + (int) (inventory.slotWidth * 0.5f), -0.2f, 0.5f, 0.5f, Color.white);
		item.render(new Vector2f(inventory.position.x + position.x + 12, inventory.position.y + position.y + 12));
	}
	
	public int getNumber() {
		return number;
	}
	
	public boolean isEmpty() {
		return isEmpty;
	}
	
	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
		
		isEmpty = item == null;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public void setPosition(Vector2f position) {
		this.position = position;
	}
}