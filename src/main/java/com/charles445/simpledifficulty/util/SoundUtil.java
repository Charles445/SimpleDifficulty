package com.charles445.simpledifficulty.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoundUtil
{
	public static void commonPlayPlayerSound(EntityPlayer player, SoundEvent sound)
	{
		player.playSound(sound, 0.5f, 1.0f);
	}
	
	public static void serverPlayBlockSound(World world, BlockPos pos, SoundEvent sound)
	{
		if(!world.isRemote)
		{
			float volume = 0.75f;
			
			if(sound==SoundEvents.ENTITY_GENERIC_DRINK)
				volume = 0.5f;
			
			world.playSound(null, pos, sound, SoundCategory.BLOCKS, volume, 1.0f);
		}
		
	}
}
