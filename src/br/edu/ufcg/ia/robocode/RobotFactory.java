package br.edu.ufcg.ia.robocode;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Creates the robot and compiles it.
 */
public class RobotFactory {

	public final static int MIN_ROBOT_ID = 1;
	public final static int MAX_ROBOT_ID = 8;
	public final static int MAX_FEATURES = 5;
	public final static String MODEL_NAME = "ia.ModelWithAllSamples*"; // "jeuj.JeujDaPeu*";
	public final static String MODEL_NAME_TO_COMPILE = "ModelWithAllSamples";

	public final static String DIR_TO_COMPILE = "robots/ia/";
	public final static String DIR_GENERATIONS = DIR_TO_COMPILE + "best_of_generations/";
	public final static String FILE_TO_COMPILE = DIR_TO_COMPILE + MODEL_NAME_TO_COMPILE + ".java"; // "robots/jeuj/JeujDaPeu.java"

	public static void create(int[] chromo) {
		createRobotFile(chromo);
		compile();
	}

	public static void compile() {
		String fileToCompile = FILE_TO_COMPILE;
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, fileToCompile);
	}

	public static void createRobotFile(int[] chromo) {
		saveFile(FILE_TO_COMPILE, MODEL_NAME_TO_COMPILE, chromo);
	}

	public static void saveCodeFromBestSolutionOfGeneration(int index, int[] chromo) {
		String className = "BestGen" + index;
		saveFile(DIR_GENERATIONS + className + ".java", className, chromo);
	}

	private static void saveFile(String filePath, String className, int[] chromo) {
		try {
			FileWriter fstream = new FileWriter(filePath);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("package ia;" +
							"\n" +
							"\nimport robocode.*;" +
							"\n" +
							"\nimport java.awt.*;" +
							"\n" +
							"\nimport static robocode.util.Utils.normalRelativeAngleDegrees;" +
							"\n" +
							"\npublic class " + className + " extends AdvancedRobot {" +
							"\n	final static int CORNERS = 1;" +
							"\n	final static int CRAZY = 2;" +
							"\n	final static int FIRE = 3;" +
							"\n	final static int RAMFIRE = 4;" +
							"\n	final static int SPINBOT = 5;" +
							"\n	final static int TRACKER = 6;" +
							"\n	final static int TRACKERFIRE = 7;" +
							"\n	final static int WALLS = 8;" +
							"\n" +
							"\n	int others; // Number of other robots in the game" +
							"\n	static int corner = 0; // Which corner we are currently using" +
							"\n	// static so that it keeps it between rounds." +
							"\n	boolean stopWhenSeeRobot = false; // See goCorner()" +
							"\n	boolean movingForward;" +
							"\n" +
							"\n	int dist = 50; // distance to move when we're hit" +
							"\n" +
							"\n	int turnDirection = 1; // Clockwise or counterclockwise" +
							"\n" +
							"\n	int count = 0; // Keeps track of how long we've" +
							"\n	// been searching for our target" +
							"\n	double gunTurnAmt; // How much to turn our gun when searching" +
							"\n	String trackName; // Name of the robot we're currently tracking" +
							"\n" +
							"\n	boolean peek; // Don't turn if there's a robot there" +
							"\n	double moveAmount; // How much to move" +
							"\n" +
							"\n	/**" +
							"\n	 * Feature 0" +
							"\n	 */" +
							"\n	public void run() {" +
							"\n		if (" + chromo[0] + " == CORNERS) {" +
							"\n			setBodyColor(Color.red);" +
							"\n			setGunColor(Color.black);" +
							"\n			setRadarColor(Color.yellow);" +
							"\n			setBulletColor(Color.green);" +
							"\n			setScanColor(Color.green);" +
							"\n			others = getOthers(); // Save # of other bots" +
							"\n			goCorner(); // Move to a corner" +
							"\n		} else if (" + chromo[0] + " == CRAZY) { // to corners" +
							"\n			setBodyColor(new Color(0, 200, 0));" +
							"\n			setGunColor(new Color(0, 150, 50));" +
							"\n			setRadarColor(new Color(0, 100, 100));" +
							"\n			setBulletColor(new Color(255, 255, 100));" +
							"\n			setScanColor(new Color(255, 200, 200));" +
							"\n		} else if (" + chromo[0] + " == FIRE) {" +
							"\n			setBodyColor(Color.orange);" +
							"\n			setGunColor(Color.orange);" +
							"\n			setRadarColor(Color.red);" +
							"\n			setScanColor(Color.red);" +
							"\n			setBulletColor(Color.red);" +
							"\n		} else if (" + chromo[0] + " == RAMFIRE) {" +
							"\n			setBodyColor(Color.lightGray);" +
							"\n			setGunColor(Color.gray);" +
							"\n			setRadarColor(Color.darkGray);" +
							"\n		} else if (" + chromo[0] + " == SPINBOT) {" +
							"\n			setBodyColor(Color.blue);" +
							"\n			setGunColor(Color.blue);" +
							"\n			setRadarColor(Color.black);" +
							"\n			setScanColor(Color.yellow);" +
							"\n		} else if (" + chromo[0] + " == TRACKER) {" +
							"\n			setBodyColor(new Color(128, 128, 50));" +
							"\n			setGunColor(new Color(50, 50, 20));" +
							"\n			setRadarColor(new Color(200, 200, 70));" +
							"\n			setScanColor(Color.white);" +
							"\n			setBulletColor(Color.blue);" +
							"\n			// Prepare gun" +
							"\n			trackName = null; // Initialize to not tracking anyone" +
							"\n			setAdjustGunForRobotTurn(true); // Keep the gun still when we turn" +
							"\n			gunTurnAmt = 10; // Initialize gunTurn to 10" +
							"\n		} else if (" + chromo[0] + " == TRACKERFIRE) {" +
							"\n			setBodyColor(Color.pink);" +
							"\n			setGunColor(Color.pink);" +
							"\n			setRadarColor(Color.pink);" +
							"\n			setScanColor(Color.pink);" +
							"\n			setBulletColor(Color.pink);" +
							"\n		} else if (" + chromo[0] + " == WALLS) {" +
							"\n			setBodyColor(Color.black);" +
							"\n			setGunColor(Color.black);" +
							"\n			setRadarColor(Color.orange);" +
							"\n			setBulletColor(Color.cyan);" +
							"\n			setScanColor(Color.cyan);" +
							"\n			// Initialize moveAmount to the maximum possible for this battlefield." +
							"\n			moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());" +
							"\n			// Initialize peek to false" +
							"\n			peek = false;" +
							"\n			// turnLeft to face a wall." +
							"\n			// getHeading() % 90 means the remainder of" +
							"\n			// getHeading() divided by 90." +
							"\n			turnLeft(getHeading() % 90);" +
							"\n			ahead(moveAmount);" +
							"\n			// Turn the gun to turn right 90 degrees." +
							"\n			peek = true;" +
							"\n			turnGunRight(90);" +
							"\n			turnRight(90);" +
							"\n		}" +
							"\n" +
							"\n		// Initialize gun turn speed to 3 -- to Corners" +
							"\n		int gunIncrement = 3;" +
							"\n" +
							"\n		while (true) {" +
							"\n			if (" + chromo[0] + " == CORNERS) {" +
							"\n				for (int i = 0; i < 30; i++) {" +
							"\n					turnGunLeft (gunIncrement);" +
							"\n				}" +
							"\n				gunIncrement *= -1;" +
							"\n			} else if (" + chromo[0] + " == CRAZY) {" +
							"\n				// Tell the game we will want to move ahead 40000 -- some large number" +
							"\n				setAhead(40000);" +
							"\n				movingForward = true;" +
							"\n				// Tell the game we will want to turn right 90" +
							"\n				setTurnRight(90);" +
							"\n				// At this point, we have indicated to the game that *when we do something*," +
							"\n				// we will want to move ahead and turn right.  That's what set means." +
							"\n				// It is important to realize we have not done anything yet!" +
							"\n				// In order to actually move, we'll want to call a method that" +
							"\n				// takes real time, such as waitFor." +
							"\n				// waitFor actually starts the action -- we start moving and turning." +
							"\n				// It will not return until we have finished turning." +
							"\n				waitFor(new TurnCompleteCondition(this));" +
							"\n				// Note:  We are still moving ahead now, but the turn is complete." +
							"\n				// Now we'll turn the other way..." +
							"\n				setTurnLeft(180);" +
							"\n				// ... and wait for the turn to finish ..." +
							"\n				waitFor(new TurnCompleteCondition(this));" +
							"\n				// ... then the other way ..." +
							"\n				setTurnRight(180);" +
							"\n				// .. and wait for that turn to finish." +
							"\n				waitFor(new TurnCompleteCondition(this));" +
							"\n				// then back to the top to do it all again" +
							"\n			} else if (" + chromo[0] + " == FIRE) {" +
							"\n				turnGunRight(5);" +
							"\n			} else if (" + chromo[0] + " == RAMFIRE) {" +
							"\n				turnRight(5 * turnDirection);" +
							"\n			} else if (" + chromo[0] + " == SPINBOT) {" +
							"\n				// Tell the game that when we take move," +
							"\n				// we'll also want to turn right... a lot." +
							"\n				setTurnRight(10000);" +
							"\n				// Limit our speed to 5" +
							"\n				setMaxVelocity(5);" +
							"\n				// Start moving (and turning)" +
							"\n				ahead(10000);" +
							"\n				// Repeat." +
							"\n			} else if (" + chromo[0] + " == TRACKER) {" +
							"\n				// turn the Gun (looks for enemy)" +
							"\n				turnGunRight(gunTurnAmt);" +
							"\n				// Keep track of how long we've been looking" +
							"\n				count++;" +
							"\n				// If we've haven't seen our target for 2 turns, look left" +
							"\n				if (count > 2) {" +
							"\n					gunTurnAmt = -10;" +
							"\n				}" +
							"\n				// If we still haven't seen our target for 5 turns, look right" +
							"\n				if (count > 5) {" +
							"\n					gunTurnAmt = 10;" +
							"\n				}" +
							"\n				// If we *still* haven't seen our target after 10 turns, find another target" +
							"\n				if (count > 11) {" +
							"\n					trackName = null;" +
							"\n				}" +
							"\n			} else if (" + chromo[0] + " == TRACKERFIRE) {" +
							"\n				turnGunRight(10); // Scans automatically" +
							"\n			} else if (" + chromo[0] + " == WALLS) {" +
							"\n				// Look before we turn when ahead() completes." +
							"\n				peek = true;" +
							"\n				// Move up the wall" +
							"\n				ahead(moveAmount);" +
							"\n				// Don't look now" +
							"\n				peek = false;" +
							"\n				// Turn to the next wall" +
							"\n				turnRight(90);" +
							"\n			}" +
							"\n		}" +
							"\n" +
							"\n	}" +
							"\n" +
							"\n	/**" +
							"\n	 * Feature 1" +
							"\n	 */" +
							"\n	public void onScannedRobot(ScannedRobotEvent e) {" +
							"\n		if (" + chromo[1] + " == CORNERS) {" +
							"\n			// Should we stop, or just fire?" +
							"\n			if (stopWhenSeeRobot) {" +
							"\n				// Stop everything!  You can safely call stop multiple times." +
							"\n				stop();" +
							"\n				// Call our custom firing method" +
							"\n				smartFire(e.getDistance());" +
							"\n				// Look for another robot." +
							"\n				// NOTE:  If you call scan() inside onScannedRobot, and it sees a robot," +
							"\n				// the game will interrupt the event handler and start it over" +
							"\n				scan();" +
							"\n				// We won't get here if we saw another robot." +
							"\n				// Okay, we didn't see another robot... start moving or turning again." +
							"\n				resume();" +
							"\n			} else {" +
							"\n				smartFire(e.getDistance());" +
							"\n			}" +
							"\n		} else if (" + chromo[1] + " == CRAZY) {" +
							"\n			fire(1);" +
							"\n		} else if (" + chromo[1] + " == FIRE) {" +
							"\n			// If the other robot is close by, and we have plenty of life," +
							"\n			// fire hard!" +
							"\n			if (e.getDistance() < 50 && getEnergy() > 50) {" +
							"\n				fire(3);" +
							"\n			} // otherwise, fire 1." +
							"\n			else {" +
							"\n				fire(1);" +
							"\n			}" +
							"\n			// Call scan again, before we turn the gun" +
							"\n			scan();" +
							"\n		} else if (" + chromo[1] + " == RAMFIRE) {" +
							"\n			if (e.getBearing() >= 0) {" +
							"\n				turnDirection = 1;" +
							"\n			} else {" +
							"\n				turnDirection = -1;" +
							"\n			}" +
							"\n			turnRight(e.getBearing());" +
							"\n			ahead(e.getDistance() + 5);" +
							"\n			scan(); // Might want to move ahead again!" +
							"\n		} else if (" + chromo[1] + " == SPINBOT) {" +
							"\n			fire(3);" +
							"\n		} else if (" + chromo[1] + " == TRACKER) {" +
							"\n			// If we have a target, and this isn't it, return immediately" +
							"\n			// so we can get more ScannedRobotEvents." +
							"\n			if (trackName != null && !e.getName().equals(trackName)) {" +
							"\n				return;" +
							"\n			}" +
							"\n			// If we don't have a target, well, now we do!" +
							"\n			if (trackName == null) {" +
							"\n				trackName = e.getName();" +
							"\n				out.println(\"Tracking \" + trackName);" +
							"\n			}" +
							"\n			// This is our target.  Reset count (see the run method)" +
							"\n			count = 0;" +
							"\n			// If our target is too far away, turn and move toward it." +
							"\n			if (e.getDistance() > 150) {" +
							"\n				gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));" +
							"\n				turnGunRight(gunTurnAmt); // Try changing these to setTurnGunRight," +
							"\n				turnRight(e.getBearing()); // and see how much Tracker improves..." +
							"\n				// (you'll have to make Tracker an AdvancedRobot)" +
							"\n				ahead(e.getDistance() - 140);" +
							"\n				return;" +
							"\n			}" +
							"\n			// Our target is close." +
							"\n			gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));" +
							"\n			turnGunRight(gunTurnAmt);" +
							"\n			fire(3);" +
							"\n			// Our target is too close!  Back up." +
							"\n			if (e.getDistance() < 100) {" +
							"\n				if (e.getBearing() > -90 && e.getBearing() <= 90) {" +
							"\n					back(40);" +
							"\n				} else {" +
							"\n					ahead(40);" +
							"\n				}" +
							"\n			}" +
							"\n			scan();" +
							"\n		} else if (" + chromo[1] + " == TRACKERFIRE) {" +
							"\n			// Calculate exact location of the robot" +
							"\n			double absoluteBearing = getHeading() + e.getBearing();" +
							"\n			double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());" +
							"\n			// If it's close enough, fire!" +
							"\n			if (Math.abs(bearingFromGun) <= 3) {" +
							"\n				turnGunRight(bearingFromGun);" +
							"\n				// We check gun heat here, because calling fire()" +
							"\n				// uses a turn, which could cause us to lose track" +
							"\n				// of the other robot." +
							"\n				if (getGunHeat() == 0) {" +
							"\n					fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));" +
							"\n				}" +
							"\n			} // otherwise just set the gun to turn." +
							"\n			// Note:  This will have no effect until we call scan()" +
							"\n			else {" +
							"\n				turnGunRight(bearingFromGun);" +
							"\n			}" +
							"\n			// Generates another scan event if we see a robot." +
							"\n			// We only need to call this if the gun (and therefore radar)" +
							"\n			// are not turning.  Otherwise, scan is called automatically." +
							"\n			if (bearingFromGun == 0) {" +
							"\n				scan();" +
							"\n			}" +
							"\n		} else if (" + chromo[1] + " == WALLS) {" +
							"\n			fire(2);" +
							"\n			// Note that scan is called automatically when the robot is moving." +
							"\n			// By calling it manually here, we make sure we generate another scan event if there's a robot on the next" +
							"\n			// wall, so that we do not start moving up it until it's gone." +
							"\n			if (peek) {" +
							"\n				scan();" +
							"\n			}" +
							"\n		}" +
							"\n	}" +
							"\n" +
							"\n	/**" +
							"\n	 * Feature 2" +
							"\n	 */" +
							"\n	public void onHitWall(HitWallEvent e) {" +
							"\n		if (" + chromo[2] + " == CRAZY) {" +
							"\n			// Bounce off!" +
							"\n			reverseDirection();" +
							"\n		}" +
							"\n	}" +
							"\n" +
							"\n	/**" +
							"\n	 * Feature 3" +
							"\n	 */" +
							"\n	public void onHitRobot(HitRobotEvent e) {" +
							"\n		if (" + chromo[3] + " == CRAZY) {" +
							"\n			// If we're moving the other robot, reverse!" +
							"\n			if (e.isMyFault()) {" +
							"\n				reverseDirection();" +
							"\n			}" +
							"\n		} else if (" + chromo[3] + " == FIRE) {" +
							"\n			double turnGunAmt = normalRelativeAngleDegrees(e.getBearing() + getHeading() - getGunHeading());" +
							"\n			turnGunRight(turnGunAmt);" +
							"\n			fire(3);" +
							"\n		} else if (" + chromo[3] + " == RAMFIRE) {" +
							"\n			if (e.getBearing() >= 0) {" +
							"\n				turnDirection = 1;" +
							"\n			} else {" +
							"\n				turnDirection = -1;" +
							"\n			}" +
							"\n			turnRight(e.getBearing());" +
							"\n			// Determine a shot that won't kill the robot..." +
							"\n			// We want to ram him instead for bonus points" +
							"\n			if (e.getEnergy() > 16) {" +
							"\n				fire(3);" +
							"\n			} else if (e.getEnergy() > 10) {" +
							"\n				fire(2);" +
							"\n			} else if (e.getEnergy() > 4) {" +
							"\n				fire(1);" +
							"\n			} else if (e.getEnergy() > 2) {" +
							"\n				fire(.5);" +
							"\n			} else if (e.getEnergy() > .4) {" +
							"\n				fire(.1);" +
							"\n			}" +
							"\n			ahead(40); // Ram him again!" +
							"\n		} else if (" + chromo[3] + " == SPINBOT) {" +
							"\n			if (e.getBearing() > -10 && e.getBearing() < 10) {" +
							"\n				fire(3);" +
							"\n			}" +
							"\n			if (e.isMyFault()) {" +
							"\n				turnRight(10);" +
							"\n			}" +
							"\n		} else if (" + chromo[3] + " == TRACKER) {" +
							"\n			// Only print if he's not already our target." +
							"\n			if (trackName != null && !trackName.equals(e.getName())) {" +
							"\n				out.println(\"Tracking \" + e.getName() + \" due to collision\");" +
							"\n			}" +
							"\n			// Set the target" +
							"\n			trackName = e.getName();" +
							"\n			// Back up a bit." +
							"\n			// Note:  We won't get scan events while we're doing this!" +
							"\n			// An AdvancedRobot might use setBack(); execute();" +
							"\n			gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));" +
							"\n			turnGunRight(gunTurnAmt);" +
							"\n			fire(3);" +
							"\n			back(50);" +
							"\n		} else if (" + chromo[3] + " == WALLS) {" +
							"\n			// If he's in front of us, set back up a bit." +
							"\n			if (e.getBearing() > -90 && e.getBearing() < 90) {" +
							"\n				back(100);" +
							"\n			} // else he's in back of us, so set ahead a bit." +
							"\n			else {" +
							"\n				ahead(100);" +
							"\n			}" +
							"\n		}" +
							"\n	}" +
							"\n" +
							"\n	/**" +
							"\n	 * Feature 4" +
							"\n	 */" +
							"\n	public void onHitByBullet(HitByBulletEvent e) {" +
							"\n		if (" + chromo[4] + " == FIRE) {" +
							"\n			turnRight(normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading())));" +
							"\n			ahead(dist);" +
							"\n			dist *= -1;" +
							"\n			scan();" +
							"\n		}" +
							"\n	}" +
							"\n" +
							"\n	/*" +
							"\n	 * intern functions" +
							"\n	 */" +
							"\n" +
							"\n	/**" +
							"\n	 * To Corners" +
							"\n	 * <p>" +
							"\n	 * goCorner:  A very inefficient way to get to a corner.  Can you do better?" +
							"\n	 */" +
							"\n	public void goCorner() {" +
							"\n		// We don't want to stop when we're just turning..." +
							"\n		stopWhenSeeRobot = false;" +
							"\n		// turn to face the wall to the right of our desired corner." +
							"\n		turnRight(normalRelativeAngleDegrees(corner - getHeading()));" +
							"\n		// Ok, now we don't want to crash into any robot in our way..." +
							"\n		stopWhenSeeRobot = true;" +
							"\n		// Move to that wall" +
							"\n		ahead(5000);" +
							"\n		// Turn to face the corner" +
							"\n		turnLeft(90);" +
							"\n		// Move to the corner" +
							"\n		ahead(5000);" +
							"\n		// Turn gun to starting point" +
							"\n		turnGunLeft(90);" +
							"\n	}" +
							"\n" +
							"\n	/**" +
							"\n	 * To Corners" +
							"\n	 * <p>" +
							"\n	 * smartFire:  Custom fire method that determines firepower based on distance." +
							"\n	 *" +
							"\n	 * @param robotDistance the distance to the robot to fire at" +
							"\n	 */" +
							"\n	public void smartFire(double robotDistance) {" +
							"\n		if (robotDistance > 200 || getEnergy() < 15) {" +
							"\n			fire(1);" +
							"\n		} else if (robotDistance > 50) {" +
							"\n			fire(2);" +
							"\n		} else {" +
							"\n			fire(3);" +
							"\n		}" +
							"\n	}" +
							"\n" +
							"\n	/**" +
							"\n	 * To Crazy" +
							"\n	 * <p>" +
							"\n	 * reverseDirection:  Switch from ahead to back & vice versa" +
							"\n	 */" +
							"\n	public void reverseDirection() {" +
							"\n		if (movingForward) {" +
							"\n			setBack(40000);" +
							"\n			movingForward = false;" +
							"\n		} else {" +
							"\n			setAhead(40000);" +
							"\n			movingForward = true;" +
							"\n		}" +
							"\n	}");
			out.append("\n}");
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

}