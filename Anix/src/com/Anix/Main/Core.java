package com.Anix.Main;

import java.awt.Font;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Collider2D;
import com.Anix.Engine.Editor;
import com.Anix.Engine.PhysicsEngine;
import com.Anix.Engine.UndoManager;
import com.Anix.Engine.Graphics.MasterRenderer;
import com.Anix.Engine.Graphics.Material;
import com.Anix.Engine.Graphics.Shader;
import com.Anix.GUI.GUI;
import com.Anix.GUI.Panel;
import com.Anix.GUI.Sprite;
import com.Anix.GUI.UI;
import com.Anix.GUI.Windows.Assets.Folder;
import com.Anix.GUI.Windows.Console;
import com.Anix.IO.Application;
import com.Anix.IO.FrameBuffer;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.IO.MeshManager;
import com.Anix.IO.ProjectSettings;
import com.Anix.IO.Time;
import com.Anix.Objects.GameObject;
import com.Anix.SceneManager.Scene;
import com.Anix.SceneManager.SceneManager;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;

//https://stackoverflow.com/questions/6327482/how-can-i-read-the-registry-values-using-java
//https://stackoverflow.com/questions/2846664/implementing-coroutines-in-java

/**Problems:<br><br>
 * TODO: Make 2 different camera positions<br>
 * one for editor mode and one for play mode.<br>
 * <br>
 * TODO: Make the camera to have an icon on the<br>
 * scene mode and it won't show on play.<br>
 * <br>
 * Make parent support for a GameObject.<br>
 * <br>
 * Java file wouldn't load when first added inside a folder.<br>
 * somehow fixed itself :D<br>
 * It is back???????? - Fixed, file had an extra '/'<br>
 * <br>
 * There is a weird outline in the Inspect object information texts:-<br>
 * Fixed: Reason, because it was rendering the text and then the box of a button.<br>
 * <br>
 * There is a leak fix it!<br>
 * Fixed?<br>
 * <br>
 * Make Physics2D as BoxCollider that has a separate list of objects and update before movement.<br>
 * Done<br>
 * <br>
 * Change BoxCollider to BoxCollider2D:<br>
 * Done<br>
 * <br>
 * Fix not being able to save Maps: Cannot find a (Map) Key deserializer for type [simple type, class Building].<br>
 * Used a different saving system :)<br>
 * <br>
 * Another memory leak<br>
 * Wasn't a memory leak, but a lot of new instances being created from the UI so I used ImGui.<br>
 * <br>
 * TODO: Make the ability of making a custom renderer much easier.<br>
 * <br>
 * TODO: Make a "Core.RequestUpdate" and "Core.RequestRender", for behaviours.<br>
 * So that, they can only be updated or rendered if it is called.<br>
 * <br>
 * TODO: Make the compiler to check for errors first AND then compile<br>
 */
public final class Core implements Runnable {
	private String projectName = "";
	
	private int frames = 0, fps = 0;
	private double frameTime = 0;
	private double firstTime = 0;
	private double lastTime = System.nanoTime() / 1000000000.0;
	private double passedTime = 0;
	private double unprocessedTime = 0;
	private final double UPDATE_CAP = 1.0/60.0;
	
	private static boolean isRunning = true;
	private boolean render = true;
	
	private Application application;
	public static MeshManager meshManager = new MeshManager();
	private FrameBuffer frameBuffer;
	private UndoManager undoManager = new UndoManager();
	private static MasterRenderer masterRenderer;
	private GUI gui = new GUI(this);
	private Editor editor = new Editor(this);
	
	private Object draggedObject;
	private Thread thread;
	
	private String[] args;
	private static List<Sprite> sprites = new ArrayList<Sprite>();
	public static List<GameObject> updateAbleObjects = new ArrayList<GameObject>();
	
