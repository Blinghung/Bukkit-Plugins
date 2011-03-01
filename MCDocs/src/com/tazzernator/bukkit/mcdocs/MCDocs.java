/*
 * MCDocs by Tazzernator 
 * (Andrew Tajsic ~ atajsicDesigns ~ http://atajsic.com)
 * 
 * THIS PLUGIN IS LICENSED UNDER THE WTFPL - (Do What The Fuck You Want To Public License)
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details.
 * 
 * TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *   
 * 0. You just DO WHAT THE FUCK YOU WANT TO.
 *   
 * */

package com.tazzernator.bukkit.mcdocs;

//Java Import
import java.util.logging.Logger;

//Bukkit Import
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

//Permissions Import
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import org.bukkit.plugin.Plugin;

/**
 * MCDocs Plugin for Bukkit
 * 
 * --> Originally coded for Hey0 by Shadow386 Thankyou for the original source and the concept! :)
 * @author Tazzernator
 *(Andrew Tajsic - atajsicDesigns - http://atajsic.com)
 *
 */

public class MCDocs extends JavaPlugin {
	//Plugin Listener.
		
	private final MCDocsListener playerListener = new MCDocsListener(this);
	
	//Logger.
	public static final Logger log = Logger.getLogger("Minecraft");
	
	//Controller for permissions and security.
	public static PermissionHandler Permissions = null;
		
	/*public MCDocs(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin,	ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
	}*/		
	
	public void onDisable() {
	}
	
	public void onEnable() {
		//Setup Permissions
		setupPermissions();
		
		//Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
		
		//Check all is well
		PluginDescriptionFile pdfFile = this.getDescription();
        log.info(pdfFile.getName() + " by Tazzernator (Andrew Tajsic) - version " + pdfFile.getVersion() + " is enabled!" );
	}
	
	//Setup Function for Nijikokun's Permissions plugin - By Nijikokun. - Slightly Modified for MCDocs use.
	//http://forums.bukkit.org/threads/admn-info-permissions-v2-0-revolutionizing-the-group-system.1403/
	@SuppressWarnings("static-access")
	public void setupPermissions() {
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
		
		if(this.Permissions == null) {
			PluginDescriptionFile pdfFile = this.getDescription();
			if(test != null) {
				this.getServer().getPluginManager().enablePlugin(test);
				Permissions = ((Permissions)test).getHandler();
				log.info( pdfFile.getName() + " - Permissions Detected!" );
			}
			else {
				log.info( pdfFile.getName() + " - Permissions NOT Detected! (Don't Worry, it's not essential!)" );
			}
		}
	}
}
	
