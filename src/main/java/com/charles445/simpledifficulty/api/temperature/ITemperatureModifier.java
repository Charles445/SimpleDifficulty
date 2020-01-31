package com.charles445.simpledifficulty.api.temperature;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITemperatureModifier
{
	/**
	 * Temperature change that relies on the player
	 * @param player
	 * @return temperature influence on the player
	 */
	public float getPlayerInfluence(EntityPlayer player);
	
	/**
	 * Temperature change that relies on the world
	 * @param world
	 * @param pos
	 * @return temperature influence on the world
	 */
	public float getWorldInfluence(World world, BlockPos pos);
	
	/**
	 * The name of the modifier, must be unique! <br>
	 * Adding your Mod ID to this is a good idea
	 * @return modifier name
	 */
	public String getName();
}
