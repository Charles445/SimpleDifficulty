package com.charles445.simpledifficulty.api.temperature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Temperature Modifier that runs after other modifiers.<br>
 * It can take the old temperature and replace it with a new one.<br>
 * This lets it make smart decisions, but should be used sparingly,<br>
 * as multiple dynamic modifiers conflict and may create unexpected results!
 */
public interface ITemperatureDynamicModifier
{
	/**
	 * Temperature change that relies on the player<br>
	 * Takes the old temperature and replaces it with a new one
	 * @param player
	 * @param currentTemperature
	 * @return newTemperature
	 */
	public float applyDynamicPlayerInfluence(EntityPlayer player, float currentTemperature);
	
	/**
	 * Temperature change that relies on the world<br>
	 * Takes the old temperature and replaces it with a new one
	 * @param world
	 * @param pos
	 * @return temperature influence on the world
	 */
	public float applyDynamicWorldInfluence(World world, BlockPos pos, float currentTemperature);
	
	/**
	 * The name of the modifier, must be unique! <br>
	 * Adding your Mod ID to this is a good idea
	 * @return modifier name
	 */
	public String getName();
}
