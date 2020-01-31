package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.api.config.ServerConfig;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageUpdateConfig implements IMessage
{
	//Side CLIENT
	
	private NBTTagCompound nbt;
	
	public MessageUpdateConfig()
	{
		//Necessary to avoid crash
	}
	
	public MessageUpdateConfig(NBTTagCompound compound)
	{
		nbt = compound;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		nbt = ByteBufUtils.readTag(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, nbt);
	}
	
	
	public static class Handler implements IMessageHandler<MessageUpdateConfig, IMessage>
	{
		@Override
		public IMessage onMessage(MessageUpdateConfig message, MessageContext ctx) 
		{
			if(ctx.side == Side.CLIENT)
			{
				Minecraft.getMinecraft().addScheduledTask(() -> 
				{
					ServerConfig.instance.updateValues(message.nbt);
				});
			}
			return null;
		}
	}


	
}
