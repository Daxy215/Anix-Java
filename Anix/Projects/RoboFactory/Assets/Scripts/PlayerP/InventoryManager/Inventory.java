package PlayerP.InventoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.Anix.Behaviours.Behaviour;
import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;

import Enums.ItemType;
import Managers.ChatManager;

public class Inventory extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;	
	
	public String name;
	
	public final int MAXAMOUNT = 100;
	public int slotWidth = 32, slotHeight = 32;
	public int sizeX = 6, sizeY = 8;
	private int padding = 4;
	
	public boolean showInventory, isDragging;
	
	private Item draggedItem;
	public Vector2f position = new Vector2f();
	
	public static Texture[] textures;
	public List<Slot> slots;
	public List<Item> items;
	
	public static List<Inventory> inventories = new ArrayList<Inventory>();
	
	/*public Inventory(String name, int sizeX, int sizeY) {
		//this.name = name + ":" + UUID.randomUUID().toString();
		//this.sizeX = sizeX;
		//this.sizeY = sizeY;
		
		//init();
		//System.err.println("added inventory: " + name);
	}*/
	
	@Override
	public void start() {
		updateSlots();
		
		requestUpdate();
	}
	
	public void update() {
		//if(Input.isKeyDown(KeyCode.I))
		//	showInventory = !showInventory;
		
		if(!showInventory)
			return;
		
		Slot slot = getSlotAt(new Vector2f((float)Input.getMouseX(), (float)Input.getMouseY()));
		
		//if(slot != null)
			//System.err.println("hovering over: " + slot.getNumber());
		
		if(slot != null) {
			if(Input.isMouseButtonDown(KeyCode.Mouse0)) {
				if(isDragging) {
					if(slot.isEmpty()) {
						isDragging = false;
						
						try {
							slot.setItem(draggedItem.clone());
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
						
						draggedItem = null;
					} else if(slot.getItem().getItemType().equals(draggedItem.getItemType())) {
						if(slot.getItem().getAmount() + draggedItem.getAmount() <= MAXAMOUNT) {
							isDragging = false;
							slot.getItem().addAmount(draggedItem.getAmount());
							
							draggedItem = null;
						} else if(slot.getItem().getAmount() < MAXAMOUNT) {
							int extra = (slot.getItem().getAmount() + draggedItem.getAmount()) - MAXAMOUNT;
							
							slot.getItem().addAmount(draggedItem.getAmount() - extra);
							draggedItem.setAmount(extra);
						}
					} else {
						Item slotItem = slot.getItem();
						slot.setItem(draggedItem);
						
						try {
							draggedItem = slotItem.clone();
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
					}
				} else if(!isDragging && !slot.isEmpty()) {
					isDragging = true;
					draggedItem = slot.getItem();
					
					slot.setItem(null);
				}
			} else if(Input.isMouseButtonDown(KeyCode.Mouse1)) {
				if(!slot.isEmpty() && !isDragging) {
					if(slot.getItem().getAmount() > 1) {
						if(slot.getItem().getAmount() % 2 == 0) {
							slot.getItem().removeAmount(Math.round(slot.getItem().getAmount() * 0.5f));
							
							isDragging = true;
							
							try {
								draggedItem = slot.getItem().clone();
							} catch (CloneNotSupportedException e) {
								e.printStackTrace();
							}
						} else {
							slot.getItem().removeAmount(Math.round(slot.getItem().getAmount() * 0.5f));
							
							isDragging = true;
							
							try {
								draggedItem = slot.getItem().clone();
								draggedItem.addAmount(1);
							} catch (CloneNotSupportedException e) {
								e.printStackTrace();
							}
						}
					}
				} else if(isDragging) {
					if(!slot.isEmpty() && slot.getItem().getItemType().equals(draggedItem.getItemType())) {
						if(slot.getItem().getAmount() < MAXAMOUNT) {
							slot.getItem().addAmount(1);
							draggedItem.removeAmount(1);
						}
					} else {
						slot.setItem(new Item(1, draggedItem.getItemType(), slot));
						draggedItem.removeAmount(1);
					}
					
					if(draggedItem.getAmount() <= 0) {
						isDragging = false;
						draggedItem = null;
					}
				}
			}
			
			/*if(!isDragging && !slot.isEmpty()) {
				//Show description of the item.
				String info = slot.getItem().getItemType().name() + "\n";
				
				UI.drawBox((float)Input.getMouseX() + 30, (float)Input.getMouseY() + UI.getFontMatrics().getHeight(), 0, 80, info.split("\n").length * UI.getFontMatrics().getHeight(), new Text(info, new Vector2f(0.5f), new Vector3f(), Color.white), Color.black);
			}*/
		} else if(isDragging) {
			Inventory inv = Inventory.getInventoryAt(new Vector2f((float)Input.getMouseX(), (float)Input.getMouseY()));
			
			if(inv != null && inv != this && inv.showInventory) {
				inv.isDragging = true;
				
				try {
					inv.draggedItem = draggedItem.clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				
				draggedItem = null;
				isDragging = false;
			}
		}
		
		if(isDragging) {
			int x = (int)(slotWidth * 0.5f);
			
			if(draggedItem.getAmount() >= 10) {
				x -= 4;
			} else if(draggedItem.getAmount() == 100) {
				x -= 10;
			}
			
			draggedItem.render(new Vector2f((float)Input.getMouseX(), (float)Input.getMouseY()));
			UI.drawString("x" + draggedItem.getAmount(), (float)Input.getMouseX() + x, (float)Input.getMouseY() + (slotWidth * 0.5f), -0.2f, 0.5f, 0.5f, Color.white);
		}
		
		for(int i = 0; i < slots.size(); i++) {
			UI.drawImage(slots.get(i).getTexture().getTextureID(), position.x + slots.get(i).getPosition().x, position.y + slots.get(i).getPosition().y, 0.1f, slotWidth, slotHeight);
			slots.get(i).render();
		}
	}
	
	public void updateSlots() {
		if(textures == null)
			textures = new Texture[ItemType.values().length];
		
		slots = new ArrayList<Slot>();
		items = new ArrayList<Item>();
		
		name += ":" + UUID.randomUUID().toString();
		
		if(position != null && position.x < 250 || position.y < 25) {
			position.x += 250;
			position.y += 25;
		}
		
		inventories.add(this);
		
		if(textures[0] == null)
			for(int i = 0; i < ItemType.values().length; i++) {
				textures[i] = UI.loadTexture("/Inventory/Items/" + ItemType.values()[i] + ".png");
			}
		
		for(int y = 0; y < sizeY; y++) {
			for(int x = 0; x < sizeX; x++) {
				slots.add(new Slot(slots.size(), new Vector2f(x * slotWidth + (padding * x), y * slotHeight + (padding * y)), this));
			}
		}
	}
	
	public static Inventory getInventoryAt(Vector2f position) {
		Inventory inventory = null;
		
		float x = position.x, y = position.y;
		
		for(int i = 0; i < inventories.size(); i++) {
			float ix = inventories.get(i).position.x, iy = inventories.get(i).position.y;
			
			if(x >= ix && x <= ix + (inventories.get(i).sizeX * inventories.get(i).slotWidth + (inventories.get(i).sizeX * inventories.get(i).padding))) {
				if(y >= iy && y <= iy + (inventories.get(i).sizeY * inventories.get(i).slotHeight + (inventories.get(i).sizeY * inventories.get(i).padding))) {
					inventory = inventories.get(i);
					
					break;
				}
			}
		}
		
		return inventory;
	}
	
	public Slot getSlotAt(Vector2f position) {
		float x = position.x, y = position.y;

		for(int i = 0; i < slots.size(); i++) {
			float sx = slots.get(i).getPosition().x + this.position.x,
					sy = slots.get(i).getPosition().y + this.position.y;

			if(x >= sx && x <= sx + slotWidth) {
				if(y >= sy && y <= sy + slotHeight) {
					return slots.get(i);
				}
			}
		}

		return null;
	}

	public int getAmountOfAnItem(ItemType itemType) {
		int amount = 0;
		
		for(int i = 0; i < slots.size(); i++) {
			if(!slots.get(i).isEmpty() && slots.get(i).getItem().getItemType().equals(itemType)) {
				amount += slots.get(i).getItem().getAmount();
			}
		}

		return amount;
	}
	
	public int getFirstItem() {
		int index = -1;
		
		for(int i = 0; i < slots.size(); i++) {
			if(!slots.get(i).isEmpty()) {
				index = i;
				
				break;
			}
		}
		
		return index;
	}

	public boolean hasAmountOfAnItem(ItemType itemType, int amount) {
		int total = 0;
		
		for(int i = 0; i < slots.size(); i++) {
			if(!slots.get(i).isEmpty() && slots.get(i).getItem().getItemType().equals(itemType)) {
				total += slots.get(i).getItem().getAmount();
			}
		}

		return total >= amount;
	}

	public int hasItem(ItemType itemType) {
		for(int i = 0; i < slots.size(); i++) {
			if(!slots.get(i).isEmpty() && slots.get(i).getItem().getItemType().equals(itemType)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public void addItem(ItemType itemType, int amount) {
		if(amount <= 0)
			return;
		
		int index = hasItem(itemType);
		
		if(index != -1) {
			Slot slot = slots.get(index);
			
			if(slot.getItem().getAmount() + amount <= MAXAMOUNT) {
				slot.getItem().addAmount(amount);
				
				return;
			}
			
			if(slot.getItem().getAmount() < MAXAMOUNT) {
				int extra = (slot.getItem().getAmount() + amount) - MAXAMOUNT;
				//TODO: am: 3, slot: 99.. 99 + 3 - 100 - 2.. 2 + 99 = 101. Try to fix this.
				//fixed? idk
				
				slot.getItem().addAmount(amount - extra);
				amount -= extra;
			}
		}
		
		if(amount <= 0)
			return;
		
		for(int i = 0; i < slots.size(); i++) {
			if(i == index)
				continue;
			
			if(slots.get(i).isEmpty()) {
				if(amount <= MAXAMOUNT) {
					slots.get(i).setItem(new Item(amount, itemType, slots.get(i)));
				} else {
					int extra = amount - MAXAMOUNT;
					
					slots.get(i).setItem(new Item(amount - extra, itemType, slots.get(i)));
					
					addItem(itemType, extra);
				}
				
				return;
			} else if(slots.get(i).getItem() != null && slots.get(i).getItem().getItemType().equals(itemType)) {
				if(slots.get(i).getItem().getAmount() + amount <= MAXAMOUNT) {
					slots.get(i).getItem().addAmount(amount);
					
					return;
				}
				
				if(slots.get(i).getItem().getAmount() < MAXAMOUNT) {
					int extra = (slots.get(i).getItem().getAmount() + amount) - MAXAMOUNT;
					
					slots.get(i).getItem().addAmount(amount - extra);
					
					addItem(itemType, extra, i + 1);
					
					return;
				}
			}
		}
		
		ChatManager.addText("[INVENTORY MANAGER] Inventory is full!", Color.red);
	}

	private void addItem(ItemType itemType, int amount, int startIndex) {
		if(amount <= 0)
			return;
		
		for(int i = startIndex; i < slots.size(); i++) {
			if(slots.get(i).isEmpty()) {
				if(amount <= MAXAMOUNT) {
					slots.get(i).setItem(new Item(amount, itemType, slots.get(i)));
				} else {
					int extra = amount - MAXAMOUNT;
					
					slots.get(i).setItem(new Item(amount - extra, itemType, slots.get(i)));
					
					addItem(itemType, extra);
				}
				
				return;
			} else if(slots.get(i).getItem() != null && slots.get(i).getItem().getItemType().equals(itemType)) {
				if(slots.get(i).getItem().getAmount() + amount <= MAXAMOUNT) {
					slots.get(i).getItem().addAmount(amount);

					return;
				}
				
				if(slots.get(i).getItem().getAmount() < MAXAMOUNT) {
					int extra = (slots.get(i).getItem().getAmount() + amount) - MAXAMOUNT;
	
					slots.get(i).getItem().addAmount(amount - extra);
	
					addItem(itemType, extra, i + 1);
	
					return;
				}
			}
		}
		
		ChatManager.addText("[INVENTORY MANAGER] Inventory is full!", Color.red);
	}
	
	public void setItem(int index, int amount, ItemType itemType) {
		slots.get(index).setItem(new Item(amount, itemType, slots.get(index)));
	}
	
	public boolean removeItem(int index, int amount) {
		int amountLeft = amount;
		
		if(amountLeft <= 0) {
			return false;
		}
		
		if(slots.get(index).getItem().getAmount() >= amount) {
			slots.get(index).getItem().removeAmount(amount);
			
			return true;
		}
		
		if(slots.get(index).getItem().getAmount() == MAXAMOUNT && amountLeft >= 100) {
			slots.get(index).setItem(null);
			amountLeft -= 100;
			
			return removeItem(index, amountLeft);
		}
		
		int extra = (slots.get(index).getItem().getAmount() + amountLeft) - MAXAMOUNT;
		
		slots.get(index).getItem().removeAmount(amountLeft - extra);
		
		amountLeft -= extra;

		if(amountLeft <= 0)
			return true;
		else
			return false;
	}
	
	public void removeItem(ItemType itemType, int amount) {
		int amountLeft = amount;
		
		if(amountLeft <= 0) {
			return;
		}
		
		for(int i = 0; i < slots.size(); i++) {
			if(!slots.get(i).isEmpty() && slots.get(i).getItem().getItemType().equals(itemType)) {
				if(slots.get(i).getItem().getAmount() >= amount) {
					slots.get(i).getItem().removeAmount(amount);

					return;
				}
				
				if(slots.get(i).getItem().getAmount() == MAXAMOUNT && amountLeft >= 100) {
					slots.get(i).setItem(null);
					amountLeft -= 100;
					
					removeItem(itemType, amountLeft);
					
					return;
				}
				
				int extra = (slots.get(i).getItem().getAmount() + amountLeft) - MAXAMOUNT;
				
				slots.get(i).getItem().removeAmount(amountLeft - extra);
				
				amountLeft -= extra;
				
				if(amountLeft <= 0)
					return;
			}
		}

		if(amountLeft > 0) {
			System.err.println("ERORR TSH: Couldn't find enough of " + itemType.name() + " left: " + amountLeft);
		}
	}
	
	public boolean isEmpty() {
		boolean isEmpty = true;
		
		for(int i = 0; i < slots.size(); i++) {
			if(!slots.get(i).isEmpty()) {
				isEmpty = false;
				
				break;
			}
		}
		
		return isEmpty;
	}
	
	public boolean isFull() {
		boolean isFull = false;
		
		for(int i = 0; i < slots.size(); i++) {
			if(slots.get(i).isEmpty()) {
				isFull = false;
				
				break;
			}
		}
		
		return isFull;
	}

	public void updateSizes(int sizeX, int sizeY) {
		if(this.sizeX != sizeX) {
			for(int y = 0; y < sizeY; y++) {
				for(int x = this.sizeX; x < sizeX; x++) {
					slots.add(new Slot(slots.size(), new Vector2f(x * slotWidth + (padding * x), y * slotHeight + (padding * y)), this));
				}
			}
			
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			
			return;
		} else if(this.sizeY != sizeY) {
			for(int y = this.sizeY; y < sizeY; y++) {
				for(int x = 0; x < sizeX; x++) {
					slots.add(new Slot(slots.size(), new Vector2f(x * slotWidth + (padding * x), y * slotHeight + (padding * y)), this));
				}
			}
			
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			
			return;
		}
		
		for(int y = this.sizeY; y < sizeY; y++) {
			for(int x = this.sizeX; x < sizeX; x++) {
				slots.add(new Slot(slots.size(), new Vector2f(x * slotWidth + (padding * x), y * slotHeight + (padding * y)), this));
			}
		}
		
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	public boolean toggle() {
		this.showInventory = !showInventory;
		
		if(!showInventory && isDragging) {
			isDragging = false;
			draggedItem = null;
		}
		
		return showInventory;
	}
	
	public boolean canShowInventory() {
		return showInventory;
	}
	
	public void setShowInventory(boolean showInventory) {
		this.showInventory = showInventory;
	}
	
	public void setPosition(Vector2f position) {
		this.position = position;
	}
}
