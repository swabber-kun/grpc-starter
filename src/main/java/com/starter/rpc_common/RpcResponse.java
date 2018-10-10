package com.starter.rpc_common;

import lombok.Data;

/**
 * @author jibingkun
 * @date 2018/10/10.
 */
@Data
public class RpcResponse {

    private String requestId;

    private Throwable error;

    private Object result;

}
