package com.charles445.simpledifficulty.compat;

import com.charles445.simpledifficulty.api.SDCompatibility;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyValue;

import net.minecraftforge.fml.common.Loader;

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
	private boolean populateBiomesOPlenty()
	{
		if(!canUseMod("biomesoplenty"))
			return false;
		
		addFluidTemperature("hot_spring_water", 3.0f);
		return true;
	}
	
	//Lycanites Mobs
	private boolean populateLycanitesMobs()
	{
		if(!canUseMod("lycanitesmobs"))
			return false;
		
		addBlockTemperature("lycanitesmobs:purelava", 12.5f);
		
		//TODO considering adding the ooze to actually chill the surrounding area
		//That could be fun
		return true;
	}
	
	//Simple Camp Fire
	private boolean populateSimpleCampfire()
	{
		if(!canUseMod("campfire"))
			return false;
		
		addBlockTemperature("campfire:campfire", 7.0f);
		return true;
	}
	
	
	//
	// API
	//
	
	private void addBlockTemperature(String registryName, float temperature, JsonPropertyValue... properties)
	{
		JsonConfig.registerBlockTemperature(registryName, temperature, properties);
		
	}
	
	private void addFluidTemperature(String fluidName, float temperature)
	{
		JsonConfig.registerFluidTemperature(fluidName, temperature);
	}
	
	//
	// Utility
	//
	
	private boolean canUseMod(String modid)
	{
		return (Loader.isModLoaded(modid) && !SDCompatibility.disabledDefaultJson.contains(modid));
	}
	
	private JsonPropertyTemperature propTemp(float temp)
	{
		return new JsonPropertyTemperature(temp);
	}
	
	private JsonPropertyTemperature propTemp(float temp, JsonPropertyValue... props)
	{
		return new JsonPropertyTemperature(temp, props);
	}
	
	/*
	//Old stuff
	
	//Adding without overriding (in case a mod adds their own default)
	private <T> void put(final Map<String, T> map, String str, final T obj)
	{
		if(!map.containsKey(str))
			map.put(str, obj);
	}
	
	private JsonTemperature temperature(float temp)
	{
		return new JsonTemperature(temp);
	}
	*/
}
