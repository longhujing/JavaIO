package com.nanfeng.io.bio.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author nanfeng
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class BioException extends RuntimeException {

    private String message;

}
