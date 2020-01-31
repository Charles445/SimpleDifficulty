package com.charles445.simpledifficulty.proxy;

import java.io.File;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public interface IProxy 
{
	public void preInit();
	
	public void postInit();
	
	public Side getSide();
	
	@Nullable
	public EntityPlayer getClientMinecraftPlayer();
	
	@Nullable
	public Boolean isClientConnectedToServer();
}
