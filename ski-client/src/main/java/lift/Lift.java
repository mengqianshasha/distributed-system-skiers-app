package lift;

public class Lift {
    int resortId;
    int seasonId;
    int dayId;
    int skierId;
    int time;
    int liftId;
    boolean signal;

    public Lift(int resortId, int seasonId, int dayId, int skierId, int time, int liftId, boolean signal) {
        this.resortId = resortId;
        this.seasonId = seasonId;
        this.dayId = dayId;
        this.skierId = skierId;
        this.time = time;
        this.liftId = liftId;
        this.signal = signal;
    }

    public Lift(int resortId, int seasonId, int dayId, int skierId, int time, int liftId) {
        this(resortId, seasonId, dayId, skierId, time, liftId, false);
    }

    public Lift() {
        this(0, 0, 0, 0, 0, 0, true);
    }

    public int getResortId() {
        return resortId;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public int getDayId() {
        return dayId;
    }

    public int getSkierId() {
        return skierId;
    }

    public int getTime() {
        return time;
    }

    public int getLiftId() {
        return liftId;
    }

    public boolean isSignal() {
        return signal;
    }
}