package com.Anix.IO;

import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLUtil;

import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.Camera.ProjectionType;
import com.Anix.GUI.GUI;
import com.Anix.GUI.Panel;
import com.Anix.GUI.UI;
import com.Anix.GUI.Windows.Console;
import com.Anix.IO.WinRegistry.WRKey;
import com.Anix.IO.WinRegistry.WRType;
import com.Anix.Math.Color;
import com.Anix.Math.Matrix4f;
import com.Anix.SceneManager.Scene;
import com.Anix.SceneManager.SceneManager;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public final class Application {
	private String glslVersion = null;
	
	private static byte minimized = 0;
	private static long window;
	
	private static int width, height;
	private static float fov = 70.0f;
	private int[] windowPosX = new int[1];
	private int[] windowPosY = new int[1];
	private String title;
	
	private static Matrix4f projection;
	
	private GLFWWindowSizeCallback sizeCallback;
	private GLFWDropCallback OnDropCallBack;
	
	private static boolean isMouseLocked;
	private boolean isFullscreen;
	
	private Input input;
	private static GUI gui;
	
	public static List<String> droppedFiles = new ArrayList<String>();
	
	private static WinRegistry wr = new WinRegistry();
	public final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
	public final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
	
	public Application(GUI gui, int width, int height, String title) {
		Application.gui = gui;
		Application.width = width;
		Application.height = height;
		this.title = title;
		
		if(!GLFW.glfwInit()) {
			System.err.println("[ERROR] [TSH] GLFW wasn't inialized!");
			
			return;
		}
		
		glslVersion = "#version 150";
	}
	
	public void create() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();
		
		//GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		//GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);
		
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		
		input = new Input();
		
		window = GLFW.glfwCreateWindow(width, height, title, isFullscreen ? GLFW.glfwGetPrimaryMonitor() : 0, 0);
		
		if(window == 0) {
			System.err.println("[ERROR] [TSH] Window wasn't created!");
			
			return;
		}
		
		GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		
		windowPosX[0] = (videoMode.width() - width) / 2;
		windowPosY[0] = (videoMode.height() - height) / 2;
		
		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwShowWindow(window);
		
		GLFW.glfwGetWindowPos(window, windowPosX, windowPosY);
		
		GL.createCapabilities();
		
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
		GLUtil.setupDebugMessageCallback(System.err);
		GL43.glDebugMessageControl(GL43.GL_DONT_CARE, GL43.GL_DONT_CARE, GL43.GL_DEBUG_SEVERITY_NOTIFICATION, (IntBuffer)null, false);
		
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST); // High quality visuals
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST); //  Really Nice Perspective Calculations
		GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);
		
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 0);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
	    GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
	    GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		
		glOrtho(0, width, height, 0, 1, -1);
		
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_ALPHA_TEST);
		glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
		glEnable(GL_DEPTH_TEST);
		
		createCallbacks();
		
		GLFW.glfwShowWindow(window);
		GLFW.glfwSwapInterval(1);
		updateProjection();
		
		ImGui.createContext();
		
		//TODO:
		//ImGui.getIO().addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
		//ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable);
		
		imGuiGlfw.init(window, true);
		imGuiGl3.init(glslVersion);
		
		//ImGui.getIO().setConfigWindowsMoveFromTitleBarOnly(true);
	}
	
	public void update() {
		Color color = null;
		
		if(Camera.main != null && Camera.main.skyColor != null) {
			color = Camera.main.skyColor;
		} else {
			color = new Color(0, 0, 0);
			UI.drawString("No camera found!", width*0.5f, height*0.5f, -0.5f, 0.5f, 0.5f, Color.white);
		}
		
		updateProjection();
		
		GL11.glClearColor(color.r, color.g, color.b, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GLFW.glfwPollEvents();
	}
	
	private void createCallbacks() {
		sizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int w, int h) {
				if(w == 0 && h == 0)
					minimized = 1;
				else
					minimized = 0;
				
				setSize(w, h);
			}
		};
		
		OnDropCallBack = new GLFWDropCallback() {
			@Override
			public void invoke(long window, int count, long names) {
				droppedFiles.clear();
				
				for(int i = 0; i < count; i++) {
					droppedFiles.add(GLFWDropCallback.getName(names, i));
				}
			}
		};
		
		/*GLDebugMessageCallback callBack = new GLDebugMessageCallback() {
			@Override
			public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
				System.err.println("Message: " + getMessage(length, message));
			}
		};*/
		
		GLFW.glfwSetKeyCallback(window, input.getKeyboardCallback());
		GLFW.glfwSetCursorPosCallback(window, input.getMouseMoveCallback());
		GLFW.glfwSetMouseButtonCallback(window, input.getMouseButtonsCallback());
		GLFW.glfwSetScrollCallback(window, input.getMouseScrollCallback());
		GLFW.glfwSetWindowSizeCallback(window, sizeCallback);
		GLFW.glfwSetDropCallback(window, OnDropCallBack);
		//KHRDebug.glDebugMessageCallback(callBack, window);
	}
	
	public static void setIcon(String path) {
		ByteBuffer bufferedImage = ResourceLoader.loadImageToByteBuffer(path);
		GLFWImage image = GLFWImage.malloc();
		
		image.set(32, 32, bufferedImage);
		
		GLFWImage.Buffer images = GLFWImage.malloc(1);
		images.put(0, image);
		
		GLFW.glfwSetWindowIcon(getWindow(), images);
		
		image.free();
		images.free();
		
		/*bufferedImage.clear();
		image.close();
		images.close();*/
	}
	
	public static void setCursorIcon(String path) {
		if(path == null) {
	        long cursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
	        GLFW.glfwSetCursor(window, cursor);
			
			return;
		}
		
		ByteBuffer bufferedImage = ResourceLoader.loadImageToByteBuffer(path);
		GLFWImage image = GLFWImage.malloc();
		
		image.set(32, 32, bufferedImage);
		
		long cursor = GLFW.glfwCreateCursor(image, 0, 0);
		
		GLFW.glfwSetCursor(window, cursor);
		
		image.free();
	}
	
	public static void setSize(int width, int height) {
		if(width == 0 || height == 0)
			return;
		
		for(int i = 0; i < Panel.allElements.size()/* || i < Panel.allTextInputs.size()*/; i++) {
			int differenceX = Application.width - width;
			//int differenceY = Application.height - height;
			
			//FUTURE ME FIX THIS somehow.
			//Dummy, it works by itself -.-
			
			/*if(i < Panel.allButtons.size()) {
				Panel.allButtons.get(i).position.x -= differenceX;
				//Panel.allButtons.get(i).position.y -= differenceY;
			}
			
			if(i < Panel.allTextInputs.size()) {
				Panel.allTextInputs.get(i).position.x -= differenceX;
				//Panel.allTextInputs.get(i).position.y -= differenceY;
			}*/
			
			Panel.allElements.get(i).position.x -= differenceX;
		}
		
		GLFW.glfwSetWindowSize(window, width, height);
		
		Application.width = width;
		Application.height = height;
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, height, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		GL11.glViewport(0, 0, width, height);
		updateProjection();
	}
	
	public void destroy() {
		input.destroy();
		
		sizeCallback.free();
		OnDropCallBack.free();
		
		imGuiGl3.dispose();
		imGuiGlfw.dispose();
		ImGui.destroyContext();
		//Callbacks.glfwFreeCallbacks(window);
		GLFW.glfwWindowShouldClose(window);
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
	}
	
	public static void save(String key, String value) {
		try {
			if(!wr.addKey(WRKey.HKCU, "SOFTWARE\\Anix\\" + ProjectSettings.gameName)) {
				System.err.println("[Error] could not create the key; Try removing spaces!");
				Console.LogErr("[Error] could not create the key; Try removing spaces!");
			}
			
			if(!wr.addValue(WRKey.HKCU, "SOFTWARE\\Anix\\" + ProjectSettings.gameName, key, value.getBytes(), WRType.REG_SZ)) {
				System.err.println("[Error] could not create the value; Try removing spaces!");
				Console.LogErr("[Error] could not create the value; Try removing spaces!");
			}
		} catch(InterruptedException | IOException e) {
			System.err.println("An error occurred. " + e.getMessage());
		}
	}
	
	public static String loadString(String key) {
		try {
			return wr.readValue(WRKey.HKCU, "SOFTWARE\\Anix\\" + ProjectSettings.gameName, key);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static void updateProjection() {
		ProjectionType projectionType = ProjectionType.projection;
		
		if(Camera.main != null)
			projectionType = Camera.main.projectionType;
		
		if(projectionType.equals(ProjectionType.projection)) {
				projection = Matrix4f.perspective(fov, (float)width / (float)height, 1, -1);
		} else {
			float rf = 7;
			float tb = 4;
			
			projection = Matrix4f.orthographic(-rf, rf, tb, -tb, -1000, 1000);
		}
	}
	
	public void swapBuffers() {
		GLFW.glfwSwapBuffers(window);
	}
	
	public static boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(window);
	}
	
	public boolean isFullscreen() {
		return isFullscreen;
	}
	
	public void setFullscreen(boolean isFullscreen) {
		this.isFullscreen = isFullscreen;
		
		if(isFullscreen) {
			GLFW.glfwGetWindowPos(window, windowPosX, windowPosY);
			GLFW.glfwSetWindowMonitor(window, GLFW.glfwGetPrimaryMonitor(), 0, 0, width, height, 0);
		} else {
			GLFW.glfwSetWindowMonitor(window, 0, windowPosX[0], windowPosY[0], width, height, 0);
		}
	}
	
	public static int getStartX() {
		return (int) (gui.getHierachy().getStartX() + gui.getHierachy().getWidth());
	}
	
	public static int getStartY() {
		return gui.getMenuBar().getHeight();
	}
	
	public static int getWidth() {
		if(ProjectSettings.isEditor) {
			return (int) (getFullWidth() - (gui.getHierachy().getWidth() + gui.getInspector().getWidth()));
		}
		
		return getFullWidth();
	}
	
	public static int getHeight() {
		if(ProjectSettings.isEditor) {
			return getFullHeight() - (gui.getAssets().getHeight() + gui.getMenuBar().getHeight());
		}
		
		return getFullHeight();
	}
	
	public static int getFullWidth() {
		return width;
	}
	
	public static int getFullHeight() {
		return height;
	}
	
	public static float getFOV() {
		return fov;
	}
	
	public static void addFOV(double value) {
		Application.fov += value;
		
		updateProjection();
	}
	
	public static void setFOV(float fov) {
		if(fov != Application.fov) {
			Application.fov = fov;
			
			updateProjection();
		}
	}
	
	public static void exit() {
		//ProjectSettings.previousAppSize = new Vector2i(Application.getFullWidth(), Application.getFullHeight());
		
		for(int i = 0; i < SceneManager.getScenes().size(); i++) {
			Scene currentScene = SceneManager.getScenes().get(i);
			
			if(currentScene != null) {
				currentScene.destroy();
			}
		}
		
		GLFW.glfwSetWindowShouldClose(Application.getWindow(), true);
	}
	
	public static long getWindow() {
		return window;
	}

	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(window, title);
	}
	
	public String getTitle() {
		return title;
	}
	
	public static boolean getMouseState() {
		return isMouseLocked;
	}
	
	public static void setMouseState(boolean lock) {
		isMouseLocked = lock;
		
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, lock ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
	}
	
	public static boolean isMinimized() {
		return minimized == 1;
	}
	
	/*public static boolean isFocused() {
		return GLFW.glfwGetWindowAttrib(Application.getWindow(), GLFW.GLFW_FOCUSED) == 1;
	}*/
	
	public static Matrix4f getProjectionMatrix() {
		return projection;
	}
}
