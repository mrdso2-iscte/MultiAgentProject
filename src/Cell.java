public class Cell {
    private int x;
    private int y;


    private Object object = null;


    public Cell(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return y;
    }



    public boolean isOccupied() {
        return object != null;
    }



    public Object getObject(){
        return object;
    }

    public void setObject(Object object){
        this.object = object;
    }

}
