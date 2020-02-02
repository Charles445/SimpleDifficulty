package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.SDEnchantments;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.JsonConfig;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.config.json.JsonTemperature;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class ModifierArmor extends ModifierBase
{
	public ModifierArmor()
	{
		super("Armor");
	}
	
	//Armor types, enchantments, and tags
	
	@Override
	public float getPlayerInfluence(EntityPlayer player)
	{
		float value = 0.0f;
		value += checkArmorSlot(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
		value += checkArmorSlot(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
		value += checkArmorSlot(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
		value += checkArmorSlot(player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
		return value;
	}
	
	private float checkArmorSlot(ItemStack stack)
	{
		if(stack.isEmpty())
			return 0.0f;
		
		float sum = 0.0f;
		
		//Enchantments
		if(EnchantmentHelper.getEnchantmentLevel(SDEnchantments.chilling, stack) > 0)
		{
			sum -= ModConfig.server.temperature.enchantmentTemperature;
		}
		else if(EnchantmentHelper.getEnchantmentLevel(SDEnchantments.heating, stack) > 0)
		{
			sum += ModConfig.server.temperature.enchantmentTemperature;
		}
		
		//ArmorTemperature
		JsonTemperature armorInfo = JsonConfig.armorTemperatures.get(stack.getItem().getRegistryName().toString());
		if(armorInfo!=null)
		{
			sum += armorInfo.temperature;
		}
		
		//NBT
		sum += TemperatureUtil.getArmorTemperatureTag(stack);
		
		return sum;
	}
}
