package uz.apextech.fbs.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "uz";

    public static final String PROFILE_PHONE_REGEX = "998\\d{9}";
    public static final String PROFILE_DEFAULT_LANGUAGE = "uz";

    private Constants() {}
}
