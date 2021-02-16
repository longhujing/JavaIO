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
public class IoMessageHeader implements Serializable {

    /**
     * 消息源地址
     */
    private String host;

    /**
     * 消息源端口
     */
    private Integer port;

    /**
     * 消息发送者
     */
    private String nickName;

}
