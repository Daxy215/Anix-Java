package com.Anix.Engine.Graphics;

import java.io.Serializable;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import com.Anix.GUI.Sprite;
import com.Anix.GUI.UI;
import com.Anix.IO.Application;
import com.Anix.IO.MeshManager;
import com.Anix.IO.ProjectSettings;
import com.Anix.IO.ProjectSettings.ProjectType;
import com.Anix.Main.Core;

public class Mesh implements Serializable, Cloneable {
	private static final long serialVersionUID = -4953707361353170722L;
	
	protected String path;
	
	protected Vertex[] vertices;
	protected int[] indices; //Which order to draw each vertex.
	
	protected Material material;
	//private Texture texture;
	protected Sprite sprite;
	
	//VAO: Vertex array object. PBO: Position buffer object IBO: Indices buffer object. Deleted=[CBO: Colour buffer object.] TBO: Texture buffer object
	protected int vao = -1, pbo, nbo, tbo, ibo;
	protected boolean hasBeenCreated, hasBeenDestoried;
	
	public Mesh() {
		
	}
	
	public Mesh(Sprite sprite) {
		if(sprite != null)
			this.path = sprite.getPath();
		this.sprite = sprite;
		
		if(ProjectSettings.projectType.equals(ProjectType.D2)) {
			this.vertices = MeshManager.Vertices2D;
			this.indices = MeshManager.Indices2D;
		} else {
			this.vertices = MeshManager.Vertices3D;
			this.indices = MeshManager.Indices3D;
		}
		
		this.material = new Material(Shader.defaultShader);
	}
	
	public Mesh(Sprite sprite, Material material) {
		if(sprite != null)
			this.path = sprite.getPath();
		
		this.sprite = sprite;
		
		if(ProjectSettings.projectType.equals(ProjectType.D2)) {
			this.vertices = MeshManager.Vertices2D;
			this.indices = MeshManager.Indices2D;
		} else {
			this.vertices = MeshManager.Vertices3D;
			this.indices = MeshManager.Indices3D;
		}
		
		this.material = material;
	}
	
	public Mesh(Sprite sprite, Vertex[] vertices, int[] indices) {
		if(sprite != null)
			this.path = sprite.getPath();
		
		this.sprite = sprite;
		this.vertices = vertices;
		this.indices = indices;
		this.material = new Material(Shader.defaultShader);
	}
	
	public Mesh(Sprite sprite, Vertex[] vertices, int[] indices, Material material) {
		if(sprite != null)
			this.path = sprite.getPath();
		this.sprite = sprite;
		this.vertices = vertices;
		this.indices = indices;
		this.material = material;
	}
	
	/*public Mesh(String path, Vertex[] vertices, int[] indices) {
		this.path = path;		
		this.vertices = vertices;
		this.indices = indices;
		
		material = new Material(Shader.defaultShader);
	}
	
	public Mesh(String path, Vertex[] vertices, int[] indices, Material material) {
		this.path = path;
		this.vertices = vertices;
		this.indices = indices;
		this.material = material;
	}*/
	
	public void create() {
		if(vertices.length == 0)
			return;
		
		if(GLFW.glfwGetCurrentContext() != Application.getWindow()) {
			Core.meshManager.push(this);
			
			return;
		}
		
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] positionData = new float[vertices.length * 3];
		
		FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
		float[] textureData = new float[vertices.length * 2];
		
		FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] normalData = new float[vertices.length * 3];
		
		for (int i = 0; i < vertices.length; i++) {
			positionData[i * 3]     = vertices[i].getPosition().getX();
			positionData[i * 3 + 1] = vertices[i].getPosition().getY();
			positionData[i * 3 + 2] = vertices[i].getPosition().getZ();

			textureData[i * 2 + 0] = vertices[i].getTextureCoord().getX();
			textureData[i * 2 + 1] = vertices[i].getTextureCoord().getY();
			
			normalData[i * 3]     = vertices[i].getNormal().getX();
			normalData[i * 3 + 1] = vertices[i].getNormal().getY();
			normalData[i * 3 + 2] = vertices[i].getNormal().getZ();
		}
		
		positionBuffer.put(positionData);
		((Buffer)positionBuffer).flip();
		textureBuffer.put(textureData);
		((Buffer)textureBuffer).flip();
		normalBuffer.put(normalData);
		((Buffer)normalBuffer).flip();
		
		pbo = storeData(positionBuffer, 0, 3);
		tbo = storeData(textureBuffer, 1, 2);
		nbo = storeData(normalBuffer, 2, 3);
		
		IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
		indicesBuffer.put(indices);
		((Buffer)indicesBuffer).flip();
		
		ibo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		MemoryUtil.memFree(positionBuffer);
		MemoryUtil.memFree(textureBuffer);
		MemoryUtil.memFree(normalBuffer);
		MemoryUtil.memFree(indicesBuffer);
		
		createTexture();
		
		hasBeenCreated = true;
	}
	
	public void createTexture() {
		try {
			if(sprite != null && sprite.getTexture() == null) {
				sprite.setTexture(UI.loadTexture(path));
			}
		} catch(Exception e) {
			e.printStackTrace(System.err);
			System.err.println("[ERROR] Couldn't find the texture at " + path);
		}
	}
	
	//sizeof float is 4
	private int storeData(FloatBuffer buffer, int index, int size) {
		int bufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		return bufferID;
	}
	
	public void updateMesh() {
		destroy();
		create();
	}
	
	@Override
	protected Mesh clone()  {
		try {
			return (Mesh)super.clone();
		} catch(CloneNotSupportedException e) {
			System.err.println("[ERORR] Cloning a mesh is not supported apparently..");
			return null;
		}
	}
	
	public void destroy() {
		if(GLFW.glfwGetCurrentContext() != Application.getWindow()) {
			Core.meshManager.pushRemove(this);
			
			return;
		}
		
		hasBeenDestoried = true;
		
		GL15.glDeleteBuffers(pbo);
		GL15.glDeleteBuffers(ibo);
		GL15.glDeleteBuffers(tbo);
		
		GL30.glDeleteVertexArrays(vao);
		
		vertices = null;
	    indices = null;
		
		if(sprite != null)
			sprite.destroy();
	}
	
	public String getPath() {
		return path;
	}
	
	public Vertex[] getVertices() {
		return vertices;
	}

	public void setVertices(Vertex[] vertices) {
		this.vertices = vertices;
		
		updateMesh();
	}
	
	public int[] getIndices() {
		return indices;
	}
	
	public void setIndices(int[] indices) {
		this.indices = indices;
		
		updateMesh();
	}
	
	public void set(List<Vertex> vertices, List<Integer> indices) {
		Vertex[] verts = new Vertex[vertices.size()];
		int[] indc = new int[indices.size()];
		
		for(int i = 0; i < verts.length || i < indc.length; i++) {
			if(i < verts.length)
				verts[i] = vertices.get(i);
			
			if(i < indc.length)
				indc[i] = indices.get(i);
		}
		
		this.vertices = verts;
		this.indices = indc;
		
		updateMesh();
	}
	
	public void set(Vertex[] vertices, int[] indices) {
		this.vertices = vertices;
		this.indices = indices;
		
		updateMesh();
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	
	/*public Texture getTexture() {
		return texture;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}*/
	
	
	/*public Sprite getSprite() {
		return sprite;
	}
	
	public void setSprite(Sprite sprite) {
		if(this.sprite != null && sprite != null &&
				this.sprite.getName().equals(sprite.getName()))
			return;
		
		this.sprite = sprite;
		
		updateMesh();
	}*/
	
	public int getVAO() {
		return vao;
	}

	public int getPBO() {
		return pbo;
	}

	public int getTBO() {
		return tbo;
	}

	public int getIBO() {
		return ibo;
	}
	
	public boolean hasBeenCreated() {
		return hasBeenCreated;
	}
}
