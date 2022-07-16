package com.Anix.GUI;

import com.Anix.GUI.Windows.Assets;
import com.Anix.GUI.Windows.AssetsMenuBar;
import com.Anix.GUI.Windows.AssetsMenuBar.MenuType;
import com.Anix.GUI.Windows.AssetsStore;
import com.Anix.GUI.Windows.Console;
import com.Anix.GUI.Windows.Hierachy;
import com.Anix.GUI.Windows.Inspector;
import com.Anix.GUI.Windows.MenuBar;
import com.Anix.GUI.Windows.SceneViewer;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.IO.ProjectSettings;
import com.Anix.Main.Core;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

public final class GUI {
	private Core core;
	
	//Windows
	private MenuBar menuBar;
	private Hierachy hierachy;
	private SceneViewer sceneViewer;
	private Inspector inspector;
	private Assets assets;
	private AssetsMenuBar assetsMenuBar;
	private Console console;
	private AssetsStore assetsStore = new AssetsStore();
	
	public static int defaultFlags;
	
	public GUI(Core core) {
		this.core = core;
		
		//window_flags |= ImGuiWindowFlags_NoTitleBar;
		defaultFlags |= ImGuiWindowFlags.NoMove;
		defaultFlags |= ImGuiWindowFlags.NoCollapse;
		//defaultFlags |= ImGuiWindowFlags.NoResize;
		//window_flags |= ImGuiWindowFlags_NoDecoration;
	}
	
	public void init() {
		menuBar = new MenuBar(core);
		hierachy = new Hierachy(this);
		sceneViewer = new SceneViewer(core);
		inspector = new Inspector(this);
		assetsMenuBar = new AssetsMenuBar();
		assets = new Assets(core);
		console = new Console(this);
		
		inspector.init();
		hierachy.init();
		assetsStore.init();
	}
	
	public void update() {
		sceneViewer.update();
		
		//if(ProjectSettings.isEditor) {
		//	assetsStore.update();
		//}
	}
	
	public void render() {
		sceneViewer.update();
		sceneViewer.render(); //TODO: MEMROY LEAKAGE
		hierachy.render();
		inspector.render();
		assetsMenuBar.render();
		
		if(assetsMenuBar.getSelectedMenu() == MenuType.Assets)
			assets.render();
		else if(assetsMenuBar.getSelectedMenu() == MenuType.Console)
			console.render();
			
		menuBar.render();
	}
	
	public static boolean centeredButton(String label, float sizeX, float sizeY, float alignment){
	    ImGuiStyle style = ImGui.getStyle();

	    float size = 0;
		try {
			ImVec2 pos = new ImVec2();
			ImGui.calcTextSize(pos, label);
			size = pos.x + style.getFramePaddingX() * 2.0f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	    float avail = ImGui.getContentRegionAvail().x;

	    float off = (avail - size) * alignment;
	    if (off > 0.0f)
	        ImGui.setCursorPosX(ImGui.getCursorPosX() + off);

	    return ImGui.button(label, sizeX, sizeY);
	}
	
	public void drawFloatFiled(float value, float padding, float x, float y, float z, float width, float height, float lineWidth, float lineHeight, Color backgroundColor, Color lineColor) {
		if(UI.drawButtonWithOutline(x + padding, y, z, width, height, lineWidth, lineHeight, "" + value, lineWidth * 2, 0, 0, 0.5f, 0.5f, Color.black, backgroundColor, lineColor)
				&& Input.isMouseButtonDown(KeyCode.Mouse0)) {
			
		}
	}
	
	public void drawFieldVector2f(Vector2f value, float padding, float x, float y, float z, float width, float height, float lineWidth, float lineHeight, Color backgroundColor, Color lineColor) {
		UI.drawButtonWithOutline(x, y, z, width, height, lineWidth, lineHeight, "" + value.getX(), lineWidth * 2, 0, 0, 0.5f, 0.5f, Color.black, backgroundColor, lineColor);
		UI.drawButtonWithOutline(x + padding, y, z, width, height, lineWidth, lineHeight, "" + value.getY(), lineWidth * 2, 0, 0, 0.5f, 0.5f, Color.black, backgroundColor, lineColor);
	}
	
	public static boolean drawFieldVector3f(Vector3f value, float padding, float x, float y, float z, float width, float height, float lineWidth, float lineHeight, Color backgroundColor, Color lineColor) {
		if(UI.drawButtonWithOutline(x, y, z, width, height, lineWidth, lineHeight, "" + round(value.x), lineWidth * 2, 0, -0.1f, 0.5f, 0.5f, Color.black, backgroundColor, lineColor)
				&& Input.isMouseButtonDown(KeyCode.Mouse0)) {
			return true;
		}
		
		if(UI.drawButtonWithOutline(x + padding, y, z, width, height, lineWidth, lineHeight, "" + round(value.y), lineWidth * 2, 0, -0.1f, 0.5f, 0.5f, Color.black, backgroundColor, lineColor)
				&& Input.isMouseButtonDown(KeyCode.Mouse0)) {
			return true;
		}
		
		if(UI.drawButtonWithOutline(x + (padding * 2), y, z, width, height, lineWidth, lineHeight, "" + round(value.z), lineWidth * 2, 0, -0.1f, 0.5f, 0.5f, Color.black, backgroundColor, lineColor)
				&& Input.isMouseButtonDown(KeyCode.Mouse0)) {
			return true;
		}
		
		return false;
	}
	
	private static double round(float value) {
		return Math.round(value * 100.0) / 100.0;
	}
	
	public Core getCore() {
		return core;
	}
	
	public MenuBar getMenuBar() {
		return menuBar;
	}
	
	public Hierachy getHierachy() {
		return hierachy;
	}
	
	public SceneViewer getSceneViewer() {
		return sceneViewer;
	}
	
	public Inspector getInspector() {
		return inspector;
	}
	
	public Assets getAssets() {
		return assets;
	}
	
	public AssetsMenuBar getAssetsMenuBar() {
		return assetsMenuBar;
	}
	
	public Console getConsole() {
		return console;
	}
	
	public AssetsStore getAssetsStore() {
		return assetsStore;
	}
}
