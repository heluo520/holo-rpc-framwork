package com.holo.api;

import lombok.*;

import java.io.Serializable;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-25
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Hello implements Serializable {
    private String message;
    private String description;
}
