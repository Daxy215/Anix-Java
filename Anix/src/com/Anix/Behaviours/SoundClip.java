package com.Anix.Behaviours;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

final class SoundClip {
	private transient Clip clip;
	private transient FloatControl gainControl;
	
	public SoundClip(String path) {
		clip = loadClip(path);
		gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
	}
	
	public static Clip loadClip(String path) {
		try {
			InputStream audioSrc = SoundClip.class.getResourceAsStream(path);
			
			if(audioSrc == null) {
				audioSrc = new FileInputStream(path);
			}
			
			InputStream bufferedIn = new BufferedInputStream(audioSrc);
			AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
			
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
			
			Clip clip = AudioSystem.getClip();
			clip.open(dais);
						
			dais.close();
			ais.close();
			bufferedIn.close();
			
			return clip;
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
		
		return null;
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
