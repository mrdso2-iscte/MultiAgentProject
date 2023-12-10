import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/* O QUE FALTA:
* - quando ficam empacotados a esoera pra ir para um attractor, escolher uma posicao atoa para sair dali
* - em vez de irem pra cima dos monumentos, ficam so ao lado
* -
* */
public class Vehicle implements Runnable{
    private final GridMap gridMap;
    private String state;
    private  Cell currentPosition;


    private int id;

    public static double pINF, pREP, pBREAK, pATR;
    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;

    public static final String NOTINFECTED = "NI", INFECTED = "I", REPAIRED = "R", BROKEN = "B";

    private static List<VehicleObserver> observers = new ArrayList<>();

    private  CentralAttractors goingToAttractor = null;

    public static void addObserver(VehicleObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(Cell previousPosition) {
        for (VehicleObserver observer : observers) {
            observer.vehicleUpdated( previousPosition,this);
        }

    }

    public Vehicle(String initialState, Cell position, Double[] probabilityMetrics, GridMap gridMap, int id) {
        this.state = initialState;
        this.gridMap = gridMap;
        this.id = id;
        currentPosition = position;
        currentPosition.setObject(this);
        pINF = probabilityMetrics[0];
        pREP = probabilityMetrics[1];
        pBREAK = probabilityMetrics[2];
        pATR = probabilityMetrics[3];

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




    //gives a random x,y position
    public int[]getRandomDirection(int newX, int newY) {
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
        return new int[]{newX, newY};
    }


    public double getDistanceToAttraction(Cell position){
        int targetX = goingToAttractor.getPosition().getX();
        int targetY = goingToAttractor.getPosition().getY();
        int x = position.getX();
        int y = position.getY();
        return Math.sqrt(Math.pow(targetX-x,2)+Math.pow(targetY-y,2));
    }
    public  int[] getClosestPosition(){
        int x= currentPosition.getX();
        int y= currentPosition.getY();
        Cell [] listNeighbouringCells = {new Cell(x-1,y), new Cell(x+1,y), new Cell(x,y-1), new Cell(x,y+1)}; //cima, baixo, left, right
        double minDistance=getDistanceToAttraction( listNeighbouringCells[0]);
         Cell nextPosition = listNeighbouringCells[0];

        for ( Cell cell : listNeighbouringCells) {
            if(getDistanceToAttraction(cell)<minDistance ){
                minDistance = getDistanceToAttraction(cell);
                nextPosition = cell;
            }
        }
        return new int[]{nextPosition.getX(), nextPosition.getY()};
    }

    //returns a possible x,y position toward an attractor
    public int[] getPathToAttraction() {

      int x = getClosestPosition()[0];
      int y = getClosestPosition()[1];
      return new int[]{x, y};
    }


    //change the positon of a vehicle
    public void changePosition(Cell newPosition) {
        Cell previousPosition = currentPosition;
        previousPosition.setObject(null);
        currentPosition = this.gridMap.getCell(newPosition.getX(), newPosition.getY());
        currentPosition.setObject(this);
    }

    //a vehicle changePosition to a random direction or toward an attractor
    public void takeNextStep() {
        int newX = currentPosition.getX(), newY = currentPosition.getY();
        int[] newPosition;
        if(goingToAttractor!=null){
            newPosition = getPathToAttraction();
            newX = newPosition[0];
            newY = newPosition[1];

        }else {
            newPosition = getRandomDirection(newX, newY);
            newX = newPosition[0];
            newY = newPosition[1];
        }
        if (newX >= 0 && newX < this.gridMap.getXLength() && newY >= 0 && newY < this.gridMap.getYLength() && !this.gridMap.isCellOccupied(newX, newY)){
            System.out.println(" Vou-me mover para " + newX + newY);
            changePosition(this.gridMap.getCell(newX, newY));
        }
    }

    //a vehicle gets infected depends on probability pINF
    public void getInfected( ){
        Random random = new Random();
        if (random.nextDouble() < pINF) {
            ArrayList<Vehicle> neighbours = gridMap.getNeighbouringVehicles(this);
            String previousState = this.state;
            for (Vehicle neighbour : neighbours) {
                if (neighbour.getState().equals(INFECTED)) {
                    this.setState(INFECTED);
                    CounterUpdater counterUpdater = gridMap.counterUpdater;
                    counterUpdater.updateCounter(previousState, this.state);
                    break;
                }
            }
        }
    }

    //a vehicle gets repaired or broken depends on probability pREP and pBREAK
    public void getRepairedOrBroken() {
        double random = new Random().nextDouble();
        String previousState = this.state;
        CounterUpdater counterUpdater = gridMap.counterUpdater;
        if (random <=pREP){
            this.setState(REPAIRED);
            counterUpdater.updateCounter(previousState, this.state);
        }
        else if (random<= pREP + pBREAK) {
            this.setState(BROKEN);
            counterUpdater.updateCounter(previousState, this.state);
            notifyObservers(currentPosition);
        }
    }



    //a vehicle moves toward an attractor depends on probability pATR
    public void movedTowardAttractor(){
        double random = new Random().nextDouble();
        if(random < pATR){
            int randomIndex = new Random().nextInt(gridMap.getCentralAttractorsList().size());
            goingToAttractor = gridMap.getCentralAttractorsList().get(randomIndex);
        }
    }

    //a vehicle moves if is not broken; after moving, it gets infected or repaired or broken
    public synchronized void move() {
            if (!state.equals(BROKEN)) {
                if(goingToAttractor==null && !gridMap.getCentralAttractorsList().isEmpty()){
                    movedTowardAttractor();
                } else {
                    if(getDistanceToAttraction(currentPosition) == 1) {
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
            notifyObservers(previousPosition);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
