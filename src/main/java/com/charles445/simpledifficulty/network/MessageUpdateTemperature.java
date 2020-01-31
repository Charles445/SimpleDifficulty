package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageUpdateTemperature implements IMessage
{
	//Side CLIENT
	
	private NBTTagCompound nbt;
	
	public MessageUpdateTemperature()
	{
		//Necessary to avoid crash
	}
	
	public MessageUpdateTemperature(NBTBase nbt)
	{
		this.nbt = (NBTTagCompound)nbt;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeTag(buf, this.nbt);
	}
	
	public NBTTagCompound getNBT()
	{
		return this.nbt;
	}
	
	public static class Handler implements IMessageHandler<MessageUpdateTemperature, IMessage>
	{
		@Override
		public IMessage onMessage(MessageUpdateTemperature message, MessageContext ctx) 
		{
			if(ctx.side == Side.CLIENT)
			{
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				if(player!=null)
				{
					Minecraft.getMinecraft().addScheduledTask(() -> 
					{
						Capability<ITemperatureCapability> capability = SDCapabilities.TEMPERATURE;
						capability.getStorage().readNBT(capability, player.getCapability(capability, null), null, message.getNBT());
						//player.sendMessage(new TextComponentString("Received MessageUpdateThirst message"));
					});
				}
			}
			return null;
		}
	}
	

}
