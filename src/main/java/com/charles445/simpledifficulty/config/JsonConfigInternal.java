package com.charles445.simpledifficulty.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyValue;
import com.charles445.simpledifficulty.api.config.json.JsonTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureMetadata;
import com.charles445.simpledifficulty.api.temperature.TemporaryModifierGroupEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.compat.JsonCompatDefaults;
import com.charles445.simpledifficulty.config.json.MaterialTemperature;
import com.charles445.simpledifficulty.item.ItemJuice;
import com.charles445.simpledifficulty.item.ItemJuice.JuiceEnum;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class JsonConfigInternal
{
	//JSON stresses me out
	
	//TODO customizable materials? Probably not
	public static MaterialTemperature materialTemperature = new MaterialTemperature();
	
	public static List<String> jsonErrors = new ArrayList<String>();
	
	//postInit
	public static void init(File jsonDirectory)
	{
		//Setup default JSON
		
		JsonConfig.registerArmorTemperature(new ItemStack(SDItems.wool_helmet), 2.0f);
		JsonConfig.registerArmorTemperature(new ItemStack(SDItems.wool_chestplate), 2.0f);
		JsonConfig.registerArmorTemperature(new ItemStack(SDItems.wool_leggings), 2.0f);
		JsonConfig.registerArmorTemperature(new ItemStack(SDItems.wool_boots), 2.0f);
		
		JsonConfig.registerArmorTemperature(new ItemStack(SDItems.ice_helmet), -2.0f);
		JsonConfig.registerArmorTemperature(new ItemStack(SDItems.ice_chestplate), -2.0f);
		JsonConfig.registerArmorTemperature(new ItemStack(SDItems.ice_leggings), -2.0f);
		JsonConfig.registerArmorTemperature(new ItemStack(SDItems.ice_boots), -2.0f);
		
		JsonConfig.registerBlockTemperature(SDBlocks.campfire, 7.0f, new JsonPropertyValue("burning", "true"));
		JsonConfig.registerBlockTemperature(SDBlocks.campfire, 0.0f, new JsonPropertyValue("burning", "false")); //Example
		
		JsonConfig.registerBlockTemperature(Blocks.LIT_FURNACE, 4.0f);
		JsonConfig.registerBlockTemperature(Blocks.LAVA, 12.5f);
		JsonConfig.registerBlockTemperature(Blocks.FLOWING_LAVA, 12.5f);
		JsonConfig.registerBlockTemperature(Blocks.MAGMA, 10f);
		
		JsonConfig.registerConsumableTemperature(TemporaryModifierGroupEnum.FOOD.group(), new ItemStack(Items.MUSHROOM_STEW), 1.0f, 1200);
		JsonConfig.registerConsumableTemperature(TemporaryModifierGroupEnum.DRINK.group(), new ItemStack(SDItems.juice,1,JuiceEnum.CACTUS.ordinal()),  1.0f, 1200);
		JsonConfig.registerConsumableTemperature(TemporaryModifierGroupEnum.DRINK.group(), new ItemStack(SDItems.juice,1,JuiceEnum.CHORUS_FRUIT.ordinal()), 1.0f, 2400);
		
		JsonConfig.registerConsumableThirst(new ItemStack(Items.MILK_BUCKET), 4, 1.0f, 0.2f);
		
		JsonConfig.registerHeldItem(new ItemStack(Blocks.MAGMA), 3.0f);
		JsonConfig.registerHeldItem(new ItemStack(Blocks.TORCH), 1.0f);
		//materialTemperature is not a Map
		
		//Mod Compatibility
		
		JsonCompatDefaults.instance.populate();
		
		//TODO consider how fluidTemperatures should actually be stored. is Fluid name a reliable way to get their name? 
		
		processAllJson(jsonDirectory);
	}
	
	public static void processAllJson(File jsonDirectory)
	{
		//Maps currently have defaults
		//Need to load the JSON
		//If the JSON process worked, register anything in the JSON
		
		//Process JSON
		
		/*JsonConfig.armorTemperatures = processJson(JsonFileName.armorTemperatures.get(), JsonConfig.armorTemperatures, JsonTypeToken.get(JsonFileName.armorTemperatures), jsonDirectory);
		JsonConfig.blockTemperatures = processJson(JsonFileName.blockTemperatures.get(), JsonConfig.blockTemperatures, JsonTypeToken.get(JsonFileName.blockTemperatures), jsonDirectory);
		JsonConfig.consumableTemperature = processJson(JsonFileName.consumableTemperature.get(), JsonConfig.consumableTemperature, JsonTypeToken.get(JsonFileName.consumableTemperature), jsonDirectory);
		JsonConfig.consumableThirst = processJson(JsonFileName.consumableThirst.get(), JsonConfig.consumableThirst, JsonTypeToken.get(JsonFileName.consumableThirst), jsonDirectory);
		JsonConfig.fluidTemperatures = processJson(JsonFileName.fluidTemperatures.get(), JsonConfig.fluidTemperatures, JsonTypeToken.get(JsonFileName.fluidTemperatures), jsonDirectory);
		JsonConfig.heldItemTemperatures = processJson(JsonFileName.heldItemTemperatures.get(), JsonConfig.heldItemTemperatures, JsonTypeToken.get(JsonFileName.heldItemTemperatures), jsonDirectory);
		*/
		
		//Armor Temperatures
		String jsonFileName = JsonFileName.armorTemperatures.get();
		Map<String, JsonTemperature> jsonArmorTemperatures = processJson(JsonFileName.armorTemperatures, JsonConfig.armorTemperatures, jsonDirectory, true);
		if(jsonArmorTemperatures!=null)
		{
			for(Map.Entry<String, JsonTemperature> entry : jsonArmorTemperatures.entrySet())
			{
				JsonConfig.registerArmorTemperature(entry.getKey(), entry.getValue().temperature);
			}
			
			try
			{
				manuallyWriteToJson(JsonFileName.armorTemperatures, JsonConfig.armorTemperatures, jsonDirectory);
			}
			catch (Exception e)
			{
				logMerge(jsonFileName,e);
			}
		}
		
		//Block Temperatures
		jsonFileName = JsonFileName.blockTemperatures.get();
		Map<String, List<JsonPropertyTemperature>> jsonBlockTemperatures = processJson(JsonFileName.blockTemperatures, JsonConfig.blockTemperatures, jsonDirectory, true);
		if(jsonBlockTemperatures!=null)
		{
			for(Map.Entry<String, List<JsonPropertyTemperature>> entry : jsonBlockTemperatures.entrySet())
			{
				for(JsonPropertyTemperature propTemp : entry.getValue())
				{
					JsonConfig.registerBlockTemperature(entry.getKey(), propTemp.temperature, propTemp.getAsPropertyArray());
				}
			}
			
			try
			{
				manuallyWriteToJson(JsonFileName.blockTemperatures, JsonConfig.blockTemperatures, jsonDirectory);
			}
			catch (Exception e)
			{
				logMerge(jsonFileName,e);
			}
		}
		
		//Consumable Temperature
		jsonFileName = JsonFileName.consumableTemperature.get();
		Map<String, List<JsonConsumableTemperature>> jsonConsumableTemperature = processJson(JsonFileName.consumableTemperature, JsonConfig.consumableTemperature, jsonDirectory, true);
		if(jsonConsumableTemperature!=null)
		{
			for(Map.Entry<String, List<JsonConsumableTemperature>> entry : jsonConsumableTemperature.entrySet())
			{
				for(JsonConsumableTemperature jct : entry.getValue())
				{
					JsonConfig.registerConsumableTemperature(jct.group, entry.getKey(), jct.metadata, jct.temperature, jct.duration);
				}
			}
			
			try
			{
				manuallyWriteToJson(JsonFileName.consumableTemperature, JsonConfig.consumableTemperature, jsonDirectory);
			}
			catch (Exception e)
			{
				logMerge(jsonFileName,e);
			}
		}
		
		//Consumable Thirst
		jsonFileName = JsonFileName.consumableThirst.get();
		Map<String, List<JsonConsumableThirst>> jsonConsumableThirst = processJson(JsonFileName.consumableThirst, JsonConfig.consumableThirst, jsonDirectory, true);
		if(jsonConsumableThirst!=null)
		{
			for(Map.Entry<String, List<JsonConsumableThirst>> entry : jsonConsumableThirst.entrySet())
			{
				for(JsonConsumableThirst jct : entry.getValue())
				{
					JsonConfig.registerConsumableThirst(entry.getKey(), jct.metadata, jct.amount, jct.saturation, jct.thirstyChance);
				}
			}
			
			try
			{
				manuallyWriteToJson(JsonFileName.consumableThirst, JsonConfig.consumableThirst, jsonDirectory);
			}
			catch (Exception e)
			{
				logMerge(jsonFileName,e);
			}
		}
		
		//Fluid Temperatures
		jsonFileName = JsonFileName.fluidTemperatures.get();
		Map<String, JsonTemperature> jsonFluidTemperatures = processJson(JsonFileName.fluidTemperatures, JsonConfig.fluidTemperatures, jsonDirectory, true);
		if(jsonFluidTemperatures!=null)
		{
			for(Map.Entry<String, JsonTemperature> entry : jsonFluidTemperatures.entrySet())
			{
				JsonConfig.registerFluidTemperature(entry.getKey(), entry.getValue().temperature);
			}
			
			try
			{
				manuallyWriteToJson(JsonFileName.fluidTemperatures, JsonConfig.fluidTemperatures, jsonDirectory);
			}
			catch (Exception e)
			{
				logMerge(jsonFileName,e);
			}
		}
		
		jsonFileName = JsonFileName.heldItemTemperatures.get();
		Map<String, List<JsonTemperatureMetadata>> jsonHeldItemTemperatures = processJson(JsonFileName.heldItemTemperatures, JsonConfig.heldItemTemperatures, jsonDirectory, true);
		if(jsonHeldItemTemperatures!=null)
		{
			for(Map.Entry<String, List<JsonTemperatureMetadata>> entry : jsonHeldItemTemperatures.entrySet())
			{
				for(JsonTemperatureMetadata jtm : entry.getValue())
				{
					JsonConfig.registerHeldItem(entry.getKey(), jtm.metadata, jtm.temperature);
				}
			}
			
			try
			{
				manuallyWriteToJson(JsonFileName.heldItemTemperatures, JsonConfig.heldItemTemperatures, jsonDirectory);
			}
			catch (Exception e)
			{
				logMerge(jsonFileName,e);
			}
		}
		
		//Material Temperature
		materialTemperature = processJson(JsonFileName.materialTemperature, materialTemperature, jsonDirectory, false);
		
	}
	
	private static void logMerge(String jsonFileName, Exception e)
	{
		SimpleDifficulty.logger.error("Error writing merged JSON File: "+jsonFileName, e);
		jsonErrors.add("config/simpledifficulty/"+jsonFileName+" failed to load!");
	}
	
	public static String manuallyExportAll()
	{
		File jsonDirectory = SimpleDifficulty.jsonDirectory;
		
		try
		{
			manuallyWriteToJson(JsonFileName.armorTemperatures, JsonConfig.armorTemperatures, jsonDirectory);
			manuallyWriteToJson(JsonFileName.blockTemperatures, JsonConfig.blockTemperatures, jsonDirectory);
			manuallyWriteToJson(JsonFileName.consumableTemperature, JsonConfig.consumableTemperature, jsonDirectory);
			manuallyWriteToJson(JsonFileName.consumableThirst, JsonConfig.consumableThirst, jsonDirectory);
			manuallyWriteToJson(JsonFileName.fluidTemperatures, JsonConfig.fluidTemperatures, jsonDirectory);
			manuallyWriteToJson(JsonFileName.heldItemTemperatures, JsonConfig.heldItemTemperatures, jsonDirectory);
			manuallyWriteToJson(JsonFileName.materialTemperature, materialTemperature, jsonDirectory);
			
			
			/*
			armorTemperatures
			blockTemperatures
			consumableTemperature
			consumableThirst
			fluidTemperatures
			heldItemTemperatures
			materialTemperature
			*/
			return "Successfully exported SimpleDifficulty configuration to JSON";
		} 
		catch (Exception e)
		{
			SimpleDifficulty.logger.error("Export to JSON Failure Details", e);
			return "Export to JSON FAILED! See log for details.";
		}
	}

	
	//public static <T> T processJson(String jsonFileName, final T container, Type type, File jsonDirectory, boolean forMerging)
	/** Nullable when forMerging is true */
	@Nullable
	public static <T> T processJson(JsonFileName jfn, final T container, File jsonDirectory, boolean forMerging)
	{
		try
		{
			return processUncaughtJson(jfn, container, jsonDirectory, forMerging);
		}
		catch(Exception e)
		{
			SimpleDifficulty.logger.error("Error managing JSON File: "+jfn.get(), e);
			jsonErrors.add("config/simpledifficulty/"+jfn.get()+" failed to load!");
			if(forMerging)
			{
				return null;
			}
			else
			{
				return container;
			}
		}
	}
	
	@Nullable
	//public static <T> T processUncaughtJson(String jsonFileName, final T container, Type type, File jsonDirectory, boolean forMerging) throws Exception
	public static <T> T processUncaughtJson(JsonFileName jfn, final T container, File jsonDirectory, boolean forMerging) throws Exception
	{
		String jsonFileName = jfn.get();
		Type type = JsonTypeToken.get(jfn);
		
		File jsonFile = new File(jsonDirectory,jsonFileName);
		if(jsonFile.exists())
		{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			//Read
			//System.out.println("fromJson");
			return (T) gson.fromJson(new FileReader(jsonFile), type);
		}
		else
		{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			//Write
			//System.out.println("toJson");
			//System.out.println(jsonFile.getAbsolutePath());
			
			FileUtils.write(jsonFile,gson.toJson(container, type),(String)null);
			if(forMerging)
			{
				return null;
			}
			else
			{
				return container;
			}
		}
	}
	
	//private static <T> void manuallyWriteToJson(String jsonFileName, final T container, Type type, File jsonDirectory) throws Exception
	private static <T> void manuallyWriteToJson(JsonFileName jfn, final T container, File jsonDirectory) throws Exception
	{
		String jsonFileName = jfn.get();
		Type type = JsonTypeToken.get(jfn);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File jsonFile = new File(jsonDirectory,jsonFileName);
		FileUtils.write(jsonFile, gson.toJson(container, type),(String)null);
	}
}
