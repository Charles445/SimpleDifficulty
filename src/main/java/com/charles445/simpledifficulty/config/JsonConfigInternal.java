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
		//Process JSON
		
		JsonConfig.armorTemperatures = processJson(JsonFileName.armorTemperatures.get(), JsonConfig.armorTemperatures, JsonTypeToken.get(JsonFileName.armorTemperatures), jsonDirectory);
		JsonConfig.consumableTemperature = processJson(JsonFileName.consumableTemperature.get(), JsonConfig.consumableTemperature, JsonTypeToken.get(JsonFileName.consumableTemperature), jsonDirectory);
		JsonConfig.consumableThirst = processJson(JsonFileName.consumableThirst.get(), JsonConfig.consumableThirst, JsonTypeToken.get(JsonFileName.consumableThirst), jsonDirectory);
		JsonConfig.fluidTemperatures = processJson(JsonFileName.fluidTemperatures.get(), JsonConfig.fluidTemperatures, JsonTypeToken.get(JsonFileName.fluidTemperatures), jsonDirectory);
		JsonConfig.heldItemTemperatures = processJson(JsonFileName.heldItemTemperatures.get(), JsonConfig.heldItemTemperatures, JsonTypeToken.get(JsonFileName.heldItemTemperatures), jsonDirectory);
		materialTemperature = processJson(JsonFileName.materialTemperature.get(), materialTemperature, JsonTypeToken.get(JsonFileName.materialTemperature), jsonDirectory);
		//blockTemperatures migration (legacy support for 0.1.0 and 0.1.1)
		//TODO once enough versions have passed, get rid of this whole thing and just leave it as what's in the try block (but with processJson instead)
		
		try
		{
			JsonConfig.blockTemperatures = processUncaughtJson(JsonFileName.blockTemperatures.get(), JsonConfig.blockTemperatures, JsonTypeToken.get(JsonFileName.blockTemperatures), jsonDirectory);
		}
		catch(Exception e)
		{
			//Attempt to read old format "<String, JsonPropertyTemperature>"
			Map<String, JsonPropertyTemperature> dummyBlockTemperatures = new HashMap<String, JsonPropertyTemperature>();
			//Manually created since it's legacy support
			dummyBlockTemperatures = processJson("blockTemperatures.json", dummyBlockTemperatures, new TypeToken<Map<String, JsonPropertyTemperature>>(){}.getType(), jsonDirectory);
			if(!dummyBlockTemperatures.isEmpty())
			{
				//Migrate the old format to the new format
				SimpleDifficulty.logger.info("Attempting to migrate old blockTemperatures.json format");
				JsonConfig.blockTemperatures.clear();
				for(Map.Entry<String, JsonPropertyTemperature> entry : dummyBlockTemperatures.entrySet())
				{
					JsonConfig.blockTemperatures.put(entry.getKey(), Arrays.asList(entry.getValue()));
				}
				
				//Migration finished, overwrite old
				
				try
				{
					manuallyWriteToJson(JsonFileName.blockTemperatures.get(), JsonConfig.blockTemperatures, JsonTypeToken.get(JsonFileName.blockTemperatures), jsonDirectory);
				}
				catch (Exception e1)
				{
					//Didn't work.
					jsonErrors.add("config/simpledifficulty/blockTemperatures.json is in an old format. Please delete it!");
				}
			}
		}
	}
	
	public static String manuallyExportAll()
	{
		File jsonDirectory = SimpleDifficulty.jsonDirectory;
		
		try
		{
			manuallyWriteToJson(JsonFileName.armorTemperatures.get(), JsonConfig.armorTemperatures, JsonTypeToken.get(JsonFileName.armorTemperatures), jsonDirectory);
			manuallyWriteToJson(JsonFileName.blockTemperatures.get(), JsonConfig.blockTemperatures, JsonTypeToken.get(JsonFileName.blockTemperatures), jsonDirectory);
			manuallyWriteToJson(JsonFileName.consumableTemperature.get(), JsonConfig.consumableTemperature, JsonTypeToken.get(JsonFileName.consumableTemperature), jsonDirectory);
			manuallyWriteToJson(JsonFileName.consumableThirst.get(), JsonConfig.consumableThirst, JsonTypeToken.get(JsonFileName.consumableThirst), jsonDirectory);
			manuallyWriteToJson(JsonFileName.fluidTemperatures.get(), JsonConfig.fluidTemperatures, JsonTypeToken.get(JsonFileName.fluidTemperatures), jsonDirectory);
			manuallyWriteToJson(JsonFileName.heldItemTemperatures.get(), JsonConfig.heldItemTemperatures, JsonTypeToken.get(JsonFileName.heldItemTemperatures), jsonDirectory);
			manuallyWriteToJson(JsonFileName.materialTemperature.get(), materialTemperature, JsonTypeToken.get(JsonFileName.materialTemperature), jsonDirectory);
			
			
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
	
	public static <T> T processJson(String jsonFileName, final T container, Type type, File jsonDirectory)
	{
		try
		{
			return processUncaughtJson(jsonFileName, container, type, jsonDirectory);
		}
		catch(Exception e)
		{
			SimpleDifficulty.logger.error("Error managing JSON File: "+jsonFileName, e);
			jsonErrors.add("config/simpledifficulty/"+jsonFileName+" failed to load!");
			return container;
		}
	}
	
	public static <T> T processUncaughtJson(String jsonFileName, final T container, Type type, File jsonDirectory) throws Exception
	{
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
			return container;
		}
		
	}
	
	private static <T> void manuallyWriteToJson(String jsonFileName, final T container, Type type, File jsonDirectory) throws Exception
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File jsonFile = new File(jsonDirectory,jsonFileName);
		FileUtils.write(jsonFile, gson.toJson(container, type),(String)null);
	}
}
