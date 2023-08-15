package PlayerP;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.GUI.UI;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Math.Color;
import com.Anix.Math.MathD;
import com.Anix.Math.Vector2f;

import Enums.ItemType;
import Managers.WorldManager;
import PlayerP.InventoryManager.Inventory;
import WorldGeneration.BiomeGenerator;
import WorldGeneration.Terrain;
import WorldGeneration.World;
import WorldGeneration.Terrain.MaterialType;

public class Player extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public static Inventory inventory;
	
	public Player() {
		
	}
	
	@Override
	public void awake() {
		inventory = (Inventory)gameObject.getBehaviour(Inventory.class);
	}
	
	@Override
	public void start() {
		//gameObject.getMesh().setMaterial(WorldManager.material);
		
		requestUpdate();
	}
	
	@Override
	public void update() {
		if(inventory == null)
			inventory = (Inventory)gameObject.getBehaviour(Inventory.class);
		
		if(Input.isKeyDown(KeyCode.I)) {
			inventory.showInventory = !inventory.showInventory;
		}
		
		if(Input.isKey(KeyCode.C))
			inventory.addItem(ItemType.Log, 1);
		
		int x = (int) gameObject.getPosition().x;
		int y = (int) gameObject.getPosition().y;
		int tx = (int)(Math.round(x / World.terrainWidth) * World.terrainWidth);
        int ty = (int)(Math.round(y / World.terrainHeight) * World.terrainHeight);
        
		Terrain terrain = World.instance.getTerrainAt(gameObject.getPosition().getXY());
		
		if(terrain != null) {
			int bx = (Math.round(x)) % World.terrainWidth;
			int by = (Math.round(y)) % World.terrainHeight;
			
			Vector2f pos = Camera.main.convertWorldToScreenSpace(bx + tx, by + ty, 0).getXY();
			Vector2f tPos = Camera.main.convertWorldToScreenSpace(tx, ty, 0).getXY();
			
			if(Input.isKey(KeyCode.C)) {
				UI.drawBox(pos.x, pos.y, 0.5f, 16, 16, Color.black);
				UI.drawBox(tPos.x, tPos.y, 0.5f, 16*16, 16*16, Color.red);
				
				int xx = 0, yy = 0;
				float closest = Float.MAX_VALUE;
				
				for(int i = 0; i < 64; i++) {
					for(int j = 0; j < 64; j++) {
						if(terrain.materials[i][j] != null && terrain.materials[i][j] != MaterialType.Tree) {
							float dis = (float)MathD.distanceBetweenVector2(new Vector2f(i, j), new Vector2f((bx % 64), (by % 64)));
							
							if(dis < closest) {
								closest = dis;
								xx = i;
								yy = j;
							}
						}
					}
				}
				
				System.err.println("terrain at: " + tx + " " + ty + " bxby: " + bx + " " + by + " - " + terrain.materials[Math.abs(bx)][Math.abs(by)]);
				System.err.println("nearest: " + xx + " - " + yy);
			}
			
			//System.err.println("found terain at: " + tx + " " + ty + " block: " + BiomeGenerator.getBiomeAt(x, y));
		}
	}
}