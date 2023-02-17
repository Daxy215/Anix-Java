package com.Anix.GUI;

import static java.lang.Math.round;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.stb.STBImageResize.STBIR_ALPHA_CHANNEL_NONE;
import static org.lwjgl.stb.STBImageResize.STBIR_COLORSPACE_SRGB;
import static org.lwjgl.stb.STBImageResize.STBIR_EDGE_CLAMP;
import static org.lwjgl.stb.STBImageResize.STBIR_FILTER_MITCHELL;
import static org.lwjgl.stb.STBImageResize.STBIR_FLAG_ALPHA_PREMULTIPLIED;
import static org.lwjgl.stb.STBImageResize.stbir_resize_uint8_generic;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.awt.Font;
import java.awt.FontMetrics;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryStack;

import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.IO.ProjectSettings;
import com.Anix.IO.ResourceLoader;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

public final class UI {
	/*public static class Button {
		private boolean isHovering, isClicked, isRightClicked;

		public Button(boolean isHovering, boolean isClicked, boolean isRightClicked) {
			this.isHovering = isHovering;
			this.isClicked = isClicked;
			this.isRightClicked = isRightClicked;
		}

		public boolean isHovering() {
			return isHovering;
		}

		public boolean isClicked() {
			return isClicked;
		}
		
		public boolean isClickedDown() {
			return isRightClicked;
		}
	}*/
	
	public static class Popup {
		private String ID;
		
		/*
		 * Menus.length * size.y.<br>
		 * Used to detect whether the mouse is hovering over the popup or not.
		 */
		private float height = -1;
		private byte lastPopup = -1, generated = 0;
		
		private Vector2f size;
		private Vector3f position;
		private Color color, textColor;
		
		private String[] menus;
		private Consumer<String> func;
		
		private List<Popup> supPopups = new ArrayList<Popup>();
		
		public Popup(String ID, Vector2f size, Vector3f position, Color color, Color textColor, String[] menus, Consumer<String> func) {
			this.ID = ID;
			this.size = size;
			this.position = position;
			this.color = color;
			this.textColor = textColor;
			this.menus = menus;
			this.func = func;
			
			height = (menus.length * size.y);
		}
		
		public void update() {
			renderPopup(this);
		}
		
		public void renderPopup(Popup popup) {
			boolean isHovering = drawButton(position.x, position.y, size.x * (supPopups.isEmpty() ? 1 : 2), size.y * menus.length);
			
			if(isHovering) {
				for(byte i = 0; i < popup.menus.length; i++) {
					boolean isHoveringP = drawButtonWithOutline(popup.position.x, popup.position.y + (i * size.y), popup.position.z, popup.size.x, popup.size.y, 1, 1, popup.menus[i].contains("<") ? popup.menus[i].split("<")[0] : popup.menus[i], 0, 0, -0.1f, 0.5f, 0.5f, popup.textColor, popup.color, Color.black);
					
					if(isHoveringP) {
						if(popup.menus[i].contains("<")) {
							if(popup.lastPopup != -1 && popup.lastPopup != i && popup.menus[popup.lastPopup].contains("<")) {//Is on another parent
								popup.generated = 0;
								popup.supPopups.clear();
							}
							
							popup.lastPopup = i;
							
							if(popup.generated == 0) {
								String[] subMenu = popup.menus[i].split("<")[1].split(";");
								subMenu[subMenu.length - 1] = subMenu[subMenu.length - 1].split(">")[0];
								
								popup.supPopups.add(new Popup(popup.ID + i, popup.size, new Vector3f(popup.position.x + popup.size.x, popup.position.y, popup.position.z), popup.color, popup.textColor, subMenu, popup.func));
								popup.generated = 1;
							}
						}
					}
					
					if(Input.isMouseButtonDown(KeyCode.Mouse0)) {
						popup.func.accept(popup.menus[i]);
						
						popups.remove(this);
						
						break;
					}
				}
				
				for(int i = 0; i < popup.supPopups.size(); i++) {
					renderPopup(popup.supPopups.get(i));
				}
			} else {
				popups.remove(this);
			}
		}

		public String getID() {
			return ID;
		}

		public float getHeight() {
			return height;
		}

		public Vector2f getSize() {
			return size;
		}

		public Vector3f getPosition() {
			return position;
		}

		public Color getColor() {
			return color;
		}

		public String[] getMenus() {
			return menus;
		}

		public Consumer<String> getFunc() {
			return func;
		}
	}
	
	public static class Toggle {
		private boolean value, isHovering;

