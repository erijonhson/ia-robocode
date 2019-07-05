package jeuj;
import robocode.*;
import java.awt.Color;


public class JeujDaPeu extends AdvancedRobot {

public void run() {
    setAdjustGunForRobotTurn(true);
    setColors(Color.red,Color.blue,Color.green);

    while (true) {
        turnGunRight(105.78550453238566);
        setAhead(167.52143521046204);
    }
}
public void onScannedRobot(ScannedRobotEvent e) {
    setAhead(202.80237353777008);

    setTurnRight(298.0274070774534);

    setTurnGunRight(309.8882464663611);

    setTurnRadarRight(571.5351219546104);

    if (e.getDistance() < 37.000179215551576) {
        setFire(6.977210871651424);
    } else {
        setFire(14.94625411762183);
    }
}
public void onHitByBullet(HitByBulletEvent e) {
    setTurnRight(159.46392821954095);
    setAhead(473.29859136022026 * -1);
}
public void onHitWall(HitWallEvent e) {
    back(130.87878933778694);
    setAhead(499.99939134560094 * -1);
}
}