import com.Anix.Behaviours.Behaviour;

import java.lang.reflect.Field;

import com.Anix.Annotation.Header;

public class StatsSystem extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;	
	
	@Header("Status")
    protected int level = 1;
    protected int curLevel;
    
    protected float xp = 0;
    protected float curXp;
    
    protected float xpTillLevelUp = 150;
    protected float curXpTillLevelUp;
    
    protected float health = 5;
    protected float curHealth = 100; //Acts as a percentage, 100%, means the dino has 5 HP, 50% 2.5 HP and 200% has 10 HP.
    
    protected float stamina = 50;
    protected float curStamina = 100;

    protected float speed = 2;
    protected float curSpeed = 100;
    
    protected float weight = 100;
    protected float curWeight = 100;

    protected float temperature = 100;
    protected float curTemperature = 100;

    protected float fortitude = 100;
    protected float curFortitude = 100;

    protected float food = 100;
    protected float curFood = 100;

    protected float thirst = 100;
    protected float curThirst = 100;

    protected float oxygen = 10;
    protected float curOxygen = 100;

    protected float attackDamage = 5;
    protected float curAttackDamage = 100;

    protected float torpidity = 50;
    protected float curTorpidity = 100;

    @Header("Leveling Mulitplayers")
    protected float healthMul         = 0.1f;
    protected float staminaMul         = 0.1f;
    protected float speedMul          = 0.1f;
    protected float weightMul         = 0.1f;
    protected float temperatureMul  = 0.1f;
    protected float fortitudeMul    = 0.1f;
    protected float foodMul           = 0.1f;
    protected float thirstMul         = 0.1f;
    protected float oxygenMul         = 0.1f;
    protected float attackMul         = 0.1f;
    protected float torpidityMul     = 0.1f;
    
    public StatsSystem() {
    	super();
    }
    
    @Override
	public void update() {
    	System.err.println("What about this?");
    }
    
    protected float levelUpStats(String statsName, float amount) {
    	Field f = StatsSystem.class.getField(statsName);
    	
    	if(f == null) {
    		System.err.println("[IN-GAME:ERROR] Couldn't find a field with the name of: " + statsName);
    		
    		return -1;
    	}
    	
    	f.set(this, f.getFloat(this) + amount);
    }
    
    protected float getHealth() {
    	return getPercentageAmount(health, curHealth);
    }
    
    protected float getStamina() {
    	return getPercentageAmount(stamina, curStamina);
    }
    
    protected float getSpeed() {
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
    
    protected float getPercentageAmount(float value, float percentage) {
    	return (percentage * value) / 100;
    }
}