		public Toggle(boolean value, boolean isHovering) {
			this.value = value;
			this.isHovering = isHovering;
		}

		public boolean getValue() {
			return value;
		}

		public boolean isHovering() {
			return isHovering;
		}
	}
	
	public static final class DropDown {
		public class DropDownClickEvent {
			private String id, buttonId;

			public DropDownClickEvent(String id, String buttonId) {
				this.id = id;
				this.buttonId = buttonId;
			}

			public String getId() {
				return id;
			}

			public String getButtonId() {
				return buttonId;
			}
		}

		public class DropDownEvent {
			private String id;

			private String buttonId;
			private DropDown dropDown;

			public DropDownEvent(String id, String buttonId, DropDown dropDown) {
				this.id = id;
				this.buttonId = buttonId;
				this.dropDown = dropDown;
			}

			public String getId() {
				return id;
			}

			public String getButtonId() {
				return buttonId;
			}

			public DropDown getDropDown() {
				return dropDown;
			}
		}

		public String id;
		private boolean toggleValue;

		private Vector3f position, toggleOffset;
		private Vector2f size, toggleSize;
		private Color textColor;

		private Texture toggleOn, toggleOff;

		public String[] children;
		private Consumer<DropDownClickEvent> func;
		private Consumer<DropDownEvent> onDraw;

		public DropDown(String id, Vector3f position, Vector3f toggleOffset, Vector2f size, Vector2f toggleSize, 
				Color textColor, Texture toggleOn, Texture toggleOff, String[] children, Consumer<DropDownClickEvent> func, Consumer<DropDownEvent> onDraw) {
			this.id = id;
			this.position = position;
			this.toggleOffset = toggleOffset;
			this.size = size;
			this.toggleSize = toggleSize;
			this.textColor = textColor;
			this.toggleOn = toggleOn;
			this.toggleOff = toggleOff;
			this.children = children;
			this.func = func;
			this.onDraw = onDraw;
		}

		public void update() {
			toggleValue = toggle(toggleOff, toggleOn, position.x + toggleOffset.x, position.y + toggleOffset.y, position.z + toggleOffset.z, toggleSize.x, toggleSize.y, toggleValue).value;

			if(toggleValue) {
				for(int i = 0; i < children.length; i++) {
					if(onDraw == null) {
						Color btnColor = new Color(Float.parseFloat(children[i].split(";")[1].split(" ")[0]), Float.parseFloat(children[i].split(";")[1].split(" ")[1]),
								Float.parseFloat(children[i].split(";")[1].split(" ")[2]));

						boolean isHovering = drawButtonWithOutline(position.x, position.y + (i * size.y) + size.y, position.z, size.x, size.y, 1, 1, children[i].split(";")[0], 0, 0, -0.1f, 0.5f, 0.5f, textColor, btnColor, Color.black);

						if(isHovering && Input.isMouseButtonDown(KeyCode.Mouse0)) {
							func.accept(new DropDownClickEvent(id, children[i]));
						}
					} else {
						onDraw.accept(new DropDownEvent(id, children[i], this));
					}
				}
			}
		}
	}
	
	//Textures
	/**
	 * This texture will be used when a popup menu has a child.<br>
	 * It'll be displayed at the end.
	 */
	//public Texture popupSup;
	
	private static FontMetrics fontMatrices;
	public static Font defaultFont;
	
	public static List<Popup> popups = new ArrayList<Popup>();
	
	public static Map<TrueTypeFont, Color> trueTypeFonts = new HashMap<TrueTypeFont, Color>();
	
	public static void init(String defaultFontName, int style, int size) {
		defaultFont = new Font(defaultFontName, style, size);
		fontMatrices = new FontMetrics(defaultFont) {private static final long serialVersionUID = 1L;};
	}
	
	public static void update() {
		for(int i = popups.size() - 1; i >= 0; i--) {
			Popup popup = popups.get(i);

			if(!(Input.getMouseX() >= popup.getPosition().x && Input.getMouseX() <= popup.getPosition().x + popup.getSize().x 
					&& Input.getMouseY() >= popup.getPosition().y && Input.getMouseY() <= popup.getPosition().y + popup.getHeight())) {
				if(popup.supPopups.isEmpty()) {
					popups.remove(i);

					i--;

					continue;
				}
			}

			popup.update();
		}
	}
	
