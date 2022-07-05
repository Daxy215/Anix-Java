package com.Anix.GUI.Windows;

import com.Anix.GUI.GUI;
import com.Anix.GUI.UI;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Math.Color;

public class AssetsMenuBar {
	public static enum MenuType {
		Assets, Console
	}
	
	private int startX = 0, startY = 0;	
	private int width = 0, height = 25;
	private final float lineWidth = 1f, lineHeight = 1f;
	
	private GUI gui;
	
	private MenuType selectedMenu = MenuType.Assets;
	
	public AssetsMenuBar(GUI gui) {
		this.gui = gui;
	}
	
	public void update() {
		startY = Application.getFullHeight() - (gui.getAssets().getHeight());
		width = Application.getFullWidth();
		
		//Panel - Tool bar
		UI.drawButtonWithOutline(startX, startY, -0.2f, width, height, lineWidth, lineHeight, Color.silver, Color.black);
		
		if(UI.drawButton(startX + 8, startY + 2, -0.3f, 50, 20, "Assets", 0, -0.5f, -0.1f, 0.5f, 0.5f, Color.black,
				(selectedMenu == MenuType.Assets ? Color.DARK_GRAY : Color.gray))
				&& Input.isMouseButtonDown(KeyCode.Mouse0)) {
			selectedMenu = MenuType.Assets;
		}
		
		if(UI.drawButton(startX + 70, startY + 2, -0.3f, 60, 20, "Console", 0, -0.5f, -0.1f, 0.5f, 0.5f, Color.black,
				(selectedMenu == MenuType.Console ? Color.DARK_GRAY : Color.gray))
				&& Input.isMouseButtonDown(KeyCode.Mouse0)) {
			selectedMenu = MenuType.Console;
		}
	}

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public MenuType getSelectedMenu() {
		return selectedMenu;
	}
}
