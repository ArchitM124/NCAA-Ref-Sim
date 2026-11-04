public class RuleEngine {
    public String determineRuling(Scenario scenario) {
        if (scenario.isTouchdown()) {
            return "TOUCHDOWN";
        }
        if (scenario.isSafety()) {
            return "SAFETY";
        }
        if (scenario.isTurnover()) {
            return "TURNOVER";
        }
        if (scenario.getDownAfterPlay() == 1) {
            return "FIRST DOWN";
        }
        if (scenario.getPlayResult().equals("INCOMPLETE")) {
            return "INCOMPLETE PASS";
        }
        if (scenario.getPlayResult().equals("COMPLETE")) {
            return "COMPLETE PASS";
        }
        if (scenario.getPlayResult().equals("GAINED YARDS")) {
            return "RUN";
        }
        if (scenario.getPlayResult().equals("LOST YARDS")) {
            return "LOSS OF YARDS";
        }
        return "PLAY CONTINUES";
    }
    public boolean shouldClockStop(Scenario scenario) {
        if (scenario.isTouchdown() || scenario.isSafety()) {
            return false;
        }
        if (scenario.getPlayResult().equals("INCOMPLETE")) {
            return true;
        }
        if (scenario.isOutOfBounds() || scenario.isTurnover()) {
            return true;
        }
        if (scenario.getDownAfterPlay() == 1) {
            return true;
        }
        return false;
    }
    public String determineClockAction(Scenario scenario) {
        //Scoring plays are skipped.
        if (scenario.isSafety() || scenario.isTouchdown()) {
            return "SKIP";
        }
        //Clock starts on the next snap.
        if (scenario.getPlayResult().equals("INCOMPLETE") || scenario.isOutOfBounds() || scenario.isTurnover()) {
            return "SNAP";
        }
        //Clock starts once ref signals ready.
        if (scenario.getDownAfterPlay() == 1) {
            return "READY";
        }
        //Clock keeps running.
        return "RUNNING";
    }
}
