package com.charles445.simpledifficulty.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.ClientConfig;
import com.charles445.simpledifficulty.api.config.ClientOptions;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.debug.DebugUtil;
import com.charles445.simpledifficulty.util.WorldUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemThermometer extends Item
{
	@SideOnly(Side.CLIENT)
	protected static Map<Integer, Long> hashAge = new HashMap<Integer, Long>();
	
	@SideOnly(Side.CLIENT)
	protected static Map<Integer, Float> hashTemp = new HashMap<Integer, Float>();
	
	@SideOnly(Side.CLIENT)
	protected static long lastAudit = 0L;
	
	@SideOnly(Side.CLIENT)
	protected static void audit(long worldTime)
	{
		//Starting with an audit every 10 seconds, 200 ticks
		if(worldTime - lastAudit >= 200 || worldTime < lastAudit)
		{
			lastAudit = worldTime;
			
			
			boolean mismatched = false;
			
			//Check for proper synchronization first (?)
			for(Integer key : hashTemp.keySet())
			{
				if(!hashAge.containsKey(key))
				{
					mismatched = true;
				}
			}
			for(Integer key : hashAge.keySet())
			{
				if(!hashTemp.containsKey(key))
				{
					mismatched = true;
				}
			}
			//End of synchronization
			
			if(mismatched)
			{
				SimpleDifficulty.logger.warn("Thermometer audit had mismatched maps!");
				hashAge.clear();
				hashTemp.clear();
				return;
			}
			
			int removalCount = 0;
			
			Set<Integer> removalSet = new HashSet<Integer>();
			
			//Maps are synchronized
			for(Map.Entry<Integer,Long> entry : hashAge.entrySet())
			{
				//Expiration should be pretty quick since it's supposed to update automatically very quickly on its own
				//Let's go with 5 seconds for now, this means any thermometers you didn't look at for 5 - 15 seconds will clean themselves up...
				//That's fine? Probably?
				
				if(worldTime - entry.getValue() >= 100L || worldTime < entry.getValue())
				{
					removalSet.add(entry.getKey());
				}
			}
			
			for(Integer value : removalSet)
			{
				hashAge.remove(value);
				hashTemp.remove(value);
				removalCount++;
			}
			
			//It's Java, you never know
			removalSet.clear();
			
			//SimpleDifficulty.logger.debug("Thermometer audit removed "+removalCount+", map sizes "+hashAge.size()+" "+hashTemp.size());
		}
	}
	
	public ItemThermometer()
	{
		addPropertyOverride(new ResourceLocation("temperature"), new IItemPropertyGetter()
		{
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase pendingEntity)
			{
				//Taken from ItemClock, mostly
				
				boolean hasEntity = pendingEntity!=null;
				Entity entity = (Entity)(hasEntity? pendingEntity : stack.getItemFrame());
				
				if(world == null && entity!=null)
				{
					//Correct the world
					world = entity.world;
				}
				
				if(world == null || entity == null)
				{
					//Can't make an informed decision on temperature
					return 0.0f;
				}
				
				if(QuickConfig.isTemperatureEnabled() && ClientConfig.instance.getBoolean(ClientOptions.ENABLE_THERMOMETER))
				{
					return wobble(world, entity, stack.hashCode());
				}
				else
				{
					return 0.0f;
				}
			}
			@SideOnly(Side.CLIENT)
			private float wobble(World world, Entity entity, int hash)
			{
				//Okay, so
				//Need to get the temperature of the world at the entity position
				//But not all the time
				
				//It defaults to once a tick with the safe method, I guess
				//That's what, 20 updates a second? That's fancy, it really doesn't need that though
				//Going to try 20/10 for now
				
				
				//Cool cool the variables are shared between items
				//I don't know if there's a way around it
				
				//Which doesn't bode well for performance
				
				//Uh oh
				
				//Which means that caching its old value isn't actually going to work
				
				//How horrible
				
				//make it not horrible, SOMEHOW
				//This is going to run a temperature check way too fast
				
				
				
				/* The crazy shit? */
				
				//Check if it's time to audit the maps
				
				Long age = hashAge.get(hash);
				Float temp = hashTemp.get(hash);
				
				long totalWorldTime = world.getTotalWorldTime();
				
				ItemThermometer.audit(totalWorldTime);
				
				boolean cleanStart = false;
				
				if(age == null && temp == null)
				{
					//Add to maps as a fresh clean start
					cleanStart = true;
				}
				else if(age==null || temp == null)
				{
					//Only one of them is null... something bad happened
					hashAge.remove(hash);
					hashTemp.remove(hash);
					
					cleanStart = true;
				}
				
				if(cleanStart)
				{
					hashAge.put(hash, totalWorldTime);
					float newTemp = calculateTemperature(world, entity);
					hashTemp.put(hash, newTemp);
					
					return newTemp;
				}
				else
				{
					//Check old values and stuff
					//Stagger it as well so they update in pseudo random fashion instead of consistently
					if(totalWorldTime - age >= 10L + (hash & 7))
					{
						//Get a new temperature, not the cached
						float newTemp = calculateTemperature(world, entity);
						hashTemp.put(hash, newTemp);
						hashAge.put(hash, totalWorldTime);
						
						return newTemp;
					}
					else
					{
						return temp;
					}
				}
				
				//And that works.
				//Really, really well?..
				//It's kinda nuts I'm really surprised
				//There you go. A thermometer that doesn't lag.
				
				/* The harsh reality... or is it?
				float newDestTemperature = TemperatureUtil.clampTemperature(TemperatureUtil.getWorldTemperature(world, entity.getPosition()));
				float tempRange = (float)(TemperatureEnum.BURNING.getUpperBound() - TemperatureEnum.FREEZING.getLowerBound() + 1);
				
				return (newDestTemperature / tempRange);
				*/
				
				/* The dream
				 * You were too good for this world.isRemote
				 * 
				long totalWorldTime = world.getTotalWorldTime();
				if(totalWorldTime - lastUpdateTick >= 10L)
				{
					//UPDATE TICK
					lastUpdateTick = totalWorldTime;
					
					float newDestTemperature = TemperatureUtil.clampTemperature(TemperatureUtil.getWorldTemperature(world, entity.getPosition()));
					float tempRange = (float)(TemperatureEnum.BURNING.getUpperBound() - TemperatureEnum.FREEZING.getLowerBound() + 1);
					
					destinationTemperature = newDestTemperature / tempRange;
					//return (newDestTemperature / tempRange);
				}
				
				
				if(totalWorldTime - lastRenderTick >= 1L)
				{
					//RENDER TICK
					lastRenderTick = totalWorldTime;
					
					//Take a step towards the destination
					
					float difference = destinationTemperature - displayTemperature;
					if(Math.abs(difference)<0.01f)
					{
						displayTemperature = destinationTemperature;
					}
					else
					{
						displayTemperature += (difference / 2.0f);
					}
				}
				
				return displayTemperature;
				*/
			}
			@SideOnly(Side.CLIENT)
			private float calculateTemperature(World world, Entity entity)
			{
				float newDestTemperature = TemperatureUtil.clampTemperature(TemperatureUtil.getWorldTemperature(world, WorldUtil.getSidedBlockPos(world,entity)));
				float tempRange = (float)(TemperatureEnum.BURNING.getUpperBound() - TemperatureEnum.FREEZING.getLowerBound() + 1);
				
				return newDestTemperature / tempRange;
			}
			
		});
	}
}
