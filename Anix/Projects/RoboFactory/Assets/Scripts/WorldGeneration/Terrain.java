package WorldGeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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
	public static enum MaterialType {
		Tree, Metal, Coal, Copper, LimeStone, Stone
	}
	
	private Random random;
	public Vector3f position;
	public Mesh mesh;
	
	public MaterialType[][] materials;
	
	public static List<Vector2f> materialsPositions = new ArrayList<>();
    public Terrain() {
    	
    }
    
    public Terrain(Vector3f position) {
    	this.position = position;
    	
    	random = new Random((int)(World.SEED * position.x + position.y));
    	materials = new MaterialType[World.terrainWidth][World.terrainWidth];
    }
	
	public void generate() {
		List<Vertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        
        for(int x = 0; x < World.terrainWidth; x++) {
			for(int y = 0; y < World.terrainHeight; y++) {
                int xx = (int)(x + position.x);
                int yy = (int)(y + position.y);
                
                BiomeGenerator.BiomeType type = BiomeGenerator.getBiomeAt(xx, yy);
                World.Block block = null;
                
                if(type.equals(BiomeGenerator.BiomeType.FOREST)) {
                	block = World.instance.getBlock("grass");
				} else if(type.equals(BiomeGenerator.BiomeType.OCEAN) || type.equals(BiomeGenerator.BiomeType.BEACH)) {
					block = World.instance.getBlock("water");
					//continue;
				} else if(type.equals(BiomeGenerator.BiomeType.SAVANNAH)) {
					block = World.instance.getBlock("savanna");
				} else if(type.equals(BiomeGenerator.BiomeType.DESERT)) {
					block = World.instance.getBlock("sand");
				} else if(type.equals(BiomeGenerator.BiomeType.SNOW)) {
					block = World.instance.getBlock("snow");
				} else if(type.equals(BiomeGenerator.BiomeType.JUNGLE)) {
					block = World.instance.getBlock("jungle");
				} else {
					System.err.println("[ERROR] Couldn't find biometype of " + type.name());
				}
                
            	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, 0), World.normal, block.uvs[0]));
				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, 0), World.normal, block.uvs[1]));
				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, 0), World.normal, block.uvs[2]));
				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, 0), World.normal, block.uvs[3]));
                
				if(block.name.equalsIgnoreCase("water"))
					continue;
				
                //Ores
                float noiseValue = World.fs.GetNoise(xx, yy);
                //noiseValue = Math.pow((noiseValue + 1) / 20, 2);
                
                float treeProbability = (float) Math.pow((noiseValue + 1) / 16, 2);
                double metalProbability = Math.pow((noiseValue + 1) / 16, 2);
                double lithiumProbability = Math.pow((noiseValue + 1) / 8, 2);
                double stoneProbability = Math.pow((noiseValue + 1) / 12, 2);
                
                if(Math.random() < treeProbability) {
                	materials[x][y] = MaterialType.Tree;
                }
                
                if(Math.random() < metalProbability) {
                	generateMaterialBlob(MaterialType.Metal, x, y);
                }
                
                if(Math.random() < lithiumProbability) {
                	//generateMaterialBlob(MaterialType.Lithium, x, y);
                }
                
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
                
                int zz = -1;
                
                World.Block tree = World.instance.getBlock(materials[x][y].name());
            	
            	vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, zz), World.normal, tree.uvs[0]));
				vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, zz), World.normal, tree.uvs[1]));
				vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, zz), World.normal, tree.uvs[2]));
				vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, zz), World.normal, tree.uvs[3]));
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
		Vector3f center = generateBlob((int) MathD.getRandomNumberBetweenI(5, 25, random));
    	
    	if(center == null)
    		return;
    	
        int BLOB_SIZE = (int) center.z;
		
		for (int i = x - BLOB_SIZE / 2; i < x + BLOB_SIZE / 2; i++) {
			for (int j = y - BLOB_SIZE / 2; j < y + BLOB_SIZE / 2; j++) {
				if (i < 0 || j < 0 || i >= World.terrainWidth || j >= World.terrainHeight) continue;
				
				if(Math.random() < 0.7f && MathD.distanceBetweenVector2(new Vector2f(i, j), new Vector2f(x, y)) < BLOB_SIZE / 2)
					materials[i][j] = type;
			}
		}
	}
	
	private Vector3f generateBlob(int BLOB_SIZE) {
		int x = MathD.getRandomNumberBetweenI(0, World.terrainWidth, random);
		int y = MathD.getRandomNumberBetweenI(0, World.terrainHeight, random);
		
		// Check if the generated position is within bounds
		if (x < BLOB_SIZE / 2 || x >= World.terrainWidth - (BLOB_SIZE / 2) ||
				y < BLOB_SIZE / 2 || y >= World.terrainHeight - (BLOB_SIZE / 2)) {
			// The generated position is out of bounds, so try again
			return generateBlob(BLOB_SIZE - 1);
		}
		
		// Check if the location is too close to any existing blobs
		for (Vector2f existingBlob : materialsPositions) {
			double distance = MathD.getDistance(existingBlob.x, x, existingBlob.y, y);
			if (distance < BLOB_SIZE * 5) { //Blob Separation
				// This location is too close to an existing blob, so try again
				return null;
				//return generateBlob(BLOB_SIZE - 1);
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