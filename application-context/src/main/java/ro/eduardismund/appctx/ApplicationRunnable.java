package ro.eduardismund.appctx;

/**
 * Interface for classes that can be run with command-line arguments.
 */
public interface ApplicationRunnable {

    /**
     * Runs the application with the given arguments.
     *
     * @param args Command-line arguments.
     */
    void run(String... args);
}
