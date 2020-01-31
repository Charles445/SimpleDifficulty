package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.capability.ThirstCapability;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public abstract class ItemDrinkBase extends Item
{
	public abstract int getThirstLevel(ItemStack stack);
	public abstract float getSaturationLevel(ItemStack stack);
	public abstract float getDirtyChance(ItemStack stack);
	
	public ItemDrinkBase()
	{
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
	}
	
	public void runSecondaryEffect(EntityPlayer player, ItemStack stack)
	{
		//Can be overridden to run a special task
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.DRINK;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 32;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		
		IThirstCapability capability = SDCapabilities.getThirstData(player);
		if(capability.isThirsty())
		{
			player.setActiveHand(hand);
			return new ActionResult(EnumActionResult.SUCCESS, stack);
		}
		
		return new ActionResult(EnumActionResult.FAIL, stack);
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
	{
		if(world.isRemote || !(entityLiving instanceof EntityPlayer))
			return stack;
		
		EntityPlayer player = (EntityPlayer)entityLiving;
		ThirstUtil.takeDrink(player, this.getThirstLevel(stack), this.getSaturationLevel(stack), this.getDirtyChance(stack));
		this.runSecondaryEffect(player, stack);
		return new ItemStack(Items.GLASS_BOTTLE);
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}
}
