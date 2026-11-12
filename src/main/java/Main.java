public void main() {
    IO.println("==================================");
    IO.println("       NCAA-REF-SIMULATOR");
    IO.println("==================================");
    IO.println("Welcome to the NCAA Ref Simulator.");
    IO.println("Your job is to manage the game clock and play clock.");
    IO.println();
    IO.println("Get ready to make realistic referee decisions!");
    IO.println();
    IO.println("Choose a difficulty:");
    IO.println("1. Regular (15 seconds per call)");
    IO.println("2. Hard (10 seconds per call)");
    int difficulty = getDifficulty();
    int timeLimit;
    if (difficulty == 1) {
        timeLimit = 15;
    } else {
        timeLimit = 10;
    }
    Game game = new Game(timeLimit);
    game.startGame();
}
public int getDifficulty() {
    while (true) {
        try {
            int diff = Integer.parseInt(IO.readln("Enter 1 or 2.\n"));
            if (diff == 1 || diff == 2) {
                return diff;
            }
        } catch (NumberFormatException e) {
        }
        IO.println("Invalid choice.");
    }
}