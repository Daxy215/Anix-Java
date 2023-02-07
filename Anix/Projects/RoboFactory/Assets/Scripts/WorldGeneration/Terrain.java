package WorldGeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.Anix.Engine.Graphics.Mesh;
import com.Anix.Engine.Graphics.Vertex;
import com.Anix.GUI.Sprite;
import com.Anix.Main.Core;
import com.Anix.Math.MathD;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

import Managers.WorldManager;

public class Terrain {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public static enum MaterialType {
		Tree, Metal, Lithium, Stone
	}
	
	public Vector3f position;
	public Mesh mesh;
	
	public MaterialType[][] materials;
	
	public static List<Vector2f> materialsPositions = new ArrayList<>();
	
    //public List<GameObject> gameObjects = new ArrayList<GameObject>();
	
    public Terrain() {
    	
    }
    
    public Terrain(Vector3f position) {
    	this.position = position;
    	
    	materials = new MaterialType[World.terrainWidth][World.terrainWidth];
    }
	
	public void generate() {
		float stoneBuff = 1, lithiumBuff = 1, ironBuff = 1;
        int treeChanceMin = 0, treeChanceMax = 0;
        
        List<Vertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        
        for(int x = 0; x < World.terrainWidth; x++) {
			for(int y = 0; y < World.terrainHeight; y++) {
                int xx = (int)(x + position.x);
                int yy = (int)(y + position.y);
                
                BiomeGenerator.BiomeType type = BiomeGenerator.getBiomeAt(xx, yy);
                
                if(type.equals(BiomeGenerator.BiomeType.FOREST)) {
                	World.Block grass = World.instance.getBlock("grass");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, 0), World.normal, grass.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, 0), World.normal, grass.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, 0), World.normal, grass.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, 0), World.normal, grass.uvs[3]));
					
					treeChanceMin = 50;
					treeChanceMax = 70;
					
					lithiumBuff = 2;
				} else if(type.equals(BiomeGenerator.BiomeType.OCEAN) || type.equals(BiomeGenerator.BiomeType.BEACH)) {
					World.Block water = World.instance.getBlock("water");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, 0), World.normal, water.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, 0), World.normal, water.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, 0), World.normal, water.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, 0), World.normal, water.uvs[3]));
					
					continue;
				} else if(type.equals(BiomeGenerator.BiomeType.SAVANNAH)) {
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
					World.Block sand = World.instance.getBlock("sand");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, 0), World.normal, sand.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, 0), World.normal, sand.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, 0), World.normal, sand.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, 0), World.normal, sand.uvs[3]));
					
					//treeChanceMin = 10;
					//treeChanceMax = 20;
					
					treeChanceMin = 0;
					treeChanceMax = 0;
					
					stoneBuff = 2;
				} else if(type.equals(BiomeGenerator.BiomeType.SNOW)) {
					World.Block snow = World.instance.getBlock("snow");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, 0), World.normal, snow.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, 0), World.normal, snow.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, 0), World.normal, snow.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, 0), World.normal, snow.uvs[3]));
					
					treeChanceMin = 10;
					treeChanceMax = 20;
				} else if(type.equals(BiomeGenerator.BiomeType.JUNGLE)) {
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
                float noiseValue = World.fs.GetNoise(xx, yy);
                float treeProbability = (float) Math.pow((noiseValue + 1) / 16, 2);
                double metalProbability = Math.pow((noiseValue + 1) / 16, 2);
                double lithiumProbability = Math.pow((noiseValue + 1) / 8, 2);
                double stoneProbability = Math.pow((noiseValue + 1) / 12, 2);
                double coalProbability = Math.pow((noiseValue + 1) / 20, 2);
                
                if(Math.random() < treeProbability) {
                	materials[x][y] = MaterialType.Tree;
                	//continue;
                }
                
                //float r = MathD.getRandomNumberBetweenF(0, 100, World.SEED + (x * 1000 + y));
                
                //if(r > treeChanceMin && r < treeChanceMax) {
                	//materials[x][y] = MaterialType.Tree;
                	//continue;
                //}
                
                //if(r > 0.01f * ironBuff) {
                if(Math.random() < metalProbability) {
                	generateMaterialBlob(MaterialType.Metal, x, y);
                }
                
                //if(r > 0.02f * lithiumBuff) {
                if(Math.random() < lithiumProbability) {
                	generateMaterialBlob(MaterialType.Lithium, x, y);
                }
                
                //if(r > 0.04f * stoneBuff) {
                if(Math.random() < stoneProbability) {
                	generateMaterialBlob(MaterialType.Stone, x, y);
                }
            }
        }
        
        if(vertices.isEmpty())
        	return;
        
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
        
        mesh = new Mesh(new Sprite("", "", World.instance.texture), WorldManager.material);
		mesh.set(vertices, indices);
		mesh.create();
		
		GameObject obj = new GameObject("Terrain_" + position.x + "-" + position.y, position);
		obj.setMesh(mesh);
		Core.getMasterRenderer().addEntity(obj);
	}
	
	public void generateMaterials() {
		List<Vertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        
        for(int x = 0; x < World.terrainWidth; x++) {
			for(int y = 0; y < World.terrainHeight; y++) {
                if(materials[x][y] == null)
                	continue;
                
                int xx = (int)(x + position.x);
                int yy = (int)(y + position.y);
                int zz = -1;
                
                switch(materials[x][y]) {
                case Tree:
                	World.Block tree = World.instance.getBlock("tree");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, zz), World.normal, tree.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, zz), World.normal, tree.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, zz), World.normal, tree.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, zz), World.normal, tree.uvs[3]));
                	
                	break;
                case Metal:
                	World.Block metal = World.instance.getBlock("metal");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, zz), World.normal, metal.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, zz), World.normal, metal.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, zz), World.normal, metal.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, zz), World.normal, metal.uvs[3]));
                	
                	break;
                case Lithium:
                	World.Block lithium = World.instance.getBlock("lithium");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, zz), World.normal, lithium.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, zz), World.normal, lithium.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, zz), World.normal, lithium.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, zz), World.normal, lithium.uvs[3]));
                	
                	break;
                case Stone:
                	World.Block stone = World.instance.getBlock("stone");
                	
                	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, zz), World.normal, stone.uvs[0]));
    				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, zz), World.normal, stone.uvs[1]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, zz), World.normal, stone.uvs[2]));
    				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, zz), World.normal, stone.uvs[3]));
                	
                	break;
				default:
					System.err.println("[IG-ERROR] [TERRAIN] Couldn't find a material with the name of: " + materials[x][y]);
					
					break;
                }
			}
        }
        
        if(vertices.isEmpty())
        	return;
        
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
        
        mesh = new Mesh(new Sprite("", "", World.instance.texture), WorldManager.material);
		mesh.set(vertices, indices);
		mesh.create();
		
		GameObject obj = new GameObject("Terrain_Meterials_" + position.x + "-" + position.y, position);
		obj.setMesh(mesh);
		Core.getMasterRenderer().addEntity(obj);
	}
	
	private void generateMaterialBlob(MaterialType type, int x, int y) {
		Vector3f center = generateBlob(type);
    	
    	if(center == null)
    		return;
    	
        int mx = (int) center.x;
        int my = (int) center.y;
        
        int BLOB_SIZE = (int) center.z;
		
		for (int i = x - BLOB_SIZE / 2; i < x + BLOB_SIZE / 2; i++) {
			for (int j = y - BLOB_SIZE / 2; j < y + BLOB_SIZE / 2; j++) {
				if (i < 0 || j < 0 || i >= World.terrainWidth || j >= World.terrainHeight) continue;
				
				if(Math.random() < 0.7f)
					materials[i][j] = type;
			}
		}
	}
	
	private Vector3f generateBlob(MaterialType type) {
		int BLOB_SIZE = (int) (Math.random() * 25) + 10;
		
		int x = MathD.getRandomNumberBetweenI(0, World.terrainWidth);
		int y = MathD.getRandomNumberBetweenI(0, World.terrainHeight);
		
		// Check if the generated position is within bounds
		if (x < BLOB_SIZE / 2 || x >= World.terrainWidth - (BLOB_SIZE / 2) ||
				y < BLOB_SIZE / 2 || y >= World.terrainHeight - (BLOB_SIZE / 2)) {
			// The generated position is out of bounds, so try again
			return generateBlob(BLOB_SIZE - 1);
		}
		
		// Check if the location is too close to any existing blobs
		for (Vector2f existingBlob : materialsPositions) {
			int dx = (int) (existingBlob.x - x);
			int dy = (int) (existingBlob.y - y);
			double distance = Math.sqrt(dx * dx + dy * dy);
			if (distance < (Math.random() * 25) + 5) { //Blob Separation
				// This location is too close to an existing blob, so try again
				return generateBlob(BLOB_SIZE - 1);
			}
		}
		
		// This location is far enough away from existing blobs, so return it
		materialsPositions.add(new Vector2f(x, y));
		return new Vector3f(x, y, BLOB_SIZE);
	}
	
	private Vector3f generateBlob(int BLOB_SIZE) {
		int x = MathD.getRandomNumberBetweenI(0, World.terrainWidth);
		int y = MathD.getRandomNumberBetweenI(0, World.terrainHeight);
		
		// Check if the generated position is within bounds
		if (x < BLOB_SIZE / 2 || x >= World.terrainWidth - BLOB_SIZE / 2 ||
				y < BLOB_SIZE / 2 || y >= World.terrainHeight - BLOB_SIZE / 2) {
			// The generated position is out of bounds, so try again
			return generateBlob(BLOB_SIZE - 1);
		}
		
		// Check if the location is too close to any existing blobs
		for (Vector2f existingBlob : materialsPositions) {
			int dx = (int) (existingBlob.x - x);
			int dy = (int) (existingBlob.y - y);
			double distance = Math.sqrt(dx * dx + dy * dy);
			if (distance < (Math.random() * 25) + 5) { //Blob Separation
				// This location is too close to an existing blob, so try again
				return null;
			}
		}
		
		// This location is far enough away from existing blobs, so return it
		materialsPositions.add(new Vector2f(x, y));
		return new Vector3f(x, y, BLOB_SIZE);
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