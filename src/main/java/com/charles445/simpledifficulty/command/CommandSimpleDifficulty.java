package com.charles445.simpledifficulty.command;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.config.JsonConfigInternal;
import com.charles445.simpledifficulty.config.JsonFileName;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class CommandSimpleDifficulty extends CommandBase
{
	private final List<String> tabCompletionsCommands = Arrays.asList(new String[]{
			"exportJson", 
			"reloadJson",
			"addArmor",
			"addBlock",
			"addConsumableTemperature",
			"addConsumableThirst",
			"addFluid"
	});
	
	private final String commandUsage = 
			  "exportJson\n"
			+ "reloadJson\n"
			+ "addArmor <temperature>\n"
			+ "addBlock <temperature>\n"
			+ "addConsumableTemperature <group> <temperature> <duration>\n"
			+ "addConsumableThirst <amount> <saturation> <thirstyChance>\n"
			+ "addFluid <temperature>";
	
	private final String warn_notPlayerAdmin = "You do not have permission, or are not a player ingame!";
	private final String warn_invalidArgs = "Invalid Arguments";
	private final String warn_noItem = "Not holding an item!";

	@Override
	public String getName()
	{
		return "simpledifficulty";
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return commandUsage;
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		//return Collections.<String>emptyList();
		return tabCompletionsCommands;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length==0)
		{
			help(sender);
			return;
		}
		
		switch(args[0].toLowerCase())
		{
			case "reloadjson": updateJson(server, sender, args); break;
			
			case "exportjson": exportJson(server, sender, args); break;
			
			case "addarmor": addArmor(server, sender, args); break;
			case "addblock": addBlock(server, sender, args); break;
			case "addconsumabletemperature": addConsumableTemperature(server, sender, args); break;
			case "addconsumablethirst": addConsumableThirst(server, sender, args); break;
			case "addfluid": addFluid(server, sender, args); break;
			
			/*
			armorTemperatures
			blockTemperatures
			consumableTemperature
			consumableThirst
			fluidTemperatures
			materialTemperature
			*/
			
			
			
			
			default: help(sender); break;
		}
		
	}
	
	private void addBlock(MinecraftServer server, ICommandSender sender, String[] args)
	{
		if(isAdminPlayer(sender))
		{
			if(args.length<2)
			{
				message(sender, warn_invalidArgs + " <temperature>");
				return;
			}
			
			try
			{
				float temperature = Float.parseFloat(args[1]);
				
				EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
				
				ItemStack stack = player.getHeldItemMainhand();
				
				if(stack.isEmpty())
				{
					message(sender, warn_noItem);
					return;
				}
				
				Block block = Block.getBlockFromItem(stack.getItem());
				
				if(block == Blocks.AIR)
				{
					message(sender, "Couldn't find block for item!");
					return;
				}
				
				boolean accepted = JsonConfig.registerBlockTemperature(block, temperature);
				
				if(accepted)
					message(sender, "Added block to "+JsonFileName.blockTemperatures+"!\n(Don't forget to exportJson !)");
				else
					message(sender, "Block has properties information in the JSON, use the JSON instead!");
			}
			catch(NumberFormatException e)
			{
				message(sender, warn_invalidArgs + " <temperature>");
				return;
			}
		}
		else
		{
			message(sender, warn_notPlayerAdmin);
		}
	}
	
	private void addFluid(MinecraftServer server, ICommandSender sender, String[] args)
	{
		if(isAdminPlayer(sender))
		{
			if(args.length<2)
			{
				message(sender, warn_invalidArgs + " <temperature>");
				return;
			}
			
			try
			{
				float temperature = Float.parseFloat(args[1]);
				
				EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
				
				ItemStack stack = player.getHeldItemMainhand();
				
				if(stack.isEmpty())
				{
					message(sender, warn_noItem);
					return;
				}
				
				FluidStack fluidStack = FluidUtil.getFluidContained(stack);
				if(fluidStack==null)
				{
					message(sender, "Couldn't find the item's fluid!");
					return;
				}
				
				JsonConfig.registerFluidTemperature(fluidStack.getFluid().getName(), temperature);
				message(sender, "Added fluid to "+JsonFileName.consumableThirst+"!\n(Don't forget to exportJson !)");
				
			}
			catch(NumberFormatException e)
			{
				message(sender, warn_invalidArgs + " <temperature>");
				return;
			}
		}
		else
		{
			message(sender, warn_notPlayerAdmin);
		}
	}
	
	private void addConsumableThirst(MinecraftServer server, ICommandSender sender, String[] args)
	{
		if(isAdminPlayer(sender))
		{
			if(args.length<4)
			{
				message(sender, warn_invalidArgs + " <amount> <saturation> <thirstyChance>");
				return;
			}
			
			try
			{
				int amount = Integer.parseInt(args[1]);
				float saturation = Float.parseFloat(args[2]);
				float thirstyChance = Float.parseFloat(args[3]);
				
				EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
				
				ItemStack stack = player.getHeldItemMainhand();
				
				if(stack.isEmpty())
				{
					message(sender, warn_noItem);
					return;
				}
				
				JsonConfig.registerConsumableThirst(stack, amount, saturation, thirstyChance);
				message(sender, "Added consumable item to "+JsonFileName.consumableThirst+"!\n(Don't forget to exportJson !)");
			}
			catch(NumberFormatException e)
			{
				message(sender, warn_invalidArgs + " <amount> <saturation> <thirstyChance>");
				return;
			}
		}
		else
		{
			message(sender, warn_notPlayerAdmin);
		}
	}
	
	private void addConsumableTemperature(MinecraftServer server, ICommandSender sender, String[] args)
	{
		if(isAdminPlayer(sender))
		{
			if(args.length<4)
			{
				message(sender, warn_invalidArgs + " <group> <temperature> <duration>");
				return;
			}
			
			try
			{
				String group = args[1];
				float temperature = Float.parseFloat(args[2]);
				int duration = Integer.parseInt(args[3]);
				
				group = group.replaceAll("\"", "");
				
				EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
				
				ItemStack stack = player.getHeldItemMainhand();
				
				if(stack.isEmpty())
				{
					message(sender, warn_noItem);
					return;
				}
				
				JsonConfig.registerConsumableTemperature(group, stack, temperature, duration);
				message(sender, "Added consumable item to "+JsonFileName.consumableTemperature+"!\n(Don't forget to exportJson !)");
			}
			catch(NumberFormatException e)
			{
				message(sender, warn_invalidArgs + " <group> <temperature> <duration>");
				return;
			}
		}
		else
		{
			message(sender, warn_notPlayerAdmin);
		}
	}
	
	private void addArmor(MinecraftServer server, ICommandSender sender, String[] args)
	{
		if(isAdminPlayer(sender))
		{
			if(args.length<2)
			{
				message(sender, warn_invalidArgs + " <temperature>");
				return;
			}
			
			try
			{
				float temperature = Float.parseFloat(args[1]);
				
				EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
				
				ItemStack stack = player.getHeldItemMainhand();
				
				if(stack.isEmpty())
				{
					message(sender, warn_noItem);
					return;
				}
				
				if(!(stack.getItem() instanceof ItemArmor))
				{
					message(sender, "Not a piece of armor!");
					return;
				}
				
				JsonConfig.registerArmorTemperature(stack, temperature);
				message(sender, "Added armor to "+JsonFileName.armorTemperatures+"!\n(Don't forget to exportJson !)");
				
				
			}
			catch(NumberFormatException e)
			{
				message(sender, warn_invalidArgs + " <temperature>");
				return;
			}
		}
		else
		{
			message(sender, warn_notPlayerAdmin);
		}
	}

	private void exportJson(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(hasPermissionLevel(sender, 4))
		{
			message(sender, "Exporting SimpleDifficulty JSON");
			String result = JsonConfigInternal.manuallyExportAll();
			message(sender, result);
		}
	}
	
	private void updateJson(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(hasPermissionLevel(sender, 4))
		{
			message(sender, "Reloading SimpleDifficulty JSON");
			JsonConfigInternal.jsonErrors.clear();
			JsonConfigInternal.processAllJson(SimpleDifficulty.jsonDirectory);
			for(String s : JsonConfigInternal.jsonErrors)
			{
				sender.sendMessage(new TextComponentString(s));
			}
		}
	}

	private boolean isAdminPlayer(ICommandSender sender)
	{
		if(hasPermissionLevel(sender, 4))
		{
			if(sender.getCommandSenderEntity() instanceof EntityPlayer)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private void help(ICommandSender sender)
	{
		sender.sendMessage(new TextComponentString(this.getUsage(sender)));
	}
	
	private void message(ICommandSender sender, String message)
	{
		sender.sendMessage(new TextComponentString(message));
	}
	
	private boolean hasPermissionLevel(ICommandSender sender, int permLevel)
	{
		return sender.canUseCommand(permLevel, "simpledifficulty");
	}
}
