
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GridMap {

    private static Cell [][] grid;
    private final int NUM_NORMAL_VEHICLES = 10;
    private final int NUM_INFECTED_VEHICLES = 5;

    private final Double[] probabilityMetrics;

    private List<Vehicle> vehicleList= new ArrayList<>();


    public GridMap(int width, int height, Double[] probabilityMetrics) {
        this.probabilityMetrics = probabilityMetrics;


        initializeGridAndVehicles(width, height);


    }


    public void initializeGridAndVehicles(int width, int height) {
        this.grid = new Cell[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
        for (int i = 0; i < NUM_NORMAL_VEHICLES; i++) {
            Vehicle vehicle= new Vehicle(Vehicle.NOTINFECTED,chooseRandomCell(), probabilityMetrics,this,i);
            vehicleList.add(vehicle);

        }
        for (int i = 0; i < NUM_INFECTED_VEHICLES; i++) {
            Vehicle vehicle = new Vehicle(Vehicle.INFECTED, chooseRandomCell(), probabilityMetrics, this,i+NUM_NORMAL_VEHICLES);

            vehicleList.add(vehicle);
        }
        startVehiclesThreads();
    }
    public void startVehiclesThreads() {

        for (Vehicle vehicle : vehicleList) {
            Thread thread = new Thread(vehicle);
            thread.start();

        }
    }


    public int getXLength(){
        return this.grid.length;
    }
    public int getYLength(){
        return this.grid[0].length;
    }

    public Cell getCell(int x,int y ) {
       return grid[x][y];
    }

    public boolean isCellOccupied(int x, int y) {
        return grid[x][y].isOccupied();
    }

    public static ArrayList<Vehicle> getNeighbouringVehicles(Vehicle vehicle) {
        Cell centerCell = vehicle.getCurrentPosition();
        int x = centerCell.getX();
        int y = centerCell.getY();
        ArrayList<Vehicle> neighbouringVehicles = new ArrayList<>();

        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (isValidCell(i, j) && !(i == x && j == y) && grid[i][j].getObject() instanceof Vehicle) {
                    neighbouringVehicles.add((Vehicle)grid[i][j].getObject());
                }
            }
        }
        return neighbouringVehicles;
    }

    private static boolean isValidCell(int x, int y) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
    }

    public Cell chooseRandomCell() {

        Random rand = new Random();
        int x = rand.nextInt(getXLength());
        int y = rand.nextInt(getYLength());
        if(isCellOccupied(x, y)) {
            chooseRandomCell();
        }

        return getCell(x, y);
    }


    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }
}
