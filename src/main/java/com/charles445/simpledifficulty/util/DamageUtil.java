package com.charles445.simpledifficulty.util;

import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class DamageUtil
{
	//Utility for damaging players safely based on difficulty and settings
	
	public static boolean isModDangerous(World world)
	{
		//Whether to do thirst or health damage to the player or not
		
		//Return false only if the world is peaceful, and the peaceful danger config is set to false
		if(world.getDifficulty() != EnumDifficulty.PEACEFUL || ServerConfig.instance.getBoolean(ServerOptions.PEACEFUL_DANGER))
				return true;
		return false;
	}
	
	public static boolean healthAboveDifficulty(World world, EntityPlayer player)
	{
		//Check if difficulty allows for damage
		
		EnumDifficulty difficulty = world.getDifficulty();
		if(difficulty==EnumDifficulty.HARD ||
			(difficulty==EnumDifficulty.NORMAL 		&& 	player.getHealth()>1.0f) ||
			(difficulty==EnumDifficulty.EASY 		&& 	player.getHealth()>10.0f) ||
			(difficulty==EnumDifficulty.PEACEFUL 	&&	player.getHealth()>10.0f)
		)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
