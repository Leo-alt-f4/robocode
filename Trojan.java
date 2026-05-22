import robocode.*;
import java.awt.Color;
import sample.*;

public class Trojan extends AdvancedRobot {
    int moveDirection = 1;

    public void run() {
        setBodyColor(Color.black);
        setGunColor(Color.white);
        setRadarColor(Color.red);
        setBulletColor(Color.black);

        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);

        while(true) {
            turnRadarRight(360); // sempre que ele nao ver um robo, ele vai ficar rodando até achar
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        
		double distance = e.getDistance();
		double firePower = 3.0;
		 /* 
            Com o robo na area do scanner, ele vai ficar rondando ao redor do robo fazendo movimento giratorio
            Se der, algm ajusta aí pfv
        */
        setTurnRadarRightRadians(robocode.util.Utils.normalRelativeAngle(getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians()) * 2);
        setTurnRight(e.getBearing() + 90 - (10 * moveDirection));
        setAhead(100 * moveDirection);

        // sistema de tiro melhorado e agora ele tem mais condicionais (tb criei umas variáveis p ficar mais legível)
		if (distance > 600) {
		    firePower = 1.0;
		} else if (distance > 400) {
		    firePower = 2.0;
		} else if (distance > 200) {
		    firePower = 2.5;
		} else {
		    firePower = 3.0;
		}
		
        // melhorei um pouco o sistema de tiro, agora ele tá visualizando se tá com um ângulo bom p acertar o inimigo
		if (Math.abs(getGunTurnRemaining()) < 10) {
		    setFire(firePower);
		}

        double gunTurn = getHeadingRadians() + e.getBearingRadians() - getGunHeadingRadians();
        setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(gunTurn));
    }

    /*
        ajustei, se der tempo, vou tentar fazer o robo se direcionar ate o inimigo (ou se algm puder fazer a boa)
    */
    // se bater na barede, dira para o outro lado e se afasta
    public void onHitWall(HitWallEvent e) {
        reverseDirection();
        setTurnRight(90); 
        setAhead(150);
    }

    // se bater em outro robo tenta gira para o lado oposto ao dele
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
