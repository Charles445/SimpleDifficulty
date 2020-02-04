package com.charles445.simpledifficulty.api.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.charles445.simpledifficulty.api.config.json.JsonConsumableTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyValue;
import com.charles445.simpledifficulty.api.config.json.JsonTemperature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class JsonConfig
{
	public static Map<String, JsonTemperature> armorTemperatures = new HashMap<String, JsonTemperature>();	
	public static Map<String, List<JsonPropertyTemperature>> blockTemperatures = new HashMap<String, List<JsonPropertyTemperature>>();
	public static Map<String, JsonTemperature> fluidTemperatures = new HashMap<String, JsonTemperature>();
	public static Map<String, List<JsonConsumableTemperature>> consumableTemperature = new HashMap<String, List<JsonConsumableTemperature>>();
	public static Map<String, List<JsonConsumableThirst>> consumableThirst = new HashMap<String, List<JsonConsumableThirst>>();
	
	//TODO jdoc
	
	//Armor
	
	public static void registerArmorTemperature(ItemStack stack, float temperature)
	{
		registerArmorTemperature(stack.getItem().getRegistryName().toString(), temperature);
	}
	
	public static void registerArmorTemperature(String registryName, float temperature)
	{
		armorTemperatures.put(registryName, new JsonTemperature(temperature));
	}
	
	//Blocks
	
	public static void registerBlockTemperature(Block block, float temperature, JsonPropertyValue... properties)
	{
		registerBlockTemperature(block.getRegistryName().toString(), temperature, properties);
	}
	
	public static void registerBlockTemperature(String registryName, float temperature, JsonPropertyValue... properties)
	{
		if(!blockTemperatures.containsKey(registryName))
			blockTemperatures.put(registryName, new ArrayList<JsonPropertyTemperature>());
		blockTemperatures.get(registryName).add(new JsonPropertyTemperature(temperature,properties));
	}
	
	//Fluid
	
	public static void registerFluidTemperature(String fluidName, float temperature)
	{
		fluidTemperatures.put(fluidName, new JsonTemperature(temperature));
	}
	
	//Consumable Temperature
	
	public static void registerConsumableTemperature(String group, ItemStack stack, float temperature, int duration)
	{
		String registryName = stack.getItem().getRegistryName().toString();
		if(!consumableTemperature.containsKey(registryName))
			consumableTemperature.put(registryName, new ArrayList<JsonConsumableTemperature>());
		
		if(stack.getHasSubtypes())
			consumableTemperature.get(registryName).add(new JsonConsumableTemperature(group, temperature, stack.getMetadata(), duration));
		else
			consumableTemperature.get(registryName).add(new JsonConsumableTemperature(group, temperature, -1, duration));
	}
	
	//ConsumableThirst
	
	public static void registerConsumableThirst(ItemStack stack, int amount, float saturation, float thirstChance)
	{
		int metadata = -1;
		
		if(stack.getHasSubtypes())
			metadata = stack.getMetadata();
		
		registerConsumableThirst(stack.getItem().getRegistryName().toString(), metadata, amount, saturation, thirstChance);
	}
	
	public static void registerConsumableThirst(String registryName, int metadata, int amount, float saturation, float thirstChance)
	{
		if(!consumableThirst.containsKey(registryName))
			consumableThirst.put(registryName, new ArrayList<JsonConsumableThirst>());
		
		consumableThirst.get(registryName).add(new JsonConsumableThirst(metadata,amount,saturation,thirstChance));
	}
}
