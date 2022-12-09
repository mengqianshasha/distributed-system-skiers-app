package lift;

import utils.Utils;

public class LiftFactory {
    private static final int SKIER_ID_MIN = 1;
    private static final int SKIER_ID_MAX = 100000;
    private static final int RESORT_ID_MIN = 1;
    private static final int RESORT_ID_MAX = 10;
    private static final int LIFT_ID_MIN = 1;
    private static final int LIFT_ID_MAX = 40;
    private static final int SEASON_ID = 2022;
    private static final int DAY_ID = 1;
    private static final int TIME_MIN = 1;
    private static final int TIME_MAX = 360;

    public Lift getLift() {
        return new Lift(
                Utils.getRandomNumber(RESORT_ID_MIN, RESORT_ID_MAX),
                SEASON_ID,
                DAY_ID,
                Utils.getRandomNumber(SKIER_ID_MIN, SKIER_ID_MAX),
                Utils.getRandomNumber(TIME_MIN, TIME_MAX),
                Utils.getRandomNumber(LIFT_ID_MIN, LIFT_ID_MAX)
        );
    }

    public Lift getLift(int dayId) {
        return new Lift(
                1,
                1,
                dayId,
                Utils.getRandomNumber(SKIER_ID_MIN, SKIER_ID_MAX),
                Utils.getRandomNumber(TIME_MIN, TIME_MAX),
                Utils.getRandomNumber(LIFT_ID_MIN, LIFT_ID_MAX)
        );
    }


}
