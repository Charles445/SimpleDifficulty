package com.charles445.simpledifficulty.api.temperature;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITemperatureTileEntity
{
	/**
	 * Returns the temperature effect this TileEntity has on the block at targetPos.<br>
	 * 
	 * @param targetPos The BlockPos requesting the temperature influence
	 * @param distance The distanceSq result from the targetPos and the TileEntity's BlockPos
	 * @return float Temperature change
	 */
	public float getInfluence(BlockPos targetPos, double distance);
}
