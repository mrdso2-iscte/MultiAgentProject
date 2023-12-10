import java.util.List;

public class CentralAttractors {

    private String attractorType;
    private Cell positon;
    public CentralAttractors(String attractorType, Cell position){
        this.attractorType = attractorType;
        this.positon = position;
        position.setObject(this);
    }

    public Cell getPosition() {
        return positon;
    }

    public String getAttractorType() {
        return attractorType;
    }



}
