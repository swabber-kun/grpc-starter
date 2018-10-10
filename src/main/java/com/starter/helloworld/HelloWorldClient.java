package com.starter.helloworld;

import com.starter.proto.GreeterGrpc;
import com.starter.proto.HelloReply;
import com.starter.proto.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author spuerKun
 * @date 2018/10/6.
 */
public class HelloWorldClient {

    private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());

    private final ManagedChannel channel;

    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public HelloWorldClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid needing certificates.
                .usePlaintext()
                .build());
    }

    private HelloWorldClient(ManagedChannel channel) {
        this.channel = channel;
        this.blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    private void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Say hello to server.
     */
    private void greet(String name) {
        logger.info("Will try to greet " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Greeting: " + response.getMessage());
    }

    public static void main(String[] args) throws Exception {
        HelloWorldClient client = new HelloWorldClient("localhost", 50051);
        try {
             /* Access a service running on the local machine on port 50051 */
            String user = "world";
            if (args.length > 0) {
                /* Use the arg as the name to greet if provided */
                user = args[0];
            }

            for(int i=0;i<5;i++){
                client.greet("world:"+i);
            }
        } finally {
            client.shutdown();
        }
    }
}
