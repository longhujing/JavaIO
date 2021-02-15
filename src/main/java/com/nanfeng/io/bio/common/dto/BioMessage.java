package com.nanfeng.io.bio.common.dto;

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
public class BioMessage implements Serializable {

    /**
     * 消息头
     */
    private BioMessageHeader header;

    /**
     * 消息内容
     */
    private String content;

}
