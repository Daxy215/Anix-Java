package com.Anix.Behaviours;

import java.util.ArrayList;
import java.util.List;

import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
import com.Anix.IO.Time;
import com.Anix.Math.Vector2f;

public class Animator2D extends Behaviour {
	private static final long serialVersionUID = -8364310716548488358L;
	
	private static class Animation {
		private int currentIndexX, currentIndexY;
		
		private int maxWidth, maxHeight;
		
		private Texture texture;
		private Animator2D animator;
		
		public Animation() { }
		
		public Animation(Animator2D animator, Texture texture) {
			this.animator = animator;
			this.texture = texture;
		}
		
		public void update() {
			if(animator.gameObject.getMesh() == null)
				return;
			
			if(animator.animateThroughEntireTexture) {
				maxWidth = (int) (texture.getWidth() / animator.xSpilt);
				maxHeight = (int) (texture.getHeight() / animator.ySpilt);
				
				currentIndexX++;
				
				if(currentIndexX == maxWidth) {
					currentIndexX = 0;
					currentIndexY++;
				}
				
				if(currentIndexY == maxHeight) {
					if(animator.maxYSplit > 0)
						currentIndexX = 0;
					
					currentIndexY = 0;
				}
				
				animator.gameObject.getMesh().setUvs(getTextureAt(currentIndexX, currentIndexY));
			} else {
				currentIndexX++;
				
				if(currentIndexX == animator.maxXSplit) {
					currentIndexX = 0;
					currentIndexY++;
				}
				
				if(currentIndexY == animator.maxYSplit) {
					if(animator.maxYSplit > 0)
						currentIndexX = 0;
					
					currentIndexY = 0;
				}
				
				animator.gameObject.getMesh().setUvs(getTextureAt(currentIndexX, currentIndexY));
			}
		}
		
		private Vector2f[] getTextureAt(int indexX, int indexY) {
			float sizeX = animator.xSpilt / texture.getWidth();
			float sizeY = animator.ySpilt / texture.getHeight();
			
			float x = ((animator.xSpilt * indexX) / texture.getWidth());
			float y = ((animator.ySpilt * indexY) / texture.getHeight());
			
			return new Vector2f[] {
					new Vector2f(x, y), //Top Left
					new Vector2f(x, y + sizeY), //Bottom Left
					new Vector2f(x + sizeX, y + sizeY), //Bottom Right
					new Vector2f(x + sizeX, y), //Top Right
			};
		}
	}
	
	public int frames = 30;
	public int maxXSplit, maxYSplit;
	
	public float time, timer;
	public float xSpilt, ySpilt;
	
	public boolean animateThroughEntireTexture = false;
	
	public List<String> textures = new ArrayList<>();
	public List<Animation> animations = new ArrayList<>();
	
	@Override
	public void awake() {
		textures = new ArrayList<>();
		animations = new ArrayList<>();
		textures.add("Batteries.png");
		
		requestUpdate();
	}
	
	@Override
	public void start() {
		for(int i = 0; i < textures.size(); i++) {
			animations.add(new Animation(this, UI.loadTexture(textures.get(i))));
		}
	}
	
	@Override
	public void update() {
		time = (frames / 60);
		
		timer += Time.deltaTime;
		
		if(timer >= time) {
			for(int i = 0; i < animations.size(); i++)
				animations.get(i).update();
			
			timer = 0;
		}
	}
	
	@Override
	public void onValueChanged(String fieldName, String oldValue, String newValue) {
		System.err.println("field@: " + fieldName);
	}
}
