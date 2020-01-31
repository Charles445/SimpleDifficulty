package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.JsonConfig;
import com.charles445.simpledifficulty.config.json.BlockTemperature;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModifierBlock extends ModifierBase
{
	public ModifierBlock()
	{
		super("Blocks");
	}
	
	@Override
	public float getWorldInfluence(World world, BlockPos pos)
	{
		//7 x 5 x 7 (245) (-3, 3, -3, 1, -3, 3)
		//9 x 5 x 9 (405) (-4, 4, -3, 1, -4, 4)
		
		float coldestValue = 0.0f;
		float hottestValue = 0.0f;
		
		for(int x = -4; x <= 4; x++)
		{
			for(int y = -3; y <= 1; y++)
			{
				for (int z = -4; z <= 4; z++)
				{
					final BlockPos blockpos = pos.add(x, y, z);
					final IBlockState blockstate = world.getBlockState(blockpos);
					final Block block = blockstate.getBlock();
					
					BlockTemperature tempInfo = JsonConfig.blockTemperatures.get(block.getRegistryName().toString());
					if(tempInfo!=null)
					{
						float blockTemp = tempInfo.temperature;
						boolean hot = blockTemp>=0.0f?true:false;
						
						if(hot)
						{
							if(blockTemp <= hottestValue)
								continue;
						}
						else
						{
							if(blockTemp >= coldestValue)
								continue;
						}
							
						if(tempInfo.matchesState(blockstate))
						{
							//Do a thing
							//SimpleDifficulty.logger.debug("Match at pos: "+blockpos.toString());
							
							if(hot)
							{
								if(blockTemp > hottestValue)
									hottestValue = blockTemp;
							}
							else
							{
								if(blockTemp < coldestValue)
								{
									coldestValue = blockTemp;
								}
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
							if(JsonConfig.materialTemperature.fire > hottestValue)
								hottestValue = JsonConfig.materialTemperature.fire;
						}
					}
				}
			}
		}
		
		return coldestValue + hottestValue;
	}
}
