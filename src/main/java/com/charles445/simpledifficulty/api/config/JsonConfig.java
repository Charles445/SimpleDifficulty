package com.charles445.simpledifficulty.api.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.charles445.simpledifficulty.api.config.json.JsonConsumableTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.config.json.JsonItemIdentity;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyValue;
import com.charles445.simpledifficulty.api.config.json.JsonTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureIdentity;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class JsonConfig
{
	public static Map<String, List<JsonTemperatureIdentity>> armorTemperatures = new HashMap<>();	
	public static Map<String, List<JsonPropertyTemperature>> blockTemperatures = new HashMap<>();
	public static Map<String, List<JsonConsumableTemperature>> consumableTemperature = new HashMap<>();
	public static Map<String, List<JsonConsumableThirst>> consumableThirst = new HashMap<>();
	public static Map<String, JsonTemperature> fluidTemperatures = new HashMap<>();
	public static Map<String, List<JsonTemperatureIdentity>> heldItemTemperatures = new HashMap<>();
	
	//TODO jdoc
	
	//Armor
	
	public static void registerArmorTemperature(ItemStack stack, float temperature)
	{
		String registryName = stack.getItem().getRegistryName().toString();
		
		int metadata = -1;	
		if(stack.getHasSubtypes())
			metadata = stack.getMetadata();
		
		registerArmorTemperature(stack.getItem().getRegistryName().toString(), temperature, new JsonItemIdentity(metadata));
	}
	
	public static void registerArmorTemperature(String registryName, float temperature)
	{
		registerArmorTemperature(registryName, temperature, new JsonItemIdentity(-1));
	}
	
	public static void registerArmorTemperature(String registryName, float temperature, JsonItemIdentity identity)
	{
		if(!armorTemperatures.containsKey(registryName))
			armorTemperatures.put(registryName, new ArrayList<JsonTemperatureIdentity>());
		
		final List<JsonTemperatureIdentity> currentList = armorTemperatures.get(registryName);
		
		JsonTemperatureIdentity result = new JsonTemperatureIdentity(temperature, identity);
		
		for(int i=0; i<currentList.size(); i++)
		{
			JsonTemperatureIdentity jtm = currentList.get(i);
			if(jtm.matches(identity))
			{
				currentList.set(i, result);
				return;
			}
		}
		
		currentList.add(result);
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
		
		registerConsumableTemperature(group, registryName, temperature, duration, new JsonItemIdentity(metadata));
	}
	public static void registerConsumableTemperature(String group, String registryName, float temperature, int duration, JsonItemIdentity identity)
	{
		if(!consumableTemperature.containsKey(registryName))
			consumableTemperature.put(registryName, new ArrayList<JsonConsumableTemperature>());
		
		final List<JsonConsumableTemperature> currentList = consumableTemperature.get(registryName);
		
		JsonConsumableTemperature result = new JsonConsumableTemperature(group, temperature, duration, identity);
		
		for(int i=0; i<currentList.size(); i++)
		{
			JsonConsumableTemperature jct = currentList.get(i);
			if(jct.matches(identity))
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
		
		registerConsumableThirst(stack.getItem().getRegistryName().toString(), amount, saturation, thirstyChance, new JsonItemIdentity(metadata));
	}
	public static void registerConsumableThirst(String registryName, int amount, float saturation, float thirstyChance, JsonItemIdentity identity)
	{
		if(!consumableThirst.containsKey(registryName))
			consumableThirst.put(registryName, new ArrayList<JsonConsumableThirst>());
		
		final List<JsonConsumableThirst> currentList = consumableThirst.get(registryName);
		
		JsonConsumableThirst result = new JsonConsumableThirst(amount, saturation, thirstyChance, identity);
		
		for(int i=0; i<currentList.size(); i++)
		{
			JsonConsumableThirst jct = currentList.get(i);
			if(jct.matches(identity))
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
		
		registerHeldItem(stack.getItem().getRegistryName().toString(), temperature, new JsonItemIdentity(metadata));
	}
	
	public static void registerHeldItem(String registryName, float temperature, JsonItemIdentity identity)
	{
		if(!heldItemTemperatures.containsKey(registryName))
			heldItemTemperatures.put(registryName, new ArrayList<JsonTemperatureIdentity>());
		
		final List<JsonTemperatureIdentity> currentList = heldItemTemperatures.get(registryName);
		
		JsonTemperatureIdentity result = new JsonTemperatureIdentity(temperature, identity);
		
		for(int i=0; i<currentList.size(); i++)
		{
			JsonTemperatureIdentity jtm = currentList.get(i);
			if(jtm.matches(identity))
			{
				currentList.set(i, result);
				return;
			}
		}
		
		currentList.add(result);
	}
}
