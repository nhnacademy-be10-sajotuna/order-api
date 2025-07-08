package shop.sajotuna.order.orders.domain;

import org.apache.commons.lang.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderNumberGenerator {
    private static final String PREFIX = "ORD-";
    public static final String DATETIME_PATTERN = "yyyyMMdd";
    public static final int RANDOM_COUNT = 16;

    public static String generate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
        String now = dtf.format(LocalDateTime.now());

        String randomString = RandomStringUtils.randomAlphanumeric(RANDOM_COUNT);

        return PREFIX + now + randomString;
    }

}
