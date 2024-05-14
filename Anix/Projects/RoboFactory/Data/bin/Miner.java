package Buildings;

import com.Anix.Behaviours.BoxCollider2D;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.IO.Time;
import com.Anix.Main.Core;
import com.Anix.Math.MathD;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

import Managers.BuilderManager;
import Managers.BuilderManager.ElectricityProduceData;
import Managers.BuilderManager.PlacementData;
import WorldGeneration.Terrain;
import WorldGeneration.Terrain.MaterialType;
import WorldGeneration.World;

public class Miner extends SourceAble {
	/*
	* Please ignore this tyvm :)
	*/
	
	private static final long serialVersionUID = 1L;
	
	public float time, timer;
	
	public int oreX, oreY;
	public Terrain terrain;
	
	public Miner() {
		
	}
	
	public Miner(float time, int maxElectricity) {
		this.time = time;
		this.maxElectricity = maxElectricity;
	}
	
	@Override
	public void start() {
		terrain = World.instance.getTerrainAt(gameObject.getPosition().getXY());
		
		int x = (int)gameObject.getPosition().x;
		int y = (int)gameObject.getPosition().y;

		//int bx = (Math.round(x)) % World.terrainWidth;
		//int by = (Math.round(y)) % World.terrainHeight;
		
		//System.err.println("terrain at: " + x + " " + y + " bxby: " + bx + " " + by + " - " + terrain.materials[bx][by]);
		
		int xx = 0, yy = 0;
		float closest = Float.MAX_VALUE;
		
		for(int i = 0; i < 64; i++) {
			for(int j = 0; j < 64; j++) {
				if(terrain.materials[i][j] != null && terrain.materials[i][j] != MaterialType.Tree) {
					float dis = (float)MathD.distanceBetweenVector2(new Vector2f(i, j), new Vector2f((x % 64), (y % 64)));
					
					if(dis < closest) {
						closest = dis;
						xx = i;
						yy = j;
					}
				}
			}
		}
		
		oreX = xx;
		oreY = yy;
		
		requestUpdate();
	}
	
	@Override
	public void update() {
		super.update();
		
		timer += Time.deltaTime;
		
		if(timer > time) {
			timer = 0;
			
			if(currentElectricity > 0) {
				currentElectricity--;
				
				MaterialType type = terrain.materials[oreX][oreY];
				
				GameObject obj = new GameObject(type.name(), gameObject.getPosition().copy().add(0, -1, -1), new Vector3f(), new Vector3f(0.5f));
				obj.setTag("ore");
				SpriteRenderer sr = new SpriteRenderer();
				sr.spriteName = type.name() + ".png";
				
				obj.addBehaviour(sr);
				obj.addBehaviour(new BoxCollider2D(true));
				
				Core.getMasterRenderer().addEntity(obj);
			}
		}
	}
	
	@Override
	public void startPlacing(PlacementData placementData) {
		Miner m = (Miner) placeBuilding(placementData.startPos, BuilderManager.get(this));
		placementData.cancel();
		
		BuilderManager.electricityProcedurable.add(new ElectricityProduceData(1, false, m));
	}
}