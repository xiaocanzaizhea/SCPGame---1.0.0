package xyz.xiaocan.doorsystem;

public enum DoorMoveMode {

    Rotate_Up("rotateup"),
    Rotate_Down("rotatedown"),
    Rotate_Left("rotateleft"),
    Rotate_Right("rotateright"),

    Move_Right("moveright");

    private String id;

    DoorMoveMode(String id) {
        this.id = id;
    }

    public static DoorMoveMode getMoveMode(String id){
        for (DoorMoveMode doorMoveMode:values()) {
            if(doorMoveMode.id.equals(id)) return doorMoveMode;
        }
        return null;
    }

    @Override
    public String toString() {
        return "DoorMoveMode{" +
                "id='" + id + '\'' +
                '}';
    }
}
