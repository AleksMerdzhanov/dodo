import greenfoot.*;
import java.util.*;

/**
 * MyDodo – compacte maar complete versie (week?1?t/m?7).
 * Bevat alle basis?, zoek? en SurpriseEgg?functies.
 */
public class MyDodo extends Dodo {

    /* === Algemeen === */
    private int  myStepsTaken = 0;
    private int  myScore      = 0;
    private List<SurpriseEgg> surpriseEggs = new ArrayList<>();

    public MyDodo() {
        super(5); // snelheid
    }

    /* === Week?1 – Basisacties === */
    public boolean canLayEgg()        { return onNest() && !onEgg(); }
    public void    turn180()          { turnLeft(); turnLeft();      }
    public void    jump()             { move(1); move(1);            }
    public boolean canMove()          { return !fenceAhead();        }

    public void climbOverFence() {
        turnLeft();  move(1);
        turnRight(); move(1);
        turnRight(); move(1);
        turnLeft();
    }

    public void walkToWorldEdgePrintingCoordinates() {
        while (canMove()) {
            System.out.println("X:" + getX() + " Y:" + getY());
            move(1);
        }
        System.out.println("X:" + getX() + " Y:" + getY());
    }

    public boolean grainAhead() {
        move(1);
        boolean grain = onGrain();
        turn180(); move(1); turn180();
        return grain;
    }

    /* === Week?2 – Nest, fences, graan === */
    public void gotoEgg()                       { while (!onEgg()) move(1); }
    public void walkToWorldEdge()               { while (canMove()) move(1); }
    public void goBackToStartOfRowAndFaceBack() { turn180(); walkToWorldEdge(); turn180(); }

    public void walkToWorldEdgeClimbingOverFences() {
        while (true) {
            if (canMove()) move(1);
            else if (fenceAhead()) climbOverFence();
            else break;
        }
    }

    public void pickUpGrainsAndPrintCoordinates() {
        while (true) {
            if (onGrain()) {
                pickUpGrain();
                System.out.println("X:" + getX() + " Y:" + getY());
            }
            if (canMove()) move(1); else break;
        }
        if (onGrain()) {
            pickUpGrain();
            System.out.println("X:" + getX() + " Y:" + getY());
        }
    }

    public void stepOneCellBackwards() { turn180(); move(1); turn180(); }

    public void noDoubleEggs() {
        while (canMove()) {
            if (onNest() && !onEgg()) layEgg();
            move(1);
        }
    }

    public void goToNestAvoidFences() {
        while (!onNest()) {
            if (fenceAhead()) climbOverFence(); else move(1);
        }
        layEgg();
    }

    public void walkAroundFencedArea() {
        while (!onEgg()) {
            if (canMove()) move(1); else turnRight();
        }
    }

    /* === Week?3 – Navigatie en locatie === */
    public void faceEast() {
        while (getDirection() != EAST) turnLeft();
    }

    public void goToLocation(int x, int y) {
        while (getX() != x || getY() != y) {
            if (getX() < x) setDirection(EAST);
            else if (getX() > x) setDirection(WEST);
            else if (getY() < y) setDirection(SOUTH);
            else setDirection(NORTH);
            move(1);
        }
    }

    public boolean locationReached(int x, int y) { return getX() == x && getY() == y; }

    public boolean validCoordinates(int x, int y) {
        if (x >= 0 && x < getWorld().getWidth() && y >= 0 && y < getWorld().getHeight()) return true;
        showError("Invalid coordinates");
        return false;
    }

    public int countEggsInRow() {
        int count  = 0;
        int startX = getX();
        while (canMove()) { if (onEgg()) count++; move(1); }
        if (onEgg()) count++;
        goToLocation(startX, getY());
        setDirection(EAST);
        return count;
    }

    /* === Week?4 – Patronen, tellen, gemiddelde === */
    public void layTrailOfEggs(int n) {
        if (n <= 0) { showError("Aantal eieren moet positief zijn."); return; }
        for (int i = 0; i < n; i++) {
            layEgg();
            if (canMove()) move(1); else break;
        }
    }

    public int countAllEggs() { return getWorld().getObjects(Egg.class).size(); }

