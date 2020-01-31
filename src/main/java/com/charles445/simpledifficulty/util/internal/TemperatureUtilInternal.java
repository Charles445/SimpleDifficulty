package com.charles445.simpledifficulty.util.internal;

import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureUtil;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.debug.DebugUtil;
import com.charles445.simpledifficulty.util.WorldUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TemperatureUtilInternal implements ITemperatureUtil
{
	private final String ARMOR_TEMPERATURE_TAG = "SDArmorTemp";
	
	@Override
	public int getPlayerTargetTemperature(EntityPlayer player)
	{
		float sum = 0.0f;
		World world = player.getEntityWorld();
		BlockPos pos = WorldUtil.getSidedBlockPos(world,player);
		
		for(ITemperatureModifier modifier : TemperatureRegistry.modifiers.values())
		{
			sum += modifier.getWorldInfluence(world, pos);
			sum += modifier.getPlayerInfluence(player);
		}
		return (int)sum;
	}
	
	@Override
	public int getWorldTemperature(World world, BlockPos pos)
	{
		
		float sum = 0.0f;
		for(ITemperatureModifier modifier : TemperatureRegistry.modifiers.values())
		{
			sum += modifier.getWorldInfluence(world, pos);
		}
		return (int)sum;
	}
	
	@Override
	public int clampTemperature(int temperature)
	{
		return MathHelper.clamp(temperature, TemperatureEnum.FREEZING.getLowerBound(), TemperatureEnum.BURNING.getUpperBound());
	}
	
	@Override
	public TemperatureEnum getTemperatureEnum(int temp)
	{
		for(TemperatureEnum temp_enum : TemperatureEnum.values())
		{
			if(temp_enum.matches(temp))
				return temp_enum;
		}
		
		//Temperature invalid, assume extremes
		if(temp<0)
			return TemperatureEnum.FREEZING;
		else
			return TemperatureEnum.BURNING;
	}

	@Override
	public void setArmorTemperatureTag(final ItemStack stack, float temperature)
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		
		final NBTTagCompound compound = stack.getTagCompound();

		compound.setFloat(ARMOR_TEMPERATURE_TAG, temperature);
	}

	@Override
	public float getArmorTemperatureTag(final ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			final NBTTagCompound compound = stack.getTagCompound();
			if(compound.hasKey(ARMOR_TEMPERATURE_TAG))
			{
				NBTBase tempTag = compound.getTag(ARMOR_TEMPERATURE_TAG);
				if(tempTag instanceof NBTTagFloat)
				{
					return ((NBTTagFloat)tempTag).getFloat();
				}
			}
		}
		
		return 0.0f;
	}

	@Override
	public void removeArmorTemperatureTag(final ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			final NBTTagCompound compound = stack.getTagCompound();
			if(compound.hasKey(ARMOR_TEMPERATURE_TAG))
			{
				compound.removeTag(ARMOR_TEMPERATURE_TAG);
			}
		}
	}
}
