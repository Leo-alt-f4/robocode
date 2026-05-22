import robocode.*;
import java.awt.Color;
import sample.*;

public class Trojan extends AdvancedRobot {
    int moveDirection = 1;
    double targetDistance = Double.MAX_VALUE; // Guarda a distância do alvo atual

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

        // FUNÇÃO DE PRIORIDADE: Se o robô escaneado for mais distante que o atual, ignora ele
        if (distance > targetDistance) {
            return; 
        }
        
        // Se chegou aqui, este é o robô mais próximo atualizado
        targetDistance = distance;
        double firePower = 3.0;

        // Movimento circular ao redor do inimigo
        setTurnRadarRightRadians(robocode.util.Utils.normalRelativeAngle(getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians()) * 2);
        setTurnRight(e.getBearing() + 90 - (10 * moveDirection));
        setAhead(100 * moveDirection);

        // Sistema de tiro por distância
        if (distance > 600) {
            firePower = 1.0;
        } else if (distance > 400) {
            firePower = 2.0;
        } else if (distance > 200) {
            firePower = 2.5;
        } else {
            firePower = 3.0;
        }
        
        // Calcula o giro da arma ANTES de tentar atirar
        double gunTurn = getHeadingRadians() + e.getBearingRadians() - getGunHeadingRadians();
        setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(gunTurn));

        // Só atira se a arma estiver quase apontada para o alvo
        if (Math.abs(getGunTurnRemaining()) < 10) {
            setFire(firePower);
        }

        // Reseta o rastreamento para o próximo ciclo de escaneamento do radar
        targetDistance = Double.MAX_VALUE;
    }

    // Se bater na parede, apenas inverte a marcha para não ficar travado
    public void onHitWall(HitWallEvent e) {
        reverseDirection();
    }

    // se bater em outro robo tenta gira para o lado oposto ao dele
    public void onHitRobot(HitRobotEvent e) {
        if (e.getBearing() > -90 && e.getBearing() <= 90) {
            reverseDirection();
        }
    }

    public void reverseDirection() {
        moveDirection *= -1;
    }
}
