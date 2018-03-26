package uk.ac.reading.ps026478.playgroundwars;

/**
 * User class needs to exist as a requirement of the Firebase Real Time Database so that it can
 * marshall the data into a User type that can later on be referenced when printing high score lists.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 20/4/2017
 */

public class User {
    private String username;
    private long score;
    private static int idCounter;

    public User() {
    }

    public User(String username, long score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public long getScore() {
        return score;
    }
}
