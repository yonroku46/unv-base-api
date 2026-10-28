package com.univus.base;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 기본 컨텍스트 실행 테스트
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
