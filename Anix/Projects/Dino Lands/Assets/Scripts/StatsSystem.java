import com.Anix.Behaviours.Behaviour;

import java.lang.reflect.Field;

import com.Anix.Annotation.Header;

public class StatsSystem extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;	
	
	@Header("Status")
    public int level = 1;
    public int curLevel;
    
    public float xp = 0;
    public float curXp;
    
    public float xpTillLevelUp = 150;
    public float curXpTillLevelUp;
    
    public float health = 5;
    public float curHealth = 100; //Acts as a percentage, 100%, means the dino has 5 HP, 50% 2.5 HP and 200% has 10 HP.
    
    public float stamina = 50;
    public float curStamina = 100;

    public float speed = 2;
    public float curSpeed = 100;
    
    public float weight = 100;
    public float curWeight = 100;

    public float temperature = 100;
    public float curTemperature = 100;

    public float fortitude = 100;
    public float curFortitude = 100;

    public float food = 100;
    public float curFood = 100;

    public float thirst = 100;
    public float curThirst = 100;

    public float oxygen = 10;
    public float curOxygen = 100;

    public float attackDamage = 5;
    public float curAttackDamage = 100;

    public float torpidity = 50;
    public float curTorpidity = 100;

    @Header("Leveling Mulitplayers")
    public float healthMul         = 0.1f;
    public float staminaMul         = 0.1f;
    public float speedMul          = 0.1f;
    public float weightMul         = 0.1f;
    public float temperatureMul  = 0.1f;
    public float fortitudeMul    = 0.1f;
    public float foodMul           = 0.1f;
    public float thirstMul         = 0.1f;
    public float oxygenMul         = 0.1f;
    public float attackMul         = 0.1f;
    public float torpidityMul     = 0.1f;
    
    public StatsSystem() {
    	super();
    }
    
    @Override
	public void update() {
    	System.err.println("What about this?");
    }
    
    public float levelUpStats(String statsName, float amount) {
    	Field f;
		try {
			f = StatsSystem.class.getField(statsName);
			
			if(f == null) {
	    		System.err.println("[IN-GAME:ERROR] Couldn't find a field with the name of: " + statsName);
	    		
	    		return -1;
	    	}
	    	
	    	f.set(this, f.getFloat(this) + amount);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return -1;
    }
    
    public float getHealth() {
    	return getPercentageAmount(health, curHealth);
    }
    
    public float getStamina() {
    	return getPercentageAmount(stamina, curStamina);
    }
    
    public float getSpeed() {
    	return getPercentageAmount(speed, curSpeed);
    }
    
    public float getWeight() {
    	return getPercentageAmount(weight, curWeight);
    }
    
    public float getTemperature() {
    	return getPercentageAmount(temperature, curTemperature);
    }
    
    public float getFortitude() {
    	return getPercentageAmount(fortitude, curFortitude);
    }
    
    public float getFood() {
    	return getPercentageAmount(food, curFood);
    }
    
    public float getThirst() {
    	return getPercentageAmount(thirst, curThirst);
    }
    
    public float getOxygen() {
    	return getPercentageAmount(oxygen, curOxygen);
    }
    
    public float getAttack() {
    	return getPercentageAmount(attackDamage, curAttackDamage);
    }
    
    public float getTorpidity() {
    	return getPercentageAmount(torpidity, curTorpidity);
    }
    
    public float getPercentageAmount(float value, float percentage) {
    	return (percentage * value) / 100;
    }
}