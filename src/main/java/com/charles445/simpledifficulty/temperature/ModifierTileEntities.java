package com.charles445.simpledifficulty.temperature;

import java.util.Map;

import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.temperature.ITemperatureTileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class ModifierTileEntities extends ModifierBase
{
	public ModifierTileEntities()
	{
		super("TileEntities");
	}
	
	@Override
	public float getWorldInfluence(World world, BlockPos selfPos)
	{
		if(!ServerConfig.instance.getBoolean(ServerOptions.TEMPERATURE_TE_ENABLED))
			return 0.0f;
		
		
		FloatPair pair = new FloatPair();
		
		// 7x7 box
		for(int x = -3 ; x <= 3; x++)
		{
			for(int z = -3; z <= 3; z++)
			{
				checkChunk(pair, world, selfPos.add(x * 16, 0, z * 16), selfPos);
			}
		}
		
		
		/* 5x5 box with points (29)
		
		for(int x = -2 ; x <= 2; x++)
		{
			for(int z = -2; z <= 2; z++)
			{
				checkChunk(pair, world, selfPos.add(x * 16, 0, z * 16), selfPos);
			}
		}
		checkChunk(pair, world, selfPos.add(-48, 0, 0), selfPos);
		checkChunk(pair, world, selfPos.add(48, 0, 0), selfPos);
		checkChunk(pair, world, selfPos.add(0, 0, -48), selfPos);
		checkChunk(pair, world, selfPos.add(0, 0, 48), selfPos);
		 */
		
		return pair.high + pair.low;
	}
	
	private void checkChunk(FloatPair pair, World world, BlockPos pos, BlockPos selfPos)
	{
		if(isChunkLoaded(world, pos))
		{
			Chunk chunk = world.getChunkProvider().provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
			for(Map.Entry<BlockPos, TileEntity> entry : chunk.getTileEntityMap().entrySet())
			{
				float tileResult = checkTileEntity(world, entry.getKey(), entry.getValue(), selfPos);
				if(tileResult > pair.high)
				{
					pair.high = tileResult;
				}
				else if(tileResult < pair.low)
				{
					pair.low = tileResult;
				}
			}
		}
	}
	
	private float checkTileEntity(World world, BlockPos pos, TileEntity tileEntity, BlockPos selfPos)
	{
		double distance = pos.distanceSq(selfPos);
		
		if(distance < 2500.0d)
		{
			//Within 50 blocks
			
			if(tileEntity instanceof ITemperatureTileEntity)
			{
				return ((ITemperatureTileEntity)tileEntity).getInfluence(selfPos, distance);
			}
		}
		return 0.0f;
	}
	
	private boolean isChunkLoaded(World world, BlockPos pos)
	{
		if(world.isRemote)
		{
			//WorldClient don't care
			return true;
		}
		else
		{
			//WorldServer
			return ((WorldServer)world).getChunkProvider().chunkExists(pos.getX() >> 4, pos.getZ() >> 4);
		}
		
	}
	
	private class FloatPair
	{
		protected float high;
		protected float low;
		
		protected FloatPair()
		{
			high = 0.0f;
			low = 0.0f;
		}
	}
}


