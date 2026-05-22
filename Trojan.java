import robocode.*;
import java.awt.Color;
import sample.*;

public class Trojan extends AdvancedRobot {
    int moveDirection = 1;
    double targetDistance = Double.MAX_VALUE;

    public void run() {
        setBodyColor(Color.black);
        setGunColor(Color.white);
        setRadarColor(Color.red);
        setBulletColor(Color.black);

        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);

        while(true) {
            turnRadarRight(360);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double distance = e.getDistance();

        if (distance > targetDistance) {
            return;
        }
        
        targetDistance = distance;
        double firePower = 3.0;

        setTurnRadarRightRadians(robocode.util.Utils.normalRelativeAngle(getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians()) * 2);
        setTurnRight(e.getBearing() + 90 - (10 * moveDirection));
        setAhead(100 * moveDirection);

        if (distance > 600) {
            firePower = 1.0;
        } else if (distance > 400) {
            firePower = 2.0;
        } else if (distance > 200) {
            firePower = 2.5;
        } else {
            firePower = 3.0;
        }
        
        double gunTurn = getHeadingRadians() + e.getBearingRadians() - getGunHeadingRadians();
        setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(gunTurn));

        if (Math.abs(getGunTurnRemaining()) < 10) {
            setFire(firePower);
        }

        targetDistance = Double.MAX_VALUE;
    }

    public void onHitWall(HitWallEvent e) {
        reverseDirection();
        setTurnRight(90); 
        setAhead(150);
    }

    public void onHitRobot(HitRobotEvent e) {
        if (e.getBearing() > -90 && e.getBearing() <= 90) {
            reverseDirection();
        }
        setAhead(100);
    }

    public void reverseDirection() {
        moveDirection *= -1;
    }
}
