package stabstabSTAB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.relauncher.FMLInjectionData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class UpdateCheck {
	static ArrayList<String> servers = new ArrayList<String>();
	static ModMetadata metadata;
	
	public UpdateCheck(ModMetadata meta){
		metadata=meta;
	}
	
	public Boolean HasJoinedBefore(String Server){
		return servers.contains(Server);
	}
	
	@ForgeSubscribe
	public void Join(EntityJoinWorldEvent event){
		EntityPlayer Joined;
		if (event.entity instanceof EntityPlayer) {
			Joined = (EntityPlayer) event.entity;
		} else {
			return;
		}// we only care if it's a player joining
		String Server=MinecraftServer.getServer().toString();
		if(HasJoinedBefore(Server)){
			return;
		}
		else{
			//the player has not been on this server this session
			servers.add(Server);
			
			Boolean firstLine = true;
			Boolean UpdateFound = false;
			for(String Line:UpdateCheck.CheckForUpdates()){
				if(firstLine){
					Joined.sendChatToPlayer(ChatMessageComponent.createFromText(Line).setColor(EnumChatFormatting.GOLD));
					firstLine=false;
					UpdateFound=true;
				}
				else
					Joined.sendChatToPlayer(ChatMessageComponent.createFromText(Line));	
			}
			if(UpdateFound){
				Joined.sendChatToPlayer(ChatMessageComponent.createFromText("Update checking for "+metadata.name+" provided by ZacTheHac").setColor(EnumChatFormatting.GOLD));
				//I simply ask that you keep a credit for me in here
			}
		}
	}

	public static String GetMcVersion(){
		String McVersion=null;
		try{
			McVersion=(String) FMLInjectionData.data()[4];
		}
		catch(Exception e){
			System.out.println("Update Checker was unable to get the current MC version through FML");
		}
		return McVersion;
	}
	
	public static boolean isInt(String str) {
	    try {
	        Integer.parseInt(str);
	        return true;
	    } catch (NumberFormatException nfe) {}
	    return false;
	}
	
	public static Boolean IsVersionNewer(String FoundVersion){
		return IsVersionNewer(metadata.version, FoundVersion);
	}
	
	public static Boolean IsVersionNewer(String CurrentVersion, String FoundVersion){//main constructor, but also used for testing
		//String CurrentVersion = metadata.version;
		if(CurrentVersion.equalsIgnoreCase(FoundVersion)){ //did you kno that == doesn't work this way on strings? it grabs where the string is, not the string
			return false;//better to say this here rather than itterate everything
		}
		List<String> CurVersion = new ArrayList<String>(Arrays.asList(CurrentVersion.split("\\.")));
		List<String> FndVersion = new ArrayList<String>(Arrays.asList(FoundVersion.split("\\.")));
		//ArrayList<String> FndVersion = (ArrayList<String>) Arrays.asList(FoundVersion.split("\\."));
		int listSize = 0;
		if(CurVersion.size()>FndVersion.size()||CurVersion.size()==FndVersion.size()){
			listSize = CurVersion.size();
		}
		else if(CurVersion.size()<FndVersion.size()){
			listSize = FndVersion.size();
		}
		try{
			for(int i = 0; i<listSize ; i++){
				if(CurVersion.get(i).equalsIgnoreCase(FndVersion.get(i))){
					continue;
				}
				if(isInt(CurVersion.get(i))&&isInt(FndVersion.get(i))){
					if(Integer.parseInt(CurVersion.get(i))>Integer.parseInt(FndVersion.get(i))){
						return false;
					}
					else if(Integer.parseInt(CurVersion.get(i))<Integer.parseInt(FndVersion.get(i))){
						return true;
					}
				}
				else{//there's a fucking letter in here
					if(CurVersion.get(i).length()==FndVersion.get(i).length()){
						try{
						for(int j = 0; j<CurVersion.get(i).length(); j++){
							if(CurVersion.get(i).charAt(j)==FndVersion.get(i).charAt(j)){
								continue;
							}
							else if(String.valueOf(CurVersion.get(i).charAt(j)).compareToIgnoreCase(String.valueOf(FndVersion.get(i).charAt(j)))>0){
								//WHAT A COMPARISON, this is if the CurVersion is newer
								return false;
							}
							else if(String.valueOf(CurVersion.get(i).charAt(j)).compareToIgnoreCase(String.valueOf(FndVersion.get(i).charAt(j)))<0){
								return true;
							}
							else{
								return false; //just in case
							}
						}
						}
						catch(Exception e){
							return false;//something fucked up, just ignore that one then
						}
					}
					else if(CurVersion.get(i).length()>FndVersion.get(i).length()){
						try{
							for(int j = 0; j<CurVersion.get(i).length(); j++){
								if(CurVersion.get(i).charAt(j)==FndVersion.get(i).charAt(j)){
									continue;
								}
								else if(String.valueOf(CurVersion.get(i).charAt(j)).compareToIgnoreCase(String.valueOf(FndVersion.get(i).charAt(j)))>0){
									//WHAT A COMPARISON, this is if the CurVersion is newer
									return false;
								}
								else if(String.valueOf(CurVersion.get(i).charAt(j)).compareToIgnoreCase(String.valueOf(FndVersion.get(i).charAt(j)))<0){
									return true;
								}
								else{
									return false; //just in case
								}
							}
						}
						catch(Exception e){
							//FndVersion ran out or something, meaning the current is probably a minor version above
							return false;
						}
					}
					else if(CurVersion.get(i).length()<FndVersion.get(i).length()){
						try{
							for(int j = 0; j<FndVersion.get(i).length(); j++){
								if(CurVersion.get(i).charAt(j)==FndVersion.get(i).charAt(j)){
									continue;
								}
								else if(String.valueOf(CurVersion.get(i).charAt(j)).compareToIgnoreCase(String.valueOf(FndVersion.get(i).charAt(j)))>0){
									//WHAT A COMPARISON, this is if the CurVersion is newer
									return false;
								}
								else if(String.valueOf(CurVersion.get(i).charAt(j)).compareToIgnoreCase(String.valueOf(FndVersion.get(i).charAt(j)))<0){
									return true;
								}
								else{
									return false; //just in case
								}
							}
						}
						catch(Exception e){
							//curVersion ran out or something, meaning it's most likely a small update
							return true;
						}
					}
				}
			}
		}
		catch(Exception e){
			if(CurVersion.size()>FndVersion.size()||CurVersion.size()==FndVersion.size()){
				return false;
			}
			else if(CurVersion.size()<FndVersion.size()){
				return true;
			}
			//most likely, this means everything was the same except for an aditional minor revision, so the longer one was probably newer
		}
		return false; //if it somehow got this far, just throw a false back
	}

	public static List<String> CheckForUpdates() {
		List<String> updateFound = new ArrayList<String>();
		String McVersion=GetMcVersion();
		if(McVersion==null){
			return null;//something broke
		}
		
		//ArrayList<String> updatefound = new ArrayList<String>();
		Boolean ChangeLogRead = false;
		String updatedVersion = "";
		URL url = null;
		try {
			url = new URL(metadata.updateUrl);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String Line;
			String[] Data = new String[2];
			String VersionHolder = "";

			while ((Line = in.readLine()) != null) {
				if(Line.startsWith("//"))
					continue; //allow us to leave notes inside the update file
				if(Line.startsWith("\t")){	
					if(ChangeLogRead){
						Line=Line.substring(Line.indexOf("\t")+1);
						if(!updatedVersion.equals(VersionHolder)){
							updateFound.add("["+updatedVersion+"]");
							VersionHolder=updatedVersion;
						}
						updateFound.add("     "+Line);
						continue;
					}
					else{
						continue;
					}
				}
				else{
					ChangeLogRead=false;
				}
				
				Data=Line.split(":");
				if(Data[0].equalsIgnoreCase(McVersion)||McVersion.startsWith(Data[0])){
					if(IsVersionNewer(Data[1])){
						if(updatedVersion.equals("")){
							updateFound.add("New version of "+metadata.name+" found! Changelog:");
						}
						ChangeLogRead=true;
						updatedVersion = Data[1];
					}
				}
			}
			in.close();
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException error1) {
			return null;
		} catch (Exception error) {
			return null;
		}

		return updateFound;
	}
}

//Update Checker By ZacTheHac