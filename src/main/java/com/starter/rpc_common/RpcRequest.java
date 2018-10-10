package com.starter.rpc_common;

import lombok.Data;

/**
 * @author jibingkun
 * @date 2018/10/10.
 */
@Data
public class RpcRequest {

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;
}
