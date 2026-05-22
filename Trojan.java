import robocode.*;
import java.awt.Color;
import sample.*;
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
        
		double distance = e.getDistance();
		double firePower = 3.0;
		 /* 
            Com o robo na area do scanner, ele vai ficar rondando ao redor do robo fazendo movimento giratorio

            acho uma boa no futuro deixar mais randomizado isso, vai ser melhor
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
