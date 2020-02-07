package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.temperature.ModifierBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class AUWModifier extends ModifierBase
{	
	private final String TAG_TLINING = "carrots:ArmorTLining";
	private final String TAGVAL_HORD_INSTRUCTIONS = "_Hord_Instructions";
	
	public AUWModifier()
	{
		super("AUW");
	}
	
	@Override
	public float getPlayerInfluence(EntityPlayer player)
	{
		if(!ModConfig.server.compatibility.toggles.armorUnderwear)
			return 0.0f;
		
		return doLinedArmor(player) + doGooPak(player);
	}
	
	private float doLinedArmor(EntityPlayer player)
	{
		float value = 0.0f;
		value += checkLinedSlot(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
		value += checkLinedSlot(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
		value += checkLinedSlot(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
		value += checkLinedSlot(player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
		return (float)ModConfig.server.compatibility.auw.linerMultiplier * value;
	}
	
	private float checkLinedSlot(ItemStack stack)
	{
		if(!stack.isEmpty() && stack.hasTagCompound())
		{
			//<minecraft:diamond_chestplate:0>.withTag({"carrots:ArmorTLining": "COOL"})
			NBTTagCompound compound = stack.getTagCompound();
			if(compound.hasKey(TAG_TLINING))
			{
				String val = compound.getString(TAG_TLINING);
				switch(val)
				{
					case "COOLEST": return -3.0f;
					case "COOLER": return -2.0f;
					case "COOL": return -1.0f;
					case "WARM": return 1.0f;
					case "WARMER": return 2.0f;
					case "WARMEST": return 3.0f;
					default: return 0.0f;
				}
			}
		}

		return 0.0f;
	}
	
	private float doGooPak(EntityPlayer player)
	{
		float value = 0.0f;
		value += checkPakSlot(player.getHeldItemOffhand());
		//Main hand is a part of the hotbar
		for(int i=0;i<player.inventory.getHotbarSize();i++)
		{
			value += checkPakSlot(player.inventory.getStackInSlot(i));
		}
		
		float maxPower = (float)ModConfig.server.compatibility.auw.goopakTemperatureModifier * (float)ModConfig.server.compatibility.auw.goopakMaximumActive;
		
		return MathHelper.clamp(value, -1.0f * maxPower, maxPower);
	}
	
	private float checkPakSlot(ItemStack stack)
	{
		float strength = (float)ModConfig.server.compatibility.auw.goopakTemperatureModifier;
		
		if(!stack.isEmpty() && stack.hasTagCompound())
		{
			NBTTagCompound compound = stack.getTagCompound();
			//{_Hord_Instructions: {BonusTempDuration: 479, Heated: 1 as byte}}
			//<armorunder:goopak_cool:0>.withTag({_Hord_Instructions: {BonusTempDuration: 478, Cooled: 1 as byte}})
			if(compound.hasKey(TAGVAL_HORD_INSTRUCTIONS))
			{
				String regName = stack.getItem().getRegistryName().toString();
				if(regName.equals("armorunder:goopak_cool"))
				{
					return -1.0f * strength;
				}
				else if(regName.equals("armorunder:goopak_heat"))
				{
					return strength;
				}
			}
		}
		
		return 0.0f;
	}
}

