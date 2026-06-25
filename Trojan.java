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

        // mantem o radar rodando continuamente pra uma busca melhor
        while(true) {
            setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double distance = e.getDistance();

        // funcao de prioridade: Se o robo escaneado for mais distante que o atual, ignora ele
        if (distance > targetDistance) {
            return;
        }
        
        targetDistance = distance;
        double firePower = 3.0;

        // com o robo na area do scanner, ele vai ficar rondando ao redor do robo fazendo movimento giratorio
        setTurnRadarRightRadians(robocode.util.Utils.normalRelativeAngle(getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians()) * 2);
        setTurnRight(e.getBearing() + 90 - (10 * moveDirection));
        setAhead(100 * moveDirection);

        // sistema de tiro melhorado com condicionais
        if (distance > 600) {
            firePower = 1.0;
        } else if (distance > 400) {
            firePower = 2.0;
        } else if (distance > 200) {
            firePower = 2.5;
        } else {
            firePower = 3.0;
        }
        
        // verifica se ta com um angulo bom para acertar o inimigo antes de atirar
        double gunTurn = getHeadingRadians() + e.getBearingRadians() - getGunHeadingRadians();
        setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(gunTurn));

        if (Math.abs(getGunTurnRemaining()) < 10) {
            setFire(firePower);
        }

        // reseta o rastreamento para o proximo ciclo
        targetDistance = Double.MAX_VALUE;
    }

    // se bater na parede, vira para o outro lado e se afasta
    public void onHitWall(HitWallEvent e) {
        reverseDirection();
        setTurnRight(90); 
        setAhead(150);
    }

    // se bater em outro robo tenta girar para o lado oposto ao dele
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
