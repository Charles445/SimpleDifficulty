package com.charles445.simpledifficulty.temperature;

import java.util.List;
import java.util.Map;

import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.temperature.ITemperatureTileEntity;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.config.JsonConfigInternal;
import com.charles445.simpledifficulty.config.ModConfig;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class ModifierBlocksTiles extends ModifierBase
{
	private final float rangeMax = TemperatureEnum.BURNING.getUpperBound() - TemperatureEnum.FREEZING.getLowerBound();
	private final float maxTemp = TemperatureEnum.BURNING.getUpperBound();
	private final float minTemp = TemperatureEnum.FREEZING.getLowerBound();
	
	private float coldestValue = 0.0f;
	private float hottestValue = 0.0f;
	private float coldestResultValue = 0.0f;
	private float hottestResultValue = 0.0f;
	private float hotTotal = 0.0f;
	private float coldTotal = 0.0f;
	
	public ModifierBlocksTiles()
	{
		super("BlocksTiles");
	}
	
	@Override
	public float getWorldInfluence(World world, BlockPos pos)
	{
		//Gathering things in the "proximity" to the player.
		
		//Things this should include:
		
		//Blocks
		//TileEntities
		//Multiblocks (if possible ?)
		//Entities (is this the right place for them too?)
		
		
		//7 x 5 x 7 (245) (-3, 3, -3, 1, -3, 3)
		//9 x 5 x 9 (405) (-4, 4, -3, 1, -4, 4)
		
		coldestValue = 0.0f;
		hottestValue = 0.0f;
		
		coldestResultValue = 0.0f;
		hottestResultValue = 0.0f;
		
		hotTotal = 0.0f;
		coldTotal = 0.0f;
		
		doBlocksRoutine(world, pos);
		
		doTileEntitiesRoutine(world,pos);
		
		if(!ModConfig.server.temperature.stackingTemperature)
		{
			//If not diminishing returns, end here
			return hottestValue + coldestValue;
		}
		
		//Attempt 3 or so
		
		//Remove hottest/coldest from totals
		hotTotal -= hottestValue;
		coldTotal -= coldestValue;
		
		float hotLogValue = hottestValue * (float)Math.sqrt(easyLog(hotTotal));
		float coldLogValue = coldestValue * (float)Math.sqrt(easyLog(coldTotal));
		
		float result = hotLogValue + coldLogValue;
		
		if(result > hottestValue)
		{
			//Hotter than hottestValue, clamp
			return Math.min(hottestValue + (float)ModConfig.server.temperature.stackingTemperatureLimit, result);
		}
		else if(result < coldestValue)
		{
			//Colder than coldestValue, clamp
			return Math.max(coldestValue - (float)ModConfig.server.temperature.stackingTemperatureLimit, result);
		}
		else
		{
			//Within bounds, no need to clamp
			return result;
		}
		
		
		
		
		// diminishing returns
		
		//Attempt 1 and 2
		
		//So two campfires next to each other normally have the power of one, which is unusual
		//This next part makes it so more blocks means more temperature, although it's still mostly dictated by the hottest or coldest block
		
		
		//Remove the hottest or coldest result value from the totals
		
		/*
		hotTotal -= hottestResultValue;
		coldTotal -= coldestResultValue;
		*/
		
		/*
		
		hotTotal -= hottestValue;
		coldTotal -= coldestValue;
		
		float diminish = 10.0f;
		
		
		//float hotLogValue = hottestValue + easyLog(hotTotal / diminish) - 1.0f;
		//float coldLogValue = coldestValue - easyLog(coldTotal / diminish) + 1.0f;
		
		float hotLogValue = hottestValue * easyLog(hotTotal / (1.0f + hottestValue + hottestValue));
		float coldLogValue = coldestValue * easyLog(coldTotal / (1.0f + (-1.0f * (coldestValue + coldestValue))));
		
		return MathHelper.clamp(hotLogValue, hottestValue, hottestValue + 2) + MathHelper.clamp(coldestValue, coldestValue - 2, coldestValue);
		*/
		
		// diminishing returns would be nice but so far the attempts have had lackluster results
		
	}		
	
	private void doBlocksRoutine(World world, BlockPos pos)
	{
		for(int x = -4; x <= 4; x++)
		{
			for(int y = -3; y <= 1; y++)
			{
				for (int z = -4; z <= 4; z++)
				{
					final BlockPos blockpos = pos.add(x, y, z);
					final IBlockState blockstate = world.getBlockState(blockpos);
					final Block block = blockstate.getBlock();
					
					//JsonPropertyTemperature tempInfo = JsonConfigInternal.blockTemperatures.get(block.getRegistryName().toString());
					List<JsonPropertyTemperature> tempInfoList = JsonConfig.blockTemperatures.get(block.getRegistryName().toString());
					
					if(tempInfoList!=null)
					{
						for(JsonPropertyTemperature tempInfo : tempInfoList)
						{
							if(tempInfo==null)
								continue;
							
							float blockTemp = tempInfo.temperature;
							
							if(blockTemp == 0.0f)
								continue;
							
							if(tempInfo.matchesState(blockstate))
							{
								//Do a thing
								//SimpleDifficulty.logger.debug("Match at pos: "+blockpos.toString());
								
								processTemp(blockTemp);
							}
						}
					}
					else
					{
						//Material 'support'
						//It's just fire and the JSON probably won't be configurable any time soon
						//There's no real need for pack makers to add materials, I don't think?
						
						//It turns out this check runs really fast
						if(blockstate.getMaterial()==Material.FIRE)
						{
							processTemp(JsonConfigInternal.materialTemperature.fire);
						}
					}
				}
			}
		}
	}
	
	private void doTileEntitiesRoutine(World world, BlockPos pos)
	{
		///
		/// TILE ENTITIES
		///
		
		if(!ServerConfig.instance.getBoolean(ServerOptions.TEMPERATURE_TE_ENABLED))
			return;
		
		
		FloatPair pair = new FloatPair();
		
		// 7x7 box
		for(int x = -3 ; x <= 3; x++)
		{
			for(int z = -3; z <= 3; z++)
			{
				checkChunkAndProcess(pair, world, pos.add(x * 16, 0, z * 16), pos);
			}
		}
		
		//The FloatPair is unused right now, it was used when it was in ModifierTileEntities
		
		
		
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
	}
	
	private void processTemp(float blockTemp)
	{
		if(blockTemp==0.0f)
			return;
		
		if(blockTemp>=0.0f)
			processHot(blockTemp);
		else
			processCold(blockTemp);
	}
	
	private void processHot(float blockTemp)
	{
		hotTotal += blockTemp;
		if(blockTemp > hottestValue)
		{
			hottestValue = blockTemp;
		}
	}
	
	private void processCold(float blockTemp)
	{
		coldTotal += blockTemp;
		if(blockTemp < coldestValue)
		{
			coldestValue = blockTemp;
		}
	}
	
	private void checkChunkAndProcess(FloatPair pair, World world, BlockPos pos, BlockPos selfPos)
	{
		if(isChunkLoaded(world, pos))
		{
			Chunk chunk = world.getChunkProvider().provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
			for(Map.Entry<BlockPos, TileEntity> entry : chunk.getTileEntityMap().entrySet())
			{
				processTemp(checkTileEntity(world, entry.getKey(), entry.getValue(), selfPos));
				
				/*
				float tileResult = checkTileEntity(world, entry.getKey(), entry.getValue(), selfPos);
				if(tileResult > pair.high)
				{
					pair.high = tileResult;
					processHot(pair.high);
				}
				else if(tileResult < pair.low)
				{
					pair.low = tileResult;
					processCold(pair.low);
				}*/
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
	
	/*
	private void processHot(float blockTemp)
	{
		float blockResult = (blockTemp / easyLogPowEight(blockTemp));
		hotTotal+=blockResult;
		if(blockTemp > hottestValue)
		{
			hottestValue = blockTemp;
			hottestResultValue = blockResult;
		}
	}
	
	private void processCold(float blockTemp)
	{
		float blockResult = (blockTemp / easyLogPowEight(blockTemp));
		coldTotal+=blockResult;
		if(blockTemp < coldestValue)
		{
			coldestValue = blockTemp;
			coldestResultValue = blockResult;
		}
	}
	*/
	
	private float easyLog(float f)
	{
		if(f >= 0.0f)
		{
			return (float)Math.log10(f + 10.0f);
		}
		else
		{
			return (float)Math.log10(-1.0f * f + 10.0f);
		}
	}
	
	private float easyLogPowTwo(float f)
	{
		float ff = easyLog(f);
		return ff * ff;
	}
	
	private float easyLogPowEight(float f)
	{
		float ff = easyLog(f);
		ff = ff * ff; //2
		ff = ff * ff; //4
		return ff * ff; //8
	}
}
