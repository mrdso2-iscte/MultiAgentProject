
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GridMap {

    private Cell[][] grid;
    private final int NUM_NORMAL_VEHICLES;
    private final int NUM_INFECTED_VEHICLES;
    private final int NUM_CENTRAL_ATTRACTORS;
    private final Double[] probabilityMetrics;
    private List<Vehicle> vehicleList = new ArrayList<>();
    private List<CentralAttractors> centralAttractorsList = new ArrayList<>();
    private VehicleSimulationGUI gui;
    private CounterUpdater counterUpdater;

    public GridMap(int width, int height, Double[] probabilityMetrics, int[] numberOfObjects) throws InterruptedException {
        this.probabilityMetrics = probabilityMetrics;
        this.NUM_NORMAL_VEHICLES = numberOfObjects[0];
        this.NUM_INFECTED_VEHICLES = numberOfObjects[1];
        this.NUM_CENTRAL_ATTRACTORS = numberOfObjects[2];
        this.counterUpdater = new CounterUpdater();

        initializeGridVehiclesAndAttractors(width, height);

    }

    public int getXLength() {
        return this.grid.length;
    }

    public int getYLength() {
        return this.grid[0].length;
    }

    public Cell getCell(int x, int y) {
        return grid[x][y];
    }

    public boolean isValidCell(int x, int y) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
    }

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public List<CentralAttractors> getCentralAttractorsList() {
        return centralAttractorsList;
    }

    public CounterUpdater getCounterUpdater() {
        return counterUpdater;
    }

    public void updateCounter(String previousState, String newState) {
        counterUpdater.updateCounter(previousState, newState);
    }

    public void initializeGridVehiclesAndAttractors(int width, int height) throws InterruptedException {
        this.grid = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }

        for (int i = 0; i < NUM_CENTRAL_ATTRACTORS; i++) {
            CentralAttractors cA = new CentralAttractors("centralAttractor", chooseRandomCell());
            centralAttractorsList.add(cA);
           
        }
        for (int i = 0; i < NUM_NORMAL_VEHICLES; i++) {
            Vehicle vehicle = new Vehicle(Vehicle.NOTINFECTED, chooseRandomCell(), probabilityMetrics, this);
            vehicleList.add(vehicle);
            counterUpdater.updateCounter(Vehicle.NOTINFECTED);
                    

        }
        for (int i = 0; i < NUM_INFECTED_VEHICLES; i++) {
            Vehicle vehicle = new Vehicle(Vehicle.INFECTED, chooseRandomCell(), probabilityMetrics, this);

            vehicleList.add(vehicle);
            counterUpdater.updateCounter(Vehicle.INFECTED);
            
        }
        initializeGUI();
        startVehiclesThreads();
    }

    public void startVehiclesThreads() throws InterruptedException {
        Thread.sleep(5000);

        for (Vehicle vehicle : vehicleList) {
            Thread thread = new Thread(vehicle);
            thread.start();

        }
    }

    public void initializeGUI() {
        this.gui = new VehicleSimulationGUI(this);
        SwingUtilities.invokeLater(() -> {
            gui.setVisible(true);
        });

    }

    public void updateGui(Cell previousPosition, Vehicle vehicle) {
        gui.updateGrid(previousPosition, vehicle);
    }

    public boolean isCellOccupied(int x, int y) {
        return grid[x][y].isOccupied();
    }

    public ArrayList<Vehicle> getNeighbouringVehicles(Vehicle vehicle) {
        Cell centerCell = vehicle.getCurrentPosition();
        int x = centerCell.getX();
        int y = centerCell.getY();
        ArrayList<Vehicle> neighbouringVehicles = new ArrayList<>();

        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (isValidCell(i, j) && !(i == x && j == y) && grid[i][j].getObject() instanceof Vehicle) {
                    neighbouringVehicles.add((Vehicle) grid[i][j].getObject());
                }
            }
        }
        return neighbouringVehicles;
    }

    public int getInfectedNeighbourCount(Vehicle vehicle) {
        int infectedCount = 0;
        Cell centerCell = vehicle.getCurrentPosition();
        int x = centerCell.getX();
        int y = centerCell.getY();

        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (isValidCell(i, j) && !(i == x && j == y) && grid[i][j].getObject() instanceof Vehicle) {
                    if (((Vehicle) grid[i][j].getObject()).getState() == Vehicle.INFECTED) {
                        infectedCount++;
                    }
                }
            }
        }
        return infectedCount;
    }

    public Cell chooseRandomCell() {
        
        Random rand = new Random();
        int x = rand.nextInt(getXLength());
        int y = rand.nextInt(getYLength());
        while(isCellOccupied(x, y)) {
            rand = new Random();
            x = rand.nextInt(getXLength());
            y = rand.nextInt(getYLength());
        }
        
       
        return getCell(x, y);
    }


}