    public int findRowWithMostEggs() {
        int max = 0, maxRow = 0;
        for (int y = 0; y < getWorld().getHeight(); y++) {
            goToLocation(0, y);
            int eggs = countEggsInRow();
            if (eggs > max) { max = eggs; maxRow = y; }
        }
        System.out.println("Meeste eieren in rij: " + maxRow);
        return maxRow;
    }

    public void calculateAverageEggsPerRow() {
        int total = 0, rows = getWorld().getHeight();
        for (int y = 0; y < rows; y++) { goToLocation(0, y); total += countEggsInRow(); }
        System.out.println("Gemiddeld per rij: " + (double) total / rows);
    }

    /* === Week?5 – Pariteitsbit === */
    public int countEggsInCol(int x) {
        int cnt = 0;
        for (int y = 0; y < getWorld().getHeight(); y++) {
            goToLocation(x, y);
            if (onEgg()) cnt++;
        }
        return cnt;
    }

    public int getIncorrectRow() {
        for (int y = 0; y < getWorld().getHeight(); y++) {
            goToLocation(0, y);
            if (countEggsInRow() % 2 != 0) return y;
        }
        return -1;
    }

    public int getIncorrectCol() {
        for (int x = 0; x < getWorld().getWidth(); x++) if (countEggsInCol(x) % 2 != 0) return x;
        return -1;
    }

    public void fixIncorrectBit() {
        int r = getIncorrectRow(), c = getIncorrectCol();
        if (r != -1 && c != -1) {
            goToLocation(c, r);
            if (onEgg()) pickUpEgg(); else layEgg();
        }
    }

    /* === Week?6 – SurpriseEggs === */
    public void fillSurpriseEggList() {
        surpriseEggs = getWorld().getObjects(SurpriseEgg.class);
    }

    public void printEggData() {
        for (SurpriseEgg e : surpriseEggs)
            System.out.println("Waarde: " + e.getValue() + " X:" + e.getX() + " Y:" + e.getY());
    }

    public void printAverageValue() {
        int sum = 0;
        for (SurpriseEgg e : surpriseEggs) sum += e.getValue();
        if (!surpriseEggs.isEmpty())
            System.out.println("Gemiddelde waarde: " + (double) sum / surpriseEggs.size());
    }

    public Egg findMostValuableEgg() {
        Egg best = null; int max = 0;
        for (SurpriseEgg e : surpriseEggs) {
            if (e.getValue() > max) {
                max = e.getValue();
                best = e;
            }
        }
        return best;
    }

    /* === Week?7 – Dodo?race === */
    public void moveRandomly() {
        while (myStepsTaken < Mauritius.MAXSTEPS) {
            setDirection(randomDirection());
            if (canMove()) {
                move(1);
                myStepsTaken++;
                updateScore();
            }
        }
    }

    public void updateScore() {
        if (onEgg()) {
            Egg e = (Egg) getOneIntersectingObject(Egg.class);
            if (e instanceof SurpriseEgg) myScore += ((SurpriseEgg) e).getValue();
            else if (e instanceof GoldenEgg) myScore += 5;
            else myScore += 1;
            pickUpEgg();
        }
    }

    public void smartEggHunter() {
        while (myStepsTaken < Mauritius.MAXSTEPS) {
            if (onEgg()) updateScore();
            Egg nearest = getNearestEgg();
            if (nearest == null) break;
            goToLocation(nearest.getX(), nearest.getY());
        }
    }

    public Egg getNearestEgg() {
        List<Egg> eggList = getWorld().getObjects(Egg.class);
        if (eggList.isEmpty()) return null;

        Egg bestChoice = null;
        int bestDist = Integer.MAX_VALUE;

        for (Egg e : eggList) {
            int d = distanceTo(e);
            if (d < bestDist) {
                bestDist = d;
                bestChoice = e;
            }
        }

        return bestChoice;
    }

    public int distanceTo(Actor a) {
        return Math.abs(getX() - a.getX()) + Math.abs(getY() - a.getY());
    }

    /* Actiemethode – zoek eieren en stop zodra klaar */
    public void act() {
        smartEggHunter();
        Greenfoot.stop(); // haal weg als hij moet blijven lopen
    }
}
