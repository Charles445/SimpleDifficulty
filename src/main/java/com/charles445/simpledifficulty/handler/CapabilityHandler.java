package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.capability.TemperatureProvider;
import com.charles445.simpledifficulty.capability.ThirstProvider;
import com.charles445.simpledifficulty.network.MessageUpdateTemperature;
import com.charles445.simpledifficulty.network.MessageUpdateThirst;
import com.charles445.simpledifficulty.network.PacketHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class CapabilityHandler
{
	
	@SubscribeEvent
	public void onAttachCapability(AttachCapabilitiesEvent event)
	{
		//Find player
		if(event.getObject() instanceof EntityPlayer)
		{
			//EntityPlayer player = (EntityPlayer)event.getObject();
			
			//Attach capabilities
			
			//Attach temperature
			event.addCapability(new ResourceLocation(SimpleDifficulty.MODID, SDCapabilities.TEMPERATURE_IDENTIFIER), new TemperatureProvider(SDCapabilities.TEMPERATURE));
			
			//Attach thirst
			//event.addCapability(new ResourceLocation(SimpleDifficulty.MODID, SimpleDifficultyCapabilities.THIRST_IDENTIFIER), new GenericProvider(SimpleDifficultyCapabilities.THIRST));
			//DebugUtil.messageAll("Capability attached to player");
			event.addCapability(new ResourceLocation(SimpleDifficulty.MODID, SDCapabilities.THIRST_IDENTIFIER), new ThirstProvider(SDCapabilities.THIRST));
		}
	}
	
	/* Player doesn't exist client side yet so this isn't going to work
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event)
	{
		EntityPlayer player = event.player;
		World world = player.world;
		
		if(world.isRemote)
			return;
		
		forceThirstUpdate(player);
	}
	*/
	
	@SubscribeEvent
	public void onClonePlayer(Clone event)
	{
		//TODO Untested
		
		EntityPlayer player = event.getEntityPlayer();
		World world = player.world;
		
		if(world.isRemote)
			return;
		
		forceCapabilityUpdate(player);
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		EntityPlayer player = event.player;
		World world = player.world;
		
		if(world.isRemote)
		{
			
			//Clientside sprinting
			//This is better but you can still hold control
			/*
				if(QuickConfig.isThirstEnabled() && player.isSprinting() && ModCapabilities.getThirstData(player).getThirstLevel()<=6)
				player.setSprinting(false);
			*/		
			return;
		}
			
		//Update Temperature
		if(QuickConfig.isTemperatureEnabled())
		{
			ITemperatureCapability temperatureCapability = SDCapabilities.getTemperatureData(player);
			temperatureCapability.tickUpdate(player, world, event.phase);
			
			if(temperatureCapability.isDirty() && event.phase == TickEvent.Phase.START)
			{
				temperatureCapability.setClean();
				sendTemperatureUpdate(player);
			}
		}
		
		//Update Thirst
		if(QuickConfig.isThirstEnabled() && !shouldPlayerSkipThirst(player))
		{
			IThirstCapability thirstCapability = SDCapabilities.getThirstData(player);
			thirstCapability.tickUpdate(player,world,event.phase);
			
			if(thirstCapability.isDirty() && event.phase == TickEvent.Phase.START)
			{
				thirstCapability.setClean();
				sendThirstUpdate(player);
			}
		}
	}
	
	private boolean shouldPlayerSkipThirst(EntityPlayer player)
	{
		//Skip thirst capability if player is in spectator or creative
		return player.isSpectator() || player.isCreative();
	}
	
	private void forceCapabilityUpdate(EntityPlayer player)
	{
		if(QuickConfig.isTemperatureEnabled())
		{
			ITemperatureCapability tempCapability = SDCapabilities.getTemperatureData(player);
			tempCapability.setClean();
		}
		
		if(QuickConfig.isThirstEnabled())
		{
			IThirstCapability thirstCapability = SDCapabilities.getThirstData(player);
			thirstCapability.setClean();
			sendThirstUpdate(player);
		}
	}
	
	private void sendTemperatureUpdate(EntityPlayer player)
	{
		//TODO EntityPlayerMP issues? Is this ever a problem?
		
		Capability capability = SDCapabilities.TEMPERATURE;
		
		//Make new message with new data
		MessageUpdateTemperature message = new MessageUpdateTemperature(capability.getStorage().writeNBT(capability, SDCapabilities.getTemperatureData(player), null));
		
		//Send to player
		PacketHandler.instance.sendTo(message, (EntityPlayerMP) player);
	}
	
	private void sendThirstUpdate(EntityPlayer player)
	{
		//TODO EntityPlayerMP issues? Is this ever a problem?
		
		//DebugUtil.messageAll("Player thirst has updated via sendThirstUpdate");
		Capability capability = SDCapabilities.THIRST;
		
		//Make new message with new data
		MessageUpdateThirst message = new MessageUpdateThirst(capability.getStorage().writeNBT(capability, SDCapabilities.getThirstData(player), null));
		
		//Send to player
		PacketHandler.instance.sendTo(message, (EntityPlayerMP) player);
	}
}
