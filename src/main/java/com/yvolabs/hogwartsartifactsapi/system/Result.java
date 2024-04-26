package com.yvolabs.hogwartsartifactsapi.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yvonne N
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {
    private boolean flag; // Two values: true means success, false means not success

    private Integer code; // Status code. e.g., 200, 400

    private String message; // Response message

    private Object data; // The response payload

}
