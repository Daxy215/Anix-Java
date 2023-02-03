import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.Engine.Graphics.Mesh;
import com.Anix.Engine.Graphics.Vertex;
import com.Anix.GUI.Sprite;
import com.Anix.Main.Core;
import com.Anix.Math.MathD;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

public class Terrain {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public Vector3f position;
	public Mesh mesh;
	
    //public List<GameObject> gameObjects = new ArrayList<GameObject>();
	
    public Terrain() {
    	
    }
    
    public Terrain(Vector3f position) {
    	this.position = position;
    }
	
	public void generate() {
		float coalBuff = 1, stoneBuff = 1, lithiumBuff = 1, ironBuff = 1;
        int treeChanceMin = 0, treeChanceMax = 0;
        
        List<Vertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        
        for(int x = 0; x < World.terrainWidth; x++) {
			for(int y = 0; y < World.terrainHeight; y++) {
                int xx = (int)(x + position.x);
                int yy = (int)(y + position.y);
                int zz = 1;
                
                BiomeGenerator.BiomeType type = BiomeGenerator.getBiomeAt(xx, yy);
                
                if(type.equals(BiomeGenerator.BiomeType.FOREST)) {
					/*GameObject grass = new GameObject(type.name());//5
					grass.setPosition(new Vector3f(xx, yy, zz));
					//grass.transform.SetParent(transform);
					
					SpriteRenderer sr = new SpriteRenderer();
					sr.spriteName = "grass.png";
					
					grass.addBehaviour(sr);
					grass.isStatic = true;
					
					gameObjects.add(grass);*/
                	
                	World.Block grass = World.instance.getBlock("grass");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, 0), World.normal, grass.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, 0), World.normal, grass.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, 0), World.normal, grass.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, 0), World.normal, grass.uvs[3]));
					
					treeChanceMin = 50;
					treeChanceMax = 70;
					
					coalBuff = 0.5f;
					lithiumBuff = 2;
				} else if(type.equals(BiomeGenerator.BiomeType.OCEAN) || type.equals(BiomeGenerator.BiomeType.BEACH)) {
					/*GameObject water = new GameObject(type.name());//9
					water.setPosition(new Vector3f(xx, yy, zz));
					//water.transform.SetParent(transform);
					
					SpriteRenderer sr = new SpriteRenderer();
					sr.spriteName = "water.png";
					
					water.addBehaviour(sr);
					water.isStatic = true;
					
					//SpriteRenderer sr = water.AddComponent<SpriteRenderer>();
					//sr.sprite = Resources.Load<Sprite>("textures/water");
					gameObjects.add(water);*/
					
					World.Block water = World.instance.getBlock("water");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, 0), World.normal, water.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, 0), World.normal, water.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, 0), World.normal, water.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, 0), World.normal, water.uvs[3]));
					
					continue;
				} else if(type.equals(BiomeGenerator.BiomeType.SAVANNAH)) {
					/*GameObject savanna = new GameObject(type.name());//9
					savanna.setPosition(new Vector3f(xx, yy, zz));
					//savanna.transform.SetParent(transform);
					
					SpriteRenderer sr = new SpriteRenderer();
					sr.spriteName = "savanna.png";
					
					savanna.addBehaviour(sr);
					savanna.isStatic = true;
					
					//SpriteRenderer sr = savanna.AddComponent<SpriteRenderer>();
					//sr.sprite = Resources.Load<Sprite>("textures/savanna");
					
					gameObjects.add(savanna);*/
					
					World.Block savanna = World.instance.getBlock("savanna");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, 0), World.normal, savanna.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, 0), World.normal, savanna.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, 0), World.normal, savanna.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, 0), World.normal, savanna.uvs[3]));
					
					//treeChanceMin = 10;
					//treeChanceMax = 20;
					
					treeChanceMin = 20;
					treeChanceMax = 30;
					
					ironBuff = 2;
				} else if(type.equals(BiomeGenerator.BiomeType.DESERT)) {
					/*GameObject sand = new GameObject(type.name());//9
					sand.setPosition(new Vector3f(xx, yy, zz));
					//sand.transform.SetParent(transform);
					
					SpriteRenderer sr = new SpriteRenderer();
					sr.spriteName = "sand.png";
					
					sand.addBehaviour(sr);
					sand.isStatic = true;
					
					//SpriteRenderer sr = sand.AddComponent<SpriteRenderer>();
					//sr.sprite = Resources.Load<Sprite>("textures/sand");
					
					gameObjects.add(sand);*/
					
					World.Block sand = World.instance.getBlock("sand");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, 0), World.normal, sand.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, 0), World.normal, sand.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, 0), World.normal, sand.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, 0), World.normal, sand.uvs[3]));
					
					//treeChanceMin = 10;
					//treeChanceMax = 20;
					
					treeChanceMin = 0;
					treeChanceMax = 0;
					
					coalBuff = 0.5f;
					stoneBuff = 2;
				} else if(type.equals(BiomeGenerator.BiomeType.SNOW)) {
					/*GameObject snow = new GameObject(type.name());//9
					snow.setPosition(new Vector3f(xx, yy, zz));
					//snow.transform.SetParent(transform);
					
					SpriteRenderer sr = new SpriteRenderer();
					sr.spriteName = "snow.png";
					
					snow.addBehaviour(sr);
					snow.isStatic = true;
					
					//SpriteRenderer sr = snow.AddComponent<SpriteRenderer>();
					//sr.sprite = Resources.Load<Sprite>("textures/snow");
					
					gameObjects.add(snow);*/
					
					World.Block snow = World.instance.getBlock("snow");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, 0), World.normal, snow.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, 0), World.normal, snow.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, 0), World.normal, snow.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, 0), World.normal, snow.uvs[3]));
					
					treeChanceMin = 10;
					treeChanceMax = 20;
				} else if(type.equals(BiomeGenerator.BiomeType.JUNGLE)) {
					/*GameObject jungle = new GameObject(type.name());//9
					jungle.setPosition(new Vector3f(xx, yy, zz));
					//jungle.transform.SetParent(transform);
					
					SpriteRenderer sr = new SpriteRenderer();
					sr.spriteName = "jungle.png";
					
					jungle.addBehaviour(sr);
					jungle.isStatic = true;
					
					//SpriteRenderer sr = jungle.AddComponent<SpriteRenderer>();
					//sr.sprite = Resources.Load<Sprite>("textures/Jungle");
					
					gameObjects.add(jungle);*/
					
					World.Block jungle = World.instance.getBlock("jungle");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, 0), World.normal, jungle.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, 0), World.normal, jungle.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, 0), World.normal, jungle.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, 0), World.normal, jungle.uvs[3]));
					
					treeChanceMin = 10;
					treeChanceMax = 20;
				} else {
					System.err.println("[ERROR] Couldn't find biometype of " + type.name());
				}
                
                //Ores
                float r = MathD.getRandomNumberBetweenF(0, 100, World.SEED * (xx * yy + 1) * 2500);
                
                if(r > treeChanceMin && r < treeChanceMax/* && !getTreeAt(xx, yy)*/) {
                	GameObject tree = new GameObject("tree");//9
					tree.setPosition(new Vector3f(xx, yy, zz - 0.01f));
					//jungle.transform.SetParent(transform);
					
					SpriteRenderer sr = new SpriteRenderer();
					sr.spriteName = "tree.png";
					
					tree.addBehaviour(sr);
					tree.isStatic = true;
					
                	continue;
                }
                
                if(r <= 0.4f * ironBuff) {
                	GameObject metal = new GameObject("metal");//9
                	metal.setPosition(new Vector3f(xx, yy, zz - 0.01f));
					//jungle.transform.SetParent(transform);
					
					SpriteRenderer sr = new SpriteRenderer();
					sr.spriteName = "metalOre.png";
					
					metal.addBehaviour(sr);
					metal.isStatic = true;
                	
                	//Metal metal = new Metal(new Vector3f(xx, yy, 0.01f), new Vector3f(), new Vector3f(1, 1, 1));
                	//metal.init(World.meshes[4]);
                	
                	continue;
                }
                
                if(r <= 0.6f * lithiumBuff) {
                	GameObject lithium = new GameObject("lithium");//9
                	lithium.setPosition(new Vector3f(xx, yy, zz - 0.01f));
					//jungle.transform.SetParent(transform);
					
					SpriteRenderer sr = new SpriteRenderer();
					sr.spriteName = "lithiumOre.png";
					
					lithium.addBehaviour(sr);
					lithium.isStatic = true;
                	
                	//Lithium lithium = new Lithium(new Vector3f(xx, yy, 0.01f), new Vector3f(), new Vector3f(1, 1, 1));
                	//lithium.init(World.meshes[3]);
                	
                	continue;
                }
                
                if(r <= 1f * stoneBuff) {
                	GameObject stone = new GameObject("stone");//9
                	stone.setPosition(new Vector3f(xx, yy, zz - 0.01f));
					//jungle.transform.SetParent(transform);
					
					SpriteRenderer sr = new SpriteRenderer();
					sr.spriteName = "stoneOre.png";
					
					stone.addBehaviour(sr);
					stone.isStatic = true;
                	
                	//Stone stone = new Stone(new Vector3f(xx, yy, 0.01f), new Vector3f(), new Vector3f(1, 1, 1));
                	//stone.init(World.meshes[2]);
                	
                	continue;
                }
                
                if(r <= 1.5f * coalBuff) {
                	GameObject coal = new GameObject("coal");//9
                	coal.setPosition(new Vector3f(xx, yy, zz - 0.5f));
					//jungle.transform.SetParent(transform);
					
					SpriteRenderer sr = new SpriteRenderer();
					sr.spriteName = "stoneOre.png";
					
					coal.addBehaviour(sr);
					coal.isStatic = true;
                	
                	//Coal coal = new Coal(new Vector3f(xx, yy, 0.01f), new Vector3f(), new Vector3f(1, 1, 1));
                	//coal.init(World.meshes[1]);
                	
                	continue;
                }
            }
			
			int numFaces = (Math.abs(vertices.size() / 4));
			
			int tl = vertices.size() - 4 * numFaces;
			for(int i = 0; i < numFaces; i++) {
				indices.add(tl + i * 4);
				indices.add(tl + i * 4 + 1);
				indices.add(tl + i * 4 + 2);
				indices.add(tl + i * 4);
				indices.add(tl + i * 4 + 2);
				indices.add(tl + i * 4 + 3);
			}
        }
        
        mesh = new Mesh(new Sprite("", "", World.instance.texture), WorldManager.material);
		mesh.set(vertices, indices);
		mesh.create();
		
		GameObject obj = new GameObject("Terrain_" + position.x + "-" + position.y, position);
		obj.setMesh(mesh);
		Core.getMasterRenderer().addEntity(obj);
	}
	
	public void destroy() {
		//for(int i = 0; i < gameObjects.size(); i++)
		//	gameObjects.get(i).destroy(true);
		
		//gameObjects.clear();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(position);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Terrain other = (Terrain) obj;
		return Objects.equals(position, other.position);
	}
}