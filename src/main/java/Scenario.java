import java.util.Random;

//Generates one random play in the last two minutes of the 4th quarter.
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
    private int downAfterPlay;
    private boolean turnover;
    private boolean touchdown;
    private boolean safety;
    private boolean outOfBounds;
    private int gameClockAfterPlay;

    public Scenario() {
        Random random = new Random();
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
        //Makes sure the distance isn't further than the goal line.
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
        int timeElapsed;
        if (playResult.equals("INCOMPLETE")) {
            timeElapsed = 0;
        } else if (playType.equals("PASS")) {
            timeElapsed = random.nextInt(11) + 5;
        } else {
            timeElapsed = random.nextInt(16) + 5;
        }
        //Always leaves atleast 1 second. A play that ends the game has no clock decision.
        timeElapsed = Math.min(timeElapsed, gameClock - 1);
        gameClockAfterPlay = Math.max(0, gameClock - timeElapsed);
        //Gains of short plays are more likely to simulate more realistic plays
        if (playResult.equals("COMPLETE") || playResult.equals("GAINED YARDS")) {
            int roll = random.nextInt(100);
            int gain;
            if (playType.equals("RUN")) {
                if (roll < 70) {
                    gain = random.nextInt(6) + 1;
                } else if (roll < 95) {
                    gain = random.nextInt(9) + 7;
                } else {
                    gain = random.nextInt(26) + 16;
                }
            } else {
                if (roll < 60) {
                    gain = random.nextInt(10) + 3;
                } else if (roll < 90) {
                    gain = random.nextInt(13) + 13;
                } else {
                    gain = random.nextInt(26) + 26;
                }
            }
            yardsGained = Math.min(gain, yardsToGoal);
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
        //Only plays where an offensive player has the ball at the end of the play can go out of bounds.
        boolean canGoOutOfBounds = playResult.equals("COMPLETE")
                || playResult.equals("GAINED YARDS")
                || playResult.equals("LOST YARDS");
        outOfBounds = canGoOutOfBounds && !touchdown && !safety && (random.nextInt(100) < 22);
        //Scores and turnovers start a new set of downs. Rule engine checks for turnovers first.
        if (touchdown || safety || turnover || yardsGained >= distance) {
            downAfterPlay = 1;
        } else {
            downAfterPlay = down + 1;
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

    public int getGameClockAfterPlay() {
        return gameClockAfterPlay;
    }

    public int getDownAfterPlay() {
        return downAfterPlay;
    }

    public boolean isTurnover() {
        return turnover;
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