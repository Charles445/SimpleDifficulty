package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperature;
import com.charles445.simpledifficulty.config.ModConfig;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;

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
		
		
		//TODO some Material.WATER liquids may not be "cold"
		//Like Biomes O' Plenty's Hot Spring Water
		
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		
		if(block instanceof IFluidBlock)
		{
			//Modded fluid
			Fluid fluid = ((IFluidBlock)block).getFluid();
			if(fluid != null)
			{
				JsonTemperature tempInfo = JsonConfig.fluidTemperatures.get(fluid.getName());
				if(tempInfo!=null)
				{
					//Overridden mod fluid
					return tempInfo.temperature;
				}
			}
		}
		
		//Vanilla fluid, or modded fluid with no override, or no fluid at all
		
		if(state.getMaterial() == Material.WATER)
		{
			return ModConfig.server.temperature.wetValue;
		}
		else if(world.isRainingAt(pos))
		{
			//Rain
			return ModConfig.server.temperature.wetValue;
		}
		else
		{
			return 0.0f;
		}
	}
}
