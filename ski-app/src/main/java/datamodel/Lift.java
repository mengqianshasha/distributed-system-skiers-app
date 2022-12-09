package datamodel;

public class Lift {
    int resortId;
    int seasonId;
    int dayId;
    int skierId;
    int time;
    int liftId;

    public Lift(int resortId, int seasonId, int dayId, int skierId, int time, int liftId) {
        this.resortId = resortId;
        this.seasonId = seasonId;
        this.dayId = dayId;
        this.skierId = skierId;
        this.time = time;
        this.liftId = liftId;
    }
}