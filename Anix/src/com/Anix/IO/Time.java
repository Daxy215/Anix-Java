package com.Anix.IO;

import java.util.concurrent.TimeUnit;

public class Time {
	public static float timeScale = 1;
	
	private static long rawTime;
	private static float time;
	private static double rawDelta;
	public static float deltaTime;
	private static float unscaledDelta;
	private static final long second = 1000000000L;
	private static int frameRate;
	
	private static long startTime;
	private static long lastFrameTime;
	private static long frameStartTime;
	private static long framePassedTime;
	private static double unprocessedTime;
	private static int currentFrameRate;
	
	public static void init() {
		lastFrameTime = System.nanoTime();
		startTime = lastFrameTime;
	}
	
	public static void process() {
		rawTime = System.nanoTime();
		time = (TimeUnit.MILLISECONDS.convert(rawTime - startTime, TimeUnit.NANOSECONDS));
		frameStartTime = rawTime;
		framePassedTime = frameStartTime - lastFrameTime;
		lastFrameTime = frameStartTime;
		
		rawDelta = framePassedTime / (double) second;
		
		if(rawDelta > 0.01f) {
			deltaTime = (float)(0.01 * timeScale);
			unscaledDelta = 0.01f;
		} else {
			deltaTime = (float)(rawDelta * timeScale);
			unscaledDelta = (float) rawDelta;
		}
		
		unprocessedTime += rawDelta;
		currentFrameRate++;
				
		if(unprocessedTime >= 1.0f) {
			frameRate = currentFrameRate;
			currentFrameRate = 0;
			unprocessedTime = 0;
		}
	}
	
	public static final float getTime() {
		return time;
	}
	
	public static final float getDeltaTime() {
		return deltaTime;
	}
	
	public static final float getUnscaledDelta() {
		return unscaledDelta;
	}
	
	public static int getFrameRate() {
		return frameRate;
	}
}
