package com.charles445.simpledifficulty.compat.mod;

import java.lang.reflect.Method;
import java.util.List;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureIdentity;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.temperature.ModifierBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class BaublesModifier extends ModifierBase
{
	private boolean enabled;
	
	private Class c_BaublesApi;
	private Method m_BaublesApi_getBaubles;
	
	public BaublesModifier()
	{
		super("Baubles");
		
		try
		{
			c_BaublesApi = Class.forName("baubles.api.BaublesApi");
			m_BaublesApi_getBaubles = c_BaublesApi.getMethod("getBaubles", EntityPlayer.class);
			
			enabled = true;
		}
		catch (Exception e)
		{
			SimpleDifficulty.logger.error("BaublesModifier reflection failed! Baubles compatibility is now disabled!",e);
			enabled = false;
		}
	}

	
	@Override
	public float getPlayerInfluence(EntityPlayer player)
	{
		if(enabled && ModConfig.server.compatibility.toggles.baubles)
		{
			try
			{
				IInventory inventory = (IInventory) m_BaublesApi_getBaubles.invoke(null, player);
				
				//Check to make sure it didn't send us nothing
				if(inventory==null)
					return 0.0f;
				
				float sum = 0.0f;
				
				for(int i=0;i<inventory.getSizeInventory();i++)
				{
					ItemStack stack = inventory.getStackInSlot(i);
					
					if(!stack.isEmpty())
					{
						sum += processStackJSON(stack);
						
						//NBT
						sum += TemperatureUtil.getArmorTemperatureTag(stack);
					}
				}
				
				return sum;
				
			}
			catch(Exception e)
			{
				SimpleDifficulty.logger.error("BaublesModifier reflection failed during getPlayerInfluence! Baubles compatibility is now disabled!",e);
				enabled = false;
			}
		}
		
		return 0.0f;
	}
	
	private float processStackJSON(ItemStack stack)
	{
		List<JsonTemperatureIdentity> armorList = JsonConfig.armorTemperatures.get(stack.getItem().getRegistryName().toString());
		
		if(armorList!=null)
		{
			for(JsonTemperatureIdentity jtm : armorList)
			{
				if(jtm==null)
					continue;
				
				if(jtm.matches(stack))
				{
					return jtm.temperature;
				}
			}
		}
		
		return 0.0f;
	}
}
