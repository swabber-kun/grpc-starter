package com.starter.rpc_server.simple.impl;

import com.starter.rpc_server.core.RpcService;
import com.starter.rpc_server.simple.HelloService;

/**
 * 指定远程接口
 *
 * @author jibingkun
 * @date 2018/10/18.
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }
}
