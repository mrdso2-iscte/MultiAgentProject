import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class VehicleSimulationGUI extends JFrame {

    private static final int CELL_SIZE = 50; // Modify this according to your cell size

    private JPanel gridPanel;
    private GridMap gridMap;
    private int GRIDWIDTH;
    private int GRIDHEIGHT;

    private BufferedImage notInfectedImage; // Image for NOT_INFECTED state
    private BufferedImage infectedImage;    // Image for INFECTED state
    private BufferedImage repairedImage;    // Image for REPAIRED state
    private BufferedImage brokenImage;      // Image for BROKEN state

    private BufferedImage cantralAttractorsImage;      // Image for BROKEN state
    private JPanel counterPanel; // Panel to display counter information
    private JLabel notInfectedCountLabel;
    private JLabel infectedCountLabel;
    private JLabel repairedCountLabel;
    private JLabel brokenCountLabel;

    public VehicleSimulationGUI(GridMap gridMap) {
        this.gridMap = gridMap;
        this.GRIDWIDTH = gridMap.getXLength();
        this.GRIDHEIGHT = gridMap.getYLength();
        loadImages(); // Load images for different vehicle states
        initializeGUI();
        initialCentralAttractors();


        initializeCounterPanel();
    }

    private void loadImages() {
        try {

            notInfectedImage = ImageIO.read(new File("./src/images/Car.jpeg"));
            infectedImage = ImageIO.read(new File("./src/images/Infected.jpeg"));
            repairedImage = ImageIO.read(new File("./src/images/Car.jpeg"));
            brokenImage = ImageIO.read(new File("./src/images/BrokenCar.jpeg"));
            cantralAttractorsImage = ImageIO.read(new File("./src/images/eiffel-tower.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeGUI() {
        setTitle("Vehicle Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(GRIDWIDTH, GRIDHEIGHT));

        // Create grid cells with JLabels showing images for vehicle states
        for (int x = 0; x < GRIDWIDTH; x++) {
            for (int y = 0; y < GRIDHEIGHT; y++) {
                Cell cell = gridMap.getCell(x, y);
                JLabel cellLabel = new JLabel();
                cellLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                cellLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cellLabel.setIcon(getImageForState(cell)); // Set image for vehicle state
                gridPanel.add(cellLabel);
            }
        }

        add(gridPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setSize(800, 600);
        setVisible(true);
    }

    private void initialCentralAttractors() {
        for (CentralAttractors cA : gridMap.getCentralAttractorsList()) {
            JLabel cellLabel = (JLabel) gridPanel.getComponent(cA.getPosition().getX() * GRIDWIDTH + cA.getPosition().getY());
            Image newimg = cantralAttractorsImage.getScaledInstance(40, 40, java.awt.Image.SCALE_REPLICATE);
            cellLabel.setIcon(new ImageIcon(newimg));

        }
    }

    private ImageIcon getImageForState(Cell cell) {
        if (cell.isOccupied() && cell.getObject() instanceof Vehicle) {
            Vehicle vehicle = (Vehicle) cell.getObject();
            BufferedImage image;
            switch (vehicle.getState()) {
                case Vehicle.NOTINFECTED:
                    image = notInfectedImage;
                    break;
                case Vehicle.INFECTED:
                    image = infectedImage;
                    break;
                case Vehicle.REPAIRED:
                    image = repairedImage;
                    break;
                case Vehicle.BROKEN:

                    image = brokenImage;
                    break;
                default:
                    image = null;
            }

            Image newimg = image.getScaledInstance(40, 40, java.awt.Image.SCALE_REPLICATE);


            return new ImageIcon(newimg);
        }
        return null;
    }



    public void updateGrid(Cell previousPosition,Vehicle vehicle) {

        JLabel previousCellLabel = (JLabel) gridPanel.getComponent(previousPosition.getX() * GRIDWIDTH + previousPosition.getY());
        previousCellLabel.setIcon(null);

        Cell currentPosition = vehicle.getCurrentPosition();
        JLabel currentCellLabel = (JLabel) gridPanel.getComponent(currentPosition.getX() * GRIDWIDTH + currentPosition.getY());

        currentCellLabel.setIcon(getImageForState(currentPosition));
        updateCounterLabels();




    }
    private void initializeCounterPanel() {
        counterPanel = new JPanel();
        counterPanel.setLayout(new GridLayout(4, 2)); // Adjust layout according to your design

        // Create labels for counter information
        notInfectedCountLabel = new JLabel("Not Infected Count: ");
        infectedCountLabel = new JLabel("Infected Count: ");
        repairedCountLabel = new JLabel("Repaired Count: ");
        brokenCountLabel = new JLabel("Broken Count: ");

        // Add labels to the counter panel
        counterPanel.add(notInfectedCountLabel);
        counterPanel.add(new JLabel());
        counterPanel.add(infectedCountLabel);
        counterPanel.add(new JLabel());
        counterPanel.add(repairedCountLabel);
        counterPanel.add(new JLabel());
        counterPanel.add(brokenCountLabel);
        counterPanel.add(new JLabel());

        add(counterPanel, BorderLayout.EAST); // Add the counter panel to the right side
        updateCounterLabels();

    }
    private void updateCounterLabels() {
        notInfectedCountLabel.setText("Not Infected Count: " + gridMap.counterUpdater.getNotInfectedCount());
        infectedCountLabel.setText("Infected Count: " + gridMap.counterUpdater.getInfectedCount());
        repairedCountLabel.setText("Repaired Count: " + gridMap.counterUpdater.getRepairedCount());
        brokenCountLabel.setText("Broken Count: " + gridMap.counterUpdater.getBrokenCount());
    }






}
