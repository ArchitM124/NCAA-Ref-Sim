public class Game {
    private final RuleEngine re = new RuleEngine();
    private int score;
    private int questionsAsked;
    private final int totalScenarios = 5;

    public void startGame() {
        questionsAsked = 0;
        score = 0;
        IO.println("==================================");
        IO.println("          GAME START");
        IO.println("==================================");
        for (int i = 1; i <= totalScenarios; i++) {
            Scenario scenario = new Scenario();
            if (scenario.isTouchdown() || scenario.isSafety()) {
                IO.println("Scoring play! Simming kickoff.");
                continue;
            }
            IO.println();
            IO.println("SCENARIO " + i + " OF " + totalScenarios);
            IO.println("----------------------------------");
            displayScenario(scenario);
            questionsAsked++;
            displayChoices();
            int answer = getAnswer();
            boolean shouldStop = re.shouldClockStop(scenario);
            if ((answer == 1) == shouldStop) {
                IO.println("Correct! The Clock ruling is correct.");
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
                int restartAnswer = getRestartAnswer();
                String correctAction = re.determineClockAction(scenario);
                int correctAnswer = -1;
                if (correctAction.equals("READY")) {
                    correctAnswer = 1;
                } else {
                    correctAnswer = 2;
                }
                if (restartAnswer == correctAnswer) {
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
    private void displayChoices() {
        IO.println();
        IO.println("What should happen to the game clock?");
        IO.println("1. Stop the game clock");
        IO.println("2. Keep the game clock running");
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