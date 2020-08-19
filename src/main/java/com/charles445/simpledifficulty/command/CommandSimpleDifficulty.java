package com.charles445.simpledifficulty.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDCompatibility;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.compat.JsonCompatDefaults;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Loader;

public class CommandSimpleDifficulty extends CommandBase
{
	private final List<String> tabCompletionsCommands = Arrays.asList(new String[]{
			"help",
			"exportJson", 
			"reloadJson",
			"addArmor",
			"addBlock",
			"addConsumableTemperature",
			"addConsumableThirst",
			"addFluid",
			"addHeldItem",
			//"loadDefaultModConfig",
			"nbt",
			"setThirst",
			"setTemperature"
	});
	
	private final String commandUsage = "/simpledifficulty help";
	
	
	private final String listOfCommands = 
			  "   help <command>\n"
			+ "   exportJson\n"
			+ "   reloadJson\n"
			+ "   addArmor <temperature>\n"
			+ "   addBlock <temperature>\n"
			+ "   addConsumableTemperature <group> <temperature> <duration>\n"
			+ "   addConsumableThirst <amount> <saturation> <thirstyChance>\n"
			+ "   addFluid <temperature>\n"
			+ "   addHeldItem <temperature>\n"
			//+ "   loadDefaultModConfig <modid>\n"
			+ "   nbt\n"
			+ "   setThirst <thirst> <saturation>\n"
			+ "   setTemperature <temperature>";

	private final String warn_notPlayerAdmin = "You do not have permission, or are not a player ingame!";
	private final String warn_invalidArgs = "Invalid Arguments";
	private final String warn_noItem = "Not holding an item!";
	private final String exportJsonReminder = "(Don't forget to exportJson !)";

