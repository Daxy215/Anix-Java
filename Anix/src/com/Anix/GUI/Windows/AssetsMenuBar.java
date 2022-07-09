package com.Anix.GUI.Windows;

import com.Anix.GUI.GUI;
import com.Anix.IO.Application;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

public class AssetsMenuBar {
	public static enum MenuType {
		Assets, Console
	}
	
	private int startX = 0, startY = 0;	
	private int width = 0, height = 25;
	
	private MenuType selectedMenu = MenuType.Assets;
	
	public void render() {
		startY = Application.getHeight();
		width = Application.getFullWidth();
		
		ImGui.setNextWindowPos(startX, startY);
		ImGui.setNextWindowSize(Application.getFullWidth(), height);
		
		ImGui.begin("##AssetsMenuBar", GUI.defaultFlags | ImGuiWindowFlags.NoDecoration);
		
		ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY() - 3.5f);
		
		for(int i = 0; i < MenuType.values().length; i++) {
			if(selectedMenu.equals(MenuType.values()[i])) {
				ImGui.pushStyleColor(ImGuiCol.Button, 0.95f, 0.21f, 0.32f, 255);
			}
			
			if(ImGui.button(MenuType.values()[i].name())) {
				if(selectedMenu.equals(MenuType.values()[i]))
					ImGui.popStyleColor();
				
				selectedMenu = MenuType.values()[i];
				
				continue;
			}
			
			if(selectedMenu.equals(MenuType.values()[i])) {
				ImGui.popStyleColor();
			}
			
			ImGui.sameLine();
		}
		
		ImGui.end();
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
