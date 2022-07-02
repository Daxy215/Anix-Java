package com.Anix.IO;

import java.util.ArrayList;
import java.util.List;

import com.Anix.Engine.Graphics.Mesh;
import com.Anix.Engine.Graphics.Vertex;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

public final class MeshManager {
	public static Vertex[] Vertices3D = new Vertex[] {
			//Back face
			new Vertex(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector3f(0, 0, -1), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(0, 0, -1), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector3f(0, 0, -1), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector3f(0, 0, -1), new Vector2f(1.0f, 0.0f)),
			
			//Front face
			new Vertex(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector3f(0, 0, 1), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector3f(0, 0, 1), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector3f(0, 0, 1), new Vector2f(1.0f, 0.0f)),

			//Right face
			new Vertex(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector3f(1, 0, 0), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector3f(1, 0, 0), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector3f(1, 0, 0), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector3f(1, 0, 0), new Vector2f(1.0f, 0.0f)),

			//Left face
			new Vertex(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector3f(-1, 0, 0), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector3f(-1, 0, 0), new Vector2f(1.0f, 0.0f)),

			//Top face
			new Vertex(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f)),

			//Bottom face
			new Vertex(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector3f(0, -1, 0), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(0, -1, 0), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector3f(0, -1, 0), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector3f(0, -1, 0), new Vector2f(1.0f, 0.0f)),
	};
	
	public static int[] Indices3D = new int[] {
			0, 1, 3, 3, 1, 2,
			4, 5, 7, 7, 5, 6,
			8, 9, 11, 11, 9, 10,
			12, 13, 15, 15, 13, 14,
			16, 17, 19, 19, 17, 18,
			20, 21, 23, 23, 21, 22
	};
	
	public static Vertex[] Vertices2D = new Vertex[] {
			new Vertex(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.0f)),
			new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(0, 0, 1), new Vector2f(0.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector3f(0, 0, 1), new Vector2f(1.0f, 1.0f)),
			new Vertex(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector3f(0, 0, 1), new Vector2f(1.0f, 0.0f)),
	};
	
	public static int[] Indices2D = new int[] {
			0, 1, 3, 3, 1, 2
	};
	
	private List<Mesh> meshes = new ArrayList<Mesh>();
	private List<Mesh> meshesToBeCreated = new ArrayList<Mesh>();
	private List<Mesh> meshesToBeRemoved = new ArrayList<Mesh>();
	
	public void update() {
		for(int i = 0; i < meshesToBeCreated.size() || i < meshesToBeRemoved.size(); i++) {
			if(i < meshesToBeCreated.size()) {
				if(meshesToBeCreated.get(i) != null)
					meshesToBeCreated.get(i).create();
				
				meshesToBeCreated.remove(i);
			}
			
			if(i < meshesToBeRemoved.size()) {
				if(meshesToBeRemoved.get(i) != null)
					meshesToBeRemoved.get(i).destroy();
				
				meshesToBeRemoved.remove(i);
			}
			
			i--;
		}
	}
	
	public List<Mesh> getMeshes() {
		return meshes;
	}
	
	public void addMesh(Mesh mesh) {
		if(mesh == null) {
			return;
		}
		
		if(getMeshByPath(mesh.getPath()) == null) {
			push(mesh);
			meshes.add(mesh);
		}
	}
	
	public Mesh getMeshByPath(String path) {
		for(int i = 0; i < meshes.size(); i++) {
			if(meshes.get(i).getPath().equals(path)) {
				return meshes.get(i);
			}
		}
		
		return null;
	}
	
	public void push(Mesh mesh) {
		if(mesh == null) {
			return;
		}
		
		meshesToBeCreated.add(mesh);
	}
	
	public void pushRemove(Mesh mesh) {
		if(mesh == null) {
			return;
		}
		
		meshesToBeRemoved.add(mesh);
	}
	
	public void clear() {
		if(ProjectSettings.isEditor)
			meshesToBeCreated.clear();
	}
}