	@Override
	public List<String> getAliases()
    {
		return Arrays.asList(new String[]{"sd"});
    }
	
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
		if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, tabCompletionsCommands);
        }
		else if(args.length==0)
		{
			return tabCompletionsCommands;
		}
		else
		{
			return Collections.<String>emptyList();
		}
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
			case "addhelditem": addHeldItem(server, sender, args); break;
			
			case "nbt": tagToString(server, sender, args); break;
			
			case "setthirst": setThirst(server, sender, args); break;
			case "settemperature": setTemperature(server, sender, args); break;
			
			/*
			armorTemperatures
			blockTemperatures
			consumableTemperature
			consumableThirst
			fluidTemperatures
			heldItemTemperatures
			materialTemperature
			*/
			
			
			//case "loaddefaultmodconfig": loadDefaultModConfig(server, sender, args); break;
			
			case "help": helpCommand(server, sender, args);break;
			
			default: help(sender); break;
		}
		
	}
	
	private void helpCommand(MinecraftServer server, ICommandSender sender, String[] args)
	{
		if(args.length<2)
		{
			//message(sender, "/simpledifficulty help <command> \n(Replace <command> with a simpledifficulty command name)\n"+listOfCommands);
			message(sender, listOfCommands);
			return;
		}
		
		switch(args[1].toLowerCase())
		{
		case "help":message(sender, "If you need more help, you can contact the mod author on CurseForge or GitHub");return;
		case "exportjson":message(sender, "Exports your in-game JSON changes to the config folder");return;
		case "reloadjson":message(sender, "Discards any unexported in-game JSON changes.\nReloads the JSON from the config folder");return;
		case "addarmor":message(sender, "Adds the held armor to the armor JSON\n(changes temperature when worn)");return;
		case "addblock":message(sender, "Adds the held block to the block JSON\n(changes temperature when near the block)");return;
		case "addconsumabletemperature":message(sender, "Adds the held item to the consumableTemperature JSON\n(modifies temperature over time when consumed)");return;
		case "addconsumablethirst":message(sender, "Adds the held item to the consumableThirst JSON\n(replenishes thirst when consumed)");return;
		case "addfluid":message(sender, "Adds the held fluid item to the fluid JSON\n(changes temperature when inside the fluid)");return;
		case "addhelditem":message(sender, "Adds the held item to the heldItems JSON\n(changes player temperature when held in mainhand or offhand)");return;
		//case "loaddefaultmodconfig":message(sender, "Loads a mod's built-in default JSON config.\nThis will overwrite any matching settings! Use caution.");return;
		case "nbt":message(sender, "Gets an item's NBT tag as a string for config use");return;
		case "setthirst":message(sender, "Sets the player's thirst");return;
		case "settemperature":message(sender, "Sets the player's temperature");return;
		
			default:message(sender, "/simpledifficulty help <command> \n(Replace <command> with a simpledifficulty command name)");return;
		}
	}
	
	private void setThirst(MinecraftServer server, ICommandSender sender, String[] args)
	{
		if(isAdminPlayer(sender))
		{
			try
			{
				if(args.length < 2)
				{
					message(sender, warn_invalidArgs + " <modid>");
					return;
				}
				
				EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
				IThirstCapability capability = SDCapabilities.getThirstData(player);
				
				capability.setThirstLevel(Integer.parseInt(args[1]));
				
				if(args.length >= 3)
				{
					capability.setThirstSaturation(Float.parseFloat(args[2]));
				}
			}
			catch(NumberFormatException e)
			{
				message(sender, warn_invalidArgs + " <thirst> <saturation>");
				return;
			}
		}
	}
	
	private void setTemperature(MinecraftServer server, ICommandSender sender, String[] args)
	{
		if(isAdminPlayer(sender))
		{
			try
			{
				if(args.length < 2)
				{
					message(sender, warn_invalidArgs + " <temperature>");
					return;
				}
				
				EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
				ITemperatureCapability capability = SDCapabilities.getTemperatureData(player);
				
				capability.setTemperatureLevel(Integer.parseInt(args[1]));
			}
			catch(NumberFormatException e)
			{
				message(sender, warn_invalidArgs + " <temperature>");
				return;
			}
		}
	}
	
	private void tagToString(MinecraftServer server, ICommandSender sender, String[] args)
	{
		if(isAdminPlayer(sender))
		{
			EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
			
			ItemStack stack = player.getHeldItemMainhand();
			
			if(stack.isEmpty())
			{
				message(sender, warn_noItem);
				return;
			}
			
			//Stack has an item
			if(stack.hasTagCompound())
			{
				NBTTagCompound compound = stack.getTagCompound();
				//message(sender, compound.toString());
				
				String compString = compound.toString();
				
				TextComponentString tc = new TextComponentString(compString);
				
				int metadata = stack.getHasSubtypes()?stack.getMetadata():-1;
				
				//Clickable style
				Style style = new Style();
				style.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sdcopyidentity "+metadata+" "+compString));
				style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to copy identity to clipboard")));
				tc.setStyle(style);
				
				sender.sendMessage(tc);
			}
			else
			{
				message(sender, "This item has no NBT tag.");
			}
		}
		else
		{
			message(sender, warn_notPlayerAdmin);
		}
	}
	//Deprecated, this no longer works or is necessary
	/*
	private void loadDefaultModConfig(MinecraftServer server, ICommandSender sender, String[] args)
	{
		if(isAdminPlayer(sender))
		{
			if(args.length<2)
			{
				message(sender, warn_invalidArgs + " <modid>");
				return;
			}
			
			String modid = args[1];
			
			boolean success = JsonCompatDefaults.instance.populate(modid);
			
			if(success)
			{
				message(sender, "Loaded JSON defaults for mod "+modid+"\n"+exportJsonReminder);
			}
			else
			{
				//Figure out why it didn't work
				if(!Loader.isModLoaded(modid))
				{
					message(sender, "The mod "+modid+" is not loaded!");
				}
				else if(SDCompatibility.disabledDefaultJson.contains(modid))
				{
					message(sender, "The mod "+modid+" has its built-in defaults disabled!");
				}
				else
				{
					message(sender, "There are no defaults for the mod "+modid+", or it's disabled in the compatibility config.");
				}
			}
		}
		else
		{
			message(sender, warn_notPlayerAdmin);
		}
	}
	*/
	
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
					FluidStack fluidStack = FluidUtil.getFluidContained(stack);
					if(fluidStack!=null)
					{
						block = fluidStack.getFluid().getBlock();
						if(block == Blocks.AIR)
						{
							message(sender, "Couldn't find block for fluid!");
							return;
						}
						else if(block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)
						{
							block = Blocks.FLOWING_LAVA;
							JsonConfig.registerBlockTemperature(Blocks.LAVA, temperature);
						}
						else if(block == Blocks.WATER || block == Blocks.FLOWING_WATER)
						{

							block = Blocks.FLOWING_WATER;
							JsonConfig.registerBlockTemperature(Blocks.WATER, temperature);
						}
					}
					else
					{
						message(sender, "Couldn't find block for item!");
						return;
					}
				}
				
				boolean accepted = JsonConfig.registerBlockTemperature(block, temperature);
				
				if(accepted)
					message(sender, "Added block to "+JsonFileName.blockTemperatures+"!\n"+exportJsonReminder);
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
				message(sender, "Added fluid to "+JsonFileName.consumableThirst+"!\n"+exportJsonReminder);
				
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
				message(sender, "Added consumable item to "+JsonFileName.consumableThirst+"!\n"+exportJsonReminder);
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
	
	private void addHeldItem(MinecraftServer server, ICommandSender sender, String[] args)
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
				
				//Okay, so it can either be a block or an item this time around
				//JSON should be storing the block's registry if it is a block
				JsonConfig.registerHeldItem(stack, temperature);
				message(sender, "Added held item to "+JsonFileName.heldItemTemperatures+"!\n"+exportJsonReminder);
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
				message(sender, "Added consumable item to "+JsonFileName.consumableTemperature+"!\n"+exportJsonReminder);
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
				message(sender, "Added armor to "+JsonFileName.armorTemperatures+"!\n"+exportJsonReminder);
				
				
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
			
			//Clear the data containers
			JsonConfigInternal.clearContainers();
			
			//Reload the json (do the startup routine)
			JsonConfigInternal.init(SimpleDifficulty.jsonDirectory);
			
			
			//JsonConfigInternal.processAllJson(SimpleDifficulty.jsonDirectory);
			
			
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
