package br.edu.ufcg.ia.robocode;

import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleMessageEvent;

public class BattleObserver extends BattleAdaptor {

//	static final boolean LOG_PARTIAL_RESULTS = true;

	public void onBattleCompleted(BattleCompletedEvent e) {
//		log("-- Battle has completed --");
//		log("     Battle results:");
		for (robocode.BattleResults result : e.getSortedResults()) {
//			log("       " + result.getTeamLeaderName() + ": score " + result.getScore());
			GPAlgorithm.updateScores(result.getTeamLeaderName(), result.getScore());
		}
	}

//	public void onBattleMessage(BattleMessageEvent e) {
//		log("Msg> " + e.getMessage());
//	}

//	public void onBattleError(BattleErrorEvent e) {
//		log("Err> " + e.getError());
//	}

//	public void log(String out) {
//		if (LOG_PARTIAL_RESULTS) {
//			System.out.println(out);
//		}
//	}

}
