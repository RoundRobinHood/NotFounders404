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

    var turns = 1.0 * getTurnNumber() - find.turnNumber;

    var new_x = find.x + Math.cos(find.moveDirection) * find.moveSpeed * turns;
    var new_y = find.y + Math.cos(find.moveDirection) * find.moveSpeed * turns;

    var dist = distanceTo(new_x, new_y);

    turns = Math.floor(dist / 17);

    new_x = new_x + Math.cos(find.moveDirection) * find.moveSpeed * turns;
    new_y = new_y + Math.sin(find.moveDirection) * find.moveSpeed * turns;

    var bearing = calcGunBearing(directionTo(new_x, new_y));

    return bearing;
  }

  // Called when a new round is started -> initialize and do some movement
  @Override
  public void run() {
    setRadarTurnRate(45);
    setTurnRate(10);
    // Repeat while the bot is running
    while (isRunning()) {
      // Limit our speed to 5
      setTargetSpeed(5);

      if(bot != null) {
        var bearing = TargetBearing(bot); 
        System.out.println(bearing);
        setGunTurnRate(bearing - 10);
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
    botFind.turnNumber = getTurnNumber();
    
    botFind.distance = distanceTo(botFind.x, botFind.y);

    if(bot == null || bot.botId == botFind.botId || bot.distance > botFind.distance) {
      System.out.printf("Scanned: %d\n", botFind.turnNumber);
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
