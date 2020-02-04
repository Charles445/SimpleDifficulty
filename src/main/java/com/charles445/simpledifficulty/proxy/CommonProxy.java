package com.charles445.simpledifficulty.proxy;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.capability.TemperatureCapability;
import com.charles445.simpledifficulty.capability.TemperatureStorage;
import com.charles445.simpledifficulty.capability.ThirstCapability;
import com.charles445.simpledifficulty.capability.ThirstStorage;
import com.charles445.simpledifficulty.compat.CompatController;
import com.charles445.simpledifficulty.config.JsonConfigInternal;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.handler.BlockHandler;
import com.charles445.simpledifficulty.handler.CapabilityHandler;
import com.charles445.simpledifficulty.handler.ConfigHandler;
import com.charles445.simpledifficulty.handler.MiscHandler;
import com.charles445.simpledifficulty.handler.TemperatureHandler;
import com.charles445.simpledifficulty.handler.ThirstHandler;
import com.charles445.simpledifficulty.temperature.ModifierAltitude;
import com.charles445.simpledifficulty.temperature.ModifierArmor;
import com.charles445.simpledifficulty.temperature.ModifierBiome;
import com.charles445.simpledifficulty.temperature.ModifierBlocksTiles;
import com.charles445.simpledifficulty.temperature.ModifierDefault;
import com.charles445.simpledifficulty.temperature.ModifierSnow;
import com.charles445.simpledifficulty.temperature.ModifierSprint;
import com.charles445.simpledifficulty.temperature.ModifierTemporary;
import com.charles445.simpledifficulty.temperature.ModifierTime;
import com.charles445.simpledifficulty.temperature.ModifierWet;
import com.charles445.simpledifficulty.util.internal.TemperatureUtilInternal;
import com.charles445.simpledifficulty.util.internal.ThirstUtilInternal;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;

public abstract class CommonProxy implements IProxy
{
	//TODO migrate all of this to the main mod class? It might as well not be here
	//Or just hook up stuff with annotations like forge appears to want
	
	@Override
	public void preInit()
	{
		//Setup utility
		TemperatureUtil.internal = new TemperatureUtilInternal();
		ThirstUtil.internal = new ThirstUtilInternal();
		
		//Setup initial configurations
		ModConfig.sendLocalServerConfigToAPI();
		ModConfig.sendLocalClientConfigToAPI();
		
		//Register Capabilities
		CapabilityManager.INSTANCE.register(ITemperatureCapability.class, new TemperatureStorage(), TemperatureCapability::new);
		CapabilityManager.INSTANCE.register(IThirstCapability.class, new ThirstStorage(), ThirstCapability::new);
		
		//Register Mod Handlers
		MinecraftForge.EVENT_BUS.register(new BlockHandler());
		MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
		MinecraftForge.EVENT_BUS.register(new ConfigHandler());
		MinecraftForge.EVENT_BUS.register(new MiscHandler());
		MinecraftForge.EVENT_BUS.register(new TemperatureHandler());
		MinecraftForge.EVENT_BUS.register(new ThirstHandler());
		
		//Populate TemperatureRegistry
		TemperatureRegistry.registerModifier(new ModifierDefault());
		TemperatureRegistry.registerModifier(new ModifierAltitude());
		TemperatureRegistry.registerModifier(new ModifierArmor());
		TemperatureRegistry.registerModifier(new ModifierBiome());
		TemperatureRegistry.registerModifier(new ModifierBlocksTiles());
		TemperatureRegistry.registerModifier(new ModifierSnow());
		TemperatureRegistry.registerModifier(new ModifierSprint());
		TemperatureRegistry.registerModifier(new ModifierTemporary());
		TemperatureRegistry.registerModifier(new ModifierTime());
		TemperatureRegistry.registerModifier(new ModifierWet());
	}
	
	@Override
	public void init()
	{
		
	}
	
	@Override
	public void postInit()
	{
		//Setup JSON Configurations
		JsonConfigInternal.init(SimpleDifficulty.jsonDirectory);
		
		//Setup Mod Compatibility
		CompatController.setup();
	}
}
