public class Main {

    private static final boolean TEST_RUN = true;  // Turn this to false for pi screen, true for windows testing
    private Clock clock;

    public Main() {
        clock = new Clock(TEST_RUN);
    }

    public static void main(String[] args) {
        new Main();
    }

}
