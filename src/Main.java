import javax.swing.*;
import java.util.Random;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        Double[] probabilities = {0.3, 0.2, 0.7}; // Adjust with actual probabilities



        GridMap map = new GridMap(10, 10, probabilities);

        SwingUtilities.invokeLater(() -> {
            VehicleSimulationGUI newGUI = new VehicleSimulationGUI(map);
        });












    }
}