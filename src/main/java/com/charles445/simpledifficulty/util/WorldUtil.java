package com.charles445.simpledifficulty.util;

import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.debug.DebugUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class WorldUtil
{
	public static BlockPos getSidedBlockPos(World world, Entity entity)
	{
		//Annoying inconsistency between logical sides and EntityPlayer.getPosition();
		
		//EntityPlayerMP = return new BlockPos(this.posX, this.posY + 0.5D, this.posZ);
		//EntityPlayerSP = return new BlockPos(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D);
		
		//MP = 0, +0.5, 0
		//SP = +0.5, +0.5, +0.5
		
		if(!world.isRemote)
		{
			return entity.getPosition();
		}
		
		//Client Side
		if(entity instanceof EntityPlayer)
		{
			//Player
			return new BlockPos(entity.getPositionVector().addVector(0, 0.5D, 0));
		}
		else if(entity instanceof EntityItemFrame)
		{
			//Item Frame
			return new BlockPos(entity.getPositionVector().addVector(0D, -0.45D, 0D));
		}
		else
		{
			//Default
			return entity.getPosition();
		}
	}
	
	public static int calculateClientWorldEntityTemperature(World world, Entity entity)
	{
		return TemperatureUtil.clampTemperature(TemperatureUtil.getWorldTemperature(world, WorldUtil.getSidedBlockPos(world,entity)));
	}
	
	public static boolean isChunkLoaded(World world, BlockPos pos)
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
}
