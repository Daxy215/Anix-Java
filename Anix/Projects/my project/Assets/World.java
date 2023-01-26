import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.Engine.Editor;
import com.Anix.Engine.Graphics.Mesh;
import com.Anix.Engine.Graphics.Vertex;
import com.Anix.GUI.Sprite;
import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
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
		
		public Vertex[] verts;
		public int[] indc;
		
		public Data(Chunk chunk) {
			this.chunk = chunk;
			this.vertices = new ArrayList<>();
			this.indices = new ArrayList<>();
		}
	}
	
	public static int chunkSizeX = 16, chunkSizeY = 16, chunkSizeZ = 16;
	public static int renderDistanceX = 16, renderDistanceY = 4;
	
	public static Texture texture;
	
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
		
		texture = UI.loadTexture("Grass.png");
		
		Thread thread = new Thread() {
			public void run() {
				while(!Application.shouldClose()) {
					if(Camera.main == null)
						continue;
					
					if(!Editor.isPlaying())
						return;
					
					while(!chunksToGenerate.isEmpty()) {
						Chunk chunk = chunksToGenerate.get(0);
						Data data = new Data(chunk);
						
						chunk.generateVertices(data);
						
						data.verts = new Vertex[data.vertices.size()];
						
						for(int i = 0; i < data.verts.length; i++)
							data.verts[i] = data.vertices.get(i);
						
						data.indc = new int[data.indices.size()];
						
						for(int i = 0; i < data.indc.length; i++)
							data.indc[i] = data.indices.get(i);
						
						data.vertices.clear();
						data.indices.clear();
						
						chunksData.add(data);
						chunksToGenerate.remove(0);
					}
					
					//the current chunk the player is in
			        int curChunkPosX = (int)Math.floor(Camera.main.gameObject.getPosition().x/16)*16;
			        int curChunkPosY = (int)Math.floor(Camera.main.gameObject.getPosition().y/16)*16;
			        int curChunkPosZ = (int)Math.floor(Camera.main.gameObject.getPosition().z/16)*16;
			        
			        for(int i = curChunkPosX - 16 * renderDistanceX; i <= curChunkPosX + 16 * renderDistanceX; i += 16) {
			        	for(int j = curChunkPosZ - 16 * renderDistanceX; j <= curChunkPosZ + 16 * renderDistanceX; j += 16) {
			        		for(int k = curChunkPosY - 16 * renderDistanceY; k <= curChunkPosY + 16 * renderDistanceY; k += 16) {
				        		if(k >= 32)
				        			continue;
				        		
				        		byte LOD = 0;
				        		double distance = MathD.distanceBetweenVector3(i, k, j, Camera.main.gameObject.getPosition());
				        		
				        		if(distance < 10000) {
				        			LOD = 2;
				        		} else if(distance > 150 && distance < 200) {
				        			LOD = 1;
				        		} else {
				        			LOD = 0;
				        		}
				        		
			        			Chunk chunk = new Chunk(LOD, i, k, j);
				        		
				        		if(!chunks.containsKey(chunk)) {
									chunksToGenerate.add(chunk);
									chunks.put(chunk, new GameObject(getName(), new Vector3f(i, k, j)));
				        		} else { //Chunk already exists.
				        			/*for(Map.Entry<Chunk, GameObject> entry : chunks.entrySet()) {
				        				if(entry.getKey().equals(chunk)) {
				        					if(entry.getKey().LODLevel != LOD) {
				        						entry.getKey().destroy();
				        		        		entry.getValue().destroy(true);
				        		        		
				        		        		chunks.remove(entry.getKey());
				        		        		
				        						chunksToGenerate.add(chunk);
				        						chunks.put(chunk, new GameObject(getName(), new Vector3f(i, k, j)));
				        					}
				        					
				        					break;
				        				}
				        			}*/
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
		while(!chunksData.isEmpty()) {
			Data data = chunksData.remove(0);
			
			if(data == null) {
				continue;
			}
			
			if(data.chunk == null) {
				data.verts = null;
				data.indc = null;
				data.chunk = null;
				
				continue;
			}
			
			GameObject obj = chunks.get(data.chunk);
			
			if(obj == null) {
				data.verts = null;
				data.indc = null;
				chunks.remove(data.chunk);
				data.chunk = null;
				
				continue;
			}
			
			Mesh mesh = new Mesh(new Sprite("", "", null), data.verts, data.indc);
			mesh.getSprite().setTexture(texture);
			mesh.create();
			
			obj.setMesh(mesh);
			
			chunks.replace(data.chunk, obj);
			Core.getMasterRenderer().addEntity(obj);
			
			data.verts = null;
			data.indc = null;
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