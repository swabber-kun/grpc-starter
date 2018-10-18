package com.starter.rpc_common;

import lombok.Data;

/**
 * @author jibingkun
 * @date 2018/10/10.
 */
@Data
public class RpcResponse {

    private String requestId;

    private Exception error;

    private Object result;

    public boolean isError() {
        return error == null;
    }
}
