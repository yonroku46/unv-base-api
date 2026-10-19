package com.univus.base.common.id;

import com.github.ksuid.Ksuid;
import org.springframework.stereotype.Component;

/**
 * KSUID (K-Sortable Unique Identifier) 生成ユーティリティ
 *
 * @author Univus
 */
@Component
public class KsuidGenerator {

    private KsuidGenerator() {}

    public static String createId() {
        return Ksuid.newKsuid().toString();
    }
}
