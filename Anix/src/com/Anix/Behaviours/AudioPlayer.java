package com.Anix.Behaviours;

import java.nio.file.Path;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.Anix.Annotation.Type;

public class AudioPlayer extends Behaviour {
	private static final long serialVersionUID = 4873083815329117177L;
	
	@Type(values = {"aiff", "au", "wav"})
	public Path path;
	
	private transient Clip clip;
	private transient FloatControl gainControl;
	
	@Override
	public void awake() {
		if(path != null) {
			clip = SoundClip.loadClip(path.toString());
			gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
		}
	}
	
	@Override
	public void start() {
		play();
	}
	
	public void play() {
		if(clip == null)
			return;
		
		stop();
		clip.setFramePosition(0);
		
		while(!clip.isRunning()) {
			clip.start();
		}
	}
	
	public void stop() {
		if(clip == null)
			return;
		
		while(clip.isRunning()) {
			clip.stop();
		}
	}
	
	public void close() {
		if(clip == null)
			return;
		
		stop();
		clip.drain();
		clip.close();
	}
	
	public void loop() {
		if(clip == null)
			return;
		
		stop();
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		play();
	}
	
	public void setVolume(float value) {
		if(clip == null)
			return;
		
		gainControl.setValue(value);
	}
	
	public boolean isPlaying() {
		if(clip == null)
			return false;
		
		return clip.isRunning();
	}
}
