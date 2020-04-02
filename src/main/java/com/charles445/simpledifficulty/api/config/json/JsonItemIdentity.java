package com.charles445.simpledifficulty.api.config.json;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.SimpleDifficulty;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class JsonItemIdentity
{
	//NOTE, this does not have the registry name
	//Any comparisons made with this assume that the registry names are matching
	
	public int metadata;
	
	@Nullable
	public String nbt;
	
	@Nullable
	//private skips serialization
	private NBTTagCompound nbtCompound;
	
	public JsonItemIdentity(int metadata)
	{
		this(metadata, null);
	}
	
	public JsonItemIdentity(int metadata, String nbt)
	{
		this.metadata = metadata;
		this.nbt = nbt;
		
		if(nbt==null)
		{
			this.nbtCompound = null;
		}
		else
		{
			//TODO inform that this failed
			
			try
			{
				this.nbtCompound = JsonToNBT.getTagFromJson(nbt);
			}
			catch (NBTException e)
			{
				SimpleDifficulty.logger.error("Configuration failed with NBT string: "+nbt);
				
				//Remove the NBT from the item
				//TODO this could cause a problem
				
				this.nbtCompound = null;
				this.nbt = null;
			}
		}
	}
	
	public boolean matches(ItemStack stack)
	{
		//TODO this gets twice
		if(stack.hasTagCompound())
		{
			return matches(stack.getMetadata(), stack.getTagCompound());
		}
		else
		{
			return matches(stack.getMetadata(), null);
		}
	}
	
	public boolean matches(int stackMetadata)
	{
		return matches(stackMetadata, null);
	}

	public boolean matches(JsonItemIdentity sentIdentity)
	{
		return matches(sentIdentity.metadata, sentIdentity.nbtCompound);
	}
	
	public boolean matches(int stackMetadata, @Nullable NBTTagCompound stackCompound)
	{
		//Check metadaata
		if(metadata == -1 || metadata == 32767 || metadata == stackMetadata)
		{
			if(nbt == null || nbt.isEmpty())
			{
				return true;
			}
			else
			{
				//Check if the stack has the required compound
				if(stackCompound==null)
					return false;
				
				//Compare all tags from the identity compound
				for(String key : this.nbtCompound.getKeySet())
				{
					NBTBase a = this.nbtCompound.getTag(key);
					NBTBase b = stackCompound.getTag(key);
					
					//continue on match, return false otherwise
					
					if(a!=null)
					{
						//nbtCompound tag exists and is not null
						//can use a simple equals operation from here
						
						if(a.equals(b))
						{
							continue;
						}
						else
						{
							return false;
						}
					}
					else
					{
						//nbtCompound tag exists but is null (somehow)
						
						if(b==null)
						{
							//Matching null
							continue;
						}
						else
						{
							//stackCompound's tag of the same thing is not null
							return false;
						}
					}
				}
				
				//If this was reached, it's a match
				return true;
			}
		}
		else
		{
			return false;
		}
	}
}
