package com.charles445.simpledifficulty;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.charles445.simpledifficulty.command.CommandSimpleDifficulty;
import com.charles445.simpledifficulty.debug.DebugVerifier;
import com.charles445.simpledifficulty.network.PacketHandler;
import com.charles445.simpledifficulty.proxy.IProxy;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod
(
	modid = SimpleDifficulty.MODID, 
	name = SimpleDifficulty.NAME, 
	version = SimpleDifficulty.VERSION,
	acceptedMinecraftVersions = "[1.12, 1.13)",
	updateJSON = "https://raw.githubusercontent.com/Charles445/SimpleDifficulty/master/modupdatechecker.json"
	
)
public class SimpleDifficulty 
{
	public static final String MODID = "simpledifficulty";
	public static final String NAME = "SimpleDifficulty";
	public static final String VERSION = "0.3.7";
	
	@Mod.Instance(SimpleDifficulty.MODID)
	public static SimpleDifficulty instance;
	
	public static Logger logger = LogManager.getLogger("SimpleDifficulty");
	
	@SidedProxy(clientSide = "com.charles445.simpledifficulty.proxy.ClientProxy",
				serverSide = "com.charles445.simpledifficulty.proxy.ServerProxy")
	public static IProxy proxy;
	
	public static File jsonDirectory;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		jsonDirectory = new File(event.getModConfigurationDirectory(), SimpleDifficulty.MODID);
		
		PacketHandler.init();
		proxy.preInit();
		
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
	}
	
	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		DebugVerifier verifier = new DebugVerifier();
		verifier.verify();
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandSimpleDifficulty());
	}
	
	static
	{
		FluidRegistry.enableUniversalBucket();
	}
}
