package com.starter.rpc_client;

import com.starter.rpc_common.RpcRequest;
import com.starter.rpc_common.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author spuerKun
 * @date 2018/10/17.
 */
public class RpcProxy {

    private String serverAddress;

    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create(Class<?> interfaceClass) {

        Object obj = Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        // 创建并初始化 RPC 请求
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        // 发现服务
                        if (serviceDiscovery != null) {
                            serverAddress = serviceDiscovery.discover();
                        }

                        if (serverAddress != null) {
                            String[] array = serverAddress.split(":");
                            String host = array[0];
                            int port = Integer.parseInt(array[1]);

                            // 初始化 RPC 客户端
                            RpcClient client = new RpcClient(host, port);

                            // 通过 RPC客户端发送RPC请求并获取RPC响应
                            RpcResponse response = client.sendMessage(request);
                            if (response.isError()) {
                                throw response.getError();
                            } else {
                                return response.getResult();
                            }
                        }
                        throw new RuntimeException();
                    }
                });
        return (T) obj;
    }
}