	public static void addPopup(float z, Vector2f size, String ID, Color color, Color textColor, String[] menus, Consumer<String> func) {
		int index = -1;
		
		for(int i = popups.size() - 1; i >= 0; i--) {
			if(popups.get(i).getID().equals(ID)) {
				index = i;
			}
		}
		
		if(index == -1) {
			popups.add(new Popup(ID, size, new Vector3f((float)Input.getMouseX() - 2, (float)Input.getMouseY() - 2, z), color, textColor, menus, func));
		} else {
			popups.remove(index);
			popups.add(new Popup(ID, size, new Vector3f((float)Input.getMouseX() - 2, (float)Input.getMouseY() - 2, z), color, textColor, menus, func));
		}
	}
	
	public static Toggle toggle(Texture off, Texture on, float x, float y, float z, float width, float height, boolean value) {
		Texture texture = value ? on : off;
		
		boolean isHovering = drawButton(texture, x, y, z, width, height);

		if(isHovering && Input.isMouseButtonDown(KeyCode.Mouse0)) {
			return new Toggle(!value, isHovering);
		}

		return new Toggle(value, isHovering);
	}
	
	public static Toggle toggle(Texture off, Texture on, float x, float y, float z, float width, float height, boolean value, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor) {
		Texture texture = value ? on : off;

		boolean isHovering = drawButton(texture, x, y, z, width, height);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);

		if(isHovering && Input.isMouseButtonDown(KeyCode.Mouse0)) {
			return new Toggle(!value, isHovering);
		}

