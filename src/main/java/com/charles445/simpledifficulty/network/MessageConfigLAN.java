package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;


public class MessageConfigLAN implements IMessage
{
	//Side SERVER

	public MessageConfigLAN()
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
	
	public static class Handler implements IMessageHandler<MessageConfigLAN, IMessage>
	{
		//TODO reconsider
		//It could just routinely check config changes
		//Or it could check the sender's privileges as well
		
		@Override
		public IMessage onMessage(MessageConfigLAN message, MessageContext ctx) 
		{
			if(ctx.side == Side.SERVER)
			{
				EntityPlayerMP sender = ctx.getServerHandler().player;
				if(sender!=null)
				{
					//DebugUtil.messageAll("Server received MessageConfigLAN");
					//DebugUtil.messageAll("Operating on logical side: "+ FMLCommonHandler.instance().getEffectiveSide());
					//DebugUtil.messageAll("Operating on physical side: "+ FMLCommonHandler.instance().getSide());
					if(FMLCommonHandler.instance().getSide().isClient())
					{
						//Physical Client, Logical Server
						//Should be able to access Logical Client things here...
						EntityPlayer receiver = SimpleDifficulty.proxy.getClientMinecraftPlayer();
						if(receiver==null)
						{
							SimpleDifficulty.logger.error("Client's player was null on physical client side!");
							return null;
						}
						//Compare the sender and receiver
						
						//TODO Proper verification that both players are the same person
						//If there's going to be some weird security loophole, it's going to be right here
						//Although the only result of any loophole is the server sending packets to players more than it should
						//The horror!
						
						if(sender.getUniqueID().equals(receiver.getUniqueID()))
						{
							//Send new config to all players
							sender.getServerWorld().addScheduledTask(() ->
							{
								ModConfig.sendServerConfigToAllPlayers();
							});
						}
					}
				}
			}
				
			return null;
		}
	}
}
