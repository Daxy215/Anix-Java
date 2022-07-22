package com.Anix.Engine.Graphics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import com.Anix.Engine.Utils.FileUtils;
import com.Anix.GUI.Windows.Console;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Math.Vector4f;

public class Shader implements Serializable {
	private static final long serialVersionUID = -7257198393116909124L;
	
	public String name;
	private String vertexFile = "", fragmentFile = "";
	private int vertexID, fragmentID, programID;
	
	public static final Shader defaultShader = new Shader("Default", "/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
	
	public static List<Shader> shaders = new ArrayList<Shader>();
	
	public Shader(String name, String path) {
		this.name = name;
		
		File file = new File(path);
		
		if(file.exists()) {
			boolean isVertex = false;
			
			if(!path.startsWith("/")) {
				String temp = "/" + path;
				path = temp;
			}
			
			InputStream is = FileUtils.class.getResourceAsStream(path);
			
			if(is == null) {
				try {
					is = new FileInputStream(new File(path));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
			String line = "";
			vertexFile = "";
			fragmentFile = "";
			
			try {
				while((line = reader.readLine()) != null) {
					if(line.equalsIgnoreCase("null"))
						continue;
					
					if(line.contains("#version")) {
						isVertex = !isVertex;
					}
					
					if(isVertex) {
						vertexFile += line + "\n";
					} else {
						fragmentFile += line + "\n";
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Console.LogErr("[ERROR] [SHADER] Couldn't find shader folder - Shader name: " + name);
		}
		
		create();
		
		shaders.add(this);
	}
	
	public Shader(String name, BufferedReader reader) {
		boolean isVertex = false;
		
		String line = "";
		vertexFile = "";
		fragmentFile = "";
		
		try {
			while((line = reader.readLine()) != null) {
				if(line.equalsIgnoreCase("null"))
					continue;
				
				if(line.contains("#version")) {
					isVertex = !isVertex;
				}
				
				if(isVertex) {
					vertexFile += line + "\n";
				} else {
					fragmentFile += line + "\n";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		create();
		
		shaders.add(this);
	}
	
	public Shader(String name, String vertexPath, String fragmentPath) {
		this.name = name;
		vertexFile = FileUtils.loadAsString(vertexPath);
		fragmentFile = FileUtils.loadAsString(fragmentPath);
		
		create();
		
		if(name.equals("Default"))
			return;
		
		shaders.add(this);
	}
	
	private void create() {
		programID = GL20.glCreateProgram();
		vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		
		if(vertexFile == null || fragmentFile == null || vertexFile.length() == 0 || fragmentFile.length() == 0) {
			Console.LogErr("[ERORR] [SHADER] Cannot load an empty shader.");
			
			return;
		}
				
		GL20.glShaderSource(vertexID, vertexFile);
		GL20.glCompileShader(vertexID);
		
		if(GL20.glGetShaderi(vertexID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("[SHADER ERROR] [VERTEX] [" + name + "] : " + GL20.glGetShaderInfoLog(vertexID));
			Console.LogErr("[SHADER ERROR] [VERTEX] [" + name + "] : " + GL20.glGetShaderInfoLog(vertexID));
			
			return;
		}
		
		fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		
		GL20.glShaderSource(fragmentID, fragmentFile);
		GL20.glCompileShader(fragmentID);
		
		if(GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("[SHADER ERROR] [FRAGMENT] [" + name + "] : " + GL20.glGetShaderInfoLog(fragmentID));
			Console.LogErr("[SHADER ERROR] [FRAGMENT] [" + name + "] : " + GL20.glGetShaderInfoLog(fragmentID));
			
			return;
		}
		
		GL20.glAttachShader(programID, vertexID);
		GL20.glAttachShader(programID, fragmentID);
		
		GL20.glLinkProgram(programID);
		
		if(GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			System.err.println("[ERROR] Program linking: " + GL20.glGetProgramInfoLog(programID));
			Console.LogErr("[SHADER ERROR] [" + name + "] : " + GL20.glGetProgramInfoLog(programID));
			
			return;
		}
		
		GL20.glValidateProgram(programID);
		
		if(GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
			System.err.println("[ERROR] Program validation: " + GL20.glGetProgramInfoLog(programID));
			Console.LogErr("[SHADER ERROR] [" + name + "] : " + GL20.glGetProgramInfoLog(programID));
			
			return;
		}
	}
	
	public int getUniformLocation(String name) {
		return GL20.glGetUniformLocation(programID, name);
	}

	public int setUniform(String name, int value) {
		if(getUniformLocation(name) == -1) {
			return -1;
			//System.err.println("Error: Couldn't find a uniform with the name of " + name);
		}
		
		/*int curProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		
		if(curProgram != programID) {
			curProgram = -1;
			
			bind();
		}*/
		
		GL20.glUniform1i(getUniformLocation(name), value);
		
		/*if(curProgram != -1) {
			GL20.glUseProgram(curProgram);
		}*/
		
		return 0;
	}
	
	public int setUniform(String name, float value) {
		if(getUniformLocation(name) == -1) {
			return -1;
			//System.err.println("Error: Couldn't find a uniform with the name of " + name);
		}
		
		//int curProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		
		//bind();
		
		GL20.glUniform1f(getUniformLocation(name), value);
		//GL20.glUseProgram(curProgram);
		
		return 0;
	}
	
	public int setUniform(String name, boolean value) {
		if(getUniformLocation(name) == -1) {
			return -1;
			//System.err.println("Error: Couldn't find a uniform with the name of " + name);
		}
		
		//int curProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		
		//bind();
		
		GL20.glUniform1f(getUniformLocation(name), value ? 1 : 0);
		//GL20.glUseProgram(curProgram);
		
		return 0;
	}

	public int setUniform(String name, Vector2f value) {
		if(getUniformLocation(name) == -1) {
			return -1;
			//System.err.println("Error: Couldn't find a uniform with the name of " + name);
		}
		
		//int curProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		
		//bind();
		
		GL20.glUniform2f(getUniformLocation(name), value.getX(), value.getY());
		//GL20.glUseProgram(curProgram);
		
		return 0;
	}
	
	public int setUniform(String name, Color color) {
		if(getUniformLocation(name) == -1) {
			return -1;
			//System.err.println("Error: Couldn't find a uniform with the name of " + name);
		}
		
		//int curProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		
		//bind();
		
		GL20.glUniform3f(getUniformLocation(name), color.getRed(), color.getGreen(), color.getBlue());
		//GL20.glUseProgram(curProgram);
		
		return 0;
	}
	
	public int setUniform(String name, Vector3f value) {
		if(getUniformLocation(name) == -1) {
			return -1;
			//System.err.println("Error: Couldn't find a uniform with the name of " + name);
		}
		
		//int curProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		
		//bind();
		
		GL20.glUniform3f(GL20.glGetUniformLocation(programID, name), value.getX(), value.getY(), value.getZ());
		//GL20.glUseProgram(curProgram);
		
		return 0;
	}
	
	public int setUniform(String name, Vector4f value) {
		if(getUniformLocation(name) == -1) {
			return -1;
			//System.err.println("Error: Couldn't find a uniform with the name of " + name);
		}
		
		//int curProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		
		//bind();
		
		GL20.glUniform4f(getUniformLocation(name), value.getX(), value.getY(), value.getZ(), value.getW());
		//GL20.glUseProgram(curProgram);
		
		return 0;
	}
	
	public int setUniform(String name, Matrix4f value) {
		if(getUniformLocation(name) == -1) {
			return -1;
			//System.err.println("Error: Couldn't find a uniform with the name of " + name);
		}
		
		//int curProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		
		//if(curProgram != programID)
		//	GL20.glUseProgram(programID);
		
		try(MemoryStack stack = MemoryStack.stackPush()) {
			GL20.glUniformMatrix4fv(getUniformLocation(name), false, value.get(stack.mallocFloat(16)));
		}
		
		//GL20.glUniformMatrix4fv(getUniformLocation(name), true, value);
		//GL20.glUseProgram(curProgram);
		
		return 0;
	}
	
	public void bind() {
		//int curProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		
		//if(curProgram == programID)
		//	return;
		
		GL20.glUseProgram(programID);
	}
	
	public static void bind(String name) {
		Shader shader = getShader(name);
		
		if(shader == null) {
			Console.LogErr("[ERROR] [SHADER] Couldn't find a shader with the name of " + name);
			
			return;
		}
		
		shader.bind();
	}
	
	public void unbind() {
		GL20.glUseProgram(0);
	}
	
	public static void unbind(String name) {
		Shader shader = getShader(name);

		if(shader == null) {
			Console.LogErr("[ERROR] [SHADER] Couldn't find a shader with the name of " + name);

			return;
		}
		
		shader.unbind();
	}

	public static Shader getShader(String name) {
		for(int i = 0; i < shaders.size(); i++) {
			if(shaders.get(i).name.equals(name))
				return shaders.get(i);
		}

		return null;
	}

	public void destroy() {
		unbind();

		GL20.glDetachShader(programID, vertexID);
		GL20.glDetachShader(programID, fragmentID);

		GL20.glDeleteProgram(programID);

		GL20.glDeleteShader(vertexID);
		GL20.glDeleteShader(fragmentID);
	}
}
