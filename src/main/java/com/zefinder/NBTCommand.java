package com.zefinder;

import java.io.FileNotFoundException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.format.TextColor;

public class NBTCommand implements CommandExecutor {

	private ConfigFile cf;
	
	public NBTCommand(ConfigFile cf) {
		this.cf = cf;
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String[] args) {
		Component error = Component.text("Wrong usage, click here to show the command usage !")
				.color(TextColor.fromHexString("0xef1515"))
				.clickEvent(ClickEvent.clickEvent(Action.RUN_COMMAND, "help nonbt"));
		
		if (args.length == 0) {
			sender.sendMessage(error);
			return false;
		}

		switch (args[0]) {
		case "add":
			if (args.length < 2) {
				sender.sendMessage(error);
			}
			
			try {
				cf.addCommand(args[1]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			break;

		case "remove":
			if (args.length < 2) {
				sender.sendMessage(error);
			}
			
			try {
				cf.removeCommand(args[1]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			break;

		case "removeAll":
			try {
				cf.removeAll();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			break;

		case "let":
			if (args.length < 2) {
				sender.sendMessage(error);
			}
			
			try {
				if (args[1].equals("allow")) {
					cf.changeAuth(ConfigFile.Authorization.ALLOW);
				} else {
					cf.changeAuth(ConfigFile.Authorization.FORBID);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			break;

		case "many":
			if (args.length < 2) {
				sender.sendMessage(error);
			}
			
			try {
				if (args[1].equals("some")) {
					cf.changeAuth(ConfigFile.Authorization.SOME);
				} else {
					cf.changeAuth(ConfigFile.Authorization.ALL);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			break;

		default:
			return false;
		}

		return true;
	}

}