	private ImGuiStyle redDarkTheme() {
		ImGuiStyle style = ImGui.getStyle();
		
		style.setWindowMinSize(1, 1);
		style.setFramePadding(4, 2);
		style.setItemSpacing(6, 2);
		style.setItemInnerSpacing(6, 4);
		style.setAlpha(0.95f);
		style.setWindowRounding(0.0f); //4.0f
		style.setWindowBorderSize(1);
		style.setFrameRounding(2.0f);
		//style.setIndentSpacing(6.0f);
		style.setItemInnerSpacing(2, 4);
		style.setColumnsMinSpacing(50.0f);
		style.setGrabMinSize(14.0f);
		style.setGrabRounding(16.0f);
		style.setScrollbarSize(12.0f);
		style.setScrollbarRounding(16.0f);
		
		style.setColor(ImGuiCol.Text, 0.86f, 0.93f, 0.89f, 0.78f);
		style.setColor(ImGuiCol.TextDisabled, 0.86f, 0.93f, 0.89f, 0.28f);
		style.setColor(ImGuiCol.WindowBg, 0.13f, 0.14f, 0.17f, 1.00f);
		//style.setColor(ImGuiCol.Border, 0.31f, 0.31f, 1.00f, 0.00f);
		style.setColor(ImGuiCol.BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f);
		style.setColor(ImGuiCol.FrameBg, 0.20f, 0.22f, 0.27f, 1.00f);
		style.setColor(ImGuiCol.FrameBgHovered, 0.92f, 0.18f, 0.29f, 0.78f);
		style.setColor(ImGuiCol.FrameBgActive, 0.92f, 0.18f, 0.29f, 1.00f);
		style.setColor(ImGuiCol.TitleBg, 0.20f, 0.22f, 0.27f, 1.00f);
		style.setColor(ImGuiCol.TitleBgCollapsed, 0.20f, 0.22f, 0.27f, 0.75f);
		style.setColor(ImGuiCol.TitleBgActive, 0.92f, 0.18f, 0.29f, 1.00f);
		style.setColor(ImGuiCol.MenuBarBg, 0.20f, 0.22f, 0.27f, 0.47f);
		style.setColor(ImGuiCol.ScrollbarBg, 0.20f, 0.22f, 0.27f, 1.00f);
		style.setColor(ImGuiCol.ScrollbarGrab, 0.09f, 0.15f, 0.16f, 1.00f);
		style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.92f, 0.18f, 0.29f, 0.78f);
		style.setColor(ImGuiCol.ScrollbarGrabActive, 0.92f, 0.18f, 0.29f, 1.00f);
		style.setColor(ImGuiCol.CheckMark, 0.71f, 0.22f, 0.27f, 1.00f);
		style.setColor(ImGuiCol.SliderGrab, 0.47f, 0.77f, 0.83f, 0.14f);
		style.setColor(ImGuiCol.SliderGrabActive, 0.92f, 0.18f, 0.29f, 1.00f);
		style.setColor(ImGuiCol.Button, 0.47f, 0.77f, 0.83f, 0.14f);
		style.setColor(ImGuiCol.ButtonHovered, 0.92f, 0.18f, 0.29f, 0.75f);
		style.setColor(ImGuiCol.ButtonActive, 0.95f, 0.21f, 0.32f, 1.00f);
		style.setColor(ImGuiCol.Header, 0.92f, 0.18f, 0.29f, 0.76f);
		style.setColor(ImGuiCol.HeaderHovered, 0.92f, 0.18f, 0.29f, 0.86f);
		style.setColor(ImGuiCol.HeaderActive, 0.92f, 0.18f, 0.29f, 1.00f);
		style.setColor(ImGuiCol.Separator, 0.3f, 0.16f, 0.19f, 1.00f); //0.14, 0.16, 0.19
		style.setColor(ImGuiCol.SeparatorHovered, 0.92f, 0.18f, 0.29f, 0.78f);
		style.setColor(ImGuiCol.SeparatorActive, 0.92f, 0.18f, 0.29f, 1.00f);
		style.setColor(ImGuiCol.ResizeGrip, 0.47f, 0.77f, 0.83f, 0.04f);
		style.setColor(ImGuiCol.ResizeGripHovered, 0.92f, 0.18f, 0.29f, 0.78f);
		style.setColor(ImGuiCol.ResizeGripActive, 0.92f, 0.18f, 0.29f, 1.00f);
		style.setColor(ImGuiCol.PlotLines, 0.86f, 0.93f, 0.89f, 0.63f);
		style.setColor(ImGuiCol.PlotLinesHovered, 0.92f, 0.18f, 0.29f, 1.00f);
		style.setColor(ImGuiCol.PlotHistogram, 0.86f, 0.93f, 0.89f, 0.63f);
		style.setColor(ImGuiCol.PlotHistogramHovered, 0.92f, 0.18f, 0.29f, 1.00f);
		style.setColor(ImGuiCol.TextSelectedBg, 0.92f, 0.18f, 0.29f, 0.43f);
		style.setColor(ImGuiCol.PopupBg, 0.20f, 0.22f, 0.27f, 0.9f);
		////style.Colors[ImGuiCol_ModalWindowDarkening] = ImVec4(0.20f, 0.22f, 0.27f, 0.73f);
		
