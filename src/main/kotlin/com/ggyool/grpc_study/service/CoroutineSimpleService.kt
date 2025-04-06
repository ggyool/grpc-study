package com.ggyool.grpc_study.service

import com.ggyool.grpc_study.proto.HelloReply
import com.ggyool.grpc_study.proto.HelloRequest
import com.ggyool.grpc_study.proto.SimpleGrpcKt
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class CoroutineSimpleService : SimpleGrpcKt.SimpleCoroutineImplBase() {

    override suspend fun sayHello(request: HelloRequest): HelloReply {
        log.info("Hello {}", request.name)

        when {
            request.name.startsWith("error") -> throw IllegalArgumentException("Bad name: ${request.name}")
            request.name.startsWith("internal") -> throw RuntimeException()
        }

        return HelloReply.newBuilder()
            .setMessage("Hello ==> ${request.name}")
            .build()
    }

    override fun streamHello(request: HelloRequest): Flow<HelloReply> = flow {
        log.info("Streaming Hello {}", request.name)

        for (i in 0 until 10) {
            emit(
                HelloReply.newBuilder()
                    .setMessage("Hello($i) ==> ${request.name}")
                    .build()
            )
            delay(1000)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}