import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Engine.Graphics.Mesh;
import com.Anix.Engine.Graphics.Vertex;
import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
import com.Anix.Math.MathD;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

public class Chunk extends Behaviour {
	private static final long serialVersionUID = 1L;
	
	public static Texture texture;
	
	int vertexIndex = 0;
	
	List<Vertex> vertices;
	List<Integer> triangles;
	
	byte[][][] voxelMap;
	
	@Override
	public void start() {
		texture = UI.loadTexture("Blocks.png");
		
		vertices = new ArrayList<Vertex>();
		triangles = new ArrayList<Integer>();
		
		voxelMap = new byte[VoxelData.chunkWidth][VoxelData.chunkHeight][VoxelData.chunkWidth];
		
		populateVoxelMap();
		createMeshData();
		
		createMesh();
	}
	
	void populateVoxelMap() {
		for(int y = 0; y < VoxelData.chunkHeight; y++) {
			for(int x = 0; x < VoxelData.chunkWidth; x++) {
				for(int z = 0; z < VoxelData.chunkWidth; z++) {
					
					if(y < 1)
						voxelMap[x][y][z] = (byte)0;
					else if(y <= VoxelData.chunkHeight - 3)
						voxelMap[x][y][z] = (byte)1;
					else
						voxelMap[x][y][z] = (byte)3;
				}
			}
		}
	}
	
	void createMeshData() {
		for(int y = 0; y < VoxelData.chunkHeight; y++) {
			for(int x = 0; x < VoxelData.chunkWidth; x++) {
				for(int z = 0; z < VoxelData.chunkWidth; z++) {
					
					addVoxelDataToChunk(new Vector3f(x, y, z));
				}
			}
		}
	}

	boolean checkVoxel(Vector3f pos) {
		int x = MathD.floorToInt(pos.x);
		int y = MathD.floorToInt(pos.y);
		int z = MathD.floorToInt(pos.z);
		
		if(x < 0 || x > VoxelData.chunkWidth - 1 || y < 0 || y > VoxelData.chunkHeight - 1 || z < 0 || z > VoxelData.chunkWidth - 1)
			return false;
		
		return World.blockTypes[voxelMap[x][y][z]].isSolid;
	}
	
	void addVoxelDataToChunk(Vector3f pos) {
		Vector2f temp;
		
		for(int p = 0; p < 6; p++) {
			
			if(!checkVoxel(pos.copy().add(VoxelData.faceChecks[p]))) {
				byte blockId = voxelMap[(int)pos.x][(int)pos.y][(int)pos.z];
				temp = addTexture(World.blockTypes[blockId].getTextureId(p));
				
				vertices.add(new Vertex(VoxelData.voxelVerts[VoxelData.voxelTris[p][0]].copy().add(pos),
						new Vector2f(temp.x + VoxelData.normalizedBlockTextureSize(), temp.y + VoxelData.normalizedBlockTextureSize())));
				
				vertices.add(new Vertex(VoxelData.voxelVerts[VoxelData.voxelTris[p][1]].copy().add(pos),
						new Vector2f(temp.x + VoxelData.normalizedBlockTextureSize(), temp.y)));
				
				vertices.add(new Vertex(VoxelData.voxelVerts[VoxelData.voxelTris[p][2]].copy().add(pos),
						new Vector2f(temp.x, temp.y + VoxelData.normalizedBlockTextureSize())));
				
				vertices.add(new Vertex(VoxelData.voxelVerts[VoxelData.voxelTris[p][3]].copy().add(pos),
						new Vector2f(temp.x, temp.y)));
				
				triangles.add(vertexIndex);
				triangles.add(vertexIndex + 1);
				triangles.add(vertexIndex + 2);
				triangles.add(vertexIndex + 2);
				triangles.add(vertexIndex + 1);
				triangles.add(vertexIndex + 3);
				
				vertexIndex += 4;
			}
		}
	}
	
	void createMesh() {
		Mesh mesh = gameObject.getMesh();
		mesh.set(vertices, triangles);
		mesh.getSprite().setTexture(texture);
		mesh.updateMesh();
		
		gameObject.setMesh(mesh);
	}
	
	Vector2f addTexture(int textureId) {
		float y = textureId / VoxelData.textureAtlasSizeInBlocks;
		float x = textureId - (y * VoxelData.textureAtlasSizeInBlocks);
		
		x *= VoxelData.normalizedBlockTextureSize();
		y *= VoxelData.normalizedBlockTextureSize();
		
		//y = 1f - y - VoxelData.normalizedBlockTextureSize();
		
		return new Vector2f(x, y);
	}
}