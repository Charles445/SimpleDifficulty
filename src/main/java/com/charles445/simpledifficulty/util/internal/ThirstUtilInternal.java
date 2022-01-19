package com.charles445.simpledifficulty.util.internal;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstUtil;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstEnumBlockPos;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.config.ModConfig;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class ThirstUtilInternal implements IThirstUtil
{
	//Returns an object based on what is being drunk
	//Not API visible
	@Nullable
	public static ThirstEnumBlockPos traceWaterToDrink(EntityPlayer player)
	{
		if(player.getHeldItemMainhand().isEmpty())
		{
			IThirstCapability capability = SDCapabilities.getThirstData(player);
			if(capability.isThirsty())
			{
				//Empty handed and thirsty
				ThirstEnumBlockPos traceResult = ThirstUtil.traceWater(player);
				if(traceResult==null)
					return null;
				
				if(traceResult.thirstEnum == ThirstEnum.PURIFIED)
				{
					if(!ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_BLOCKS))
						return null;
					
					if(!ServerConfig.instance.getBoolean(ServerOptions.INFINITE_PURIFIED_WATER))
						player.world.setBlockToAir(traceResult.pos);
				}
				else if(traceResult.thirstEnum == ThirstEnum.RAIN && !ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_RAIN))
				{
					return null;
				}
				else if(traceResult.thirstEnum == ThirstEnum.NORMAL && !ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_BLOCKS))
				{
					return null;
				}
				
				return traceResult;
			}
		}
		
		return null;
	}
	
	//API
	
	//Returns an object based on what is being looked at
	@Nullable
	@Override
	public ThirstEnumBlockPos traceWater(EntityPlayer player)
	{	
		//Check if player is looking up, if it's raining, if they can see sky, and if THIRST_DRINK_RAIN is enabled
		//This essentially means rain can't be a trace result for drinking or for a canteen
		
		if(player.rotationPitch < -75.0f && player.world.isRainingAt(player.getPosition()) && player.world.canSeeSky(player.getPosition()) && ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_RAIN))
		{
			//Drinking rain
			return new ThirstEnumBlockPos(ThirstEnum.RAIN, player.getPosition());
		}
		
		//Handle ray tracing
		
		//Get the player's reach distance attribute and cut it in half
		double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() * 0.5d;
		
		//Similar to Entity.rayTrace
		Vec3d eyevec = player.getPositionEyes(1.0f);
		Vec3d lookvec = player.getLook(1.0f);
		Vec3d targetvec = eyevec.addVector(lookvec.x * reach, lookvec.y * reach, lookvec.z * reach);
		
		//Ray trace from the player's eyepos to where they are looking, and stop at liquids
		RayTraceResult trace = player.getEntityWorld().rayTraceBlocks(eyevec, targetvec, true);
		
		if(trace==null || trace.typeOfHit != RayTraceResult.Type.BLOCK)
			return null;
		
		//Hit a block
		//TODO is there a better way to do this?
		Block traceBlock = player.getEntityWorld().getBlockState(trace.getBlockPos()).getBlock();
		if(traceBlock == Blocks.WATER)
		{
			return new ThirstEnumBlockPos(ThirstEnum.NORMAL, trace.getBlockPos());
		}
		else if(traceBlock == SDFluids.blockPurifiedWater)
		{
			return new ThirstEnumBlockPos(ThirstEnum.PURIFIED, trace.getBlockPos());
		}
		
		return null;
	}
	
	@Override
	public void takeDrink(EntityPlayer player, int thirst, float saturation, float dirtyChance)
	{
		if(!QuickConfig.isThirstEnabled())
			return;
		
		IThirstCapability capability = SDCapabilities.getThirstData(player);
		
		if(capability.isThirsty())
		{
			capability.addThirstLevel(thirst);
			
			//In TAN, any drink with a hydration higher than 0.5f will be more beneficial to you if you drink it when you're not as thirsty
			
			//In this mod, there's no saturation advantageous time to drink anything
			
			
			capability.addThirstSaturation(saturation);
			
			//Old
			//capability.addThirstSaturation(Math.min(1.0f, saturation) * thirst);
			
			//Test for dirtiness
			if(dirtyChance != 0.0f && player.world.rand.nextFloat() < dirtyChance)
			{
				player.addPotionEffect(new PotionEffect(SDPotions.thirsty,600));
				
				//Test for parasites
				if(ModConfig.server.thirst.thirstParasites && player.world.rand.nextDouble() < ModConfig.server.thirst.thirstParasitesChance)
				{
					player.addPotionEffect(new PotionEffect(SDPotions.parasites, ModConfig.server.thirst.thirstParasitesDuration));
				}
			}
		}
		else
		{
			//Player isn't thirsty, so check if the saturation of the drink itself is more, and set to that
			
			if(capability.getThirstSaturation() < saturation)
				capability.setThirstSaturation(saturation);
		}
	}
	
	@Override
	public void takeDrink(EntityPlayer player, int thirst, float saturation)
	{
		//Clean water
		takeDrink(player, thirst, saturation, 0.0f);
	}
	
	@Override
	public void takeDrink(EntityPlayer player, ThirstEnum type)
	{
		takeDrink(player, type.getThirst(), type.getSaturation(), type.getThirstyChance());
	}
	
	@Override
	public ItemStack createPurifiedWaterBucket()
	{
		return FluidUtil.getFilledBucket(new FluidStack(SDFluids.purifiedWater, Fluid.BUCKET_VOLUME));
	}
}
