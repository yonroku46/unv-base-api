package com.univus.base;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 基本コンテキスト起動テスト
 *
 * @author Univus
 */
@ActiveProfiles("dev")
@SpringBootTest
class MainApplicationTests {

    @Test
    void contextLoads() {
        assertTrue(true);
    }
}
