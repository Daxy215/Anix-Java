import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Engine.Graphics.Mesh;
import com.Anix.Engine.Graphics.Vertex;
import com.Anix.GUI.Sprite;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Main.Core;
import com.Anix.Math.FastNoise;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

public class World extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public static int chunkSizeX = 16, chunkSizeY = 16, chunkSizeZ = 16;
		
	public static FastNoise fs = new FastNoise();
	
	private Chunk chunkToGenerate;
	private List<Chunk> chunksQueue;
	
	public static List<Vertex> vertices;
	public static List<Integer> indices;
	
	@Override
	public void start() {
		vertices = new ArrayList<Vertex>();
		indices = new ArrayList<Integer>();
		
		chunksQueue = new ArrayList<Chunk>();
		
		Thread thread = new Thread() {
			public void run() {
				for(int x = 0; x < 5; x++) {
					for(int y = 0; y < 1; y++) {
						for(int z = 0; z < 5; z++) {
							chunksQueue.add(new Chunk(x * chunkSizeX, 0, z * chunkSizeZ));
						}
					}
				}
				
				while(!Application.shouldClose()) {
					if(chunkToGenerate == null && !chunksQueue.isEmpty() && vertices.isEmpty() && indices.isEmpty()) {
						chunkToGenerate = chunksQueue.get(0);
						chunkToGenerate.generateVertices();
						chunksQueue.remove(0);
						
						System.out.println("Generating new chunk " + chunksQueue.size());
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
		if(Input.isKeyDown(KeyCode.S)) {
			System.err.println("is: " + (chunkToGenerate != null) + " - " + (chunkToGenerate == null));
			System.err.println("verts: " + (vertices.isEmpty()) + " - " + (indices.isEmpty()));
		}
		
		if(chunkToGenerate != null) {
			System.err.println("generating");
			
			Vertex[] verts = new Vertex[World.vertices.size()];
			
			for(int i = 0; i < verts.length; i++) {
				verts[i] = World.vertices.get(i);
			}
			
			int[] ind = new int[World.indices.size()];
			
			for(int i = 0; i < ind.length; i++)
				ind[i] = World.indices.get(i);
			
			Mesh mesh = new Mesh(new Sprite(null, "Grass.png", null), verts, ind);
			mesh.create();
			
			GameObject obj = new GameObject(getName(), new Vector3f(chunkToGenerate.x, chunkToGenerate.y, chunkToGenerate.z), new Vector3f(), new Vector3f(1));
			obj.setMesh(mesh);
			
			Core.getMasterRenderer().addEntity(obj);
			
			chunkToGenerate = null;
			
			vertices.clear();
			indices.clear();
			vertices = new ArrayList<Vertex>();
			indices = new ArrayList<Integer>();
			System.err.println("Should be empty: " + (vertices.isEmpty()) + " - " + (indices.isEmpty()));
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