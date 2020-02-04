package com.charles445.simpledifficulty.proxy;

import java.io.File;

import com.charles445.simpledifficulty.SimpleDifficulty;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class ServerProxy extends CommonProxy
{
	//Participation Award
	
	@Override
	public void preInit() 
	{
		super.preInit();
	}
	
	@Override
	public void init()
	{
		super.init();
	}
	
	@Override
	public void postInit()
	{
		super.postInit();
	}
	
	@Override
	public Side getSide()
	{
		return Side.SERVER;	
	}

	@Override
	public EntityPlayer getClientMinecraftPlayer()
	{
		return null;
	}

	@Override
	public Boolean isClientConnectedToServer()
	{
		return null;
	}
}
