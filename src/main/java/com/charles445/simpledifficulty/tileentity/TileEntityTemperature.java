package com.charles445.simpledifficulty.tileentity;

import com.charles445.simpledifficulty.api.temperature.ITemperatureTileEntity;
import com.charles445.simpledifficulty.block.BlockTemperature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityTemperature extends TileEntity implements ITemperatureTileEntity
{
	@Override
	public float getInfluence(BlockPos targetPos, double distance)
	{
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if(block instanceof BlockTemperature)
		{
			boolean enabled = world.getBlockState(pos).getValue(BlockTemperature.ENABLED);
			if(enabled)
			{
				//Math and stuff
				float activeTemp = ((BlockTemperature)block).getActiveTemperature();
				if(distance < 1250.0d)
					return activeTemp;
				else
					return activeTemp * Math.max(0.0f, 1.0f - (float)((distance - 1250.0d) / 1250.0d));
			}
			else
			{
				return 0.0f;
			}
		}
		else
		{
			//TODO Block is missing
			//Shouldn't happen, but maybe there should be a failsafe to get rid of itself?
			return 0.0f;
		}
	}
}
