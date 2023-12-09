import java.util.HashMap;
import java.util.Map;

public class CounterUpdater {
    private Map<String, Integer> stateCounters;

    public CounterUpdater() {
        stateCounters = new HashMap<>();
        stateCounters.put(Vehicle.NOTINFECTED, 0);
        stateCounters.put(Vehicle.INFECTED, 0);
        stateCounters.put(Vehicle.REPAIRED, 0);
        stateCounters.put(Vehicle.BROKEN, 0);
    }

    public void updateCounter(String previousState, String newState) {
        decrementCounter(previousState);
        incrementCounter(newState);
    }
    public void updateCounter( String newState) {

        incrementCounter(newState);
    }

    private void decrementCounter(String state) {
        if (stateCounters.containsKey(state)) {
            int count = stateCounters.get(state);
            stateCounters.put(state, count - 1);
        }
    }

    private void incrementCounter(String state) {
        if (stateCounters.containsKey(state)) {
            int count = stateCounters.get(state);
            stateCounters.put(state, count + 1);
        }
    }

    // Getters for counters if needed
    public int getNotInfectedCount() {
        return stateCounters.get(Vehicle.NOTINFECTED);
    }

    public int getInfectedCount() {
        return stateCounters.get(Vehicle.INFECTED);
    }

    public int getRepairedCount() {
        return stateCounters.get(Vehicle.REPAIRED);
    }

    public int getBrokenCount() {
        return stateCounters.get(Vehicle.BROKEN);
    }

    public String toString(){

        return "Grid updated: noInfect "+ stateCounters.get(Vehicle.NOTINFECTED) +
                " infect " + stateCounters.get(Vehicle.INFECTED) +
                " repaired " + stateCounters.get(Vehicle.REPAIRED) +
                " broken " + stateCounters.get(Vehicle.BROKEN) ;
    }


}
