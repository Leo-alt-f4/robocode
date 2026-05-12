import robocode.*;
import java.awt.Color;
/*
    Se acharem relevante alterar o nome, coloca aqui o 
    nome anterior para ver se tá melhor o de agr ou de antes
    apoio colocar como 'CavaloDeTroiaDaSilva'
*/
public class Trojan extends AdvancedRobot {
    int moveDirection = 1;

    public void run() {
        setBodyColor(Color.black);
        setGunColor(Color.white);
        setRadarColor(Color.red);
        setBulletColor(Color.yellow);

        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);

        while(true) {
            turnRadarRight(360); // sempre que ele nao ver um robo, ele vai ficar rodando até achar
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        /* 
            Com o robo na area do scanner, ele vai ficar rondando ao redor do robo fazendo movimento giratorio

            acho uma boa no futuro deixar mais randomizado isso, vai ser melhor
        */
        // Substitua as duas linhas do radar por esta:
        setTurnRadarRightRadians(robocode.util.Utils.normalRelativeAngle(getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians()) * 2);

        setTurnRight(e.getBearing() + 90 - (10 * moveDirection));

        setAhead(100 * moveDirection);

        /* 
            se a distancia baseada no raio de scan for menor que 2, ele atira o fodão, senão é o fraquinho
            tem que dar um jeito de melhorar o sistema de tiro com mais probabilidades
        */
        if (e.getDistance() < 200) {
            fire(3);
        } else {
            fire(1);
        }

        double gunTurn = getHeadingRadians() + e.getBearingRadians() - getGunHeadingRadians();
        setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(gunTurn));
    }

    /*
        tudo isso aq embaixo eh so pra caso do robo bater em algo ou algm, ent isso pode continuar assim (acho eu)
    */
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