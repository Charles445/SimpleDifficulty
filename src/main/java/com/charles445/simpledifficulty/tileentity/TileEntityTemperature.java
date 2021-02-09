package com.charles445.simpledifficulty.tileentity;

import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.temperature.ITemperatureTileEntity;
import com.charles445.simpledifficulty.block.BlockTemperature;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.WorldUtil;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

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
				float activeTemp = ((BlockTemperature)block).getActiveTemperatureMult() * ModConfig.server.temperature.heaterTemperature;
				
				double fullPowerSq = sq(ModConfig.server.temperature.heaterFullPowerRange);
				
				if(distance < fullPowerSq)
				{
					return handleStrict(targetPos, activeTemp);
				}
				else
				{
					double distanceDiv = sq(ModConfig.server.temperature.heaterMaxRange) - fullPowerSq;
					
					if(distanceDiv <= 0d)
						return 0.0f;
					
					return handleStrict(targetPos, activeTemp * Math.max(0.0f, 1.0f - (float)((distance - fullPowerSq) / distanceDiv)));
				}
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
	
	private float handleStrict(BlockPos targetPos, float distanceTemp)
	{
		if(!ServerConfig.instance.getBoolean(ServerOptions.STRICT_HEATERS))
			return distanceTemp;
		
		//Strictly walk towards the target
		BlockPos thisPos = this.getPos();
		
		int curX = targetPos.getX();
		int curY = targetPos.getY();
		int curZ = targetPos.getZ();
		
		int destX = thisPos.getX();
		int destY = thisPos.getY();
		int destZ = thisPos.getZ();
		
		int xinc = curX<destX?1:-1;
		int yinc = curY<destY?1:-1;
		int zinc = curZ<destZ?1:-1;
		
		//But first check both the start and the destination
		if(isUnprotected(new BlockPos(curX,curY,curZ)) || isUnprotected(new BlockPos(destX,destY,destZ)))
			return 0.0f;
		
		while(curX != destX || curZ != destZ || curY != destY)
		{
			if(curX != destX)
				curX += xinc;
			
			if(curY != destY)
				curY += yinc;
			
			if(curZ != destZ)
				curZ += zinc;
			
			if(isUnprotected(new BlockPos(curX,curY,curZ)))
				return 0.0f;
		}
		
		//Succeeded
		
		return distanceTemp;
	}
	
	private boolean isUnprotected(BlockPos pos)
	{
		if(!WorldUtil.isChunkLoaded(this.world, pos))
			return true;
		
		Chunk chunk = this.world.getChunkProvider().provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
		
		if(!chunk.canSeeSky(pos))
			return false;
		
		if (chunk.getPrecipitationHeight(pos).getY() > pos.getY())
			return false;
		
		return true;
	}
	
	private double sq(double d)
	{
		return d * d;
	}
}
