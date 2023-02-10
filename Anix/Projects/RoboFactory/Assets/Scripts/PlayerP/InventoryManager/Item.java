package PlayerP.InventoryManager;

import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
import com.Anix.Math.Vector2f;

import Enums.ItemType;

public class Item implements Cloneable {
	private int amount = 1;
	
	public static int width = 32, height = 32;
	
	private ItemType itemType;
	
	private Texture texture;
	private Slot parent;
	
	public Item() {
		
	}
	
	public Item(int amount, ItemType itemType, Slot parent) {
		this.amount = amount;
		this.itemType = itemType;
		this.parent = parent;
		
		int index = 0;
		
		for(int i = 0; i < ItemType.values().length; i++) {
			if(ItemType.values()[i].name().equals(itemType.name())) {
				index = i;
				
				break;
			}
		}
		
		texture = parent.inventory.textures[index];
	}
	
	public void render(Vector2f position) {
		UI.drawImage(getTexture().getTextureID(), position.x, position.y, -0.1f, width, height);
	}
	
	@Override
	protected Item clone() throws CloneNotSupportedException {
		return (Item) super.clone();
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public void addAmount(int amount) {
		this.amount += amount;
		
		if(amount <= 0) {
			parent.setItem(null);
		}
	}
	
	public void removeAmount(int amount) {
		this.amount -= amount;
		
		if(amount <= 0) {
			parent.setItem(null);
		}
	}

	public ItemType getItemType() {
		return itemType;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public Slot getParent() {
		return parent;
	}
}