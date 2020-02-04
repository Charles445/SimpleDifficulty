package com.charles445.simpledifficulty.command;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.JsonConfigInternal;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class CommandSimpleDifficulty extends CommandBase
{
	private final List<String> tabCompletions = Arrays.asList(new String[]{"updateJson"});
	
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
		return "Options: updateJson";
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		//return Collections.<String>emptyList();
		return tabCompletions;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length==0)
		{
			help(sender);
			return;
		}
		
		switch(args[0])
		{
			case "updateJson": updateJson(server, sender, args); break;
			default: help(sender); break;
		}
		
	}
	
	private void help(ICommandSender sender)
	{
		sender.sendMessage(new TextComponentString(this.getUsage(sender)));
	}
	
	private void updateJson(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(hasPermissionLevel(sender, 2))
		{
			sender.sendMessage(new TextComponentString("Reloading SimpleDifficulty JSON"));
			JsonConfigInternal.jsonErrors.clear();
			JsonConfigInternal.processAllJson(SimpleDifficulty.jsonDirectory);
			for(String s : JsonConfigInternal.jsonErrors)
			{
				sender.sendMessage(new TextComponentString(s));
			}
		}
	}
	
	private boolean hasPermissionLevel(ICommandSender sender, int permLevel)
	{
		return sender.canUseCommand(permLevel, "simpledifficulty");
	}
}
