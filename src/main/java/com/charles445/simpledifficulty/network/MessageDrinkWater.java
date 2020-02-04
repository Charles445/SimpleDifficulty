package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.util.internal.ThirstUtilInternal;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;


public class MessageDrinkWater implements IMessage
{
	//Side SERVER
	
	public MessageDrinkWater()
	{
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		//No data is shared
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		//No data is shared
	}
	
	public static class Handler implements IMessageHandler<MessageDrinkWater, IMessage>
	{
		@Override
		public IMessage onMessage(MessageDrinkWater message, MessageContext ctx) 
		{
			if(ctx.side == Side.SERVER)
			{
				//DebugUtil.messageAll("Server received MessageDrinkWater");
				
				EntityPlayerMP player = ctx.getServerHandler().player;
				if(player!=null)
				{
					player.getServerWorld().addScheduledTask(() -> 
					{
						ThirstEnum result = ThirstUtilInternal.traceWaterToDrink(player).thirstEnum;
						if(result==null)
							return;
						
						//Enum now has important values
						
						ThirstUtil.takeDrink(player, result.getThirst(), result.getSaturation(), result.getThirstyChance());
						
					});
				}
			}
			return null;
		}
	}
}
