package com.charles445.simpledifficulty.compat;

import static com.charles445.simpledifficulty.config.JsonConfig.armorTemperatures;
import static com.charles445.simpledifficulty.config.JsonConfig.blockTemperatures;
import static com.charles445.simpledifficulty.config.JsonConfig.fluidTemperatures;

import com.charles445.simpledifficulty.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.config.json.JsonTemperature;
import com.charles445.simpledifficulty.config.json.PropertyValue;

public class JsonCompatDefaults
{
	public static JsonCompatDefaults instance = new JsonCompatDefaults();
	//Default mod support for JSON
	
	public void populate()
	{
		populateBiomesOPlenty();
		populateLycanitesMobs();
		populateSimpleCampfire();
	}
	
	//Biomes O' Plenty
	private void populateBiomesOPlenty()
	{
		fluidTemperatures.put("hot_spring_water", temperature(3.0f));
	}
	
	//Lycanites Mobs
	private void populateLycanitesMobs()
	{
		blockTemperatures.put("lycanitesmobs:purelava", propTemp(15.0f));
		
		//TODO considering adding the ooze to actually chill the surrounding area
		//That could be fun
	}
	
	//Simple Camp Fire
	private void populateSimpleCampfire()
	{
		blockTemperatures.put("campfire:campfire", propTemp(7.0f));
	}
	
	
	
	//Utility
	
	private JsonTemperature temperature(float temp)
	{
		return new JsonTemperature(temp);
	}
	
	private JsonPropertyTemperature propTemp(float temp)
	{
		return new JsonPropertyTemperature(temp);
	}
	
	private JsonPropertyTemperature propTemp(float temp, PropertyValue... props)
	{
		return new JsonPropertyTemperature(temp, props);
	}
	
	
}
