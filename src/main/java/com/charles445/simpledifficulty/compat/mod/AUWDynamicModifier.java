package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.temperature.ModifierDynamicBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class AUWDynamicModifier extends ModifierDynamicBase
{
	//Ozzy
	//<minecraft:diamond_chestplate:0>.withTag({"carrots:ArmorXLining": "TEMPERATURE_REGULATOR", "carrots:ArmorXLining_value": 0})
	
	//Ollie
	//<minecraft:leather_chestplate:0>.withTag({"carrots:ArmorXLining": "OBSIDIAN_SHIELD"})
	
	//This never really gets used and it appears to be unfinished
	//Otto
	//<minecraft:golden_chestplate:0>.withTag({"carrots:ArmorXLining": "ANTIFREEZE_SHIELD"})
	
	private final String TAG_XLINING = "carrots:ArmorXLining";
	
	//MILD = 0, EXTRA COOLING = -1, EXTRA WARMTH = 1
	private final String TAG_XLINING_VALUE = "carrots:ArmorXLining_value";
	
	private final String TAGVAL_OZZY = "TEMPERATURE_REGULATOR";
	//private final String TAGVAL_OLLIE = "OBSIDIAN_SHIELD";
	//private final String TAGVAL_OTTO = "ANTIFREEZE_SHIELD";
	
	//Ollie has no effect on temperature
	
	public AUWDynamicModifier()
	{
		super("AUWDynamic");
	}
	
	@Override
	public float applyDynamicPlayerInfluence(EntityPlayer player, float currentTemperature)
	{
		if(!ModConfig.server.compatibility.auw.enableAUW)
			return currentTemperature;
		
		//TODO clean this up
		
		final ItemStack chestplate = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		final ItemStack leggings = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
		
		boolean hasChest = false;
		boolean hasLeggings = false;
		int valueChest = 0;
		int valueLeggings = 0;
		
		if(!leggings.isEmpty())
		{
			if(leggings.hasTagCompound())
			{
				NBTTagCompound compound = leggings.getTagCompound();
				String xLiner = compound.getString(TAG_XLINING);
				if(xLiner.equals(TAGVAL_OZZY))
				{
					hasLeggings = true;
					if(compound.hasKey(TAG_XLINING_VALUE))
					{
						valueLeggings = compound.getInteger(TAG_XLINING_VALUE);
					}
				}
			}
		}
		
		if(!chestplate.isEmpty())
		{
			if(chestplate.hasTagCompound())
			{
				NBTTagCompound compound = chestplate.getTagCompound();
				String xLiner = compound.getString(TAG_XLINING);
				if(xLiner.equals(TAGVAL_OZZY))
				{
					hasChest = true;
					if(compound.hasKey(TAG_XLINING_VALUE))
					{
						valueChest = compound.getInteger(TAG_XLINING_VALUE);
					}
				}
			}
		}
		
		if(hasChest || hasLeggings)
		{
			float baseStretch = (float)ModConfig.server.compatibility.auw.ozzyBaseRange;
			float valueStretchModifier = (float)ModConfig.server.compatibility.auw.ozzyExtraRange;
			float valueStretchDefault = valueStretchModifier / 3.0f;
			
			//Both mild = +8 to -8
			//Both one way = +12 to -6
			//Both different = +9 to -9
			
			float lowStretch = 0.0f; 
			float highStretch = 0.0f;
			
			if(hasChest)
			{
				lowStretch -= baseStretch;
				highStretch += baseStretch;
				
				switch(valueChest)
				{
					case -1: lowStretch -= valueStretchModifier;break;
					case 1: highStretch += valueStretchModifier;break; 
					default:
						lowStretch -= valueStretchDefault;
						highStretch += valueStretchDefault;
						break;
				}
			}
			
			if(hasLeggings)
			{
				lowStretch -= baseStretch;
				highStretch += baseStretch;
				
				switch(valueLeggings)
				{
					case -1: lowStretch -= valueStretchModifier;break;
					case 1: highStretch += valueStretchModifier;break;
					default:
						lowStretch -= valueStretchDefault;
						highStretch += valueStretchDefault;
						break;
				}
				
			}
			
			float distanceToDefault = MathHelper.clamp(defaultTemperature - currentTemperature, lowStretch, highStretch);
			
			return currentTemperature += distanceToDefault;
		}

		return currentTemperature;
	}
}