package com.zefinder;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class NoNBTCommand extends JavaPlugin {

	@Override
	public void onEnable() {
		ConfigFile cf = new ConfigFile();

		Bukkit.getPluginManager().registerEvents(new PlayerListener(cf), this);
		this.getCommand("nonbt").setExecutor(new NBTCommand(cf));
	}

}
