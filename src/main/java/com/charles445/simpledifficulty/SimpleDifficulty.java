package com.charles445.simpledifficulty;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.charles445.simpledifficulty.debug.DebugVerifier;
import com.charles445.simpledifficulty.network.PacketHandler;
import com.charles445.simpledifficulty.proxy.IProxy;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod
(
	modid = SimpleDifficulty.MODID, 
	name = SimpleDifficulty.NAME, 
	version = SimpleDifficulty.VERSION,
	acceptedMinecraftVersions = "[1.12]"
	
)
public class SimpleDifficulty 
{
	public static final String MODID = "simpledifficulty";
	public static final String NAME = "SimpleDifficulty";
	public static final String VERSION = "0.1.0";
	
	@Mod.Instance(SimpleDifficulty.MODID)
	public static SimpleDifficulty instance;
	
	public static Logger logger = LogManager.getLogger("SimpleDifficulty");
	
	@SidedProxy(clientSide = "com.charles445.simpledifficulty.proxy.ClientProxy",
				serverSide = "com.charles445.simpledifficulty.proxy.ServerProxy")
	public static IProxy proxy;
	
	public static File jsonDirectory;
	
	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent event)
	{
		jsonDirectory = new File(event.getModConfigurationDirectory(), SimpleDifficulty.MODID);
		
		PacketHandler.init();
		proxy.preInit();
		
	}
	
	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
	}
	
	@Mod.EventHandler
	public static void loadComplete(FMLLoadCompleteEvent event)
	{
		DebugVerifier verifier = new DebugVerifier();
		verifier.verify();
	}
	
	static
	{
		FluidRegistry.enableUniversalBucket();
	}
}
