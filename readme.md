## O Trojan (Cavalo de Tróia da Silva)

Esta documentação detalha a arquitetura, o funcionamento lógico e o histórico de desenvolvimento do robô **Trojan**, desenvolvido para a plataforma Robocode utilizando a classe `AdvancedRobot`.

---

#### Sobre o robô "vindo dos infernos" - Trojan

Bem, o propósito base do Trojan era ser um **tracker**. O objetivo da sua estratégia de combate consiste em localizar um alvo, travar o radar nele e orbitar ao seu redor para atirar e girar.

Dessa forma, o Trojan consegue desviar de tiros inimigos movendo-se de forma perpendicular, enquanto mantém a sua arma constantemente apontada para o oponente, disparando rajadas com potências calculadas dinamicamente com base na distância.

---

#### Como o se comporta em combate

Se você colocar o Trojan em uma batalha no Robocode, eis o que verá na tela:

* **Visual:** É um robô com a base e os tiros totalmente pretos, um canhão branco e um radar vermelho piscante. 
* **Modo de Busca:** Assim que a partida começa, a base fica parado, mas o radar vermelho gira incessantemente em 360 graus à procura de uma vítima.
* **O Engajamento (Trava de Mira):** Quando o feixe do radar detecta um inimigo, o Trojan "trava". O radar para de girar livremente e passa a tremer rapidamente apenas em cima daquele inimigo específico.
* **A Dança da Morte (Movimento):** Imediatamente, a base do Trojan vira de lado para o inimigo e começa a andar para a frente, criando um círculo imperfeito (uma espiral) ao redor do alvo. Se o inimigo tentar fugir, o Trojan o acompanha de lado.
* **Disparos:** Atirará projéteis pequenos e fracos se o alvo estiver muito longe, mas conforme a espiral do Trojan o aproxima do inimigo, os tiros ficam visivelmente maiores e mais devastadores.
* **Reação a Obstáculos:** Se o Trojan bater na parede da arena enquanto faz o seu círculo, ele dá um "solavanco", vira 90 graus para o meio da arena, engata a marcha ré e continua a atirar de onde parou.

---

#### O Código (Como funciona)

O Trojan se beneficia da classe `AdvancedRobot`, o que significa que todas as suas ações (mover a base, girar canhão e girar radar) são processadas de forma independente. 

Aqui estão as partes mais vitais do código que fazem a estratégia funcionar:

###### Independência Mecânica e Busca
No método `run()`, o robô desacopla as suas partes mecânicas. Isto é vital para que o robô possa andar em círculos sem que o canhão saia da mira do inimigo.
```java
setAdjustRadarForRobotTurn(true);
setAdjustGunForRobotTurn(true);

// Sempre que ele não vir um robô, vai ficar rodando até achar
while(true) {
    turnRadarRight(360); 
}
```

###### O Movimento Orbital

Dentro do método `onScannedRobot`, o Trojan calcula como se manter perpendicular (de lado) ao inimigo:

```java
// Com o robô na área do scanner, ele vai ficar rondando ao redor do robô fazendo movimento giratório
setTurnRight(e.getBearing() + 90 - (10 * moveDirection));
setAhead(100 * moveDirection);
```

**Por que o `- (10 * moveDirection)`?** Se ele ficasse exatamente a 90 graus, faria um círculo perfeito e nunca se aproximaria. Esse desconto de 10 graus faz o Trojan andar ligeiramente na diagonal para a frente, criando uma **espiral** que encurrala o inimigo gradativamente.

###### Economia Dinâmica de Energia

Atirar custa a própria vida (energia) do robô. O Trojan usa condicionais (sistema de tiro melhorado) para não desperdiçar vida atirando forte em alvos muito distantes:

```java
if (distance > 600) {
    firePower = 1.0; // Longe: Tiro fraco para poupar energia
} else if (distance > 400) {
    firePower = 2.0; // Média distância: Força moderada
} else if (distance > 200) {
    firePower = 2.5; // Perto: Tiro perigoso
} else {
    firePower = 3.0; // Muito perto: Força máxima
}
```

###### O Gatilho de Precisão

O Trojan não atira às cegas. Ele verifica se está com um ângulo bom para acertar no inimigo antes de atirar:

```java
double gunTurn = getHeadingRadians() + e.getBearingRadians() - getGunHeadingRadians();
setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(gunTurn));

// Se o canhão estiver quase perfeitamente alinhado com o alvo, ele atira
if (Math.abs(getGunTurnRemaining()) < 10) {
    setFire(firePower);
}
```

###### Reflexos de Sobrevivência (Colisões)

Se o robô colidir na parede, ele executa uma manobra de fuga instintiva virando para o outro lado e afastando-se:

```java
public void onHitWall(HitWallEvent e) {
    reverseDirection(); // Inverte a direção
    setTurnRight(90);   // Gira a base
    setAhead(150);      // Afasta-se da parede
}
```

O mesmo acontece se bater em outro robô: ele tenta girar para o lado oposto ao dele para não ficar preso.

---

#### Instruções de Instalação e Execução

Para colocar este robô no sistema e testá-lo no seu ambiente local do Robocode, siga os passos abaixo:

1. Abra o **Robocode**.
2. No menu superior, vá ao editor de robôs (**Robot Editor**).
3. Abra um novo arquivo de robô e cole o código contido no seu arquivo `Trojan.java`.
4. Após isso, **'compile'** o arquivo (no menu *Compiler* -> *Compile*) e salve-o em uma pasta do Robocode.
5. Este robô em si tem os comentários de como funciona dentro dele e não precisa de muitas explicações para rodar.

---
