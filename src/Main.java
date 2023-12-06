import javax.swing.*;
import java.util.Random;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        Double porbabilityMetrics[] = {0.1, 0.1, 0.1};

        GridMap gridMap = new GridMap(15, 15,porbabilityMetrics);

        SwingUtilities.invokeLater(() -> new VehicleSimulationGUI(gridMap));











    }
}