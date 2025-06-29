import greenfoot.*;
import java.util.*;

/**
 * Aangepaste versie van MyDodo.
 *
 */
public class MyDodo extends Dodo {

    private int stepCounter = 0;
    private int totalScore = 0;

    public MyDodo() {
        super(5); // snelheid
    }

    public boolean canLayEggHere() {
        return onNest() && !onEgg();
    }

    public void rotate180() {
        setDirection((getDirection() + 180) % 360);
    }

    public void doubleStep() {
        move(1);
        move(1);
    }

    public boolean pathFree() {
        return !fenceAhead();
    }

    public void climbFence() {
        turnLeft(); move(1);
        turnRight(); move(1);
        turnRight(); move(1);
        turnLeft();
    }

    public void moveUntilBlocked() {
        while (pathFree()) {
            move(1);
        }
    }

    public void moveTo(int targetX, int targetY) {
        while (getX() != targetX || getY() != targetY) {
            if (getX() < targetX) setDirection(EAST);
            else if (getX() > targetX) setDirection(WEST);
            else if (getY() < targetY) setDirection(SOUTH);
            else setDirection(NORTH);
            move(1);
        }
    }

    public int countEggsInCurrentRow() {
        int eggsFound = 0;
        int originalX = getX();
        while (pathFree()) {
            if (onEgg()) eggsFound++;
            move(1);
        }
        if (onEgg()) eggsFound++;
        moveTo(originalX, getY());
        return eggsFound;
    }

    public int countEggsInColumn(int colX) {
        int total = 0;
        for (int y = 0; y < getWorld().getHeight(); y++) {
            moveTo(colX, y);
            if (onEgg()) total++;
        }
        return total;
    }

    public void dropEggTrail(int count) {
        for (int i = 0; i < count && pathFree(); i++) {
            layEgg();
            move(1);
        }
    }

    public void showAverageEggsPerRow() {
        int allEggs = 0;
        for (int y = 0; y < getWorld().getHeight(); y++) {
            moveTo(0, y);
            allEggs += countEggsInCurrentRow();
        }
        double average = (double) allEggs / getWorld().getHeight();
        System.out.println("Gemiddeld per rij: " + average);
    }

    public int findOddRow() {
        for (int y = 0; y < getWorld().getHeight(); y++) {
            moveTo(0, y);
            if (countEggsInCurrentRow() % 2 != 0) return y;
        }
        return -1;
    }

    public int findOddColumn() {
        for (int x = 0; x < getWorld().getWidth(); x++) {
            if (countEggsInColumn(x) % 2 != 0) return x;
        }
        return -1;
    }

    public void correctParityError() {
        int wrongRow = findOddRow();
        int wrongCol = findOddColumn();
        if (wrongRow != -1 && wrongCol != -1) {
            moveTo(wrongCol, wrongRow);
            if (onEgg()) pickUpEgg();
            else layEgg();
        }
    }

    public void updatePoints() {
        if (onEgg()) {
            Egg e = (Egg) getOneIntersectingObject(Egg.class);
            if (e instanceof GoldenEgg) totalScore += 5;
            else totalScore += 1;
            pickUpEgg();
        }
        ((Mauritius) getWorld()).updateScore(totalScore, stepCounter);
    }

    public void randomWalk() {
        while (stepCounter < Mauritius.MAXSTEPS) {
            int dir = Greenfoot.getRandomNumber(4);
            setDirection(dir * 90);
            if (pathFree()) {
                move(1);
                stepCounter++;
                updatePoints();
            }
        }
    }

    public void eggSearchSmart() {
        while (stepCounter < Mauritius.MAXSTEPS) {
            if (onEgg()) updatePoints();
            Egg nearest = getNearestEgg();
            if (nearest == null) break;
            moveTo(nearest.getX(), nearest.getY());
        }
    }

    public Egg getNearestEgg() {
        List<Egg> eggList = getWorld().getObjects(Egg.class);
        if (eggList.isEmpty()) return null;

        Egg bestChoice = eggList.get(0);
        int bestDist = distanceTo(bestChoice);

        for (Egg e : eggList) {
            int d = distanceTo(e);
            if (d < bestDist) {
                bestDist = d;
                bestChoice = e;
            }
        }

        return bestChoice;
    }

    private int distanceTo(Actor obj) {
        return Math.abs(getX() - obj.getX()) + Math.abs(getY() - obj.getY());
    }

    // automatisch naar elke ei gaan wnr je op act klikt
    public void act() {
        eggSearchSmart();     // laat Dodo automatisch eieren zoeken
        Greenfoot.stop();     // stopt simulatie zodra klaar (haal weg als je wil blijven zoeken)
    }
}
