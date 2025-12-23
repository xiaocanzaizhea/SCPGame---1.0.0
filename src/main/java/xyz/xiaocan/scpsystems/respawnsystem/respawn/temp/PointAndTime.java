package xyz.xiaocan.scpsystems.respawnsystem.respawn.temp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointAndTime {
    private int point;
    private int time;

    public PointAndTime(int point, int time) {
        this.point = point;
        this.time = time;
    }

    @Override
    public String toString() {
        return "PointAndTime{" +
                "point=" + point +
                ", time=" + time +
                '}';
    }
}
