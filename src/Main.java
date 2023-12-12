import javax.swing.*;
import java.util.Random;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        Double pInf = 0.5;
        Double pRep = 0.2;
        Double pBreak = 0.1;
        Double pAtr = 0.1;
        Double epsilonINF = 0.3;
        Double epsilonREPandBREAK = 0.001;
        int NUM_NORMAL_VEHICLES = 10;
        int NUM_INFECTED_VEHICLES = 5;
        int NUM_CENTRAL_ATTRACTORS = 5;
        int gridSizeX = 10;
        int gridSizeY = 10;

        Double[] probabilities = { pInf, pRep, pBreak, pAtr, epsilonINF, epsilonREPandBREAK };
        int[] numberOfObjects = { NUM_NORMAL_VEHICLES, NUM_INFECTED_VEHICLES, NUM_CENTRAL_ATTRACTORS };

        new GridMap(gridSizeX, gridSizeY, probabilities, numberOfObjects);

    }
}