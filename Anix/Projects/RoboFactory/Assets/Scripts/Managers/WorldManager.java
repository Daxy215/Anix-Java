package Managers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.Engine.Graphics.Material;
import com.Anix.Engine.Graphics.Shader;
import com.Anix.GUI.UI;
import com.Anix.Math.Color;

public class WorldManager extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;	
	
	public static class Time {
		public int years, months, days, hours, minutes, seconds;
		
		public Time() {
			hours = 6;
		}
		
		public void update(boolean isThroughServer) {
			//if(!Core.isConnected && isThroughServer)
			//	Core.isConnected = true;
			
			if(seconds >= 60) {
				seconds = 0;
				
				minutes++;
			}
			
			if(minutes >= 60) {
				minutes = 0;
				
				hours++;
				
				//if(!isThroughServer) {
					//if(hours == 21) {
						//GameManager.saveWorld();
					//}
					
					updateLighting();
				//}
				
				if(hours == 22) {//22
					//SpawnerManager.spawnEnemies(days, server);
				}
			}
			
			if(hours >= 24) {
				hours = 0;
				
				days++;
			}
			
			if(days >= 30) {
				days = 0;

				months++;
			}
			
			if(months >= 12) {
				months = 0;
				
				years++;
			}
		}
		
		public void setTime(String time) {
			String[] times = time.split(":");
			
			years    = Integer.parseInt(times[0].split(":")[0]);
			months   = Integer.parseInt(times[1].split(":")[0]);
			days     = Integer.parseInt(times[2].split(":")[0]);
			hours    = Integer.parseInt(times[3].split(":")[0]);
			minutes  = Integer.parseInt(times[4].split(":")[0]);
			seconds  = Integer.parseInt(times[5].split(":")[0]);
		}
		
		public boolean isDay() {
			return hours >= 6 && hours <= 18;
		}
		
		@Override
		public String toString() {
			return "Years: " + years + " Months: " + months + " Days: " + days + "Hours: " + hours + " Minutes: " + minutes + " Seconds: " + seconds;
		}
		
		public int getYears() {
			return years;
		}
		
		public void setYears(int years) {
			this.years = years;
		}
		
		public int getMonths() {
			return months;
		}
		
		public void setMonths(int months) {
			this.months = months;
		}
		
		public int getDays() {
			return days;
		}
		
		public void setDays(int days) {
			this.days = days;
		}
		
		public int getHours() {
			return hours;
		}
		
		public void setHours(int hours) {
			this.hours = hours;
		}
		
		public int getMinutes() {
			return minutes;
		}
		
		public void setMinutes(int minutes) {
			this.minutes = minutes;
		}
		
		public void addMinute() {
			minutes++;
		}
		
		public int getSeconds() {
			return seconds;
		}
		
		public void setSeconds(int seconds) {
			this.seconds = seconds;
		}
		
		public void addSecond(boolean isThroughServer) {
			seconds++;
			
			update(isThroughServer);
		}
	}
	
	public static Time time = new Time();
	public static Material material;
	private static Shader shader;
	
	public static float lightStrength = 1;
	
	@Override
	public void awake() {
		shader = Shader.getShader("GameShader");
		material = new Material(shader);
		
		requestUpdate();
		requestRender();
	}
	
	@Override
	public void start() {
		updateLighting();
		
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		
		Runnable task = () -> {
			time.addSecond(false);
        };
        
        executor.scheduleAtFixedRate(task, 0, 50, TimeUnit.MILLISECONDS);
        //executor.shutdown();
	}
	
	@Override
	public void update() {
		String time = "Time: " + (WorldManager.time.getYears() > 0 ? WorldManager.time.getYears() + ":" : "")
				+ (WorldManager.time.getMonths() > 0 ? WorldManager.time.getMonths() + ":" : "")
				+ (WorldManager.time.getDays() > 0 ? WorldManager.time.getDays() + ":" : "")
				+ WorldManager.time.getHours() + ":" + WorldManager.time.getMinutes() + ":" + WorldManager.time.getSeconds();
		
		UI.drawString(time, 300, 25, 0.02f, 1.0f, 1.0f, Color.red);
	}
	
	@Override
	public void render() {
		if(Camera.main == null)
			return;
		
		shader.bind();
		
		shader.setUniform("lightPosition", Camera.main.gameObject.getPosition());
		shader.setUniform("lightColor", Color.white);
		shader.setUniform("strength", lightStrength);
		
		shader.unbind();
	}
	
	public static void updateLighting() {
		int hours = time.getHours();
		
		if(hours >= 8 && hours <= 13) {
			WorldManager.setLight(0.8f);
		} else if(hours == 14) {
			WorldManager.setLight(0.7f);
		} else if(hours == 15) {
			WorldManager.setLight(0.6f);
		} else if(hours >= 17 && hours <= 19) {
			WorldManager.setLight(0.5f);
		} else if(hours >= 20 && hours <= 21) {
			WorldManager.setLight(0.4f);
		} else if(hours == 22) {
			WorldManager.setLight(0.3f);
		} else if(hours == 23) {
			WorldManager.setLight(0.2f);
		} else if(hours == 24) {
			WorldManager.setLight(0.1f);
		} else if(hours == 1) {
			WorldManager.setLight(0.1f);
		} else if(hours == 2) {
			WorldManager.setLight(0.2f);
		} else if(hours == 3) {
			WorldManager.setLight(0.3f);
		} else if(hours == 4) {
			WorldManager.setLight(0.4f);
		} else if(hours == 5) {
			WorldManager.setLight(0.5f);
		} else if(hours == 6) {
			WorldManager.setLight(0.6f);
		} else if(hours == 7) {
			WorldManager.setLight(0.7f);
		}
	}

	public static void setLight(float value) {
		//Anix.getLight().setStrength(value);
		WorldManager.lightStrength = value;
	}
}