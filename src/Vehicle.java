
import java.util.Random;

public class Vehicle implements Runnable {
    private String state;
    private Cell currentPosition;
    private static GridMap gridMap;
    private int reparationCount = 0;
    private CentralAttractors goingToAttractor = null;
    public static double pINF, pREP, pBREAK, pATR, epsilonINF, epsilonREPandBREAK;
    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    public static final String NOTINFECTED = "NI", INFECTED = "I", REPAIRED = "R", BROKEN = "B";

    public Vehicle(String initialState, Cell position, Double[] probabilityMetrics, GridMap gridMap, int id) {
        this.state = initialState;
        Vehicle.gridMap = gridMap;
        currentPosition = position;
        currentPosition.setObject(this);
        pINF = probabilityMetrics[0];
        pREP = probabilityMetrics[1];
        pBREAK = probabilityMetrics[2];
        pATR = probabilityMetrics[3];
        epsilonINF = probabilityMetrics[4];
        epsilonREPandBREAK = probabilityMetrics[5];
    }

    public String getState() {
        return this.state;
    }

    private void setState(String newState) {
        String previousState = this.state;
        this.state = newState;
        Vehicle.gridMap.updateCounter(previousState, this.state);
    }

    public Cell getCurrentPosition() {
        return currentPosition;
    }

    // gives a random x,y position
    public int[] getNextRandomPosition(int newX, int newY) {
        Random rand = new Random();
        int direction = rand.nextInt(4);
        switch (direction) {
            case UP:
                newX--;
                break;
            case DOWN:
                newX++;
                break;
            case LEFT:
                newY--;
                break;
            case RIGHT:
                newY++;
                break;
            default:
                throw new IllegalArgumentException("Invalid direction");
        }
        return new int[] { newX, newY };
    }

    // change the positon of a vehicle
    public void changePosition(Cell newPosition) {
        Cell previousPosition = currentPosition;
        previousPosition.setObject(null);
        currentPosition = gridMap.getCell(newPosition.getX(), newPosition.getY());
        currentPosition.setObject(this);
    }

    // a vehicle changePosition to a random direction or toward an attractor
    public void takeNextStep() {
        int newX = currentPosition.getX(), newY = currentPosition.getY();
        int[] newPosition;
        if (goingToAttractor != null) {
            newPosition = getNextCalculatedPosition();
            newX = newPosition[0];
            newY = newPosition[1];

        } else {
            newPosition = getNextRandomPosition(newX, newY);
            newX = newPosition[0];
            newY = newPosition[1];
        }
        if (gridMap.isValidCell(newX, newY) && !gridMap.isCellOccupied(newX, newY)) {
            changePosition(gridMap.getCell(newX, newY));
        }

    }

    public void getInfected() {
        Random random = new Random();
        int infectedNeighbours = gridMap.getInfectedNeighbourCount(this);
        if (infectedNeighbours > 0 && random.nextDouble() < pINF + epsilonINF * infectedNeighbours) {
            this.setState(INFECTED);
        }
    }

    public void getRepairedOrBroken() {
        double random = new Random().nextDouble();
        if (random > 1 - (pREP - (epsilonREPandBREAK * reparationCount))) {
            this.setState(REPAIRED);
            reparationCount++;
        } else if (random <= pBREAK + (epsilonREPandBREAK * reparationCount)) {
            this.setState(BROKEN);
        }
    }

    public double getDistanceToAttraction(Cell position) {
        int targetX = goingToAttractor.getPosition().getX();
        int targetY = goingToAttractor.getPosition().getY();
        int x = position.getX();
        int y = position.getY();
        return Math.sqrt(Math.pow(targetX - x, 2) + Math.pow(targetY - y, 2));
    }

    // calculates the closest position to the central attractor; if that position is
    // occupied, returns a random one to avoid traffic
    public int[] getNextCalculatedPosition() {
        int x = currentPosition.getX();
        int y = currentPosition.getY();
        Cell[] listNeighbouringCells = { new Cell(x - 1, y), new Cell(x + 1, y), new Cell(x, y - 1),
                new Cell(x, y + 1) };
        double minDistance = getDistanceToAttraction(listNeighbouringCells[0]);
        Cell nextPosition = listNeighbouringCells[0];

        for (Cell cell : listNeighbouringCells) {
            if (getDistanceToAttraction(cell) < minDistance) {
                minDistance = getDistanceToAttraction(cell);
                nextPosition = cell;
            }
        }
        if (gridMap.isValidCell(nextPosition.getX(), nextPosition.getY())
                && gridMap.isCellOccupied(nextPosition.getX(), nextPosition.getY())) {
            return getNextRandomPosition(x, y);
        }
        return new int[] { nextPosition.getX(), nextPosition.getY() };
    }

    // a vehicle moves toward an attractor depends on probability pATR
    public void movedTowardAttractor() {
        double random = new Random().nextDouble();
        if (random < pATR) {
            int randomIndex = new Random().nextInt(gridMap.getCentralAttractorsList().size());
            goingToAttractor = gridMap.getCentralAttractorsList().get(randomIndex);
        }
    }

    // a vehicle moves if is not broken; after moving, it gets infected or repaired
    // or broken
    public synchronized void move() {
        if (!state.equals(BROKEN)) {
            if (goingToAttractor == null && !gridMap.getCentralAttractorsList().isEmpty()) {
                movedTowardAttractor();
            } else {
                if (getDistanceToAttraction(currentPosition) == 1) {
                    goingToAttractor = null;
                }
            }
            takeNextStep();
            switch (this.state) {
                case NOTINFECTED, REPAIRED -> getInfected();
                case INFECTED -> getRepairedOrBroken();
            }
        }
    }

    @Override
    public void run() {
        while (!this.state.equals(BROKEN)) {
            Cell previousPosition = currentPosition;
            move();
            gridMap.updateGui(previousPosition, this);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
