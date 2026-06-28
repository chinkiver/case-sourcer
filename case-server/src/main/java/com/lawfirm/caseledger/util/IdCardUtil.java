package com.lawfirm.caseledger.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * 中国大陆 18 位身份证号工具：格式校验、校验位、提取出生日期/性别/年龄。
 */
public final class IdCardUtil {

    private static final Pattern PATTERN = Pattern.compile(
            "^[1-9]\\d{5}(?:18|19|20)\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");

    private static final int[] WEIGHT = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    private static final char[] CHECK_CODE = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    private static final DateTimeFormatter BIRTH_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private IdCardUtil() {
    }

    public static boolean isValid(String idCard) {
        if (idCard == null || !PATTERN.matcher(idCard).matches()) {
            return false;
        }
        return verifyChecksum(idCard);
    }

    private static boolean verifyChecksum(String idCard) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += Character.digit(idCard.charAt(i), 10) * WEIGHT[i];
        }
        char expected = CHECK_CODE[sum % 11];
        char actual = Character.toUpperCase(idCard.charAt(17));
        return expected == actual;
    }

    public static LocalDate getBirthDate(String idCard) {
        if (!isValid(idCard)) {
            throw new IllegalArgumentException("身份证号不合法");
        }
        try {
            return LocalDate.parse(idCard.substring(6, 14), BIRTH_FMT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("身份证号出生日期无效");
        }
    }

    public static String getGender(String idCard) {
        if (!isValid(idCard)) {
            throw new IllegalArgumentException("身份证号不合法");
        }
        // 第 17 位（索引 16）：奇数男、偶数女
        return Character.digit(idCard.charAt(16), 10) % 2 == 1 ? "男" : "女";
    }

    public static int getAge(String idCard) {
        return getAge(idCard, LocalDate.now());
    }

    public static int getAge(String idCard, LocalDate now) {
        LocalDate birth = getBirthDate(idCard);
        return Period.between(birth, now).getYears();
    }
}