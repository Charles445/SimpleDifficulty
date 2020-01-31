package com.charles445.simpledifficulty.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentArmorTemperature extends Enchantment
{
	public EnchantmentArmorTemperature()
	{
		super(Enchantment.Rarity.COMMON, EnumEnchantmentType.ARMOR, 
				new EntityEquipmentSlot[]{
						EntityEquipmentSlot.CHEST,
						EntityEquipmentSlot.FEET,
						EntityEquipmentSlot.HEAD,
						EntityEquipmentSlot.LEGS
						});
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		//return this != ench;
		return !(ench instanceof EnchantmentArmorTemperature);
	}
}
