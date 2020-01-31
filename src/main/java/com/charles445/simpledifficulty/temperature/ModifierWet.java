package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModifierWet extends ModifierBase
{
	public ModifierWet()
	{
		super("Wet");
	}
	
	/*
	@Override
	public float getPlayerInfluence(EntityPlayer player)
	{
		if(player.isWet())
			return ModConfig.server.temperature.wetValue;
		else
			return 0.0f;
	}
	*/
	
	@Override
	public float getWorldInfluence(World world, BlockPos pos)
	{
		//TODO isRainingAt is currently tricked by trap doors and doors
		
		//Optifine + Serene Seasons = misleading particle effects
		//Nothing I can do about that...
		
		if(world.getBlockState(pos).getMaterial()==Material.WATER || world.isRainingAt(pos))
			return ModConfig.server.temperature.wetValue;
		else
			return 0.0f;
	}
}
