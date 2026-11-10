import java.util.Random;
public class Game {
    private final RuleEngine re = new RuleEngine();
    private int score;
    private int questionsAsked;
    private final int totalScenarios = 5;
    private final Random random = new Random();
    private final int timeLimit;

    public Game(int timeLimit) {
        this.timeLimit = timeLimit;
    }
    public void startGame() {
        questionsAsked = 0;
        score = 0;
        IO.println("==================================");
        IO.println("          GAME START");
        IO.println("==================================");
        int scenarioNumber = 1;
        while (scenarioNumber <= totalScenarios) {
            Scenario scenario = new Scenario();
            if (scenario.isTouchdown() || scenario.isSafety()) {
                continue;
            }
            IO.println();
            IO.println("SCENARIO " + scenarioNumber + " OF " + totalScenarios);
            IO.println("----------------------------------");
            displayScenario(scenario);
            questionsAsked++;
            displayChoices();
            long startTime = System.currentTimeMillis();
            int answer = getAnswer();
            boolean tooSlow = isTooSlow(startTime);
            boolean shouldStop = re.shouldClockStop(scenario);
            boolean rightCall = (answer == 1) == shouldStop;
            if (tooSlow && rightCall) {
                IO.println("Right call, but too late.");
            } else if (rightCall) {
                IO.println("Correct! That's the right decision.");
                score++;
            } else {
                IO.print("Incorrect. The clock should ");
                if (shouldStop) {
                    IO.println("stop.");
                } else {
                    IO.println("keep running.");
                }
            }
            if (shouldStop) {
                IO.println();
                IO.println("When should the game clock restart?");
                IO.println("1. On the referee's ready signal");
                IO.println("2. On the snap");
                questionsAsked++;
                long restartStart = System.currentTimeMillis();
                int restartAnswer = getRestartAnswer();
                boolean restartTooSlow = (isTooSlow(restartStart));
                String correctAction = re.determineClockAction(scenario);
                int correctAnswer = -1;
                if (correctAction.equals("READY")) {
                    correctAnswer = 1;
                } else {
                    correctAnswer = 2;
                }
                boolean rightRestart = restartAnswer == correctAnswer;
                if (restartTooSlow && rightRestart) {
                    IO.println("Right call, but too late.");
                } else if (rightRestart) {
                    IO.println("Correct! That's the right decision");
                    score++;
                } else {
                    IO.print("Incorrect. The clock should restart ");
                    if (correctAnswer == 1) {
                        IO.println("on the referee's ready signal.");
                    } else {
                        IO.println("on the snap.");
                    }
                }
            }
            showTimeToNextSnap(scenario);
            pause(2000);
            scenarioNumber++;
        }
        IO.println();
        IO.println("==================================");
        IO.println("           GAME OVER");
        IO.println("==================================");
        IO.println("Your final score: " + score + "/" + questionsAsked);
    }
    private void displayScenario(Scenario scenario) {
        IO.println("Possession: " + scenario.getPossession());
        IO.print("Score: HOME " + scenario.getHomeScore());
        IO.println(" - AWAY " + scenario.getAwayScore());
        IO.println("Clock before snap: " + formatClock(scenario.getGameClock()));
        IO.println("Clock after play: " + formatClock(scenario.getGameClockAfterPlay()));
        IO.print("Down: " + scenario.getDown());
        IO.println(" and " + scenario.getDistance());
        IO.println("Field position: " + scenario.getFieldPosition());
        IO.println("Play type: " + scenario.getPlayType());
        IO.println("Play result: " + scenario.getPlayResult());
        IO.println("Yards gained: " + scenario.getYardsGained());
        if (scenario.isOutOfBounds()) {
            IO.println("Out of bounds: Yes");
        } else {
            IO.println("Out of bounds: No");
        }
    }
    private String formatClock(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    private boolean isTooSlow(long startTime) {
        int secondsTaken = (int) ((System.currentTimeMillis() - startTime) / 1000);
        if (secondsTaken > timeLimit) {
            IO.println("Too slow! You had " + timeLimit + " seconds. You took " + secondsTaken + " seconds.");
            return true;
        }
        IO.println("You took " + secondsTaken + " seconds out of " + timeLimit + ".");
        return false;
    }
    private void pause(int mls) {
        try {
            Thread.sleep(mls);
        } catch (InterruptedException e) {
        }
    }
    private void showTimeToNextSnap(Scenario scenario) {
        String action = re.determineClockAction(scenario);
        int playClockLeft = random.nextInt(25) + 1;
        int secondsUntilSnap = 40 - playClockLeft;
        int runoff;
        if (action.equals("RUNNING")) {
            runoff = secondsUntilSnap;
        } else if (action.equals("READY")) {
            int chainTime = random.nextInt(6) + 5;
            runoff = Math.max(0, secondsUntilSnap - chainTime);
        } else {
            runoff = 0;
        }
        int clockAtSnap = Math.max(0, scenario.getGameClockAfterPlay() - runoff);
        IO.println();
        IO.println("Play clock starts at 40.");
        IO.println("Offense snaps with " + playClockLeft + " seconds left on the play clock.");
        IO.println("Game clock: " + formatClock(scenario.getGameClockAfterPlay())
                + " --> " + formatClock(clockAtSnap) + " at the next snap");
        if (clockAtSnap == 0) {
            IO.println("Time expires before the snap. Ball Game.");
        }
    }
    private void displayChoices() {
        IO.println();
        IO.println("What should happen to the game clock?");
        IO.println("1. Stop the game clock");
        IO.println("2. Keep the game clock running");
        IO.println("You have " + timeLimit + " seconds.");
    }
    private int getAnswer() {
        while (true) {
            try {
                int answer = Integer.parseInt(IO.readln("Enter your choice (1-2)\n"));
                if (answer == 1 || answer == 2) {
                    return answer;
                }
            } catch (NumberFormatException e) {
            }
            IO.println("Invalid choice. Enter 1 or 2.");
        }
    }
    private int getRestartAnswer() {
        while (true) {
            try {
                int answer = Integer.parseInt(IO.readln("When should the game clock restart? (1-2)\n"));
                if (answer == 1 || answer == 2) {
                    return answer;
                }
            } catch (NumberFormatException e) {
            }
            IO.println("Invalid choice. Enter 1 or 2.");
        }
    }
}