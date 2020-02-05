package com.charles445.simpledifficulty.capability;

import java.util.HashMap;
import java.util.Map;

import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.api.temperature.TemporaryModifier;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.debug.DebugUtil;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TemperatureCapability implements ITemperatureCapability
{
	private int temperature = 12;
	private int ticktimer = 0;
	
	private Map<String, TemporaryModifier> temporaryModifiers = new HashMap<String, TemporaryModifier>();
	
	//Unsaved data
	private int oldtemperature = 0;
	private int updatetimer = 500; //Update immediately first time around
	private int targettemp = 0;
	private int debugtimer = 0;
	private boolean manualDirty = false;
	private int oldmodifiersize = 0;

	@Override
	public int getTemperatureLevel()
	{
		return temperature;
	}

	@Override
	public int getTemperatureTickTimer()
	{
		return ticktimer;
	}

	@Override
	public void setTemperatureLevel(int temperature)
	{
		this.temperature = TemperatureUtil.clampTemperature(temperature);
	}

	@Override
	public void setTemperatureTickTimer(int ticktimer)
	{
		this.ticktimer=ticktimer;	
	}

	@Override
	public void addTemperatureLevel(int temperature)
	{
		this.setTemperatureLevel(this.getTemperatureLevel() + temperature);
	}

	@Override
	public void addTemperatureTickTimer(int ticktimer)
	{
		this.setTemperatureTickTimer(this.getTemperatureTickTimer() + ticktimer);
	}

	@Override
	public void tickUpdate(EntityPlayer player, World world, TickEvent.Phase phase)
	{
		//NOTE Player can be in Creative or Spectator
		
		if(phase==TickEvent.Phase.START)
			return;
		
		//DEBUG START
		debugtimer++;
		if(debugtimer>=40 && ServerConfig.instance.getBoolean(ServerOptions.DEBUG))
		{
			debugtimer = 0;
			debugRoutine(player, world);
			
		}
		//DEBUG END
		
		//Every 5 ticks
		updatetimer++;
		if(updatetimer>=5)
		{
			updatetimer = 0;
			
			//Refresh the player's target temperature
			targettemp = TemperatureUtil.getPlayerTargetTemperature(player);
		}
		
		//Increment tick timer
		addTemperatureTickTimer(1);
		
		if(getTemperatureTickTimer() >= getTemperatureTickLimit())
		{
			setTemperatureTickTimer(0);
			//Clamp target first
			int destinationTemp = TemperatureUtil.clampTemperature(targettemp);
			if(getTemperatureLevel()!=destinationTemp)
			{
				//Temperature is actually different, so it needs to be incremented or decremented
				if(getTemperatureLevel() > destinationTemp)
					addTemperatureLevel(-1);
				else
					addTemperatureLevel(1);
				
				//DebugUtil.clientMessage(player, "Current Temperature: "+getTemperatureLevel());
			}
			
			//Effects
			
			TemperatureEnum tempEnum = getTemperatureEnum();
			if(tempEnum==TemperatureEnum.BURNING)
			{
				if(TemperatureEnum.BURNING.getMiddle() < getTemperatureLevel() && !player.isPotionActive(SDPotions.heat_resist) && !player.isSpectator() && !player.isCreative())
				{
					//Hyperthermia
					player.removePotionEffect(SDPotions.hyperthermia);
					player.addPotionEffect(new PotionEffect(SDPotions.hyperthermia, 300, 0));
				}
			}
			else if(tempEnum==TemperatureEnum.FREEZING)
			{
				if(TemperatureEnum.FREEZING.getMiddle() >= getTemperatureLevel() && !player.isPotionActive(SDPotions.cold_resist) && !player.isSpectator() && !player.isCreative())
				{
					//Hypothermia
					player.removePotionEffect(SDPotions.hypothermia);
					player.addPotionEffect(new PotionEffect(SDPotions.hypothermia, 300, 0));
				}
			}
		}
		
		//I hope this isn't an expensive or leaky operation
		//There's probably a better way to do this but I'm worried about concurrency, as always
		Map<String, TemporaryModifier> tweaks = new HashMap<String, TemporaryModifier>();
		
		int modifierSize = temporaryModifiers.size();
		
		for(Map.Entry<String, TemporaryModifier> entry : temporaryModifiers.entrySet())
		{	
			TemporaryModifier tm = entry.getValue();
			
			if(tm.duration > 0)
			{
				tweaks.put(entry.getKey(), new TemporaryModifier(tm.temperature, tm.duration - 1));
			}
		}
		
		temporaryModifiers.clear();
		temporaryModifiers.putAll(tweaks);
		tweaks.clear();
		
		if(oldmodifiersize != temporaryModifiers.size())
		{
			this.manualDirty = true;
		}
		
		oldmodifiersize = temporaryModifiers.size();
	}
	
	private void debugRoutine(EntityPlayer player, World world)
	{
		//DebugUtil.clientMessage(player, ""+TemperatureUtil.getPlayerTargetTemperature(player));
		
		DebugUtil.clientMessage(player, "----------------");
		BlockPos pos = player.getPosition();
		
		float cumulative = 0;
		for(ITemperatureModifier modifier : TemperatureRegistry.modifiers.values())
		{
			float modsum = 0;
			long nanotime = System.nanoTime();
			modsum += modifier.getWorldInfluence(world, pos);
			modsum += modifier.getPlayerInfluence(player);
			long nanotime2 = System.nanoTime();
			DebugUtil.clientMessage(player, ""+(nanotime2-nanotime)+" : "+modifier.getName()+" - "+modsum);
			cumulative += modsum;	
		}
		for(ITemperatureDynamicModifier dynmodifier : TemperatureRegistry.dynamicModifiers.values())
		{
			float oldcumulative = cumulative;
			long nanotime = System.nanoTime();
			cumulative = dynmodifier.applyDynamicWorldInfluence(world, pos, cumulative);
			cumulative = dynmodifier.applyDynamicPlayerInfluence(player, cumulative);
			long nanotime2 = System.nanoTime();
			DebugUtil.clientMessage(player, ""+(nanotime2-nanotime)+" : "+dynmodifier.getName()+" - "+(cumulative-oldcumulative));
		}
		
		DebugUtil.clientMessage(player, "( "+TemperatureUtil.getPlayerTargetTemperature(player)+ " )");	
		DebugUtil.clientMessage(player, "TempTickLimit: "+getTemperatureTickLimit());
	}
	
	private int getTemperatureTickLimit()
	{
		//Get how quickly to change the player's temperature based on their current temperature and their target temperature

		//TAN clamps the targettemp, this doesn't
		
		int tickrange = ModConfig.server.temperature.temperatureTickMax - ModConfig.server.temperature.temperatureTickMin;
		int temprange = TemperatureEnum.BURNING.getUpperBound() - TemperatureEnum.FREEZING.getLowerBound();
		
		//Distance of temperature range
		int currentrange = Math.abs(getTemperatureLevel() - targettemp);
		
		return Math.max(ModConfig.server.temperature.temperatureTickMin, ModConfig.server.temperature.temperatureTickMax - ((currentrange * tickrange) / temprange));
	}

	@Override
	public boolean isDirty()
	{
		return manualDirty || this.temperature!=this.oldtemperature;
	}

	@Override
	public void setClean()
	{
		this.oldtemperature = this.temperature;
		this.manualDirty = false;
	}

	@Override
	public TemperatureEnum getTemperatureEnum()
	{
		return TemperatureUtil.getTemperatureEnum(getTemperatureLevel());
	}

	@Override
	public ImmutableMap<String, TemporaryModifier> getTemporaryModifiers()
	{
		return ImmutableMap.copyOf(temporaryModifiers);
	}

	@Override
	public void setTemporaryModifier(String name, float temp, int duration)
	{
		if(this.temporaryModifiers.containsKey(name))
		{
			//Reset
			this.manualDirty = true;
		}
		this.temporaryModifiers.put(name, new TemporaryModifier(temp, duration));
	}
	
	@Override
	public void clearTemporaryModifiers()
	{
		this.temporaryModifiers.clear();
	}
}
