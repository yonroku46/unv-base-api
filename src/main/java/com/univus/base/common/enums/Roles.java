package com.univus.base.common.enums;

import lombok.Getter;

/**
 * ユーザーロール権限定義
 *
 * @author Univus
 */
@Getter
public enum Roles {
    CLIENT(1),
    PARTNER(2),
    MANAGER(8),
    ROOT(9);

    private final int value;

    Roles(int value) {
        this.value = value;
    }

    public static String findByValue(int value) {
        for (Roles role : Roles.values()) {
            if (role.getValue() == value) {
                return role.name();
            }
        }
        return null;
    }
}
