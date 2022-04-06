package uz.apextech.fbs.service;

import org.apache.commons.lang3.RandomStringUtils;

public final class RandomUtil {

    private static final int DEF_COUNT = 20;

    private static final int ACCESS_TOKEN_DEF_COUNT = 64;

    private static final int DEF_NAME_COUNT = 10;

    private RandomUtil() {
    }

    /**
     * Generate a password.
     *
     * @return the generated password
     */
    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
    }

    /**
     * Generate an activation key.
     *
     * @return the generated activation key
     */
    public static String generateActivationKey() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }

    /**
     * Generate a reset key.
     *
     * @return the generated reset key
     */
    public static String generateResetKey() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }

    public static String generateAccessToken() {
        return RandomStringUtils.randomAlphanumeric(ACCESS_TOKEN_DEF_COUNT);
    }

    public static String generateImageName() {
        return RandomStringUtils.randomAlphabetic(DEF_NAME_COUNT);
    }
}
