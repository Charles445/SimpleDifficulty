package com.charles445.simpledifficulty.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.config.json.PropertyValue;
import com.charles445.simpledifficulty.config.json.ArmorTemperature;
import com.charles445.simpledifficulty.config.json.BlockTemperature;
import com.charles445.simpledifficulty.config.json.MaterialTemperature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraft.init.Blocks;

public class JsonConfig
{
	//TODO doesn't support different states on the same block...
	//JSON stresses me out
	
	//TODO this whole thing isn't good
	//TODO now that I get how this stuff works, sort of, make it better
	
	public static Map<String, ArmorTemperature> armorTemperatures = new HashMap<String, ArmorTemperature>();
	
	public static Map<String, BlockTemperature> blockTemperatures = new HashMap<String, BlockTemperature>();
	
	//TODO customizable materials? Probably not
	public static MaterialTemperature materialTemperature = new MaterialTemperature();
	
	public static void init(File jsonDirectory)
	{
		armorTemperatures.put(SDItems.wool_helmet.getRegistryName().toString(), new ArmorTemperature(2.0f));
		armorTemperatures.put(SDItems.wool_chestplate.getRegistryName().toString(), new ArmorTemperature(2.0f));
		armorTemperatures.put(SDItems.wool_leggings.getRegistryName().toString(), new ArmorTemperature(2.0f));
		armorTemperatures.put(SDItems.wool_boots.getRegistryName().toString(), new ArmorTemperature(2.0f));
		
		armorTemperatures.put(SDItems.ice_helmet.getRegistryName().toString(), new ArmorTemperature(-2.0f));
		armorTemperatures.put(SDItems.ice_chestplate.getRegistryName().toString(), new ArmorTemperature(-2.0f));
		armorTemperatures.put(SDItems.ice_leggings.getRegistryName().toString(), new ArmorTemperature(-2.0f));
		armorTemperatures.put(SDItems.ice_boots.getRegistryName().toString(), new ArmorTemperature(-2.0f));
		
		
		armorTemperatures = processJson("armorTemperatures.json", armorTemperatures, new TypeToken<Map<String, ArmorTemperature>>(){}.getType(), jsonDirectory);
		
		//blockTemperatures.put(Blocks.LIT_FURNACE.getRegistryName().toString(),
		//		new BlockTemperature(3.0f, new BlockProperty("burning","true")));

		blockTemperatures.put(SDBlocks.campfire.getRegistryName().toString(), new BlockTemperature(7.0f, new PropertyValue("burning","true")));
		blockTemperatures.put(Blocks.LIT_FURNACE.getRegistryName().toString(), new BlockTemperature(3.0f));
		blockTemperatures.put(Blocks.LAVA.getRegistryName().toString(), new BlockTemperature(15.0f));
		blockTemperatures.put(Blocks.FLOWING_LAVA.getRegistryName().toString(), new BlockTemperature(15.0f));
		blockTemperatures.put(Blocks.MAGMA.getRegistryName().toString(), new BlockTemperature(12.5f));
		
		blockTemperatures = processJson("blockTemperatures.json", blockTemperatures, new TypeToken<Map<String, BlockTemperature>>(){}.getType(), jsonDirectory);
		
		materialTemperature = processJson("materialTemperature.json", materialTemperature, new TypeToken<MaterialTemperature>(){}.getType(), jsonDirectory);
		
		/*
		for(Map.Entry<String, BlockTemperature> entry : blockTemperatures.entrySet())
		{
			SimpleDifficulty.logger.debug(entry.getKey());
			SimpleDifficulty.logger.debug(entry.getValue().temperature);
			SimpleDifficulty.logger.debug(entry.getValue().properties.size());
		}
		*/
	}
	
	public static <T> T processJson(String jsonFileName, final T container, Type type, File jsonDirectory)
	{
		try
		{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			File jsonFile = new File(jsonDirectory,jsonFileName);
			if(jsonFile.exists())
			{
				//Read
				//System.out.println("fromJson");
				return (T) gson.fromJson(new FileReader(jsonFile), type);
			}
			else
			{
				//Write
				//System.out.println("toJson");
				//System.out.println(jsonFile.getAbsolutePath());
				
				FileUtils.write(jsonFile,gson.toJson(container, type),(String)null);
				return container;
			}
		}
		catch(Exception e)
		{
			SimpleDifficulty.logger.error("Error reading JSON File: "+jsonFileName, e);
			return container;
		}
	}
}
