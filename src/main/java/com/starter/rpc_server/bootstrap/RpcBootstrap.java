package com.starter.rpc_server.bootstrap;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 服务器启动类
 *
 * @author jibingkun
 * @date 2018/10/18.
 */
public class RpcBootstrap {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-server.xml");
    }
}
