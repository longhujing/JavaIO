package com.nanfeng.io.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nanfeng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IoMessage implements Serializable {

    /**
     * 消息头
     */
    private IoMessageHeader header;

    /**
     * 消息内容
     */
    private String content;

}
