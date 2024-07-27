package com.holo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-25
 * @Description:
 */
@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnum {
    SUCCESS(200, "The remote call is successful"),
    FAIL(500, "The remote call is fail");
    private final int code;

    private final String message;

}
