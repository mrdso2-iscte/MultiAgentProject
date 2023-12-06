import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class VehicleSimulationGUI extends JFrame implements ActionListener {

    private static final int CELL_SIZE = 50; // Modify this according to your cell size

    private JPanel gridPanel;
    private GridMap gridMap;
    private int GRIDWIDTH;
    private int GRIDHEIGHT;

    private BufferedImage notInfectedImage; // Image for NOT_INFECTED state
    private BufferedImage infectedImage;    // Image for INFECTED state
    private BufferedImage repairedImage;    // Image for REPAIRED state
    private BufferedImage brokenImage;      // Image for BROKEN state

    public VehicleSimulationGUI(GridMap gridMap) {
        this.gridMap = gridMap;
        this.GRIDWIDTH = gridMap.getXLength();
        this.GRIDHEIGHT = gridMap.getYLength();
        loadImages(); // Load images for different vehicle states
        initializeGUI();
        simulate();
    }

    private void loadImages() {
        try {

            notInfectedImage = ImageIO.read(new File("./src/images/Car.jpeg"));
            infectedImage = ImageIO.read(new File("./src/images/Infected.jpeg"));
            repairedImage = ImageIO.read(new File("./src/images/Car.jpeg"));
            brokenImage = ImageIO.read(new File("./src/images/BrokenCar.jpeg"));
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
        setVisible(true);
    }

    private ImageIcon getImageForState(Cell cell) {
        if (cell.isOccupied()) {
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

    private void simulate() {
        Timer timer = new Timer(1000, this); // Change delay as needed
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int x = 0; x < GRIDWIDTH; x++) {
            for (int y = 0; y < GRIDHEIGHT; y++) {
                Cell cell = gridMap.getCell(x, y);
                if (cell.isOccupied()) {
                    Vehicle vehicle = (Vehicle) cell.getObject();
                    vehicle.move();
                }
            }
        }
        updateGrid();
    }

    private void updateGrid() {
        for (int x = 0; x < GRIDWIDTH; x++) {
            for (int y = 0; y < GRIDHEIGHT; y++) {
                Cell cell = gridMap.getCell(x, y);
                JLabel cellLabel = (JLabel) gridPanel.getComponent(x * GRIDWIDTH + y);

                // Update JLabel icons based on vehicle state
                if (cell.isOccupied()) {
                    ImageIcon icon = getImageForState(cell);
                    cellLabel.setIcon(icon);
                }
            }
        }
    }


}
