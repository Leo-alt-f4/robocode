import robocode.*;
import java.awt.Color;

public class Arroz extends AdvancedRobot {
    int moveDirection = 1;

    public void run() {
        setBodyColor(Color.black);
        setGunColor(Color.white);
        setRadarColor(Color.red);
        setBulletColor(Color.yellow);

        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);

        while(true) {
            turnRadarRight(360);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
        setTurnRadarRightRadians(robocode.util.Utils.normalRelativeAngle(radarTurn));

        setTurnRight(e.getBearing() + 90 - (10 * moveDirection));

        setAhead(100 * moveDirection);

        if (e.getDistance() < 200) {
            fire(3);
        } else {
            fire(1);
        }

        double gunTurn = getHeadingRadians() + e.getBearingRadians() - getGunHeadingRadians();
        setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(gunTurn));
    }

    public void onHitWall(HitWallEvent e) {
        reverseDirection();
    }

    public void onHitRobot(HitRobotEvent e) {
        reverseDirection();
    }

    public void reverseDirection() {
        moveDirection *= -1;
    }
}