		return new Toggle(value, isHovering);
	}
	
	public static Toggle toggle(Texture off, Texture on, float x, float y, float z, float width, float height, boolean value, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Vector2f offset, Color backgroundColor) {
		Texture texture = value ? on : off;

		drawButton(x, y, z, width + offset.x, height + offset.y, backgroundColor);
		boolean isHovering = drawButton(x, y, z, width, height, text, textOffsetX, textOffsetY, textOffsetZ, textSizeX, textSizeY, textColor, texture);

		if(isHovering && Input.isMouseButtonDown(KeyCode.Mouse0)) {
			return new Toggle(!value, isHovering);
		}

		return new Toggle(value, isHovering);
	}
	
	public static Toggle toggle(Texture off, Texture on, float x, float y, float z, float width, float height, boolean value, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Vector2f offset, Vector3f backgroundOffset, Color backgroundColor) {
		Texture texture = value ? on : off;

		drawButton(x + backgroundOffset.x, y + backgroundOffset.y, z + backgroundOffset.z, width + offset.x, height + offset.y, backgroundColor);
		boolean isHovering = drawButton(x, y, z, width, height, text, textOffsetX, textOffsetY, textOffsetZ, textSizeX, textSizeY, textColor, texture);
		
		if(isHovering && Input.isMouseButtonDown(KeyCode.Mouse0)) {
			return new Toggle(!value, isHovering);
		}

		return new Toggle(value, isHovering);
	}
	
	public void addDropDown() {

	}
	
	public static boolean drawButton(float x, float y, float width, float height, Color color) {
		drawBox(x, y, width, height, color);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(String text, float width, float height, float x, float y, float sizeX, float sizeY, Color textColor, Color boxColor) {
		drawBox(x, y, width, height, boxColor);
		drawString(text, x, y, 0, sizeX, sizeY, textColor);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float width, float height, String text, float textOffsetX, float textOffsetY, float textSizeX, float textSizeY, Color textColor) {
		drawString(text, x + textOffsetX, y + textOffsetY, textSizeX, textSizeY, textColor);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float z, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor) {
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(String text, float width, float height, float x, float y, float sizeX, float sizeY, Color textColor) {
		drawString(text, x, y, 0, sizeX, sizeY, textColor);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(String text, float width, float height, float x, float y, float z, float sizeX, float sizeY, Color textColor) {
		drawString(text, x, y, z, sizeX, sizeY, textColor);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float z, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Texture texture) {
		drawImage(texture.getTextureID(), x, y, z, width, height);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float z, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color, Texture texture) {
		drawImage(texture.getTextureID(), color, x, y, z, width, height);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float z, float width, float height, Texture texture) {
		drawImage(texture.getTextureID(), x, y, z, width, height);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color) {
		drawBox(x, y, width, height, color);
		drawString(text, x + textOffsetX, y + textOffsetY, textSizeX, textSizeY, textColor);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float z, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color) {
		drawBox(x, y, z, width, height, color);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float z, float width, float height, Color color) {
		drawBox(x, y, z, width, height, color);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public static boolean drawButtonWithOutline(float x, float y, float z, float width, float height, float lineWidth, float lineHeight, Color color, Color lineColor) {
		drawBoxWithOutline(x, y, z, width, height, lineWidth, lineHeight, color, lineColor);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public static boolean drawButtonWithOutline(float x, float y, float z, float width, float height, float lineWidth, float lineHeight, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color, Color lineColor) {
		drawBoxWithOutline(x, y, z, width, height, lineWidth, lineHeight, color, lineColor);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);
		
		text = null;
		color = null;
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public static boolean drawButtonWithOutlineWithDepth(float x, float y, float z, float width, float height, float lineWidth, float lineHeight, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color, Color lineColor) {
		GL13.glDepthRange(0, 1);
		drawBoxWithOutline(x, y, z, width, height, lineWidth, lineHeight, color, lineColor);
		GL13.glDepthRange(0, 0.01);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public static boolean drawRoundedButtonWithOutLine(float x, float y, float z, float width, float height, float radius, float lineWidth, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color, Color lineColor) {
		boolean returnValue = drawRoundedButton(x, y, z, width, height, radius, lineWidth, color, lineColor);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);

		return returnValue;
	}

	public static boolean drawRoundedButton(float x, float y, float z, float size_x, float size_y, float radius, float lineWidth, Color color, Color lineColor) {
		double PI = Math.PI;
		glPushMatrix();

		glColor3f(color.getRed(), color.getGreen(), color.getBlue());
		glTranslatef(0, 0, z);

		glBegin(GL13.GL_POLYGON);
		
		// top-left corner
		drawGLRoundedCorner(x, y + radius, 3 * PI / 2, PI / 2, radius);
		
		// top-right
		drawGLRoundedCorner(x + size_x - radius, y, 0.0, PI / 2, radius);

		// bottom-right
		drawGLRoundedCorner(x + size_x, y + size_y - radius, PI / 2, PI / 2, radius);

		// bottom-left
		drawGLRoundedCorner(x + radius, y + size_y, PI, PI / 2, radius);

		glEnd();
		
		glLoadIdentity();
		glPopMatrix();

		if(lineWidth > 0) {
			x += 1;
			z -= 0.1f;

			//Middle left - Top Left
			UI.drawline(x + radius, y, z, x - (radius * 0.2f), y + radius, z, lineColor, lineWidth);

			//Middle left
			UI.drawline(x - (radius * 0.2f), y + radius, z, x - (radius * 0.2f), y + (size_y - radius), z, lineColor, lineWidth);

			//Top Middle
			UI.drawline(x + radius, y, z, x + size_x - radius, y, z, lineColor, lineWidth);

			//Middle left - bottom left
			UI.drawline(x + radius, y + size_y, z, x - (radius * 0.2f), y - radius + size_y, z, lineColor, lineWidth);

			//Bottom Middle
			UI.drawline(x + radius, y + size_y, z, x + size_x - radius, y + size_y, z, lineColor, lineWidth);
			
			//Bottom right - Middle
			UI.drawline(x - radius + size_x, y + size_y, z, x - (radius * 0.2f) + size_x, y - radius + size_y, z, lineColor, lineWidth);

			//Middle right
			UI.drawline(x - (radius * 0.2f) + size_x, y + radius, z, x - (radius * 0.2f) + size_x, y + (size_y - radius), z, lineColor, lineWidth);

			//Middle right - Top right
			UI.drawline(x - radius + size_x, y, z, x - (radius * 0.2f) + size_x, y + radius, z, lineColor, lineWidth);
		}

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + size_x && mouseY >= y && mouseY <= y + size_y;
	}
	
	private static void drawGLRoundedCorner(float x, float y, double sa, double arc, float r) {
		int N_ROUNDING_PIECES = 4;
		double PI = Math.PI;

		// centre of the arc, for clockwise sense
		float cent_x = (float) (x + r * Math.cos(sa + PI / 2));
		float cent_y = (float) (y + r * Math.sin(sa + PI / 2));

		// build up piecemeal including end of the arc
		int n = (int) Math.ceil(N_ROUNDING_PIECES * arc / PI * 2);
		for (int i = 0; i <= n; i++) {
			double ang = sa + arc * (double)i  / (double)n;

			// compute the next point
			float next_x = (float) (cent_x + r * Math.sin(ang));
			float next_y = (float) (cent_y - r * Math.cos(ang));
			GL13.glVertex2f(next_x, next_y);
		}
	}

	public static boolean drawButton(float x, float y, float width, float height) {
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(Texture texture, float x, float y, float z, float width, float height) {
		drawImage(texture.getTextureID(), x, y, z, width, height);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(int textureID, float x, float y, float z, float width, float height) {
		drawImage(textureID, x, y, z, width, height);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static void drawString(String text, float x, float y, float sizeX, float sizeY, Color color) {
		if(defaultFont == null) {
			System.err.println("[ERROR] You must call the initializion method first!");

			return;
		}

		//glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_TEXTURE_2D);

		if(color == null)
			color = Color.white;

		boolean found = false;

		for(Map.Entry<TrueTypeFont, Color> entry : trueTypeFonts.entrySet()) {
			if(entry.getValue().equals(color)) {
				entry.getKey().drawString(x, y, text, sizeX, sizeY);
				
				found = true;

				break;
			}
		}

		if(!found) {
			trueTypeFonts.put(new TrueTypeFont(defaultFont, true, color), color);
			drawString(text, x, y, sizeX, sizeY, color);
		}

		//glDisable(GL_COLOR_MATERIAL);
		glDisable(GL_TEXTURE_2D);
	}

	public static TrueTypeFont drawString(String text, float x, float y, float z, float sizeX, float sizeY, Color color) {
		if(defaultFont == null) {
			System.err.println("[ERROR] You must call the initializion method first!");

			return null;
		}

		if(text == null) {
			System.err.println("[ERROR] You cannot draw empty text! " + x + " " + y);

			return null;
		}

		//glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_TEXTURE_2D);

		if(color == null)
			color = Color.white;

		boolean found = false;

		for(Map.Entry<TrueTypeFont, Color> entry : trueTypeFonts.entrySet()) {
			if(entry.getValue().equals(color)) {
				entry.getKey().drawString(x, y, z, text, sizeX, sizeY);
				
				found = true;

				return entry.getKey();
			}
		}

		if(!found) {
			trueTypeFonts.put(new TrueTypeFont(defaultFont, true, color), color);
			return drawString(text, x, y, z, sizeX, sizeY, color);
		}

		//glDisable(GL_COLOR_MATERIAL);
		glDisable(GL_TEXTURE_2D);

		return null;
	}

	public static TrueTypeFont drawString(String text, float x, float y, float z, int format, float sizeX, float sizeY, Color color) {
		if(defaultFont == null) {
			System.err.println("[ERROR] You must call the initializion method first!");

			return null;
		}

		if(text == null) {
			System.err.println("[ERROR] You cannot draw empty text! " + x + " " + y);

			return null;
		}

		//glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_TEXTURE_2D);

		if(color == null)
			color = Color.white;

		boolean found = false;

		for(Map.Entry<TrueTypeFont, Color> entry : trueTypeFonts.entrySet()) {
			if(entry.getValue().equals(color)) {
				entry.getKey().drawString(x, y, z, format, text, sizeX, sizeY);

				found = true;

				return entry.getKey();
			}
		}

		if(!found) {
			trueTypeFonts.put(new TrueTypeFont(defaultFont, true, color), color);
			drawString(text, x, y, z, format, sizeX, sizeY, color);
		}

		//glDisable(GL_COLOR_MATERIAL);
		glDisable(GL_TEXTURE_2D);

		return null;
	}

	public static void drawString(String text, float x, float y, float sizeX, float sizeY, int format, Color color) {
		if(defaultFont == null) {
			System.err.println("[ERROR] You must call the initializion method first!");

			return;
		}

		//glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_TEXTURE_2D);

		if(color == null)
			color = Color.white;

		boolean found = false;

		for(Map.Entry<TrueTypeFont, Color> entry : trueTypeFonts.entrySet()) {
			if(entry.getValue().equals(color)) {
				entry.getKey().drawString(x, y, text, sizeX, sizeY, format);
				
				found = true;
				
				break;
			}
		}

		if(!found) {
			trueTypeFonts.put(new TrueTypeFont(defaultFont, true, color), color);
			drawString(text, x, y, sizeX, sizeY, format, color);
		}

		//glDisable(GL_COLOR_MATERIAL);
		glDisable(GL_TEXTURE_2D);
	}

	public static void drawBox(float x, float y, float width, float height, Color color) {
		glBindTexture(GL_TEXTURE_2D, 0);
		//glDisable(GL_TEXTURE_2D);

		//float[] currentColor = new float[4];
		//GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, currentColor);

		glPushMatrix();
		glTranslatef(x, y, -1);

		glBegin(GL_QUADS);
		glColor3f(color.getRed(), color.getGreen(), color.getBlue());

		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();
		glPopMatrix();

		//GL11.glColor4f(currentColor[0], currentColor[1], currentColor[2], currentColor[3]);

		glLoadIdentity();
	}

	public static void drawBox(float x, float y,  float z, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color) {
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);

		//float[] currentColor = new float[4];
		//GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, currentColor);

		glBindTexture(GL_TEXTURE_2D, 0);
		glPushMatrix();
		glTranslatef(x, y, z);

		glBegin(GL_QUADS);
		glColor3f(color.getRed(), color.getGreen(), color.getBlue());

		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();
		glPopMatrix();

		//GL11.glColor4f(currentColor[0], currentColor[1], currentColor[2], currentColor[3]);

		glBindTexture(GL_TEXTURE_2D, 0);
		
		glLoadIdentity();
	}

	public static void drawBox(float x, float y,  float z, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, int r, int g, int b) {
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);

		//float[] currentColor = new float[4];
		//GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, currentColor);

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glPushMatrix();
		glTranslatef(x, y, z);

		glBegin(GL_QUADS);
		glColor3f(r, g, b);

		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();
		glPopMatrix();

		//GL11.glColor4f(currentColor[0], currentColor[1], currentColor[2], currentColor[3]);

		glLoadIdentity();
	}

	public static void drawBox(float x, float y, float z, float width, float height, Color color) {
		//float[] currentColor = new float[4];
		//GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, currentColor);
		
		glPushMatrix();
		
		glColor3f(color.getRed(), color.getGreen(), color.getBlue());
		glTranslatef(x, y, z);
		
		glBegin(GL_QUADS);
		
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);
		
		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);
		
		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);
		
		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();
		
		//GL11.glColor4f(currentColor[0], currentColor[1], currentColor[2], currentColor[3]);
		glPopMatrix();
		glLoadIdentity();
	}

	public static void drawBoxWithOutline(float x, float y, float z, float width, float height, float lineWidth, float lineHeight, Color color, Color outlineColor) {
		//Top Left to right
		drawBox(x, y, z, width, lineHeight, outlineColor);
		
		//Top left to bottom left
		drawBox(x, y, z, lineWidth, height, outlineColor);
		
		//Top right to bottom right
		drawBox(x + width - lineWidth, y, z, lineWidth, height, outlineColor);
		
		//Bottom left to right
		drawBox(x, y + height - lineHeight, z, width, lineHeight, outlineColor);
		
		//Actual box.
		drawBox(x, y, z, width, height, color);
	}
	
	public static void drawImage(Texture texture, float x, float y, float z) {
		glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
		glEnable(GL_TEXTURE_2D);
		
		glTranslatef(x, y, z);
		
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(texture.getWidth(), 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(texture.getWidth(), texture.getHeight());

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, texture.getHeight());
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity();
	}
	
	public static void drawImage(Texture texture, float x, float y, float z, float scaler) {
		glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
		glEnable(GL_TEXTURE_2D);

		glTranslatef(x, y, z);

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(texture.getWidth()*scaler, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(texture.getWidth()*scaler, texture.getHeight()*scaler);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, texture.getHeight()*scaler);
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity();
	}
	
	public static void drawImage(int texture, float x, float y, float width, float height) {
		glBindTexture(GL_TEXTURE_2D, texture);
		glEnable(GL_TEXTURE_2D);

		glTranslatef(x, y, -1);

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity();
	}

	public static void drawImage(int texture, float x, float y, float z, float width, float height) {
		glBindTexture(GL_TEXTURE_2D, texture);
		glEnable(GL_TEXTURE_2D);

		glTranslatef(x, y, z);

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity();
	}

	public static void drawImage(int texture, Color color, float x, float y, float z, float width, float height) {
		//float[] currentColor = new float[4];
		//GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, currentColor);

		glColor3f(color.getRed(), color.getGreen(), color.getBlue());
		glBindTexture(GL_TEXTURE_2D, texture);
		
		glPushMatrix();
		glTranslatef(x, y, z);

		glBegin(GL_QUADS);

		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();
		glPopMatrix();

		//GL11.glColor4f(currentColor[0], currentColor[1], currentColor[2], currentColor[3]);

		glBindTexture(GL_TEXTURE_2D, 0);

		glLoadIdentity();
	}

	public static void drawImageInverted(int texture, float x, float y, float width, float height) {
		glBindTexture(GL_TEXTURE_2D, texture);
		glEnable(GL_TEXTURE_2D);

		glTranslatef(x, y, -1);

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, height);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, height);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, 0);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, 0);
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity();
	}

	public static void drawImageInverted(int texture, float x, float y, float z, float width, float height) {
		glBindTexture(GL_TEXTURE_2D, texture);
		glEnable(GL_TEXTURE_2D);

		glTranslatef(x, y, z);
		glColor3f(1, 1, 1);

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, height);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, height);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, 0);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, 0);
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity();
	}

	public static void drawSphere(float positionX, float positionY, double radius, Color color, int lats, int longs) {
		int i, j;
		for(i = 0; i <= lats; i++) {
			double lat0 = Math.PI * (-0.5 + (double) (i - 1) / lats);
			double z0  = Math.sin(lat0);
			double zr0 =  Math.cos(lat0);

			double lat1 = Math.PI * (-0.5 + (double) i / lats);
			double z1 = Math.sin(lat1);
			double zr1 = Math.cos(lat1);
			GL11.glPushMatrix();
			GL11.glTranslatef(positionX, positionY, 0);

			GL11.glBegin(GL11.GL_QUAD_STRIP);
			for(j = 0; j <= longs; j++) {
				double lng = 2 * Math.PI * (double) (j - 1) / longs;
				double x = Math.cos(lng);
				double y = Math.sin(lng);

				GL11.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
				GL11.glNormal3d(x * zr0, y * zr0, z0);
				GL11.glVertex3d(radius * x * zr0, radius * y * zr0, radius * z0);
				GL11.glNormal3d(x * zr1, y * zr1, z1);
				GL11.glVertex3d(radius * x * zr1, radius * y * zr1, radius * z1);
			}

			GL11.glEnd();
			GL11.glPopMatrix();
		}
	}

	public static void drawline(float startX, float startY, float startZ, float endX, float endY, float endZ, Color color, float lineWidth) {
		GL13.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
		
		GL11.glLineWidth(lineWidth);
		
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
		GL11.glVertex3f(startX, startY, startZ);
		GL11.glVertex3f(endX, endY, endZ);
		GL11.glEnd();
		
		GL11.glPopAttrib();
		GL13.glPopMatrix();
	}

	public static boolean drawlineButton(float startX, float startY, float startZ, float endX, float endY, float endZ, Color color, float lineWidth) {
		GL11.glLineWidth(lineWidth);
		
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
		GL11.glVertex3f(startX, startY, startZ);
		GL11.glVertex3f(endX, endY, endZ);
		GL11.glEnd();
		
		float xDif = endX - startX;
		float yDif = endY - startY;
		
		return Input.getMouseX() >= startX && Input.getMouseX() <= startY + (xDif == 0 ? lineWidth : xDif)
				&& Input.getMouseY() >= startY && Input.getMouseY() <= startY + (yDif == 0 ? lineWidth : yDif);
	}

	public static Texture loadTexture(String fileName) {
		Texture returnImage = null;
		
		try {
			//InputStream is = null;
			
			//if(ProjectSettings.isEditor) {
				/*is = UI.class.getResourceAsStream(fileName);
			
				if(is == null) {
					String tempPath = "";
					
					if(!Files.exists(Paths.get(fileName))) {
						tempPath = ProjectSettings.getProjectPath() + fileName;
					} else {
						tempPath = fileName;
					}
					
					is = new FileInputStream(new File(tempPath));
				}*/
				
			    ByteBuffer image;
				
			    //Listen.. I got too lazy ;-;
			    ByteBuffer imageBuffer = null;
			    
			    try {
			    	imageBuffer = ResourceLoader.ioResourceToByteBuffer((
			    			ProjectSettings.isEditor ? "" : fileName.startsWith("/") ? "" : "/") +
			    			ProjectSettings.getProjectPath() + fileName, 8 * 1024);
			    } catch (Exception e) {
			    	try {
				    	imageBuffer = ResourceLoader.ioResourceToByteBuffer(fileName, 8 * 1024);
			    	} catch(Exception ee) {
			    		try {
			    			imageBuffer = ResourceLoader.ioResourceToByteBuffer("resources/" + fileName, 8 * 1024);
			    		} catch(Exception eee) {
			    			try {
				    			imageBuffer = ResourceLoader.ioResourceToByteBuffer("/" + fileName, 8 * 1024);
				    		} catch(Exception eeee) {
				    			try {
				    				//resources
				    				String temp = fileName.substring(9, fileName.length());
				    				
				    				imageBuffer = ResourceLoader.ioResourceToByteBuffer(temp, 8 * 1024);
				    			} catch(Exception x) {
				    				//x.printStackTrace();
				    				imageBuffer = null;
				    			}
				    		}
			    		}
			    	}
			    }
			    
			    if(imageBuffer == null) {
    				System.err.println("[ERORR] Couldn't locate a texture with the name of " + fileName);
			    	
			    	return null;
			    }
			    
				try (MemoryStack stack = stackPush()) {
			        IntBuffer w    = stack.mallocInt(1);
			        IntBuffer h    = stack.mallocInt(1);
			        IntBuffer comp = stack.mallocInt(1);
			        
			        // Use info to read image metadata without decoding the entire image.
			        // We don't need this for this demo, just testing the API.
			        if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
			        	System.err.println("[ERROR] Couldn't load an image with the name of: " + fileName);
			            //throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
			        }
			        
			        //System.out.println("Image width: " + w.get(0));
			        //System.out.println("Image height: " + h.get(0));
			        //System.out.println("Image components: " + comp.get(0));
			        //System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));
			        
			        // Decode the image
			        image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
			        
			        if (image == null) {
			            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
			        }
			        
			        returnImage = new Texture(w.get(0), h.get(0), createTexture(w.get(0), h.get(0), comp.get(0), image));
			    }
				
				//texture = TextureLoader.getTexture(fileName.split("[.]")[1], is);
				
				//is.close();
			//} else {
				/*if(!fileName.startsWith("/")) {
					String temp = "/" + fileName;
					fileName = temp;
				}
				
				try (InputStream in = UI.class.getResourceAsStream(fileName)) {
					//BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					
					texture = TextureLoader.getTexture(fileName.split("[.]")[1], in);
					
					in.close();
				}*/
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnImage;
	}
	
	private static int createTexture(int w, int h, int comp, ByteBuffer image) {
		int texID = glGenTextures();
		
		glBindTexture(GL_TEXTURE_2D, texID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST/*GL_LINEAR_MIPMAP_LINEAR*/);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		int format;
		if (comp == 3) {
			if ((w & 3) != 0) {
				glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w & 1));
			}
			format = GL_RGB;
		} else {
			premultiplyAlpha(w, h, image);
			
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			
			format = GL_RGBA;
		}
		
		glTexImage2D(GL_TEXTURE_2D, 0, format, w, h, 0, format, GL_UNSIGNED_BYTE, image);
		
		ByteBuffer input_pixels = image;
		int        input_w      = w;
		int        input_h      = h;
		int        mipmapLevel  = 0;
		while (1 < input_w || 1 < input_h) {
			int output_w = Math.max(1, input_w >> 1);
			int output_h = Math.max(1, input_h >> 1);

			ByteBuffer output_pixels = memAlloc(output_w * output_h * comp);
			stbir_resize_uint8_generic(
					input_pixels, input_w, input_h, input_w * comp,
					output_pixels, output_w, output_h, output_w * comp,
					comp, comp == 4 ? 3 : STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED,
							STBIR_EDGE_CLAMP,
							STBIR_FILTER_MITCHELL,
							STBIR_COLORSPACE_SRGB
					);
			
			if (mipmapLevel == 0) {
				stbi_image_free(image);
			} else {
				memFree(input_pixels);
			}
			
			glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, format, output_w, output_h, 0, format, GL_UNSIGNED_BYTE, output_pixels);
			
			input_pixels = output_pixels;
			input_w = output_w;
			input_h = output_h;
		}
		
		if (mipmapLevel == 0) {
			stbi_image_free(image);
		} else {
			memFree(input_pixels);
		}
		
		return texID;
	}
	
	private static void premultiplyAlpha(int w, int h, ByteBuffer image) {
        int stride = w * 4;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = y * stride + x * 4;
                
                float alpha = (image.get(i + 3) & 0xFF) / 255.0f;
                image.put(i + 0, (byte)round(((image.get(i + 0) & 0xFF) * alpha)));
                image.put(i + 1, (byte)round(((image.get(i + 1) & 0xFF) * alpha)));
                image.put(i + 2, (byte)round(((image.get(i + 2) & 0xFF) * alpha)));
            }
        }
    }
	
	public static Vector2f getScale(float width, float height, float scalerMatchWidthOrHeight) {
		Vector2f scalerReferenceResolution = new Vector2f(height, width);
		
		return new Vector2f((float)Math.pow(width/scalerReferenceResolution.x, 1f - scalerMatchWidthOrHeight),
				(float)Math.pow(height/scalerReferenceResolution.y, scalerMatchWidthOrHeight));
	}
	
	public static FontMetrics getFontMatrics() {
		return fontMatrices;
	}
}
