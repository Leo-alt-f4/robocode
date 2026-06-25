import robocode.*;
import java.awt.Color;
import sample.*;

public class Trojan extends AdvancedRobot {
    int moveDirection = 1;
    double targetDistance = Double.MAX_VALUE;
    String enemyName = null;

    public void run() {
        setBodyColor(Color.black);
        setGunColor(Color.white);
        setRadarColor(Color.red);
        setBulletColor(Color.black);

        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        setMaxVelocity(8);

        // mantem o radar rodando continuamente pra uma busca melhor
        while(true) {
            setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double distance = e.getDistance();
        String scannedName = e.getName();

        // se já tenho um alvo, ignora outros robôs exceto o alvo atual
        if (enemyName != null && !enemyName.equals(scannedName)) {
            return;
        }

        // função de prioridade: se o robo escaneado for mais distante que o atual, ignora ele
        if (distance > targetDistance) {
            return;
        }
        
        enemyName = scannedName;
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

        // reduz potência de tiro quando a energia está baixa
        if (getEnergy() < 15) {
            firePower = Math.min(firePower, 1.5);
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

    // quando for atingido por um tiro, muda de direção para evitar novo impacto
    public void onHitByBullet(HitByBulletEvent e) {
        reverseDirection();
        setAhead(150 * moveDirection);
    }

    // quando um robo morre, reseta a distancia de alvo para poder escolher o proximo mais proximo
    public void onRobotDeath(RobotDeathEvent e) {
        targetDistance = Double.MAX_VALUE;
    }

    public void reverseDirection() {
        moveDirection *= -1;
    }
}
