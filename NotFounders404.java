import dev.robocode.tankroyale.botapi.*;
import dev.robocode.tankroyale.botapi.events.*;
import java.util.*;
import java.lang.Math;

public class BotFind {
  public double x, y;

  public int botId;
  public double moveDirection;
  public double moveSpeed;
  public double distance;
  public int turnNumber;
}

// ------------------------------------------------------------------
// NotFounders404
// ------------------------------------------------------------------
// ------------------------------------------------------------------
public class NotFounders404 extends Bot {
  public BotFind bot = null;

  // The main method starts our bot
  public static void main(String[] args) {
    new NotFounders404().start();
  }

  public double TargetBearing(BotFind find) {
    var bulletSpeed = 17; // assume fire(1)
    var botSpeed = find.moveSpeed;
    var turnNumber = getTurnNumber();

    var dirTo = directionTo(find.x, find.y);
    
    var botX = Math.cos(dirTo);
    var botY = Math.sin(dirTo);

    var rotX = -botY;
    var rotY = botX;

    var movX = Math.cos(find.moveDirection);
    var movY = Math.sin(find.moveDirection);

    var desiredBearing = Math.atan2(bulletSpeed, botSpeed);

    // Dot product check
    if(rotX * movX + rotY * movY > 0) {
      desiredBearing = -desiredBearing;
    }

    var bearing = calcGunBearing(directionTo(find.x, find.y));

    return desiredBearing - bearing;
  }

  // Called when a new round is started -> initialize and do some movement
  @Override
  public void run() {
    setRadarTurnRate(45);
    // Repeat while the bot is running
    while (isRunning()) {
      // Tell the game that when we take move, we'll also want to turn right... a lot
      setTurnRight(10_000);
      // Limit our speed to 5
      setTargetSpeed(5);

      if(bot != null) {
        var bearing = -TargetBearing(bot);
        System.out.println(bearing);
        if(Math.abs(bearing) > 20) {
          setGunTurnRate(Math.signum(bearing) * 20);
        } else {
          setGunTurnRate(bearing);
        }
        if(Math.abs(bearing) < 5) {
          fire(1);
          continue;
        }
      }

      go();
    }
  }

  @Override
  public void onScannedBot(ScannedBotEvent e) {
    var botFind = new BotFind();

    botFind.botId = e.getScannedBotId();
    botFind.x = e.getX();
    botFind.y = e.getY();
    botFind.moveDirection = e.getDirection();
    botFind.moveSpeed = e.getSpeed();
    
    botFind.distance = distanceTo(botFind.x, botFind.y);

    if(bot == null || bot.botId == botFind.botId || bot.distance > botFind.distance) {
      bot = botFind;
    }
  }


  // We were hit by a bullet -> turn perpendicular to the bullet
  @Override
  public void onHitBot(HitBotEvent e) {
    var direction = directionTo(e.getX(), e.getY());
    var bearing = calcBearing(direction);
    if (bearing > -10 && bearing < 10) {
      fire(3);
    }
    if (e.isRammed()) {
      turnRight(10);
    }
  }
}
