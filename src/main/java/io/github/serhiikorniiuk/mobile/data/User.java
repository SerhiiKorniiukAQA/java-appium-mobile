package io.github.serhiikorniiuk.mobile.data;

public record User(String username, String password) {

    /** Valid user of Sauce Labs My Demo App. */
    public static final User STANDARD = new User("bob@example.com", "10203040");
    /** User that is locked out by design. */
    public static final User LOCKED = new User("alice@example.com", "10203040");
    public static final User UNKNOWN = new User("nobody@example.com", "wrong-password");
}
