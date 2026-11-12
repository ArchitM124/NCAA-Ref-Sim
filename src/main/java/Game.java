import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
public class Game {
    private final RuleEngine ruleEngine = new RuleEngine();
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
            //Scoring plays result in kickoffs so they are skipped and a new play is generated
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
            int answer = getChoice("Enter your choice (1-2)");
            boolean tooSlow = isTooSlow(startTime);
            boolean shouldStop = ruleEngine.shouldClockStop(scenario);
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
            //Asked when the clock should have stopped, even if player answered incorrectly.
            if (shouldStop) {
                IO.println();
                IO.println("When should the game clock restart?");
                IO.println("1. On the referee's ready signal");
                IO.println("2. On the snap");
                questionsAsked++;
                long restartStart = System.currentTimeMillis();
                int restartAnswer = getChoice("When should the game clock restart?(1-2)");
                boolean restartTooSlow = isTooSlow(restartStart);
                String correctAction = ruleEngine.determineClockAction(scenario);
                int correctAnswer;
                if (correctAction.equals("READY")) {
                    correctAnswer = 1;
                } else {
                    correctAnswer = 2;
                }
                boolean rightRestart = restartAnswer == correctAnswer;
                if (restartTooSlow && rightRestart) {
                    IO.println("Right call, but too late.");
                } else if (rightRestart) {
                    IO.println("Correct! That's the right decision.");
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
        IO.println();
        String name = IO.readln("Enter your name for the leaderboard:\n").trim();
        //Commas separate fields so names should not have commas or it would mess up the leaderboard
        name = name.replace(",", "");
        if (name.isEmpty()) {
            name = "Anonymous";
        }
        saveScore(name);
        showLeaderBoard();
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
    //The time is checked after the player answer so it doesn't cut them off.
    //Would be too complex to add a live countdown thread that would cut of, and not necessary for gameplay.
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
            //Should never happen as multiple threads arent called in this game
        }
    }
    //Each game is saved as one line. (name,correctAnswered,totalAsked,difficulty)
    private void saveScore(String name) {
        String difficulty;
        if (timeLimit == 10) {
            difficulty = "Hard";
        } else {
            difficulty = "Regular";
        }
        try (FileWriter fw = new FileWriter("leaderboard.txt", true)) {
            fw.write(name + "," + score + "," + questionsAsked + "," + difficulty + "\n");
        } catch (IOException e) {
            IO.println("Could not save score");
        }
    }
    //Ranked by percentage because games ask different numbers of questions.
    private int percent(String line) {
        String[] parts = line.split(",");
        int correct = Integer.parseInt(parts[1]);
        int total = Integer.parseInt(parts[2]);
        if (total == 0) {
            return 0;
        }
        return correct * 100 / total;
    }
    // Ranks by percentage answered correctly, then Hard over Regular, then whoever got the score first stays on top.
    private int compareLines(String a, String b) {
        int byPercent = Integer.compare(percent(b), percent(a));
        if (byPercent != 0) {
            return byPercent;
        }
        String difficultyA = a.split(",") [3];
        String difficultyB = b.split(",") [3];
        if (difficultyA.equals("Hard") && difficultyB.equals("Regular")) {
            return -1;
        }
        if (difficultyA.equals("Regular") && difficultyB.equals("Hard")) {
            return 1;
        }
        return 0;
    }
    private void showLeaderBoard() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("leaderboard.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank() && line.split(",").length == 4) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            IO.println("Couldn't load the leaderboard");
            return;
        }
        lines.sort((a, b) -> compareLines(a, b));
        IO.println();
        IO.println("========== LEADERBOARD ==========");
        for (int i = 0; i < Math.min(5, lines.size()); i++) {
            String[] parts = lines.get(i).split(",");
            IO.println((i + 1) + ". " + parts[0] + " " + parts[1] + "/" + parts[2] + " (" + percent(lines.get(i)) + "%) " + parts[3]);
        }

    }
    //Uses the correct call, not player's answer so all details remain accurate.
    //Play clock is always 40 because the scenarios do not include timeouts or penalties.
    private void showTimeToNextSnap(Scenario scenario) {
        String action = ruleEngine.determineClockAction(scenario);
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
    private int getChoice(String prompt) {
        while (true) {
            try {
                int choice = Integer.parseInt(IO.readln(prompt + "\n"));
                if (choice == 1 || choice == 2) {
                    return choice;
                }
            } catch (NumberFormatException e) {
            }
            IO.println("Invalid choice. Enter 1 or 2.");
        }
    }
}