package Managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.Engine.Editor;
import com.Anix.GUI.GUI;
import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

import Buildings.BasicGenerator;
import Buildings.Building;
import Buildings.Furnace;
import Buildings.MainBuilding;
import Buildings.Miner;
import Buildings.Pole;
import Enums.ItemType;
import Player.Player;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

/**
 * Categories:
 * 	0 - Special
 * 	1 - Constructors
 *  2 - Electronics
 */
public class BuilderManager extends Behaviour {
	/*
	 * Please ignore this tyvm :)
	 */
	private static final long serialVersionUID = 1L;	
	
	public static class BuildingData {
		public int category;
		
		public Building building;
		public Texture texture;
		
		public BuildingData(int category, Building building) {
			this.category = category;
			this.building = building;
			
			this.texture = UI.loadTexture(building.getName() + "0.png");
		}
	}
	
	private int currentCategory;
	
	public boolean isBuilding, showMenu;
	public BuildingData selectedBuilding;
	
	private GameObject placeHolder;
	
	public static BuilderManager instance;
	
	public List<Vector2f> placedBuildings;
	//public List<BuildingData> buildings;
	public Map<String, BuildingData> buildings;
	
	@Override
	public void awake() {
		//buildings = new ArrayList<BuildingData>();
		buildings = new HashMap<>();
		placedBuildings = new ArrayList<>();
		
		requestUpdate();
		requestRender();
	}
	
	@Override
	public void start() {
		instance = this;
		
		placeHolder = new GameObject("PlaceHolder");
		placeHolder.setIsEnabled(false);
		placeHolder.addBehaviour(SpriteRenderer.class).spriteName = "hoveredArea.png";
		
		buildings.put("Speical", new BuildingData(0, new MainBuilding()));
		buildings.put("Furnace", new BuildingData(1, new Furnace()));
		buildings.put("Miner", new BuildingData(1, new Miner()));
		buildings.put("Electricity", new BuildingData(2, new Pole()));
		
		/*
		 *   Iron Plates: 5
  		 *   Copper Wires: 5
		 *   Gears: 2
		 */
		addBuilding("Electricity", 2, new BasicGenerator(), Arrays.asList(Arrays.asList(new Building.Requirement(5, ItemType.IronBar),
				new Building.Requirement(5, ItemType.CopperWire), new Building.Requirement(2, ItemType.Gear))));
	}
	
