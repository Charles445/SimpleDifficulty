package com.charles445.simpledifficulty.compat;

import com.charles445.simpledifficulty.api.SDCompatibility;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyValue;

import net.minecraftforge.fml.common.Loader;

import static com.charles445.simpledifficulty.compat.ModNames.*;

public class JsonCompatDefaults
{
	public static JsonCompatDefaults instance = new JsonCompatDefaults();
	//Default mod support for JSON
	
	public void populate()
	{
		populateBiomesOPlenty();
		populateHarvestCraft();
		populateLycanitesMobs();
		populateSimpleCampfire();
	}
	
	public boolean populate(String modid)
	{
		switch(modid)
		{
			case BIOMESOPLENTY: return populateBiomesOPlenty();
			case HARVESTCRAFT: return populateHarvestCraft();
			case LYCANITESMOBS: return populateLycanitesMobs();
			case SIMPLECAMPFIRE: return populateSimpleCampfire();
		
		
			default: return false;
		}
	}
	
	//Biomes O' Plenty
	private boolean populateBiomesOPlenty()
	{
		if(!canUseMod(BIOMESOPLENTY))
			return false;
		
		addFluidTemperature("hot_spring_water", 3.0f);
		return true;
	}
	
	//HarvestCraft (Pam's)
	private boolean populateHarvestCraft()
	{
		if(!canUseMod(HARVESTCRAFT))
			return false;
		
		//Juice, soda, and smoothies are handled in ThirstHandler as they are more broad
		
		//There's bound to be a typo in here somewhere
		
		addDrink("harvestcraft:teaitem", 6, 5.0f);
		addDrink("harvestcraft:coffeeitem", 6, 5.0f);
		addDrink("harvestcraft:hotchocolateitem", 6, 5.0f);
		addDrink("harvestcraft:lemonaideitem", 9, 7.0f);
		addDrink("harvestcraft:raspberryicedteaitem", 9, 7.0f);
		addDrink("harvestcraft:chaiteaitem", 9, 7.0f);
		addDrink("harvestcraft:espressoitem", 12, 9.0f);
		addDrink("harvestcraft:coffeeconlecheitem", 18, 15.0f);
		addDrink("harvestcraft:coconutmilkitem", 3, 3.0f);
		addDrink("harvestcraft:chocolatemilkitem", 6, 5.0f);
		addDrink("harvestcraft:fruitpunchitem", 6, 5.0f);
		addDrink("harvestcraft:pinacoladaitem", 6, 5.0f);
		addDrink("harvestcraft:eggnogitem", 15, 12.0f);
		addDrink("harvestcraft:soymilkitem", 3, 3.0f);
		addDrink("harvestcraft:applecideritem", 6, 5.0f);
		addDrink("harvestcraft:energydrinkitem", 15, 12.0f);
		addDrink("harvestcraft:greenteaitem", 6, 5.0f);
		addDrink("harvestcraft:earlgreyteaitem", 6, 5.0f);
		addDrink("harvestcraft:bubbleteaitem", 12, 9.0f);
		addDrink("harvestcraft:rosepetalteaitem", 6, 5.0f);
		addDrink("harvestcraft:cherryslushieitem", 9, 7.0f);
		addDrink("harvestcraft:lycheeteaitem", 9, 7.0f); //uncraftable?
		addDrink("harvestcraft:dandelionteaitem", 9, 7.0f);
		addDrink("harvestcraft:raspberrymilkshakeitem", 15, 12.0f);
		addDrink("harvestcraft:pumpkinspicelatteitem", 20, 19.0f);
		addDrink("harvestcraft:rootbeerfloatitem", 18, 15.0f);
		addDrink("harvestcraft:hotcocoaitem", 15, 12.0f);
		
		//shakes
		addDrink("harvestcraft:strawberrymilkshakeitem", 9, 7.0f);
		addDrink("harvestcraft:chocolatemilkshakeitem", 9, 7.0f);
		addDrink("harvestcraft:bananamilkshakeitem", 9, 7.0f);
		addDrink("harvestcraft:gooseberrymilkshakeitem", 9, 7.0f);
		addDrink("harvestcraft:durianmilkshakeitem", 9, 7.0f);
		
		

		//meal
		addDrink("harvestcraft:cookiesandmilkitem", 8, 7.0f);
		addDrink("harvestcraft:sundayhighteaitem", 12, 10.0f);
		addDrink("harvestcraft:delightedmealitem", 14, 13.0f);
		addDrink("harvestcraft:weekendpicnicitem", 20, 20.0f);
		addDrink("harvestcraft:theatreboxitem", 16, 15.0f);
		addDrink("harvestcraft:friedfeastitem", 20, 20.0f);
		addDrink("harvestcraft:bbqplatteritem", 20, 20.0f);
		
		return true;
	}
	
	//Lycanites Mobs
	private boolean populateLycanitesMobs()
	{
		if(!canUseMod(LYCANITESMOBS))
			return false;
		
		addBlockTemperature("lycanitesmobs:purelava", 12.5f);
		
		//TODO considering adding the ooze to actually chill the surrounding area
		//That could be fun
		return true;
	}
	
	//Simple Camp Fire
	private boolean populateSimpleCampfire()
	{
		if(!canUseMod(SIMPLECAMPFIRE))
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
	
	private void addDrink(String registryName, int amount, float saturation)
	{
		addDrink(registryName, amount, saturation, 0.0f);
	}
	
	private void addDrink(String registryName, int amount, float saturation, float thirstyChance)
	{
		JsonConfig.registerConsumableThirst(registryName, -1, amount, saturation, thirstyChance);
	}
	
	private void addFluidTemperature(String fluidName, float temperature)
	{
		JsonConfig.registerFluidTemperature(fluidName, temperature);
	}
	
	//
	// Utility
	//
	
	public boolean canUseMod(String modid)
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
