public void main() {
    //WELCOME MESSAGE
    IO.println("==================================");
    IO.println("       NCAA-REF-SIMULATOR");
    IO.println("==================================");
    IO.println("Welcome to the NCAA Ref Simulator.");
    IO.println("Your job is to manage the game clock and play clock.");
    IO.println();
    IO.println("Get ready to make realistic referee decisions!");

    Game game = new Game();
    game.startGame();
}