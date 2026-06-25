import robocode.*;
import java.awt.Color;

public class Trojan extends AdvancedRobot {
    int moveDirection = 1;
    String trackName = null;

    public void run() {
        setBodyColor(Color.black);
        setGunColor(Color.white);
        setRadarColor(Color.red);
        setBulletColor(Color.black);

        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);

        // sempre que ele nao ver um robo, ele vai ficar rodando ate achar (gira infinito para batalhas melee)
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

        while(true) {
            // inverte a direcao aleatoriamente para que o movimento nao seja muito previsivel
            if (Math.random() > 0.99) {
                reverseDirection();
            }
            
            // garante que o radar nunca pare de rodar procurando inimigos
            if (getRadarTurnRemaining() == 0.0) {
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            }
            
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // funcao de prioridade: foca sempre no mesmo robo, mas muda se alguem chegar muito perto
        if (trackName == null || e.getName().equals(trackName) || e.getDistance() < 150) {
            trackName = e.getName();

            double distance = e.getDistance();
            double absoluteBearing = getHeadingRadians() + e.getBearingRadians();

            // com o robo na area do scanner, ele vai ficar rondando ao redor do robo fazendo movimento giratorio
            setTurnRight(e.getBearing() + 90 - (10 * moveDirection));
            setAhead(1000 * moveDirection);

            // sistema de tiro melhorado com condicionais
            double firePower;
            if (distance > 600) {
                firePower = 1.0;
            } else if (distance > 400) {
                firePower = 2.0;
            } else if (distance > 200) {
                firePower = 2.5;
            } else {
                firePower = 3.0;
            }

            // condicao de sobrevivencia para poupar energia
            if (getEnergy() < 15) {
                firePower = 0.1;
            }

            // calcula o movimento do inimigo para atirar onde ele vai estar (mira preditiva)
            double bulletSpeed = 20 - (3 * firePower);
            double lateralVelocity = e.getVelocity() * Math.sin(e.getHeadingRadians() - absoluteBearing);
            double escapeAngle = Math.asin(lateralVelocity / bulletSpeed);

            double gunTurn = absoluteBearing + escapeAngle - getGunHeadingRadians();
            setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(gunTurn));

            // verifica se ta com um angulo bom para acertar o inimigo antes de atirar
            if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
                setFire(firePower);
            }
        }
    }

    // reseta o rastreamento para o proximo ciclo caso o inimigo focado morra
    public void onRobotDeath(RobotDeathEvent e) {
        if (e.getName().equals(trackName)) {
            trackName = null;
        }
    }

    // se bater na parede, vira para o outro lado e se afasta
    public void onHitWall(HitWallEvent e) {
        reverseDirection();
    }

    // se bater em outro robo tenta girar para o lado oposto ao dele
    public void onHitRobot(HitRobotEvent e) {
        reverseDirection();
        if (e.getBearing() > -90 && e.getBearing() <= 90) {
            setFire(3.0);
        }
    }

    public void reverseDirection() {
        moveDirection *= -1;
    }
}