		return style;
	}
	
	private void init() {
		//?
		new Material(Shader.defaultShader);
		new SceneManager(this);
		//new Panel(gui);
		new ProjectSettings(this);
		
		editor.init(args);
		gui.init();
		editor.loadProject();
		
		frameBuffer = new FrameBuffer(1920, 1080);
		
		if(!ProjectSettings.isEditor) {
			editor.setIsPlaying(true);
		}
		
		Input.init();
		
		args = null;
	}
	
	@Override
	public void run() {
		application = new Application(gui, 1280, 720, "Anix " + editor.getVersion());
		application.create();
		masterRenderer = new MasterRenderer();
		
		ImGui.styleColorsDark();
		redDarkTheme();
		
		init();
		UI.init("Serif", Font.BOLD, 32);
		Time.init();
		
		Editor.canAddObjects = true;
		
		while(isRunning && !Application.shouldClose()) {
			render = false;
			
			firstTime = System.nanoTime() / 1000000000.0;
			passedTime = firstTime - lastTime;
			lastTime = firstTime;
			unprocessedTime += passedTime;
			frameTime += passedTime;
			
			while(unprocessedTime >= UPDATE_CAP) {
				unprocessedTime -= UPDATE_CAP;
				render = true;
				
				Time.process();
				
				frameBuffer.bindReflectionFrameBuffer();
				application.update();
				frameBuffer.unbindCurrentFrameBuffer();
				application.update();
				UI.update();
				
				update();
				input();
				
				PhysicsEngine.update();
				
				if(frameTime >= 1.0) {
					frameTime = 0;
					fps = frames;
					frames = 0;
					
					String sceneName, title = "";
					
					if(ProjectSettings.isEditor && projectName.contains("\\")) {
						sceneName = " | No Scene opened";
						
						if(SceneManager.getCurrentScene() != null) {
							sceneName = " | " + SceneManager.getCurrentScene().getName();
						}
						
						title = application.getTitle() + " | " + fps + " | " + 
								(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1000*1000)+"MB"
								+ sceneName + " | " + projectName.split("\\\\")[1];
					} else {
						title = ProjectSettings.gameName;
					}
					
					application.setTitle(title);
					
					if(!ProjectSettings.isEditor) {
						editor.setIsPlaying(true);
					}
				}
			}
			
			if(render) {
				application.imGuiGlfw.newFrame();
				ImGui.newFrame();
				
				frameBuffer.bindReflectionFrameBuffer();
				
				render();
				renderBehaviours();
				
				frameBuffer.unbindCurrentFrameBuffer();
				
				Panel.render();
				gui.render();
				
				//EndFrame
				ImGui.render();
				application.imGuiGl3.renderDrawData(ImGui.getDrawData());
				
				if(ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
					final long backupWindowPtr = GLFW.glfwGetCurrentContext();
					ImGui.updatePlatformWindows();
					ImGui.renderPlatformWindowsDefault();
					GLFW.glfwMakeContextCurrent(backupWindowPtr);
				}
				
				Input.update();
				
				application.swapBuffers();
				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			
			if(!isRunning || Application.shouldClose()) {
				if(!Editor.isPlaying()) { //Only save if not during gameplay!
					for(int i = 0; i < SceneManager.getScenes().size(); i++) {
						editor.saveScene(SceneManager.getScenes().get(i));
					}
					
					editor.saveScene();
				}
				
				Application.exit();
				
				frameBuffer.cleanUp();
				destroy();
			}
		}
	}
	
	private void update() {
		if(Application.isMinimized() /*|| !Application.isFocused()*/)
			return;
		
		editor.update();
		masterRenderer.update();
		meshManager.update();
		undoManager.update();
		
		Scene s = SceneManager.getCurrentScene();
		
		if(s != null)
			s.update();
		
		if(ProjectSettings.isEditor) {
			gui.update();
			
			for(int i = 0; i < Application.droppedFiles.size(); i++) {
				String path = Application.droppedFiles.get(i);
				
				File file = new File(path);
				
				if(!file.exists()) {
					System.err.println("[ERROR] [TSH] Couldn't find the dragged file of " + path);
					
					continue;
				}
				
				String msg = "";
				String fullPath = System.getProperty("user.dir");
				String fileSeparator = System.getProperty("file.separator");
				
				String[] absolutePath = file.getAbsolutePath().split("\\\\");
				
				if(gui.getAssets().getInFolder() != null) {
					msg = gui.getAssets().getInFolder().getName();
					
					Folder curFolder = gui.getAssets().getInFolder().getParentFolder();
					
					while(curFolder != null) {
						msg += "/" + curFolder.getName();
						
						curFolder = curFolder.getParentFolder();
					}
					
					String[] msgs = msg.split("/");
					
					msg = "";
					
					for(int j = msgs.length - 1; j >= 0; j--) {
						msg += "/" + msgs[j];
					}
				}
				
				addFolder(path, fullPath + fileSeparator + projectName + fileSeparator + "Assets" + fileSeparator + msg + fileSeparator
						+ absolutePath[absolutePath.length - 1]);
				
				Application.droppedFiles.remove(i);
				i--;
			}
		}
		
		updateBehaviours();
	}
	
	public void updateBehaviours() {
		if(!Editor.isPlaying() && ProjectSettings.isEditor)
			return;
		
		if(updateAbleObjects.isEmpty())
			return;
		
		if(SceneManager.getCurrentScene() != null) {
			for(int i = 0; i < updateAbleObjects.size(); i++) {
				GameObject obj = updateAbleObjects.get(i);
				
				/*if(obj == null || obj.shouldBeRemoved) {
					updateAbleObjects.remove(i);
					i--;
					continue;
				}*/
				
				if(!obj.getBehaviours().isEmpty() && obj.isEnabled()) {
					for(int j = 0; j < obj.getBehaviours().size(); j++) {
						Behaviour b = obj.getBehaviours().get(j);
						
						if(b == null) {
							obj.getBehaviours().remove(j);
							j--;
							continue;
						}
						
						if(!b.isEnabled)
							continue;
						
						try {
							b.update();
						} catch(Exception | Error e) {
							CharArrayWriter cw = new CharArrayWriter();
							PrintWriter w = new PrintWriter(cw);
							e.printStackTrace(w);
							w.close();
							String trace = cw.toString();
							
							System.err.println("[ERROR] " + b.getName() + " because " + trace);
							Console.LogErr("[ERROR] " + b.getName() + " because " + trace);
						}
					}
				}
			}
		}
		
		Collider2D.updateColliders();
	}
	
	private void input() {
		if(Application.isMinimized() || !ProjectSettings.isEditor /*|| !Application.isFocused()*/)
			return;
		
		if(Input.isKeyDown(KeyCode.G)) {
			editor.setIsPlaying(!Editor.isPlaying());
		}
		
		/*if(Input.isKeyDown(KeyCode.W)) {
			generateApkThroughAnt("C:\\Users\\smsmk\\OneDrive\\Eclipse\\DaxyEngine-UIMaker\\build.xml");
		}*/
		
		if(Input.isKey(KeyCode.LeftControl) && Input.isKeyDown(KeyCode.S)) {
			editor.saveProject();
		}
	}
	
	public void render() {
		if(!Application.isMinimized() /*&& Application.isFocused(*/ && masterRenderer != null) {
			masterRenderer.render();
		}
	}
	
	public void renderBehaviours() {
		if(Application.isMinimized() || updateAbleObjects.isEmpty() || updateAbleObjects.size() < 1)
			return;
		
		Scene currentScene = SceneManager.getCurrentScene();
		
		if(/*&& Application.isFocused() */currentScene != null) {
			for(int i = 0; i < updateAbleObjects.size(); i++) {
				GameObject obj = updateAbleObjects.get(i);
				
				/*if(obj == null || obj.shouldBeRemoved) {
					updateAbleObjects.remove(i);
					i--;
					
					continue;
				}*/
				
				if(!obj.getBehaviours().isEmpty() && obj.isEnabled()) {
					for(int j = 0; j < obj.getBehaviours().size(); j++) {
						Behaviour b = obj.getBehaviours().get(j);
						
						if(b == null) {
							obj.getBehaviours().remove(j);
							j--;
							continue;
						}
						
						if(!b.isEnabled)
							continue;
						
						try {
							b.render();
						} catch(Exception | Error e) {
							CharArrayWriter cw = new CharArrayWriter();
							PrintWriter w = new PrintWriter(cw);
							e.printStackTrace(w);
							w.close();
							String trace = cw.toString();
							
							System.err.println("[ERROR] " + b.getName() + " because " + trace);
							Console.LogErr("[ERROR] " + b.getName() + " because " + trace);
						}
					}
				}
			}
		}
	}
	
	/*private static void generateApkThroughAnt(String buildPath) {
		File antBuildFile = new File(buildPath);
		
		Project p = new Project();
		p.setUserProperty("ant.file", antBuildFile.getAbsolutePath());
		
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);
		
		BuildException ex = null;
		try {
			p.fireBuildStarted();
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			p.addReference("ant.projectHelper", helper);
			helper.parse(p, antBuildFile);
			p.executeTarget("clean");
			p.executeTarget("release");
		} catch (BuildException e) {
			ex = e;
		} finally {
			p.fireBuildFinished(ex);
		}
	}*/
	
	private void destroy() {
		if(masterRenderer != null)
			masterRenderer.destroy();
		
		if(Shader.defaultShader != null)
			Shader.defaultShader.destroy();
		
		application.destroy();
		editor.destroy();
		
		System.exit(0);
	}
	
	public static void main(String[] args) {
		Core core = new Core();
		
		core.args = args;
		
		core.thread = new Thread(core);
		core.thread.setName("Main Thread");
		core.thread.start();
	}
	
	private void addFolder(String startPath, String targetPath) {
		File file = new File(startPath);
		
		if(file.isDirectory()) {
			for(int i = 0; i < file.listFiles().length; i++) {
				File f = file.listFiles()[i];
				
				String[] absolutePath = f.getAbsolutePath().split("\\\\");
				
				try {
					Files.copy(Paths.get(startPath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
				
				addFolder(startPath + "\\" + absolutePath[absolutePath.length - 1], targetPath + "\\" + absolutePath[absolutePath.length - 1]);
			}
		} else {
			try {
				Files.copy(Paths.get(startPath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		
		editor.addFolder(targetPath, null);
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public static MasterRenderer getMasterRenderer() {
		return masterRenderer;
	}
	
	public GUI getGUI() {
		return gui;
	}
	
	public Editor getEditor() {
		return editor;
	}
	
	public Object getDraggedObject() {
		return draggedObject;
	}
	
	public void setDraggedObject(Object draggedObject) {
		this.draggedObject = draggedObject;
	}
	
	public static List<Sprite> getSprites() {
		return sprites;
	}
	
	public FrameBuffer getFrameBuffer() {
		return frameBuffer;
	}
}