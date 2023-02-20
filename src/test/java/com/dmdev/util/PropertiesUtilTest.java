package com.dmdev.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesUtilTest {
    @ParameterizedTest
    @MethodSource("getPropertyArgumentsForDB")
    void checkGetConnection(String key, String expectedResult) {
        String actualResult = PropertiesUtil.get(key);

        assertEquals(expectedResult, actualResult);
    }

    static Stream<Arguments> getPropertyArgumentsForDB() {
        return Stream.of(
                Arguments.of("db.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"),
                Arguments.of("db.user", "sa"),
                Arguments.of("db.password", "")
        );
    }
}