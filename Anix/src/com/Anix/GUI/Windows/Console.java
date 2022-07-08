package com.Anix.GUI.Windows;

import java.util.HashMap;
import java.util.Map;

import com.Anix.GUI.GUI;
import com.Anix.GUI.UI;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.Math.Color;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

public final class Console {
	private enum LogLevel {
		None, Warning, Error
	}
	
	static final class Message {
		String message;
		int counter;
		
		public Message(String message) {
			this.message = message;
		}
	}
	
	private int startX = 0, startY = 0;
	private int width = 0, height = 250;
	private int scrollY = 0;
	private final float lineWidth = 1f, lineHeight = 1f;
	
	private GUI gui;
	
	private static Map<Message, LogLevel> messages = new HashMap<Message, LogLevel>();
	
	public Console(GUI gui) {
		this.gui = gui;
	}
	
	public void update() {
		startY = Application.getFullHeight() - (gui.getAssets().getHeight());
		width = Application.getFullWidth();
		
		scrollY += Input.getScrollY() * 4;
		
		//Panel - Tool bar
		UI.drawButtonWithOutline(startX, startY, 0.4f, width, height, lineWidth, lineHeight, Color.gray, Color.black);
		
		if(true) {
			int textHeight =  UI.getFontMatrics().getHeight();
			Object[] logs = messages.keySet().toArray();
			
			for(int i = logs.length - 1; i >= 0; i--) {
				Message message = (Message)logs[i];
				String msg = message.message;
				LogLevel logLevel = messages.get(message);
				
				Color color = null;
				
				if(logLevel == LogLevel.None)
					color = Color.black;
				else if(logLevel == LogLevel.Warning)
					color = Color.yellow;
				else if(logLevel == LogLevel.Error)
					color = Color.darkRed;
				
				UI.drawString(msg + (message.counter > 0 ? " (" + message.counter + ")" : ""),
						startX, startY + /*(i * 25) + (i * 5) +*/ (i * textHeight) + textHeight + scrollY, 0.38f, 0.5f, 0.5f, color);
				UI.drawButtonWithOutline(startX, startY + /*(i * 25) + (i * 5) +*/ (i * textHeight) + textHeight + scrollY, 0.39f, width, textHeight,
						lineWidth, lineHeight, Color.silver, Color.black);
			}
		}
	}
	
	public void render() {
		startY = Application.getHeight();
		
		ImGui.setNextWindowPos(startX, startY+25);
		ImGui.setNextWindowSize(Application.getFullWidth(), height-50);
		
		ImGui.begin("##", GUI.defaultFlags | ImGuiWindowFlags.NoDecoration);
		
		Object[] logs = messages.keySet().toArray();
		
		for(int i = logs.length - 1; i >= 0; i--) {
			Message message = (Message)logs[i];
			String msg = message.message;
			LogLevel logLevel = messages.get(message);
			
			Color color = null;
			
			if(logLevel == LogLevel.None)
				color = Color.black;
			else if(logLevel == LogLevel.Warning)
				color = Color.black;
			else if(logLevel == LogLevel.Error)
				color = Color.black;
			
			ImGui.pushStyleColor(ImGuiCol.Button, color.r, color.g, color.b, 255);
			ImGui.button(msg + (message.counter > 0 ? " (" + message.counter + ")" : ""));
			ImGui.popStyleColor();
		}
		
		ImGui.end();
		
		/*if(Input.isKey(KeyCode.H)) {
			RandomString r = new RandomString(MathD.getRandomNumberBetweenI(500, 1000));
			
			switch(MathD.getRandomNumberBetweenI(0, 3)) {
			case 0:
				Log(r.nextString());
				break;
			case 1:
				LogWar(r.nextString());
				break;
			case 2:
				LogErr(r.nextString());
				break;
			default:
				System.err.println("bruh");
				break;
			}
		}*/
	}
	
	public void clear() {
		messages.clear();
	}
	
	public static void Log(String message) {
		Message msg = getMessage(message);
		
		System.out.println(message);
		
		if(msg == null)
			msg = new Message(message);
		else {
		//if(messages.containsKey(msg)) {
			msg.counter++;
			
			return;
		}
				
		messages.put(msg, LogLevel.None);
	}
	
	public static void LogWar(String message) {
		Message msg = getMessage(message);
		
		System.out.println(message);
		
		if(msg == null)
			msg = new Message(message);
		else {
		//if(messages.containsKey(msg)) {
			msg.counter++;
			
			return;
		}
				
		messages.put(msg, LogLevel.Warning);
	}
	
	public static void LogErr(String message) {
		Message msg = getMessage(message);
		
		System.err.println(message);
		
		if(msg == null)
			msg = new Message(message);
		else {
		//if(messages.containsKey(msg)) {
			msg.counter++;
			
			return;
		}
		
		messages.put(msg, LogLevel.Error);
	}
	
	private static Message getMessage(String msg) {
		Object[] logs = messages.keySet().toArray();
		
		for(int i = 0; i < logs.length; i++) {
			Message messg = (Message)logs[i];
			
			if(messg.message.equals(msg)) {
				return (Message)logs[i];
			}
		}
		
		return null;
	}
}
