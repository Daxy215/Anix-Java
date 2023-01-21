import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.Engine.Graphics.Mesh;
import com.Anix.Engine.Graphics.Vertex;
import com.Anix.GUI.Sprite;
import com.Anix.IO.Application;
import com.Anix.Main.Core;
import com.Anix.Math.FastNoise;
import com.Anix.Math.MathD;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

public class World extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public static class Data {
		public Chunk chunk;
		
		public List<Vertex> vertices;
		public List<Integer> indices;
		
		public Data(Chunk chunk) {
			this.chunk = chunk;
			this.vertices = new ArrayList<>();
			this.indices = new ArrayList<>();
		}
	}
	
	public static int chunkSizeX = 16, chunkSizeY = 16, chunkSizeZ = 16;
	public static int renderDistanceX = 6, renderDistanceY = 1;
	
	public static Vector3f normal = new Vector3f(0, 0, -1);
	
	public static Vector2f topLeftCoord 	 = new Vector2f(0, 0);
	public static Vector2f bottomLeftCoord  = new Vector2f(0, 1);
	public static Vector2f bottomRightCoord = new Vector2f(1, 1);
	public static Vector2f topRightCoord 	 = new Vector2f(1, 0);
	
	public static FastNoise fs = new FastNoise();
	
	public static List<Chunk> chunksToGenerate;
	public List<Data> chunksData;
	
	//public static List<Chunk> chunks;
	public static ConcurrentMap<Chunk, GameObject> chunks;
	
	@Override
	public void start() {
		chunksToGenerate = new ArrayList<>();
		chunksData = new ArrayList<>();
		
		//chunks = new ArrayList<>();
		chunks = new ConcurrentHashMap<>();
		
		Thread thread = new Thread() {
			public void run() {
				while(!Application.shouldClose()) {
					while(!chunksToGenerate.isEmpty()) {
						Chunk chunk = chunksToGenerate.get(0);
						Data data = new Data(chunk);
						
						chunk.generateVertices(data);
						
						chunksData.add(data);
						chunksToGenerate.remove(0);
					}
					
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		thread.setName("World Thread");
		thread.start();
	}
	
	@Override
	public void update() {
		//the current chunk the player is in
        int curChunkPosX = (int)Math.floor(Camera.main.gameObject.getPosition().x/16)*16;
        int curChunkPosY = (int)Math.floor(Camera.main.gameObject.getPosition().y/16)*16;
        int curChunkPosZ = (int)Math.floor(Camera.main.gameObject.getPosition().z/16)*16;
        
        for(int i = curChunkPosX - 16 * renderDistanceX; i <= curChunkPosX + 16 * renderDistanceX; i += 16) {
        	for(int j = curChunkPosZ - 16 * renderDistanceX; j <= curChunkPosZ + 16 * renderDistanceX; j += 16) {
        		for(int k = curChunkPosY - 16 * renderDistanceY; k <= curChunkPosY + 16 * renderDistanceY; k += 16) {
	        		if(k >= 16)
	        			continue;
	        		
	        		byte LOD = 0;
	        		double distance = MathD.distanceBetweenVector3(i, k, j, Camera.main.gameObject.getPosition());
	        		
	        		if(distance < 40) {
	        			LOD = 2;
	        		} else if(distance > 45 && distance < 55) {
	        			LOD = 1;
	        		} else {
	        			LOD = 0;
	        		}
	        		
        			Chunk chunk = new Chunk(LOD, i, k, j);
	        		
	        		if(!chunks.containsKey(chunk)) {
						chunksToGenerate.add(chunk);
						chunks.put(chunk, new GameObject(getName(), new Vector3f(i, k, j), new Vector3f(), new Vector3f(1)));
	        		} else { //Chunk already exists.
	        			for(Map.Entry<Chunk, GameObject> entry : chunks.entrySet()) {
	        				if(entry.getKey().equals(chunk)) {
	        					if(entry.getKey().LODLevel != LOD) {
	        						entry.getKey().destroy();
	        		        		entry.getValue().destroy(true);
	        		        		chunks.remove(entry.getKey());
	        						chunksToGenerate.add(chunk);
	        						chunks.put(chunk, new GameObject(getName(), new Vector3f(i, k, j), new Vector3f(), new Vector3f(1)));
	        					}
	        					
	        					break;
	        				}
	        			}
	        		}
        		}
        	}
        }
        
        for(Map.Entry<Chunk, GameObject> entry : chunks.entrySet()) {
        	if(MathD.distanceBetweenVector3(entry.getKey().x, entry.getKey().y, entry.getKey().z, Camera.main.gameObject.getPosition()) > renderDistanceX * 25) {
        		entry.getKey().destroy();
        		entry.getValue().destroy(true);
        		chunks.remove(entry.getKey());
        	}
        }
		
		while(!chunksData.isEmpty()) {
			Data data = chunksData.remove(0);
			
			GameObject obj = chunks.get(data.chunk);
			
			if(obj == null) {
				data.vertices.clear();
				data.indices.clear();
				chunks.remove(data.chunk);
				data.chunk = null;
				
				continue;
			}
			
			Vertex[] verts = new Vertex[data.vertices.size()];
			
			for(int i = 0; i < verts.length; i++)
				verts[i] = data.vertices.get(i);
			
			int[] ind = new int[data.indices.size()];
			
			for(int i = 0; i < ind.length; i++)
				ind[i] = data.indices.get(i);
			
			Mesh mesh = new Mesh(new Sprite(null, "Grass.png", null), verts, ind);
			mesh.create();
			//mesh.getMaterial().color = Color.RED;
			obj.setMesh(mesh);
			
			//chunks.replace(data.chunk, obj);
			System.err.println("FOUndL " + chunks.get(data.chunk));
			
			Core.getMasterRenderer().addEntity(obj);
			
			data.vertices.clear();
			data.indices.clear();
			data.chunk = null;
		}
	}
	
	public static float PerlinNoise3D(float x, float y, float z) {
		float AB = fs.GetNoise(x, y);
		float BC = fs.GetNoise(y, z);
		float AC = fs.GetNoise(x, z);
		float BA = fs.GetNoise(y, x);
		
		float CB = fs.GetNoise(z, y);
		float CA = fs.GetNoise(z, x);
		
		float ABC = (AB + (BC + (AC + (BA + (CB + CA)))));
		
		return (ABC / 6);
	}
}