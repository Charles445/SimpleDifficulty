package com.charles445.simpledifficulty.proxy;

import java.io.File;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public interface IProxy 
{
	public void preInit();
	
	public void init();
	
	public void postInit();
	
	public Side getSide();
	
	@Nullable
	public EntityPlayer getClientMinecraftPlayer();
	
	@Nullable
	public Boolean isClientConnectedToServer();
	
	public void spawnClientParticle(World world, String type, double xPos, double yPos, double zPos, double motionX, double motionY, double motionZ);
}
