package com.zefinder;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class NoNBTCommand extends JavaPlugin {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		
	}


}
