package com.charles445.simpledifficulty.capability;

import javax.vecmath.Vector3d;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.debug.DebugUtil;
import com.charles445.simpledifficulty.util.DamageUtil;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ThirstCapability implements IThirstCapability
{
	private float exhaustion = 0.0f;
	private int thirst = 20;
	private float saturation = 0.0f;
	private int ticktimer = 0;
	
	//Unsaved data
	private int oldthirst = 0;
	private float oldsaturation = 0.0f;
	Vector3d position = null;
	
	@Override
	public void tickUpdate(EntityPlayer player, World world, TickEvent.Phase phase)
	{
		//Allowing sprinting, for now, I don't see a reliable way to actually disable player sprinting
		//Stop sprinting when thirsty (same as hunger)
		if(phase == TickEvent.Phase.START)
		{
			//checkSprint(player);
			return;
		}
		
		//Initialize position
		if(position == null)
			position = new Vector3d(player.posX,player.posY,player.posZ);
		
		//Get the new position
		Vector3d newPosition = new Vector3d(player.posX,player.posY,player.posZ);
		
		//Find the movement distance and set the old position to the new position
		position.sub(newPosition);
		position.absolute();
		
		//Movement is sensitive to every hundredth of a block
		int moveDistance = (int)Math.round(position.length() * 100);
		position = newPosition;
		
		//Avoid getting thirsty on teleport (if you can move 10 blocks in a tick, you win!)
		if(moveDistance > 1000)
		{
			moveDistance = 0;
		}
		
		if(moveDistance>0)
		{
			//Manage exhaustion
			float moveSensitivity = 0.01f;
			if(player.isInWater() || player.isInsideOfMaterial(Material.WATER))
			{
				moveSensitivity = 0.015F;
			}
			else if(player.onGround)
			{
				if(player.isSprinting())
				{
					moveSensitivity = 0.1f;
				}
				else
				{
					moveSensitivity = 0.01f;
				}
			}
			//Sensitive to every hundredth of a block, so multiply by 1/100
			//DebugUtil.clientMessage(player, ""+(moveSensitivity*0.01f*moveDistance));
			this.addThirstExhaustion(moveSensitivity * 0.01f * moveDistance);
		}
		
		//Process exhaustion to determine whether to make thirsty
		if(this.getThirstExhaustion() > 4.0f)
		{
			//Exhausted, do a thirst tick
			this.addThirstExhaustion(-4.0f);
			
			if(this.getThirstSaturation() > 0.0f)
			{
				//Exhaust from saturation
				this.addThirstSaturation(-1.0f);
			}
			else if(DamageUtil.isModDangerous(world))
			{
				//Exhaust from thirst
				this.addThirstLevel(-1);
			}
		}
		
		//Hurt ticking
		if(this.getThirstLevel()<=0)
		{
			this.addThirstTickTimer(1);
			if(this.getThirstTickTimer()>=80)
			{
				this.setThirstTickTimer(0);
				
				if(DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player))
				{
					player.attackEntityFrom(DamageSource.STARVE, 1.0f);
				}
			}
		}
		else
		{
			//Reset the timer if not dying of thirst
			this.setThirstTickTimer(0);
		}
		
		//checkSprint(player);
	}
	
	private void checkSprint(EntityPlayer player)
	{
		//Server side sprinting check
		if(player.isSprinting() && this.getThirstLevel()<=6)
		{
			player.setSprinting(false);
		}
	}
	
	@Override
	public boolean isDirty()
	{
		return (this.thirst!=this.oldthirst || this.saturation!=this.oldsaturation);
	}
	
	@Override
	public void setClean()
	{
		this.oldthirst=this.thirst;
		this.oldsaturation=this.saturation;
	}
	
	@Override
	public float getThirstExhaustion()
	{
		return exhaustion;
	}

	@Override
	public int getThirstLevel()
	{
		return thirst;
	}

	@Override
	public float getThirstSaturation()
	{
		return saturation;
	}

	@Override
	public int getThirstTickTimer()
	{
		return ticktimer;
	}

	@Override
	public void setThirstExhaustion(float exhaustion)
	{
		this.exhaustion=Math.max(exhaustion,0.0f);
	}

	@Override
	public void setThirstLevel(int thirst)
	{
		this.thirst = MathHelper.clamp(thirst, 0, 20);
	}

	@Override
	public void setThirstSaturation(float saturation)
	{
		this.saturation = MathHelper.clamp(saturation, 0.0f, 20.0f);
	}

	@Override
	public void setThirstTickTimer(int ticktimer)
	{
		this.ticktimer=ticktimer;
	}

	@Override
	public void addThirstExhaustion(float exhaustion)
	{
		this.setThirstExhaustion(this.getThirstExhaustion() + exhaustion);
	}

	@Override
	public void addThirstLevel(int thirst)
	{
		this.setThirstLevel(this.getThirstLevel() + thirst);
	}

	@Override
	public void addThirstSaturation(float saturation)
	{
		this.setThirstSaturation(this.getThirstSaturation() + saturation);
	}
	
	@Override
	public void addThirstTickTimer(int ticktimer)
	{
		this.setThirstTickTimer(this.getThirstTickTimer() + ticktimer);
	}
	
	@Override
	public boolean isThirsty()
	{
		return this.getThirstLevel() < 20;
	}
}
