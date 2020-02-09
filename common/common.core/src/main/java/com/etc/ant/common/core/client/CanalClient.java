package com.etc.ant.common.core.client;

import org.springframework.context.ApplicationContextAware;

/**
 * @author
 * @version 1.0
 * @create 2018-11-09 16:07
 * @since JDK1.8
 **/
public interface CanalClient extends ApplicationContextAware {

    /**
     * 启动Canal客户端
     */
    void start();

    /**
     * 停止Canal客户端
     */
    void stop();
}
