package ia;

import robocode.*;

import java.awt.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class ModelWithAllSamples extends AdvancedRobot {
	final static int CORNERS = 1;
	final static int CRAZY = 2;
	final static int FIRE = 3;
	final static int RAMFIRE = 4;
	final static int SPINBOT = 5;
	final static int TRACKER = 6;
	final static int TRACKERFIRE = 7;
	final static int WALLS = 8;

	int others; // Number of other robots in the game
	static int corner = 0; // Which corner we are currently using
	// static so that it keeps it between rounds.
	boolean stopWhenSeeRobot = false; // See goCorner()
	boolean movingForward;

	int dist = 50; // distance to move when we're hit

	int turnDirection = 1; // Clockwise or counterclockwise

	int count = 0; // Keeps track of how long we've
	// been searching for our target
	double gunTurnAmt; // How much to turn our gun when searching
	String trackName; // Name of the robot we're currently tracking

	boolean peek; // Don't turn if there's a robot there
	double moveAmount; // How much to move

	/**
	 * Feature 0
	 */
	public void run() {
		if (2 == CORNERS) {
			setBodyColor(Color.red);
			setGunColor(Color.black);
			setRadarColor(Color.yellow);
			setBulletColor(Color.green);
			setScanColor(Color.green);
			others = getOthers(); // Save # of other bots
			goCorner(); // Move to a corner
		} else if (2 == CRAZY) { // to corners
			setBodyColor(new Color(0, 200, 0));
			setGunColor(new Color(0, 150, 50));
			setRadarColor(new Color(0, 100, 100));
			setBulletColor(new Color(255, 255, 100));
			setScanColor(new Color(255, 200, 200));
		} else if (2 == FIRE) {
			setBodyColor(Color.orange);
			setGunColor(Color.orange);
			setRadarColor(Color.red);
			setScanColor(Color.red);
			setBulletColor(Color.red);
		} else if (2 == RAMFIRE) {
			setBodyColor(Color.lightGray);
			setGunColor(Color.gray);
			setRadarColor(Color.darkGray);
		} else if (2 == SPINBOT) {
			setBodyColor(Color.blue);
			setGunColor(Color.blue);
			setRadarColor(Color.black);
			setScanColor(Color.yellow);
		} else if (2 == TRACKER) {
			setBodyColor(new Color(128, 128, 50));
			setGunColor(new Color(50, 50, 20));
			setRadarColor(new Color(200, 200, 70));
			setScanColor(Color.white);
			setBulletColor(Color.blue);
			// Prepare gun
			trackName = null; // Initialize to not tracking anyone
			setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
			gunTurnAmt = 10; // Initialize gunTurn to 10
		} else if (2 == TRACKERFIRE) {
			setBodyColor(Color.pink);
			setGunColor(Color.pink);
			setRadarColor(Color.pink);
			setScanColor(Color.pink);
			setBulletColor(Color.pink);
		} else if (2 == WALLS) {
			setBodyColor(Color.black);
			setGunColor(Color.black);
			setRadarColor(Color.orange);
			setBulletColor(Color.cyan);
			setScanColor(Color.cyan);
			// Initialize moveAmount to the maximum possible for this battlefield.
			moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
			// Initialize peek to false
			peek = false;
			// turnLeft to face a wall.
			// getHeading() % 90 means the remainder of
			// getHeading() divided by 90.
			turnLeft(getHeading() % 90);
			ahead(moveAmount);
			// Turn the gun to turn right 90 degrees.
			peek = true;
			turnGunRight(90);
			turnRight(90);
		}

		// Initialize gun turn speed to 3 -- to Corners
		int gunIncrement = 3;

		while (true) {
			if (2 == CORNERS) {
				for (int i = 0; i < 30; i++) {
					turnGunLeft (gunIncrement);
				}
				gunIncrement *= -1;
			} else if (2 == CRAZY) {
				// Tell the game we will want to move ahead 40000 -- some large number
				setAhead(40000);
				movingForward = true;
				// Tell the game we will want to turn right 90
				setTurnRight(90);
				// At this point, we have indicated to the game that *when we do something*,
				// we will want to move ahead and turn right.  That's what set means.
				// It is important to realize we have not done anything yet!
				// In order to actually move, we'll want to call a method that
				// takes real time, such as waitFor.
				// waitFor actually starts the action -- we start moving and turning.
				// It will not return until we have finished turning.
				waitFor(new TurnCompleteCondition(this));
				// Note:  We are still moving ahead now, but the turn is complete.
				// Now we'll turn the other way...
				setTurnLeft(180);
				// ... and wait for the turn to finish ...
				waitFor(new TurnCompleteCondition(this));
				// ... then the other way ...
				setTurnRight(180);
				// .. and wait for that turn to finish.
				waitFor(new TurnCompleteCondition(this));
				// then back to the top to do it all again
			} else if (2 == FIRE) {
				turnGunRight(5);
			} else if (2 == RAMFIRE) {
				turnRight(5 * turnDirection);
			} else if (2 == SPINBOT) {
				// Tell the game that when we take move,
				// we'll also want to turn right... a lot.
				setTurnRight(10000);
				// Limit our speed to 5
				setMaxVelocity(5);
				// Start moving (and turning)
				ahead(10000);
				// Repeat.
			} else if (2 == TRACKER) {
				// turn the Gun (looks for enemy)
				turnGunRight(gunTurnAmt);
				// Keep track of how long we've been looking
				count++;
				// If we've haven't seen our target for 2 turns, look left
				if (count > 2) {
					gunTurnAmt = -10;
				}
				// If we still haven't seen our target for 5 turns, look right
				if (count > 5) {
					gunTurnAmt = 10;
				}
				// If we *still* haven't seen our target after 10 turns, find another target
				if (count > 11) {
					trackName = null;
				}
			} else if (2 == TRACKERFIRE) {
				turnGunRight(10); // Scans automatically
			} else if (2 == WALLS) {
				// Look before we turn when ahead() completes.
				peek = true;
				// Move up the wall
				ahead(moveAmount);
				// Don't look now
				peek = false;
				// Turn to the next wall
				turnRight(90);
			}
		}

	}

	/**
	 * Feature 1
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		if (4 == CORNERS) {
			// Should we stop, or just fire?
			if (stopWhenSeeRobot) {
				// Stop everything!  You can safely call stop multiple times.
				stop();
				// Call our custom firing method
				smartFire(e.getDistance());
				// Look for another robot.
				// NOTE:  If you call scan() inside onScannedRobot, and it sees a robot,
				// the game will interrupt the event handler and start it over
				scan();
				// We won't get here if we saw another robot.
				// Okay, we didn't see another robot... start moving or turning again.
				resume();
			} else {
				smartFire(e.getDistance());
			}
		} else if (4 == CRAZY) {
			fire(1);
		} else if (4 == FIRE) {
			// If the other robot is close by, and we have plenty of life,
			// fire hard!
			if (e.getDistance() < 50 && getEnergy() > 50) {
				fire(3);
			} // otherwise, fire 1.
			else {
				fire(1);
			}
			// Call scan again, before we turn the gun
			scan();
		} else if (4 == RAMFIRE) {
			if (e.getBearing() >= 0) {
				turnDirection = 1;
			} else {
				turnDirection = -1;
			}
			turnRight(e.getBearing());
			ahead(e.getDistance() + 5);
			scan(); // Might want to move ahead again!
		} else if (4 == SPINBOT) {
			fire(3);
		} else if (4 == TRACKER) {
			// If we have a target, and this isn't it, return immediately
			// so we can get more ScannedRobotEvents.
			if (trackName != null && !e.getName().equals(trackName)) {
				return;
			}
			// If we don't have a target, well, now we do!
			if (trackName == null) {
				trackName = e.getName();
				out.println("Tracking " + trackName);
			}
			// This is our target.  Reset count (see the run method)
			count = 0;
			// If our target is too far away, turn and move toward it.
			if (e.getDistance() > 150) {
				gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
				turnGunRight(gunTurnAmt); // Try changing these to setTurnGunRight,
				turnRight(e.getBearing()); // and see how much Tracker improves...
				// (you'll have to make Tracker an AdvancedRobot)
				ahead(e.getDistance() - 140);
				return;
			}
			// Our target is close.
			gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
			turnGunRight(gunTurnAmt);
			fire(3);
			// Our target is too close!  Back up.
			if (e.getDistance() < 100) {
				if (e.getBearing() > -90 && e.getBearing() <= 90) {
					back(40);
				} else {
					ahead(40);
				}
			}
			scan();
		} else if (4 == TRACKERFIRE) {
			// Calculate exact location of the robot
			double absoluteBearing = getHeading() + e.getBearing();
			double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
			// If it's close enough, fire!
			if (Math.abs(bearingFromGun) <= 3) {
				turnGunRight(bearingFromGun);
				// We check gun heat here, because calling fire()
				// uses a turn, which could cause us to lose track
				// of the other robot.
				if (getGunHeat() == 0) {
					fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
				}
			} // otherwise just set the gun to turn.
			// Note:  This will have no effect until we call scan()
			else {
				turnGunRight(bearingFromGun);
			}
			// Generates another scan event if we see a robot.
			// We only need to call this if the gun (and therefore radar)
			// are not turning.  Otherwise, scan is called automatically.
			if (bearingFromGun == 0) {
				scan();
			}
		} else if (4 == WALLS) {
			fire(2);
			// Note that scan is called automatically when the robot is moving.
			// By calling it manually here, we make sure we generate another scan event if there's a robot on the next
			// wall, so that we do not start moving up it until it's gone.
			if (peek) {
				scan();
			}
		}
	}

	/**
	 * Feature 2
	 */
	public void onHitWall(HitWallEvent e) {
		if (1 == CRAZY) {
			// Bounce off!
			reverseDirection();
		}
	}

	/**
	 * Feature 3
	 */
	public void onHitRobot(HitRobotEvent e) {
		if (7 == CRAZY) {
			// If we're moving the other robot, reverse!
			if (e.isMyFault()) {
				reverseDirection();
			}
		} else if (7 == FIRE) {
			double turnGunAmt = normalRelativeAngleDegrees(e.getBearing() + getHeading() - getGunHeading());
			turnGunRight(turnGunAmt);
			fire(3);
		} else if (7 == RAMFIRE) {
			if (e.getBearing() >= 0) {
				turnDirection = 1;
			} else {
				turnDirection = -1;
			}
			turnRight(e.getBearing());
			// Determine a shot that won't kill the robot...
			// We want to ram him instead for bonus points
			if (e.getEnergy() > 16) {
				fire(3);
			} else if (e.getEnergy() > 10) {
				fire(2);
			} else if (e.getEnergy() > 4) {
				fire(1);
			} else if (e.getEnergy() > 2) {
				fire(.5);
			} else if (e.getEnergy() > .4) {
				fire(.1);
			}
			ahead(40); // Ram him again!
		} else if (7 == SPINBOT) {
			if (e.getBearing() > -10 && e.getBearing() < 10) {
				fire(3);
			}
			if (e.isMyFault()) {
				turnRight(10);
			}
		} else if (7 == TRACKER) {
			// Only print if he's not already our target.
			if (trackName != null && !trackName.equals(e.getName())) {
				out.println("Tracking " + e.getName() + " due to collision");
			}
			// Set the target
			trackName = e.getName();
			// Back up a bit.
			// Note:  We won't get scan events while we're doing this!
			// An AdvancedRobot might use setBack(); execute();
			gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
			turnGunRight(gunTurnAmt);
			fire(3);
			back(50);
		} else if (7 == WALLS) {
			// If he's in front of us, set back up a bit.
			if (e.getBearing() > -90 && e.getBearing() < 90) {
				back(100);
			} // else he's in back of us, so set ahead a bit.
			else {
				ahead(100);
			}
		}
	}

	/**
	 * Feature 4
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		if (1 == FIRE) {
			turnRight(normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading())));
			ahead(dist);
			dist *= -1;
			scan();
		}
	}

	/*
	 * intern functions
	 */

	/**
	 * To Corners
	 * <p>
	 * goCorner:  A very inefficient way to get to a corner.  Can you do better?
	 */
	public void goCorner() {
		// We don't want to stop when we're just turning...
		stopWhenSeeRobot = false;
		// turn to face the wall to the right of our desired corner.
		turnRight(normalRelativeAngleDegrees(corner - getHeading()));
		// Ok, now we don't want to crash into any robot in our way...
		stopWhenSeeRobot = true;
		// Move to that wall
		ahead(5000);
		// Turn to face the corner
		turnLeft(90);
		// Move to the corner
		ahead(5000);
		// Turn gun to starting point
		turnGunLeft(90);
	}

	/**
	 * To Corners
	 * <p>
	 * smartFire:  Custom fire method that determines firepower based on distance.
	 *
	 * @param robotDistance the distance to the robot to fire at
	 */
	public void smartFire(double robotDistance) {
		if (robotDistance > 200 || getEnergy() < 15) {
			fire(1);
		} else if (robotDistance > 50) {
			fire(2);
		} else {
			fire(3);
		}
	}

	/**
	 * To Crazy
	 * <p>
	 * reverseDirection:  Switch from ahead to back & vice versa
	 */
	public void reverseDirection() {
		if (movingForward) {
			setBack(40000);
			movingForward = false;
		} else {
			setAhead(40000);
			movingForward = true;
		}
	}
}