package com.Anix.IO;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import imgui.ImGui;

public final class Input {
	static int currentPressedKey = 0;
	
	static float mouseX, mouseY, lastMouseX, lastMouseY;
	static float scrollX, scrollY;
	
	static long lastClicked;
	
	static boolean doubleClicked;
	static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST + 1];
	static boolean[] keysLast = new boolean[GLFW.GLFW_KEY_LAST + 1];
	
	static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
	static boolean[] buttonsLast = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
	
	static boolean[] controllerButtons = new boolean[GLFW.GLFW_JOYSTICK_LAST];
	static boolean[] controllerButtonsLast = new boolean[GLFW.GLFW_JOYSTICK_LAST];
	
	GLFWKeyCallback keyboard;
	GLFWCursorPosCallback mouseMove;
	GLFWMouseButtonCallback mouseButtons;
	GLFWScrollCallback mouseScroll;
	
	public Input() {
		keyboard = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if(key < 0)
					return;
				
				keys[key] = action != GLFW.GLFW_RELEASE;
				
				if(action != GLFW.GLFW_RELEASE) {
					currentPressedKey = key;
				}
			}
		};
		
		mouseMove = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xPos, double yPos) {
				mouseX = (float)xPos;
				mouseY = (float)yPos;
			}
		};
		
		mouseButtons = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				buttons[button] = (action != GLFW.GLFW_RELEASE);
				
				if(action == GLFW.GLFW_RELEASE && System.currentTimeMillis() - lastClicked < 200) {
					doubleClicked = true;
				}
				
				if(action == GLFW.GLFW_RELEASE) {
					lastClicked = System.currentTimeMillis();
				}
			}
		};
		
		mouseScroll = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double offsetX, double offsetY) {
				scrollX += offsetX;
				scrollY += offsetY;
			}
		};
	}
	
	public static void init() {
		Thread thread = new Thread() {
			public void run() {
				while(!Application.shouldClose()) {
					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					scrollX = 0;
					scrollY = 0;
					
					lastMouseX = mouseX;
					lastMouseY = mouseY;
				}
			}
		};
		
		thread.setName("Input Thread");
		thread.start();
	}
	
	public static void update() {
		for(int i = 0; i < keys.length; i++) {
			keysLast[i] = keys[i];
		}
		
		for(int i = 0; i < buttonsLast.length; i++) {
			buttonsLast[i] = buttons[i];
		}
		
		doubleClicked = false;
	}
	
	public void destroy() {
		keyboard.free();
		mouseMove.free();
		mouseButtons.free();
		mouseScroll.free();
	}
	
	public static boolean isKey(int key) {
		if(ImGui.getIO().getWantCaptureMouse())
			return false;
		
		return keys[key];
	}
	
	public static boolean isKeyDown(int key) {
		if(ImGui.getIO().getWantCaptureMouse())
			return false;
		
		return keys[key] && !keysLast[key];
	}
	
	public static boolean isKeyUp(int key) {
		if(ImGui.getIO().getWantCaptureMouse())
			return false;
		
		return !keys[key] && keysLast[key];
	}
	
	public static boolean isMouseButton(int button) {
		if(ImGui.getIO().getWantCaptureMouse())
			return false;
		
		return buttons[button];
	}
	
	public static boolean isMouseButtonDown(int button) {
		if(ImGui.getIO().getWantCaptureMouse())
			return false;
		
		return buttons[button] && !buttonsLast[button];
	}
	
	public static boolean isMouseButtonUp(int button) {
		if(ImGui.getIO().getWantCaptureMouse())
			return false;
		
		return !buttons[button] && buttonsLast[button];
	}
	
	public static boolean doubleClicked() {
		if(ImGui.getIO().getWantCaptureMouse())
			return false;
		
		return doubleClicked;
	}
	
	public static boolean isDragging() {
		return Math.abs(mouseX - lastMouseX) > 1 || Math.abs(mouseY - lastMouseY) > 1;
		//return mouseX != lastMouseX || mouseY != lastMouseY;
	}
	
	public static int getCurrentPressedKey() {
		return currentPressedKey;
	}
	
	public static double getMouseX() {
		return mouseX;
	}
	
	public static double getMouseY() {
		return mouseY;
	}
	
	public static float getLastMouseX() {
		return lastMouseX;
	}

	public static float getLastMouseY() {
		return lastMouseY;
	}

	public static float getScrollX() {
		if(ImGui.getIO().getWantCaptureMouse())
			return 0;
		
		return scrollX;
	}

	public static float getScrollY() {
		if(ImGui.getIO().getWantCaptureMouse())
			return 0;
		
		return scrollY;
	}

	public GLFWKeyCallback getKeyboardCallback() {
		return keyboard;
	}

	public GLFWCursorPosCallback getMouseMoveCallback() {
		return mouseMove;
	}

	public GLFWMouseButtonCallback getMouseButtonsCallback() {
		return mouseButtons;
	}

	public GLFWScrollCallback getMouseScrollCallback() {
		return mouseScroll;
	}
}
