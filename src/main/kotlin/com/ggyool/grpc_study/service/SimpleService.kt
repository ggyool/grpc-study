package com.ggyool.grpc_study.service

import com.ggyool.grpc_study.proto.HelloReply
import com.ggyool.grpc_study.proto.HelloRequest
import com.ggyool.grpc_study.proto.SimpleGrpc
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory

//@GrpcService
class SimpleService : SimpleGrpc.SimpleImplBase() {

    override fun sayHello(req: HelloRequest, responseObserver: StreamObserver<HelloReply>) {
        log.info("Hello {}", req.name)

        when {
            req.name.startsWith("error") -> {
                throw IllegalArgumentException("Bad name: ${req.name}")
            }

            req.name.startsWith("internal") -> {
                throw RuntimeException()
            }
        }

        val reply = HelloReply.newBuilder()
            .setMessage("Hello ==> ${req.name}")
            .build()

        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }

    override fun streamHello(req: HelloRequest, responseObserver: StreamObserver<HelloReply>) {
        log.info("Hello {}", req.name)

        for (count in 0 until 10) {
            val reply = HelloReply.newBuilder()
                .setMessage("Hello($count) ==> ${req.name}")
                .build()

            responseObserver.onNext(reply)

            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                responseObserver.onError(e)
                return
            }
        }

        responseObserver.onCompleted()
    }

    companion object {
        private val log = LoggerFactory.getLogger(SimpleService::class.java)
    }
}