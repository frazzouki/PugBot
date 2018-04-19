package core.commands;

import core.Constants;
import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdHelp extends Command{
	
	public CmdHelp(){
		this.helpMsg = Constants.HELP_HELP;
		this.description = Constants.HELP_DESC;
		this.name = Constants.HELP_NAME;
		this.dm = true;
	}
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		if (args.length == 0) {
			this.response = Utils.createMessage(helpBuilder(server, member));
		} else {
			if (server.cmds.validateCommand(args[0])) {
				Command cmd = server.cmds.getCommandObj(args[0]);
				this.response = Utils.createMessage(String.format("!%s - %s. Usage: %s", cmd.getName(), cmd.getDescription(), cmd.help()));
			}
		}
		System.out.println(success());
		
		return response;
	}
	
	private String helpBuilder(Server server, Member member){
		String s = "Commands:\n\n";
		Command cmdObj;
		
		// List commands
		for(String cmd : server.cmds.getCmds()){
			cmdObj = server.cmds.getCommandObj(cmd);
			if(cmd != "terminate"){
				s += String.format("!%s - %s%n", cmdObj.getName(), cmdObj.getDescription());
			} 
		}
		
		// List custom commands
		for(String cmd : server.cmds.getCustomCmds()){
			cmdObj = server.cmds.getCommandObj(cmd);
			s += String.format("!%s%n", cmdObj.getName()); 
		}
		
		// List admin commands
		if(server.isAdmin(member)){
			s += "\nAdmin commands:\n\n";
			for(String cmd : server.cmds.getAdminCmds()){
				cmdObj = server.cmds.getCommandObj(cmd);
				s += String.format("!%s - %s%n", cmdObj.getName(), cmdObj.getDescription()); 
			}
		}
		
		s = String.format("```%s```", s);
		return s;
	}
}
