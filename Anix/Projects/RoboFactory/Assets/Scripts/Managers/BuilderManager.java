import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.Engine.Editor;
import com.Anix.GUI.Panel.Text;
import com.Anix.GUI.UI;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

import Buildings.Building;
import Enums.ItemType;

public class BuilderManager extends Behaviour {
	/*
	 * Please ignore this tyvm :)
	 */
	private static final long serialVersionUID = 1L;	
	
	private int buildingIndex;
	
	public boolean isBuilding;
	public Building selectedBuilding;
	
	private GameObject placeHolder;
	
	public static BuilderManager instance;
	
	public List<Vector2f> placedBuildings;
	public List<Building> buildings;
	
	@Override
	public void awake() {
		buildings = new ArrayList<Building>();
		placedBuildings = new ArrayList<>();
	}
	
	@Override
	public void start() {
		instance = this;
		
		placeHolder = new GameObject("PlaceHolder");
		placeHolder.setIsEnabled(false);
		placeHolder.addBehaviour(SpriteRenderer.class).spriteName = "hoveredArea.png";
	}
	
	@Override
	public void update() {
		if(Input.isKeyDown(KeyCode.T)) {
			isBuilding = !isBuilding;
			
			placeHolder.setIsEnabled(isBuilding);
		}
		
		//buildingIndex += Input.getScrollX();
		
		//if(buildingIndex > buildings.size())
		//	buildingIndex = 0;
		//if(buildingIndex < 0)
		//	buildingIndex = buildings.size() - 1;
		
		//selectedBuilding = buildings.get(buildingIndex);
		
		for(int i = 0; i < buildings.size(); i++) {
			Building b = buildings.get(i);
			
			int craftAmount = getCraftAmount(b);
			
			boolean canCraft = craftAmount != 0;
			
			String name = b.getName() + (canCraft ? " x" + craftAmount : "");
			
			float x = 250;
			
			if(UI.drawButton(x, (i * 32) + (i * 8), 64, 32, name, 0, -8, 0.5f, 0.5f, Color.white)) {
				if(Input.isMouseButtonDown(KeyCode.Mouse0)) {
					System.err.println("canL: " + canCraft);
					if(canCraft) {
						selectedBuilding = b;
					}
				} else {
					//UI.drawBox(x, (i * 32) + (i * 8), 0.03f, 64, 32, Color.black);
					
					for(int j = 0; j < b.requirements.get(0).size(); j++) {
						boolean hasAmount = Player.inventory.hasAmountOfAnItem(ItemType.valueOf(b.requirements.get(0).get(j).type.name()), b.requirements.get(0).get(j).amount);
						
						UI.drawString(b.requirements.get(0).get(j).type + ": x" + b.requirements.get(0).get(j).amount, x, (i * 32)+(i*8)+(j*16)-2, 0.02f, 0.5f, 0.5f, hasAmount ? Color.white : Color.red);
					}
				}
			}
		}
		
		if(!isBuilding)
			return;
		
		if(selectedBuilding == null)
			selectedBuilding = buildings.get(0);
		
		Vector2f cord = Camera.main.convertScreenToWorldSpace();
		Vector2f pos = new Vector2f(Math.round(cord.x), Math.round(cord.y));
		placeHolder.setPosition(pos.x, pos.y);
		
		if(Input.isMouseButtonDown(KeyCode.Mouse0)) {
			if(selectedBuilding != null && !placedBuildings.contains(pos)) {
				System.err.println("Placing..");
				
				GameObject obj = new GameObject(selectedBuilding.getName(), new Vector3f(pos.x, pos.y, -2));
				SpriteRenderer sr = new SpriteRenderer();
				sr.spriteName = selectedBuilding.getName() + "0.png";
				sr.material = WorldManager.material;
				
				obj.addBehaviour(sr);
				obj.addBehaviour(Editor.getBehaviour(selectedBuilding.getName()));
				
				placedBuildings.add(pos);
			}
		}
	}
	
	public int getCraftAmount(Building building) {
		if(!building.requirements.isEmpty()) {
			int smallest = Integer.MAX_VALUE, cur;
			int[] amount = new int[building.requirements.get(0).size()];
			
			for(int i = 0; i < building.requirements.get(0).size(); i++) {
				cur = building.requirements.get(0).get(i).amount;
				
				if(cur == 0)
					cur = 1;
				
				amount[i] = Math.round(Player.inventory.getAmountOfAnItem(ItemType.valueOf(building.requirements.get(0).get(i).type.name())) / cur);
				
				if(amount[i] < smallest) {
					smallest = amount[i];
				}
			}
			
			return smallest;
		}
		
		return -1;
	}
}