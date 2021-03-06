package core.commands;

import java.util.concurrent.TimeUnit;

import core.entities.Game;
import core.entities.Game.GameStatus;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdFinishGame extends Command {
	
	public CmdFinishGame(Server server) {
		super(server);
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		QueueManager qm = server.getQueueManager();
		
		if(qm.isPlayerWaiting(caller)){
			return null;
		}
		
		Game game = qm.getPlayersGame(caller);
		
		if(game == null){
			throw new InvalidUseException("You are not currently in-game");
		}
		
		if(!game.isCaptain(caller)){
			throw new InvalidUseException("You must be a captain to finish your game");
		}
		
		if(game.getStatus() == GameStatus.PICKING || args.length == 0){
			qm.finishGame(game, null);
			
			return Utils.createMessage("`Game cancelled`");
		}
		
		String title = String.format("Game '%s' finished", game.getQueueName());
		String result = args[0].toLowerCase();
		int team = caller.equals(game.getCaptain1()) ? 1 : 2;
		int winningTeam;
		
		switch(result){
		case "tie": winningTeam = 0; break;
		case "win": winningTeam = team; break;
		case "loss": winningTeam = (team == 1) ? 2 : 1; break;
		default: throw new BadArgumentsException("Result must either be **win**, **loss**, or **tie**");
		}
		
		qm.finishGame(game, winningTeam);
		
		long msDiff = System.currentTimeMillis() - game.getTimestamp();
		long duration = TimeUnit.MINUTES.convert(msDiff, TimeUnit.MILLISECONDS);
		
		if(winningTeam != 0){
			Member winner = (winningTeam == 1) ? game.getCaptain1() : game.getCaptain2();
			String teamName = "Team " + winner.getEffectiveName();
			
			return Utils.createMessage(title, String.format("**Winner:** %s%n**Duration:** %d Minutes",
					teamName, duration), true);
		}
		else{
			return Utils.createMessage(title, String.format("**Tie game**%n**Duration:** %d Minutes", duration), true);
		}
	}

	@Override
	public boolean isAdminRequired() {
		return false;
	}

	@Override
	public boolean isGlobalCommand() {
		return false;
	}

	@Override
	public String getName() {
		return "FinishGame";
	}

	@Override
	public String getDescription() {
		return "Finishes a game so that users can requeue";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " - Finishes an unstarted game\n" +
				getBaseCommand() + " win/loss/tie - Finishes a game and records the result";
	}

}
