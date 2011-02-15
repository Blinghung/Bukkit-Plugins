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

package com.bukkit.tazzernator.mcdocs;

//java imports
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

//bukkit iimports
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

//Listener Class
public class MCDocsListener extends PlayerListener {
	
	//Some Variables for the class.
	private MCDocs plugin;
	private ArrayList<String> data = new ArrayList<String>();
	private ArrayList<String> lines = new ArrayList<String>();
	private ArrayList<String> fixedLines = new ArrayList<String>();
	private ArrayList<MCDocsCommands> records = new ArrayList<MCDocsCommands>();
	private Server server = null;
	
	//Constructor.
	public MCDocsListener(MCDocs instance, Server server) {
        this.plugin = instance;
        this.server = server;
    }

	//Method to read our files.
	private ArrayList<String> readLines(String filename) throws IOException {
		data.clear();
		records.clear();
		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
            	data.add(line.toLowerCase());
            }
        bufferedReader.close();
        return data;
	}
	
	//Method to determine the online names.
	private String onlineNames(){
		Player online[] = server.getOnlinePlayers();
        String onlineNames = null;
        for (Player o : online){
        	if (onlineNames == null){
        		onlineNames = o.getName();
        	}
        	else{
        		onlineNames = onlineNames + ", " + o.getName();
        	}
        }
        return onlineNames;
	}

	//Method to determine how many people are online.
	private String onlineCount(){
		Player online[] = server.getOnlinePlayers();
        int onlineCount = 0;
        for (@SuppressWarnings("unused") Player o : online){
        	onlineCount++;
        }
        return Integer.toString(onlineCount);
	}
	
	private void linesProcess(Player player, String command, int page){
		//Change all ampersands to Minecraft's weird thingo. And now in 4.0, change some variables.
        for(String l : lines){
        	String fixedLine = l.replace('&', '§');
        	fixedLine = fixedLine.replace("%name", player.getName());
        	fixedLine = fixedLine.replace("%online", onlineNames());
        	fixedLine = fixedLine.replace("%size", onlineCount());
        	if (MCDocs.Permissions != null){
        		String group = MCDocs.Permissions.getGroup(player.getName());
        		fixedLine = fixedLine.replace("%group", group);
        		try{
	        		fixedLine = fixedLine.replace("%prefix", MCDocs.Permissions.getGroupPrefix(group));
	        		fixedLine = fixedLine.replace("%suffix", MCDocs.Permissions.getGroupSuffix(group));
        		}
        		catch (Exception e){
        			fixedLine = fixedLine.replace("%prefix", "");
            		fixedLine = fixedLine.replace("%suffix", "");
        		}
        	}
        	fixedLines.add(fixedLine);
        }
      
        
        //Define our page numbers
        int size = fixedLines.size();
        int pages;
        
        if(size % 9 == 0){
        	pages = size / 9;
        }
        else{
        	pages = size / 9 + 1;
        }
        
        //This here grabs the specified 9 lines, or if it's the last page, the left over amount of lines.
        String commandName = command.replace("/", "");
        commandName = commandName.toUpperCase();
        File folder = plugin.getDataFolder();
        String folderName = folder.getParent();
        String header = null;
        
        if(pages != 1){
        	//Custom Header
        	try {
    			FileInputStream fis = new FileInputStream(folderName + "/MCDocs/headerformat.txt");
                Scanner scanner = new Scanner(fis, "UTF-8");
                    while (scanner.hasNextLine()) {
                    	try{
                    		header = scanner.nextLine();
                            }
                    	catch(Exception ex){
                    	}
                    }
                scanner.close();
                fis.close();
                
                //Replace variables.
                header = header.replace('&', '§');
                header = header.replace("%commandname", commandName);
                header = header.replace("%current", Integer.toString(page));
                header = header.replace("%count", Integer.toString(pages));
                header = header.replace("%command", command);
                header = header + " ";
                
                player.sendMessage(header);
    			
    		} catch (IOException e) {
    			player.sendMessage("§c" + commandName + " - Page " + Integer.toString(page) + " of " + Integer.toString(pages) + "  §f|  §7" + command +" <page>");
    		}
        }
        //Some Maths.
        int highNum = (page * 9);
        int lowNum = (page - 1) * 9;
        for (int number = lowNum; ((number < highNum) && (number < size)); number++){
        	player.sendMessage(fixedLines.get(number));
        }
	}
	
	public void onPlayerCommand(PlayerChatEvent event) {
		
		
		//Find our player and message
		String[] split = event.getMessage().split(" ");
        Player player = event.getPlayer();
        File folder = plugin.getDataFolder();
        String folderName = folder.getParent();
        
       
		//Update our Commands
        MCDocsCommands record = null;
        try {
			readLines(folderName + "/MCDocs/commands.txt");
		} catch (IOException e) {
			System.out.println(folderName + "/MCDocs/commands.txt not found!");
		}
        for (String d : data){
        	try{
        		String[] parts = d.split(":");
        		if(parts.length == 3){
        			record = new MCDocsCommands(parts[0], folderName + "/MCDocs/" + parts[1], parts[2]);
        		}
        		else if(parts.length == 2){
        			record = new MCDocsCommands(parts[0], folderName + "/MCDocs/" + parts[1], "null");
           		}
        		records.add(record);
        	}
        	catch (Exception e) {
        		System.out.println("Your commands.txt file is corrupt - Make sure there is no empty lines and the structure of each line is correct");
        	}
        	
        }
		
        
        for (MCDocsCommands r : records){
        	lines.clear();
        	fixedLines.clear();
        	String command = r.getCommand();
        	
        	String permission = "allow";
        	
        	if (split[0].equalsIgnoreCase(command)){
        		
        		//Permissions check - Hopefully should default to allow if it isn't installed.
        		if (MCDocs.Permissions != null){
        			String permissionCommand = "mcdocs." + command;
        			String group = MCDocs.Permissions.getGroup(player.getName());
        			if((r.getGroup().equalsIgnoreCase(group)) || (r.getGroup().equals("null"))){
        				permission = "allow";
        			}
        			else{
        				permission = "deny";
        			}
        			if(!MCDocs.Permissions.has(player, permissionCommand)){
        				permission = "deny";
        			}
        		}
        		if (permission == "allow"){
	        		try {
	        			//Add out lines to the list "lines"
	                    FileInputStream fis = new FileInputStream(r.getFile());
	                    Scanner scanner = new Scanner(fis, "UTF-8");
	    	                while (scanner.hasNextLine()) {
	    	                	try{
	    	                		lines.add(scanner.nextLine() + " ");
	    	                	}
	    	                	catch(Exception ex){
	    	                		lines.add(" ");
	    	                	}
	    	                }
	                    scanner.close();
	                    fis.close();
	                    
	                    //If split[1] does not exist, or has a letter, page = 1
	                    int page;
	                    try{
	                        page = Integer.parseInt(split[1]);
	                    }
	                    catch(Exception ex){
	                    	page = 1;
	                    }
	                    
	                    //Finally - Process our lines!
	                    linesProcess(player, command, page);
	                    }
	        		
	                 catch (Exception ex) {
	                	player.sendMessage("File not found!");
	                 	}
        		}
                 event.setCancelled(true);
        	}
        }   
	}

	public void onPlayerJoin(PlayerEvent event){
    	//MOTD On Login -- We try to find a group motd file, and if that fails, we try and find a normal motd file, and if that fails we give up.
		if (MCDocs.Permissions != null){
			groupMotd(event);
		}
		else{
			standardMotd(event);
		}
	}

	public void groupMotd(PlayerEvent event){
		File folder = plugin.getDataFolder();
        String folderName = folder.getParent();
		Player player = event.getPlayer();
		String group = MCDocs.Permissions.getGroup(player.getName()).toLowerCase();
		lines.clear();
    	fixedLines.clear();
    	try {
            FileInputStream fis = new FileInputStream(folderName + "/MCDocs/motd-" + group + ".txt");
            Scanner scanner = new Scanner(fis, "UTF-8");
                while (scanner.hasNextLine()) {
                	try{
                		lines.add(scanner.nextLine() + " ");
                	}
                	catch(Exception ex){
                		lines.add(" ");
                	}
                }
            scanner.close();
            fis.close();
          
            linesProcess(player, "/motd", 1);
            }
    	catch (Exception ex) {
    		standardMotd(event);
     	}
	}
	public void standardMotd(PlayerEvent event){
		File folder = plugin.getDataFolder();
        String folderName = folder.getParent();
		Player player = event.getPlayer();
		lines.clear();
    	fixedLines.clear();	
    	
    	try{
    		FileInputStream fis = new FileInputStream(folderName + "/MCDocs/motd.txt");
            Scanner scanner = new Scanner(fis, "UTF-8");
                while (scanner.hasNextLine()) {
                	try{
                		lines.add(scanner.nextLine() + " ");
                	}
                	catch(Exception ex1){
                		lines.add(" ");
                	}
                }
            scanner.close();
            fis.close();
          
            linesProcess(player, "/motd", 1);
    	}
    	catch (Exception ex) {
        	
     	}
	}
}
