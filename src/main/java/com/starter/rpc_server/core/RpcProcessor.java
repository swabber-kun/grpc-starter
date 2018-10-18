package com.starter.rpc_server.core;

import com.starter.rpc_common.RpcRequest;
import com.starter.rpc_common.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务逻辑的实际处理
 *
 * @author jibingkun
 * @date 2018/10/17.
 */
public class RpcProcessor extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProcessor.class);

    private final ConcurrentHashMap<String, Object> PROCESS_MAP;

    public RpcProcessor(ConcurrentHashMap<String, Object> processMap) {
        this.PROCESS_MAP = processMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());

        try {
            Object result = process(rpcRequest);
            response.setResult(result);
        } catch (Exception ex) {
            response.setError(ex);
        }

        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object process(RpcRequest rpcRequest) throws InvocationTargetException {

        String className = rpcRequest.getClassName();
        Object serviceBean = PROCESS_MAP.get(className);

        // 接口类
        Class<?> serviceClass = serviceBean.getClass();

        // 方法名
        String methodName = rpcRequest.getMethodName();

        // 参数类型
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();

        // 参数
        Object[] parameters = rpcRequest.getParameters();

        // 使用反射执行对应的方法
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        Object obj = serviceFastMethod.invoke(serviceBean, parameters);
        return obj;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }
}
