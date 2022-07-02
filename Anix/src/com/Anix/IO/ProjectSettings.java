package com.Anix.IO;

import java.io.BufferedReader;
import java.io.IOException;

import com.Anix.Main.Core;

public class ProjectSettings {
	public static enum ProjectType {
		D2, D3
	}
	
	public static String gameName = "Anix-Game";
	
	public static boolean isEditor = true;
	
	public static ProjectType projectType = ProjectType.D2;
	
	//public static Vector2i previousAppSize = new Vector2i(1280, 720);
	private static Core core;
	
	public ProjectSettings(Core core) {
		ProjectSettings.core = core;
	}
	
	public static void load(BufferedReader br, boolean editor) {
		isEditor = editor;
		
		try {
			while(br.ready()) {
				String line = br.readLine();
				
				System.err.println("Data found: " + line);
				
				if(line.startsWith("Name:")) {
					String gameName = line.split(" ")[1];
					
					ProjectSettings.gameName = gameName;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getProjectPath() {
		if(!isEditor) {
			return "Assets";
		}
		
		String fileSeparator = System.getProperty("file.separator");
		
		return System.getProperty("user.dir") + fileSeparator + core.getProjectName() + fileSeparator + "Assets";
	}
}
