package com.Anix.Behaviours;

import java.util.ArrayList;
import java.util.List;

import com.Anix.Annotation.HideFromInspector;
import com.Anix.Math.Color;

public class LightSource extends Behaviour {
	private static final long serialVersionUID = -4822120711522049337L;

	public float strength = 1;
	
	public Color color = Color.white;
	
	@HideFromInspector
	public transient static List<LightSource> lights = new ArrayList<LightSource>();
	
	@Override
	public void awake() {
		if(lights.contains(this))
			return;
		
		lights.add(this);
	}
	
	@Override
	public void onEnable() {
		if(lights.contains(this))
			return;
		
		lights.add(this);
	}
	
	@Override
	public void onDisable() {
		lights.remove(this);
	}
	
	@Override
	public void onRemove() {
		lights.remove(this);
	}
	
	@Override
	public void onDestroy() {
		lights.remove(this);
	}
}
