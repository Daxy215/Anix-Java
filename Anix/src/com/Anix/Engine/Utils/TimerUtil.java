package com.Anix.Engine.Utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

public class TimerUtil {
	public static void waitUntil(BooleanSupplier condition, long timeoutms) throws TimeoutException {
		long start = System.currentTimeMillis();
		
		while (!condition.getAsBoolean()) {
			if (System.currentTimeMillis() - start > timeoutms) {
				throw new TimeoutException(String.format("Condition not meet within %s ms",timeoutms));
			}
		}
	}
	
	public static void sleepFor(long sleepFor, TimeUnit unit) {
		boolean interrupted = false;
		try {
			long remainingNanos = unit.toNanos(sleepFor);
			long end = System.nanoTime() + remainingNanos;
			while (true) {
				try {
					// TimeUnit.sleep() treats negative timeouts just like zero.
					TimeUnit.NANOSECONDS.sleep(remainingNanos);
					return;
				} catch (InterruptedException e) {
					interrupted = true;
					remainingNanos = end - System.nanoTime();
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	public static void pause(double seconds) {
		try {
			Thread.sleep((long) (seconds * 1000));
		} catch (InterruptedException e) {}
	}
}