	@Override
	public void update() {
		if(Input.isKeyDown(KeyCode.T)) {
			isBuilding = !isBuilding;
			
			placeHolder.setIsEnabled(isBuilding);
		}
		
		if(Input.isKeyDown(KeyCode.Tab)) {
			showMenu = !showMenu;
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
				
				GameObject obj = new GameObject(selectedBuilding.building.getName(), new Vector3f(pos.x, pos.y, -2));
				SpriteRenderer sr = new SpriteRenderer();
				sr.spriteName = selectedBuilding.building.getName() + "0.png";
				sr.material = WorldManager.material;
				//Mesh mesh = new Mesh(new Sprite("", "", selectedBuilding.texture), WorldManager.material);
				//mesh.create();
				//obj.setMesh(mesh);
				
				obj.addBehaviour(sr);
				//obj.getMesh().setMaterial(WorldManager.material);
				obj.addBehaviour(Editor.getBehaviour(selectedBuilding.building.getName()));
				
				placedBuildings.add(pos);
			}
		}
	}
	
	@Override
	public void render() {
		if(showMenu) {
			float startX = Application.getStartX() + 64, startY = Application.getStartY() + 64;
			
			ImGui.setNextWindowPos(startX, startY);
			ImGui.setNextWindowSize(Application.getWidth() - (64 * 2), Application.getHeight() - (64 * 2));
			
			ImGui.begin("##Menu", GUI.defaultFlags | ImGuiWindowFlags.NoDecoration);
			
			//Top side
			{
				ImGuiStyle style = ImGui.getStyle();
				float width = 120 * 3 + (style.getItemSpacingX() * 2);
				alignForWidth(width, 0.5f);
				
				if(ImGui.button("Special", 120, 32)) { currentCategory = 0; }
				ImGui.sameLine();
				if(ImGui.button("Constructors", 120, 32)) { currentCategory = 1; }
				ImGui.sameLine();
				if(ImGui.button("Electronics", 120, 32)) { currentCategory = 2; }
			}
			
			ImGui.separator();
			
			//Down side
			{
				int width = 64;
				int amount = 150 / width;
				
				ImGui.columns(amount, "##Bro?", false);
				ImGui.setColumnOffset(1, 80);
				
				for(Entry<String, BuildingData> entry : buildings.entrySet()) {
					float y = ImGui.calcTextSize(entry.getKey()).y * 0.5f;
					separatorWithText(entry.getKey(), startX, startY + y + ImGui.getCursorPosY(),
							(ImGui.getWindowPosX()) + ImGui.getWindowSizeX(), startY + y + ImGui.getCursorPosY(), 0.5f, 0.5f);
					
					if(entry.getValue().category == currentCategory) {
						ImGui.pushID("Building of: " + entry.toString());
						
						if(ImGui.imageButton(entry.getValue().texture.getId(), 
								64, 64)) {
							selectedBuilding = entry.getValue();
						}
						
						ImGui.popID();
					}
					
					//ImGui.separator();
					ImGui.nextColumn();
				}
				
				/*for(int i = 0; i < buildings.size(); i++) {
					if(buildings.get(i).category == currentCategory) {
						ImGui.pushID("Building of: " + i + buildings.get(i));
						
						if(ImGui.imageButton(buildings.get(i).texture.getId(), 
								64, 64)) {
							selectedBuilding = buildings.get(i);
						}
						
						ImGui.popID();
						
						ImGui.nextColumn();
					}
				}*/
			}
			
			ImGui.end();
		}
	}
	
	private void separatorWithText(String text, float sx, float sy, float ex, float ey, float alignment, float textAlignment) {
		ImGuiStyle style = ImGui.getStyle();
		ImVec2 size = ImGui.calcTextSize(text);
		
		float valuex = (ImGui.getWindowPosX()) + (ImGui.getWindowSizeX() * alignment);
		
		float left = valuex - ex;
		
		ImVec4 c = style.getColor(ImGuiCol.Separator);
		
		ImGui.getWindowDrawList().addLine(sx, sy, valuex - (size.x * textAlignment) - 4, ey, ImGui.colorConvertFloat4ToU32(c.x, c.y, c.y, c.w), 2);
		
		ImGui.setCursorPosX(((Application.getWidth() - (64 * 2)) * alignment) - (size.x * textAlignment));
		ImGui.text(text);
		
		ImGui.getWindowDrawList().addLine(valuex + size.x - (size.x * textAlignment), sy, ex, ey, ImGui.colorConvertFloat4ToU32(c.x, c.y, c.y, c.w), 2);
	}
	
	private void alignForWidth(float width, float alignment) {
		ImGuiStyle style = ImGui.getStyle();
		float avail = ImGui.getContentRegionAvailX();
		float off = (avail - width) * alignment;
		
		if(off > 0.0f)
			ImGui.setCursorPosX(ImGui.getCursorPosX() + off);
	}
	
	private boolean centeredButton(String label, float sizeX, float sizeY, float alignment) {
		ImGuiStyle style = ImGui.getStyle();
		
		float size = ImGui.calcTextSize(label).x + style.getFramePaddingX() * 2.0f;
		float avail = ImGui.getContentRegionAvailX();
		
		float off = (avail - size) * alignment;
		if(off > 0.0f)
			ImGui.setCursorPosX(ImGui.getCursorPosX() + off);
		
		return ImGui.button(label, sizeX, sizeY);
	}
	
	public int getCraftAmount(Building building) {
		if(Player.inventory == null)
			return -1;
		
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
	
	private void addBuilding(String categoryType, int category, Building building, List<List<Building.Requirement>> requirements) {
		building.requirements = requirements;
		buildings.put(categoryType, new BuildingData(category, building));
	}
}