package com.charles445.simpledifficulty.client.command;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class ClientCommandIdentityCopy extends ClientCommandBase
{
	@Override
	public String getName()
	{
		return "sdcopyidentity";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/sdcopyidentity <integer> <string>";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length<1)
		{
			sender.sendMessage(new TextComponentString("Copy failed! Too few arguments"));
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		
		for(int i=1;i<args.length;i++)
		{
			sb.append(args[i]);
			sb.append(" ");
		}
		
		if(sender.getEntityWorld().isRemote)
		{
			String nbtResult = sb.toString().trim();
			nbtResult = nbtResult.replaceAll("\"",  "\\\"");
			
			StringBuilder sbResult = new StringBuilder();
			
			
			sbResult.append("      \"identity\": {\n");
			sbResult.append("        \"metadata\": ");
			sbResult.append(args[0]);
			sbResult.append(",\n");
			sbResult.append("        \"nbt\": ");
			sbResult.append("\"");
			sbResult.append(nbtResult);
			sbResult.append("\"");
			sbResult.append("\n");
			sbResult.append("      },");
			
			StringSelection selection = new StringSelection(sbResult.toString());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, null);
			
			sender.sendMessage(new TextComponentString("Copied identity JSON to clipboard!"));
		}
		else
		{
			sender.sendMessage(new TextComponentString("World was not remote, skipping identity copy execution!"));
		}
	}
}
