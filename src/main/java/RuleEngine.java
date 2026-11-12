public class RuleEngine {
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
        return "RUNNING";
    }
}
