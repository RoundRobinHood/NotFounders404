import dev.robocode.tankroyale.botapi.*;
import dev.robocode.tankroyale.botapi.events.*;

// ------------------------------------------------------------------
// NotFounders404
// ------------------------------------------------------------------
// A sample bot original made for Robocode by Mathew Nelson.
//
// Probably the first bot you will learn about.
// Moves in a seesaw motion and spins the gun around at each end.
// ------------------------------------------------------------------
public class NotFounders404 extends Bot {

    // The main method starts our bot
    public static void main(String[] args) {
        new NotFounders404().start();
    }

    // Called when a new round is started -> initialize and do some movement
    @Override
    public void run() {
        // Repeat while the bot is running
        while (isRunning()) {
            // Tell the game that when we take move, we'll also want to turn right... a lot
            setTurnRight(10_000);
            // Limit our speed to 5
            setMaxSpeed(5);
            // Start moving (and turning)
            forward(10_000);
        }
    }

    // We saw another bot -> fire!
    @Override
    public void onScannedBot(ScannedBotEvent e) {
        fire(3);
    }

    // We were hit by a bullet -> turn perpendicular to the bullet
    @Override
    public void onHitByBullet(HitByBulletEvent e) {
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
