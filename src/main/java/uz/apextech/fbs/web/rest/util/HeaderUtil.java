package uz.apextech.fbs.web.rest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import uz.apextech.fbs.config.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class HeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(HeaderUtil.class);

    private HeaderUtil() {
    }

    public static String resolveLanguage(HttpServletRequest request) {
        String language = request.getHeader("Language");
        if (StringUtils.hasText(language)) {
            return language;
        }
        return Constants.PROFILE_DEFAULT_LANGUAGE;
    }

    public static Optional<String> resolveClientDevice(HttpServletRequest request) {
        String clientDevice = request.getHeader("Client-Device");
        if (StringUtils.hasText(clientDevice)) {
            return of(clientDevice);
        }
        return empty();
    }

    public static Optional<String> resolveAppUuid(HttpServletRequest request) {
        String clientDevice = request.getHeader("App-Uuid");
        if (StringUtils.hasText(clientDevice)) {
            return of(clientDevice);
        }
        return empty();
    }
}
