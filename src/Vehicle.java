import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;
import javax.swing.*;

public class Vehicle implements Runnable{
    private final GridMap gridMap;
    private String state;
    private  Cell currentPosition;
    private static Lock moveLock = new ReentrantLock();

    private int id;



    public static double pINF, pREP, pBREAK;
    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;

    public static final String NOTINFECTED = "NI", INFECTED = "I", REPAIRED = "R", BROKEN = "B";


    public Vehicle(String initialState, Cell position, Double[] probabilityMetrics, GridMap gridMap, int id) {
        this.state = initialState;
        currentPosition = position;
        currentPosition.setObject(this);
        pINF = probabilityMetrics[0];
        pREP = probabilityMetrics[1];
        pBREAK = probabilityMetrics[2];
        this.gridMap = gridMap;
        this.id = id;

    }

    public String getState() {
        return this.state;
    }

    private void setState(String newState) {
        this.state = newState;
    }

    public  Cell getCurrentPosition() {
        return currentPosition;
    }




    public void takeNextStep( ) {
        int newX = currentPosition.getX(), newY = currentPosition.getY();

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
        if (newX >= 0 && newX < this.gridMap.getXLength() && newY >= 0 && newY < this.gridMap.getYLength() && !this.gridMap.isCellOccupied(newX, newY)){
            Cell previousPosition = currentPosition;

            previousPosition.setObject(null);

            currentPosition = this.gridMap.getCell(newX, newY);
            currentPosition.setObject(this);
            notifyObservers(previousPosition);

        }
    }

    public void getInfected( ){
        Random random = new Random();
        if (random.nextDouble() > pINF) {
            ArrayList<Vehicle> neighbours = gridMap.getNeighbouringVehicles(this);

            for (Vehicle neighbour : neighbours) {
                if (neighbour.getState().equals(INFECTED)) {
                    this.setState(INFECTED);
                    break;
                }
            }

        }
    }

    public void getRepairedOrBroken() {
        double random = new Random().nextDouble();
        if (random < pREP){
            this.setState(REPAIRED);
        }
        else if (random <= pREP + pBREAK) {
            this.setState(BROKEN);
        }

    }



    public void move() {

        moveLock.lock();
        try {

            if (!state.equals(BROKEN)) {

                takeNextStep();
                switch (this.state) {
                    case NOTINFECTED, REPAIRED -> getInfected();
                    case INFECTED -> getRepairedOrBroken();
                }


            }
        } finally {
            moveLock.unlock();

        }


    }

    @Override
    public void run() {
        while (true) {

            move();
            try {

                Thread.sleep(2000); // Sleep for 1 second

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public int getId() {
        return id;
    }
}
