package xyz.xiaocan.elevatorsystem;

public enum ElevatorState {
    OPEN("CLOSED"),
    RUNNING(""),
    CLOSED("OPEN");

    public String invert;
    ElevatorState(String invert){
        this.invert = invert;
    }
}
