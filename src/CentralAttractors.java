import java.util.List;

public class CentralAttractors {

    private String attractorType;
    private Cell position;
    public CentralAttractors(String attractorType, Cell position){
        this.attractorType = attractorType;
        this.position = position;
        position.setObject(this);
    }

    public Cell getPosition() {
        return position;
    }

    public String getAttractorType() {
        return attractorType;
    }



}
