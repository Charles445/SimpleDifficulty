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
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureMetadata;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class JsonConfig
{
	public static Map<String, JsonTemperature> armorTemperatures = new HashMap<String, JsonTemperature>();	
	public static Map<String, List<JsonPropertyTemperature>> blockTemperatures = new HashMap<String, List<JsonPropertyTemperature>>();
	public static Map<String, List<JsonConsumableTemperature>> consumableTemperature = new HashMap<String, List<JsonConsumableTemperature>>();
	public static Map<String, List<JsonConsumableThirst>> consumableThirst = new HashMap<String, List<JsonConsumableThirst>>();
	public static Map<String, JsonTemperature> fluidTemperatures = new HashMap<String, JsonTemperature>();
	public static Map<String, List<JsonTemperatureMetadata>> heldItemTemperatures = new HashMap<String, List<JsonTemperatureMetadata>>();
	
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
	
	public static boolean registerBlockTemperature(Block block, float temperature, JsonPropertyValue... properties)
	{
		return registerBlockTemperature(block.getRegistryName().toString(), temperature, properties);
	}
	
	public static boolean registerBlockTemperature(String registryName, float temperature, JsonPropertyValue... properties)
	{
		if(!blockTemperatures.containsKey(registryName))
			blockTemperatures.put(registryName, new ArrayList<JsonPropertyTemperature>());

		final List<JsonPropertyTemperature> currentList = blockTemperatures.get(registryName);
		JsonPropertyTemperature result = new JsonPropertyTemperature(temperature,properties);

		if(properties.length>0)
		{
			//With property
			for(int i=0;i<currentList.size();i++)
			{
				JsonPropertyTemperature jpt = currentList.get(i);
				if(jpt.matchesDescribedProperties(properties))
				{
					currentList.set(i, result);
					return true;
				}
			}
			
			currentList.add(result);
			return true;
		}
		else
		{
			//No property
			//Do NOT interfere with it if one with a property specification exists, and return false
			
			for(int i=0;i<currentList.size();i++)
			{
				JsonPropertyTemperature jpt = currentList.get(i);
				if(jpt.properties.keySet().size() > 0)
				{
					return false;
				}
			}
			
			//Okay, none with properties got found, go through it again and look for the one to replace as usual
			for(int i=0;i<currentList.size();i++)
			{
				JsonPropertyTemperature jpt = currentList.get(i);
				if(jpt.properties.keySet().size() == 0)
				{
					currentList.set(i, result);
					return true;
				}
			}
			
			currentList.add(result);
			return true;
		}
		
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
		
		int metadata = -1;
		if(stack.getHasSubtypes())
			metadata = stack.getMetadata();
		
		registerConsumableTemperature(group, registryName, metadata, temperature, duration);
	}
	
	public static void registerConsumableTemperature(String group, String registryName, int metadata, float temperature, int duration)
	{
		if(!consumableTemperature.containsKey(registryName))
			consumableTemperature.put(registryName, new ArrayList<JsonConsumableTemperature>());
		
		final List<JsonConsumableTemperature> currentList = consumableTemperature.get(registryName);
		
		JsonConsumableTemperature result = new JsonConsumableTemperature(group, temperature, metadata, duration);
		
		for(int i=0; i<currentList.size(); i++)
		{
			JsonConsumableTemperature jct = currentList.get(i);
			if(jct.matches(metadata))
			{
				currentList.set(i, result);
				return;
			}
		}
		
		currentList.add(result);
	}
	
	//ConsumableThirst
	
	public static void registerConsumableThirst(ItemStack stack, int amount, float saturation, float thirstyChance)
	{
		String registryName = stack.getItem().getRegistryName().toString();
		
		int metadata = -1;	
		if(stack.getHasSubtypes())
			metadata = stack.getMetadata();
		
		registerConsumableThirst(stack.getItem().getRegistryName().toString(), metadata, amount, saturation, thirstyChance);
	}
	
	public static void registerConsumableThirst(String registryName, int metadata, int amount, float saturation, float thirstyChance)
	{
		if(!consumableThirst.containsKey(registryName))
			consumableThirst.put(registryName, new ArrayList<JsonConsumableThirst>());
		
		final List<JsonConsumableThirst> currentList = consumableThirst.get(registryName);
		
		JsonConsumableThirst result = new JsonConsumableThirst(metadata, amount, saturation, thirstyChance);
		
		for(int i=0; i<currentList.size(); i++)
		{
			JsonConsumableThirst jct = currentList.get(i);
			if(jct.matches(metadata))
			{
				currentList.set(i, result);
				return;
			}
		}
		
		currentList.add(result);
	}
	
	//HeldItem
	
	public static void registerHeldItem(ItemStack stack, float temperature)
	{
		String registryName = stack.getItem().getRegistryName().toString();
		
		int metadata = -1;	
		if(stack.getHasSubtypes())
			metadata = stack.getMetadata();
		
		registerHeldItem(stack.getItem().getRegistryName().toString(), metadata, temperature);
	}
	
	public static void registerHeldItem(String registryName, int metadata, float temperature)
	{
		if(!heldItemTemperatures.containsKey(registryName))
			heldItemTemperatures.put(registryName, new ArrayList<JsonTemperatureMetadata>());
		
		final List<JsonTemperatureMetadata> currentList = heldItemTemperatures.get(registryName);
		
		JsonTemperatureMetadata result = new JsonTemperatureMetadata(metadata, temperature);
		
		for(int i=0; i<currentList.size(); i++)
		{
			JsonTemperatureMetadata jtm = currentList.get(i);
			if(jtm.matches(metadata))
			{
				currentList.set(i, result);
				return;
			}
		}
		
		currentList.add(result);
	}
}
