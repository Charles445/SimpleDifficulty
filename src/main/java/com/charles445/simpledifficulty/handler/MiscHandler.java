package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstEnumBlockPos;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.config.ModConfig;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class MiscHandler
{
	//Event handler for miscellaneous things that come up
	
	//Fix stupid jumping vanilla glitch
	@SubscribeEvent
	public void onDismount(EntityMountEvent event)
	{
		if(event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer)
		{
			((EntityPlayer) event.getEntityMounting()).setJumping(false);
		}
	}
	
	//Prevent canteens from being repaired in the anvil
	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event)
	{
		if(event.getLeft().getItem() == SDItems.canteen || event.getRight().getItem() == SDItems.canteen)
			event.setCanceled(true);
	}
	
	
	
	//TODO Feature to have glass bottles remove Material.WATER blocks as a config option?
	//Doesn't work yet
	/*
	@SubscribeEvent
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		//Both Sides
		if(event.getItemStack().getItem()==Items.GLASS_BOTTLE)
		{
			ThirstEnumBlockPos traceResult = ThirstUtil.traceWater(event.getEntityPlayer());
			if(traceResult!=null && traceResult.thirstEnum == ThirstEnum.NORMAL)
			{
				//Glass bottle, found water, verify it's water block
				World world = event.getWorld();
				Material tracedMaterial = event.getWorld().getBlockState(traceResult.pos).getMaterial();
				if(tracedMaterial==Material.WATER)
				{
					world.setBlockToAir(traceResult.pos);
				}
			}
		}
		
	}
	*/
}
