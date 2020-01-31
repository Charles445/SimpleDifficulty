package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class ConfigHandler
{
	//Config update when player logs in
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event)
	{
		EntityPlayer player = event.player;
		World world = player.world;
		
		if(world.isRemote)
			return;
		
		//Server Side
		//TODO Casting safety
		ModConfig.sendServerConfigToPlayer((EntityPlayerMP) player);
	}
	
	//Config update when player goes back to title screen
	@SubscribeEvent
	public void onWorldEventUnload(WorldEvent.Unload event)
	{
		if(event.getWorld().isRemote)
		{
			//Client Side
			Boolean connectedToServer = SimpleDifficulty.proxy.isClientConnectedToServer();
			if(connectedToServer==null)
			{
				SimpleDifficulty.logger.error("Server proxy called isClientConnectedToServer while world was remote!");
				return;
			}
			if(!connectedToServer.booleanValue())
			{
				//Not connected to anyone
				ModConfig.sendLocalClientConfigToAPI();
				ModConfig.sendLocalServerConfigToAPI();
			}
		}
	}
}
