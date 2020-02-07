package com.charles445.simpledifficulty.proxy;

import java.io.File;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.client.gui.TemperatureGui;
import com.charles445.simpledifficulty.client.gui.ThirstGui;
import com.charles445.simpledifficulty.compat.CompatController;
import com.charles445.simpledifficulty.handler.TooltipHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit() 
	{
		super.preInit();
		MinecraftForge.EVENT_BUS.register(new TemperatureGui());
		MinecraftForge.EVENT_BUS.register(new ThirstGui());
		MinecraftForge.EVENT_BUS.register(new TooltipHandler());
	}
	
	@Override
	public void init()
	{
		super.init();
	}
	
	@Override
	public void postInit()
	{
		super.postInit();
		
		//Setup Mod Compatibility
		CompatController.setupClient();
	}

	@Override
	public Side getSide()
	{
		return Side.CLIENT;	
	}

	@Override
	public EntityPlayer getClientMinecraftPlayer()
	{
		return Minecraft.getMinecraft().player;
	}

	@Override
	public Boolean isClientConnectedToServer()
	{
		return Minecraft.getMinecraft().getConnection().getNetworkManager().isChannelOpen();
	}
}
