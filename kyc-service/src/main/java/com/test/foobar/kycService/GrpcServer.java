package com.test.foobar.kycService;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.HealthStatusManager;
import io.grpc.protobuf.services.ProtoReflectionService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class GrpcServer {
    private Server server;
    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void start() throws Exception {
        int port = 9009;
        ServerBuilder<?> builder = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create());
        builder.addService(new KYCService());
        addHealthService(builder);
        addReflectionService(builder);
        server = builder.build().start();
        log.info("GRPC Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    GrpcServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void addReflectionService(ServerBuilder<?> builder) {
        builder.addService(ProtoReflectionService.newInstance());
    }

    private void addHealthService(ServerBuilder<?> builder) {
        var healthStatusManager = new HealthStatusManager();
        builder.addService(healthStatusManager.getHealthService());
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}