import java.util.Random;
//Initializes the scenario and scoreboard
public class Scenario {
    private int gameClock;
    private int homeScore;
    private int awayScore;
    private String possession;
    private int fieldPosition;
    private int down;
    private int distance;
    private String playType;
    private String playResult;
    private int yardsGained;
    private int distanceAfterPlay;
    private int downAfterPlay;
    private boolean turnover;
    private int fieldPositionAfterPlay;
    private boolean touchdown;
    private boolean safety;
    private int timeElapsed;
    private boolean outOfBounds;
    public Scenario() {
        Random random = new Random();
        //Generates game situation
        gameClock = random.nextInt(120) + 1;
        homeScore = random.nextInt(36);
        awayScore = random.nextInt(36);
        if (random.nextBoolean()) {
            possession = "HOME";
        } else {
            possession = "AWAY";
        }
        fieldPosition = random.nextInt(99) + 1;
        down = random.nextInt(4) + 1;
        int yardsToGoal = 100 - fieldPosition;

        if (down == 1) {
            distance = Math.min(10, yardsToGoal);
        } else {
            distance = random.nextInt(Math.min(10, yardsToGoal)) + 1;
        }

        if (random.nextBoolean()) {
            playType = "PASS";
        } else {
            playType = "RUN";
        }
        if (playType.equals("PASS")) {
            int outcome = random.nextInt(100);
            if (outcome < 60) {
                playResult = "COMPLETE";
            } else if (outcome < 96) {
                playResult = "INCOMPLETE";
            } else {
                playResult = "INTERCEPTION";
            }
        } else {
            int outcome = random.nextInt(100);
            if (outcome < 85) {
                playResult = "GAINED YARDS";
            } else if (outcome < 97) {
                playResult = "LOST YARDS";
            } else {
                playResult = "FUMBLE";
            }
        }
        if (playResult.equals("INCOMPLETE")) {
            timeElapsed = 0;
        } else if (playType.equals("PASS")) {
            timeElapsed = random.nextInt(11) + 5;
        } else {
            timeElapsed = random.nextInt(16) + 5;
        }
        gameClock = Math.max(0, gameClock - timeElapsed);
        if (playResult.equals("COMPLETE") || playResult.equals("GAINED YARDS")) {
            yardsGained = random.nextInt(Math.min(30, yardsToGoal)) + 1;
        } else if (playResult.equals("LOST YARDS")) {
            yardsGained = -(random.nextInt(5) + 1);
        } else {
            yardsGained = 0;
        }
        int endPosition = fieldPosition + yardsGained;
        touchdown = endPosition >= 100;
        safety = endPosition <= 0;
        boolean turnoverOnDowns = (down == 4) && (yardsGained < distance) && !safety;
       turnover = playResult.equals("INTERCEPTION") || playResult.equals("FUMBLE") || turnoverOnDowns;
       boolean canGoOutOfBounds = playResult.equals("COMPLETE")
               || playResult.equals("GAINED YARDS")
               || playResult.equals("LOST YARDS");
       outOfBounds = canGoOutOfBounds && !touchdown && !safety && (random.nextInt(100) < 22);
       //Changes scoreboard based on play result
        if (touchdown || safety) {
            if (touchdown) {
                if (possession.equals("HOME")) {
                    homeScore += 7;
                } else {
                    awayScore += 7;
                }
            } else {
                if (possession.equals("HOME")) {
                    awayScore += 2;
                } else {
                    homeScore += 2;
                }
            }
            if (possession.equals("HOME")) {
                possession = "AWAY";
            } else {
                possession = "HOME";
            }
            fieldPositionAfterPlay = 25;
            downAfterPlay = 1;
            distanceAfterPlay = 10;
        } else if (turnover) {
            if (possession.equals("HOME")) {
                possession = "AWAY";
            } else {
                possession = "HOME";
            }
            fieldPositionAfterPlay = 100 - endPosition;
            downAfterPlay = 1;
            distanceAfterPlay = Math.min(10, 100 - fieldPositionAfterPlay);
        } else if (yardsGained >= distance) {
            fieldPositionAfterPlay = endPosition;
            downAfterPlay = 1;
            distanceAfterPlay = Math.min(10, 100 - endPosition);
        } else {
            fieldPositionAfterPlay = endPosition;
            downAfterPlay = down + 1;
            distanceAfterPlay = distance - yardsGained;
        }
    }
    public int getGameClock() {
        return gameClock;
    }
    public int getHomeScore() {
        return homeScore;
    }
    public int getAwayScore() {
        return awayScore;
    }
    public String getPossession() {
        return possession;
    }
    public String getFieldPosition() {
        if (fieldPosition < 50) {
            return ("Own " + fieldPosition + "-yard line");
        } else if (fieldPosition == 50) {
            return "50-yard line";
        } else {
            return "Opponent's " + (100 - fieldPosition) + "-yard line";
        }
    }
    public int getDown() {
        return down;
    }
    public int getDistance() {
        return distance;
    }
    public String getPlayType() {
        return playType;
    }
    public String getPlayResult() {
        return playResult;
    }
    public int getYardsGained() {
        return yardsGained;
    }
    public int getTimeElapsed() {
        return timeElapsed;
    }
    public int getDistanceAfterPlay() {
        return distanceAfterPlay;
    }
    public int getDownAfterPlay() {
        return downAfterPlay;
    }
    public boolean isTurnover() {
        return turnover;
    }
    public int getFieldPositionAfterPlay() {
        return fieldPositionAfterPlay;
    }
    public boolean isTouchdown() {
        return touchdown;
    }
    public boolean isSafety() {
        return safety;
    }
    public boolean isOutOfBounds() {
        return outOfBounds;
    }
}