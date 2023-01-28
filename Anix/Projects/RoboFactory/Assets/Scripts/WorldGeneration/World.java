import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
import com.Anix.Math.FastNoise;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

public class World extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public static class Block {
		public String name;
		
		public int indexX, indexY;
		
		public Vector2f[] uvs;
		
		public Block(String name, int indexX, int indexY) {
			this.name = name;
			this.indexX = indexX;
			this.indexY = indexY;
			
			float sizeX = World.instance.tileSizeX / World.instance.textureWidth;
			float sizeY = World.instance.tileSizeY / World.instance.textureHeight;
			
			float x = ((World.instance.tileSizeX * indexX) / World.instance.textureWidth);
			float y = ((World.instance.tileSizeY * indexY) / World.instance.textureHeight);
			
			uvs = new Vector2f[] {
					new Vector2f(x, y), //Top Left
					new Vector2f(x, y + sizeY), //Bottom Left
					new Vector2f(x + sizeX, y + sizeY), //Bottom Right
					new Vector2f(x + sizeX, y), //Top Right
			};
		}
	}
	
    public static final int renderDistance = 4;
	
	public static final int terrainWidth = 16, terrainHeight = 16;
	
	public static Vector3f normal = new Vector3f(0, 0, -1);
	
	public static Vector2f topLeftCoord 	 = new Vector2f(0, 0);
	public static Vector2f bottomLeftCoord  = new Vector2f(0, 1);
	public static Vector2f bottomRightCoord = new Vector2f(1, 1);
	public static Vector2f topRightCoord 	 = new Vector2f(1, 0);
	
	public float textureWidth = 224, textureHeight = 224, tileSizeX = 32, tileSizeY = 32;
	public Texture texture;
	
	public static FastNoise fs = new FastNoise(999);
	public static World instance;
	
	public List<Terrain> terrains = new ArrayList<Terrain>();
	
	public List<Block> blocks = null;
	
	@Override
	public void start() {
		World.instance = this;
		
		blocks = Arrays.asList(new Block("Grass", 0, 0), new Block("Ice", 1, 0), new Block("Jungle", 2, 0), new Block("Water", 3, 0),
				new Block("Sand", 0, 1), new Block("Savanna", 1, 1), new Block("Snow", 3, 1));
		
		texture = UI.loadTexture("tileMap.png");
		
		/*Thread thread = new Thread() {
			public void run() {
				while(!Application.shouldClose()) {
					if(Camera.main == null)
						continue;
					
					if(!Editor.isPlaying())
						return;
					
					
					
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		thread.setName("World Thread");
		thread.start();*/
	}
	
	@Override
	public void update() {
		int x = (int)(Math.round(Camera.main.gameObject.getPosition().x / terrainWidth) * terrainWidth);
        int y = (int)(Math.round(Camera.main.gameObject.getPosition().y / terrainHeight) * terrainHeight);
        
        for (int k = x - terrainWidth * renderDistance; k <= x + terrainWidth * renderDistance; k += terrainWidth) {
			for(int i = y - terrainHeight * renderDistance; i <= y + terrainHeight * renderDistance; i += terrainHeight) {
				Terrain terrain = new Terrain(new Vector3f(k, i, 0));
				
                if(terrains.contains(terrain))
                    continue;
                
                terrain.generate();
                
                terrains.add(terrain);
            }
        }
        
        /*for(int i = 0; i < terrains.size(); i++) {
        	Vector3f pos = Camera.main.gameObject.getPosition();
        	
        	if(Math.abs(terrains.get(i).position.x - pos.x) < terrainWidth * (renderDistance + 3) ||
        			Math.abs(terrains.get(i).position.y - pos.y) < terrainHeight * (renderDistance + 3)) {
        		terrains.get(i).destroy();
        		terrains.remove(i);
        		i--;
        		continue;
        	}
        }*/
	}
	
	public Block getBlock(String name) {
		for(int i = 0; i < blocks.size(); i++)
			if(blocks.get(i).name.equalsIgnoreCase(name.toLowerCase()))
				return blocks.get(i);
		
		return null;
	}
}