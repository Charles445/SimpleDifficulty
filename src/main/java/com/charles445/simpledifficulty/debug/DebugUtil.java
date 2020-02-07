package com.charles445.simpledifficulty.debug;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.ClientConfig;
import com.charles445.simpledifficulty.api.config.ClientOptions;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class DebugUtil
{	
	private static long elapsedTotal = 0L;
	private static long elapsedInst = 0L;
	private static long elapsed = 0L;
	private static long snapshot = 0L;
	private static long snapshotTime = 0L;
	private static long count = 0L;
	
	private static long nanoCache = 0L;
	
	public static void messageAll(String s)
	{
		if(ServerConfig.instance.getBoolean(ServerOptions.DEBUG))
		{
			for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
			{
				player.sendMessage(new TextComponentString(s));
			}
		}
	}
	
	public static void clientMessage(EntityPlayer player, String s)
	{
		if(player.getEntityWorld().isRemote)
		{
			//Client side
			if(!ClientConfig.instance.getBoolean(ClientOptions.DEBUG))
				return;
		}
		else
		{
			if(!ServerConfig.instance.getBoolean(ServerOptions.DEBUG))
				return;
		}
		
		player.sendMessage(new TextComponentString(s));
	}
	
	public static void startTimer()
	{
		elapsed = System.nanoTime();
	}
	
	public static void stopTimer(boolean client)
	{
		nanoCache = System.nanoTime(); 
		elapsedInst = nanoCache - elapsed;
		elapsedTotal += elapsedInst;
		count++;
		
		if(count == 0)
			return;
		
		if(snapshotTime < nanoCache)
		{
			if(client)
			{
				EntityPlayer player = SimpleDifficulty.proxy.getClientMinecraftPlayer();
				if(player!=null)
					DebugUtil.clientMessage(player, ""+(elapsedTotal - snapshot+" : "+count + " ("+((elapsedTotal-snapshot)/count))+")");
			}
			else
			{
				DebugUtil.messageAll(""+(elapsedTotal - snapshot+" : "+count + " ("+((elapsedTotal-snapshot)/count))+")");
			}
			
			snapshot = elapsedTotal;
			count = 0;
			snapshotTime = System.nanoTime() + 1000000000L;
		}
	}
}
