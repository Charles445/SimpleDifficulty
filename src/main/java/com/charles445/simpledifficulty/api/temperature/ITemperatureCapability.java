package com.charles445.simpledifficulty.api.temperature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public interface ITemperatureCapability
{
	public int getTemperatureLevel();
	public int getTemperatureTickTimer();
	
	public void setTemperatureLevel(int temperature);
	public void setTemperatureTickTimer(int ticktimer);
	
	public void addTemperatureLevel(int temperature);
	public void addTemperatureTickTimer(int ticktimer);
	
	/**
	 * Returns the capability's matching TemperatureEnum enum
	 * @return TemperatureEnum for the temperature
	 */
	public TemperatureEnum getTemperatureEnum();
	
	/**
	 * (Don't use this!) <br>
	 * Runs a tick update for the player's temperature capability
	 * @param player
	 * @param world
	 * @param phase
	 */
	public void tickUpdate(EntityPlayer player, World world, TickEvent.Phase phase);
	
	/**
	 * (Don't use this!) <br>
	 * Checks if the capability needs an update
	 * @return boolean has temperature changed
	 */
	public boolean isDirty();
	/**
	 * (Don't use this!) <br>
	 * Sets the capability as updated
	 */
	public void setClean();
}
