/*
 * Copyright 2019 IBM All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.shim;

import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.hyperledger.fabric.metrics.Metrics;
import org.hyperledger.fabric.protos.peer.ChaincodeMessage;
import org.hyperledger.fabric.shim.chaincode.EmptyChaincode;
import org.hyperledger.fabric.traces.Traces;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ChaincodeBaseTest {
    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void testNewSuccessResponseEmpty() {
        final org.hyperledger.fabric.shim.Chaincode.Response response = ResponseUtils.newSuccessResponse();
        assertThat(response.getStatus()).withFailMessage("Response status is incorrect").isEqualTo(org.hyperledger.fabric.shim.Chaincode.Response.Status.SUCCESS);
        assertThat(response.getMessage()).withFailMessage("Response message in not null").isNull();
        assertThat(response.getPayload()).withFailMessage("Response payload in not null").isNull();
    }

    @Test
    public void testNewSuccessResponseWithMessage() {
        final org.hyperledger.fabric.shim.Chaincode.Response response = ResponseUtils.newSuccessResponse("Simple message");
        assertThat(response.getStatus()).withFailMessage("Response status is incorrect").isEqualTo(org.hyperledger.fabric.shim.Chaincode.Response.Status.SUCCESS);
        assertThat(response.getMessage()).withFailMessage("Response message is not correct").isEqualTo("Simple message");
        assertThat(response.getPayload()).withFailMessage("Response payload in not null").isNull();
    }

    @Test
    public void testNewSuccessResponseWithPayload() {
        final org.hyperledger.fabric.shim.Chaincode.Response response = ResponseUtils.newSuccessResponse("Simple payload".getBytes(Charset.defaultCharset()));
        assertThat(response.getStatus()).withFailMessage("Response status is incorrect").isEqualTo(org.hyperledger.fabric.shim.Chaincode.Response.Status.SUCCESS);
        assertThat(response.getMessage()).withFailMessage("Response message in not null").isNull();
        assertThat(response.getPayload()).withFailMessage("Response payload is incorrect").isEqualTo("Simple payload".getBytes(Charset.defaultCharset()));
    }

    @Test
    public void testNewSuccessResponseWithMessageAndPayload() {
        final org.hyperledger.fabric.shim.Chaincode.Response response = ResponseUtils.newSuccessResponse("Simple message",
                "Simple payload".getBytes(Charset.defaultCharset()));
        assertThat(response.getStatus()).withFailMessage("Response status is incorrect").isEqualTo(org.hyperledger.fabric.shim.Chaincode.Response.Status.SUCCESS);
        assertThat(response.getMessage()).withFailMessage("Response message is not correct").isEqualTo("Simple message");
        assertThat(response.getPayload()).withFailMessage("Response payload is incorrect").isEqualTo("Simple payload".getBytes(Charset.defaultCharset()));
    }

    @Test
    public void testNewErrorResponseEmpty() {
        final org.hyperledger.fabric.shim.Chaincode.Response response = ResponseUtils.newErrorResponse();
        assertThat(response.getStatus()).withFailMessage("Response status is incorrect").isEqualTo(org.hyperledger.fabric.shim.Chaincode.Response.Status.INTERNAL_SERVER_ERROR);
        assertThat(response.getMessage()).withFailMessage("Response message in not null").isNull();
        assertThat(response.getPayload()).withFailMessage("Response payload in not null").isNull();
    }

    @Test
    public void testNewErrorResponseWithMessage() {
        final org.hyperledger.fabric.shim.Chaincode.Response response = ResponseUtils.newErrorResponse("Simple message");
        assertThat(response.getStatus()).withFailMessage("Response status is incorrect").isEqualTo(org.hyperledger.fabric.shim.Chaincode.Response.Status.INTERNAL_SERVER_ERROR);
        assertThat(response.getMessage()).withFailMessage("Response message is not correct").isEqualTo("Simple message");
        assertThat(response.getPayload()).withFailMessage("Response payload is not null").isNull();
    }

    @Test
    public void testNewErrorResponseWithPayload() {
        final org.hyperledger.fabric.shim.Chaincode.Response response = ResponseUtils.newErrorResponse("Simple payload".getBytes(Charset.defaultCharset()));
        assertThat(response.getStatus()).withFailMessage("Response status is incorrect").isEqualTo(org.hyperledger.fabric.shim.Chaincode.Response.Status.INTERNAL_SERVER_ERROR);
        assertThat(response.getMessage()).withFailMessage("Response message is not null").isNull();
        assertThat(response.getPayload()).withFailMessage("Response payload is incorrect").isEqualTo("Simple payload".getBytes(Charset.defaultCharset()));
    }

    @Test
    public void testNewErrorResponseWithMessageAndPayload() {
        final org.hyperledger.fabric.shim.Chaincode.Response response = ResponseUtils.newErrorResponse("Simple message",
                "Simple payload".getBytes(Charset.defaultCharset()));
        assertThat(response.getStatus()).withFailMessage("Response status is incorrect").isEqualTo(org.hyperledger.fabric.shim.Chaincode.Response.Status.INTERNAL_SERVER_ERROR);
        assertThat(response.getMessage()).withFailMessage("Response message is not correct").isEqualTo("Simple message");
        assertThat(response.getPayload()).withFailMessage("Response payload is incorrect").isEqualTo("Simple payload".getBytes(Charset.defaultCharset()));
    }

    @Test
    public void testNewErrorResponseWithException() {
        final org.hyperledger.fabric.shim.Chaincode.Response response = ResponseUtils.newErrorResponse(new Exception("Simple exception"));
        assertThat(response.getStatus()).withFailMessage("Response status is incorrect").isEqualTo(org.hyperledger.fabric.shim.Chaincode.Response.Status.INTERNAL_SERVER_ERROR);
        assertThat(response.getMessage()).withFailMessage("Response message is not correct").isEqualTo("Unexpected error");
        assertThat(response.getPayload()).withFailMessage("Response payload is not null").isNull();
    }

    @Test
    public void testNewErrorResponseWithChaincodeException() {
        final org.hyperledger.fabric.shim.Chaincode.Response response = ResponseUtils.newErrorResponse(new ChaincodeException("Chaincode exception"));
        assertThat(response.getStatus()).withFailMessage("Response status is incorrect").isEqualTo(org.hyperledger.fabric.shim.Chaincode.Response.Status.INTERNAL_SERVER_ERROR);
        assertThat(response.getMessage()).withFailMessage("Response message is not correct").isEqualTo("Chaincode exception");
        assertThat(response.getPayload()).withFailMessage("Response payload is not null").isNull();
    }

    @Test
    public void testOptions() throws Exception {
        final ChaincodeBase cb = new EmptyChaincode();

        assertThat(cb.getHost()).withFailMessage("Host incorrect").isEqualTo(ChaincodeBase.DEFAULT_HOST);
        assertThat(cb.getPort()).withFailMessage("Port incorrect").isEqualTo(ChaincodeBase.DEFAULT_PORT);
        assertThat(cb.isTlsEnabled()).withFailMessage("TLS should not be enabled").isFalse();

        environmentVariables.set("CORE_CHAINCODE_ID_NAME", "mycc");
        environmentVariables.set("CORE_PEER_ADDRESS", "localhost:7052");
        environmentVariables.set("CORE_PEER_TLS_ENABLED", "true");
        environmentVariables.set("CORE_TLS_CLIENT_CERT_PATH", "non_exist_path3");
        environmentVariables.set("CORE_TLS_CLIENT_KEY_PATH", "non_exist_path2");
        environmentVariables.set("CORE_PEER_TLS_ROOTCERT_FILE", "non_exist_path1");
        cb.processEnvironmentOptions();
        assertThat(cb.getId()).withFailMessage("CCId incorrect").isEqualTo("mycc");
        assertThat(cb.getHost()).withFailMessage("Host incorrect").isEqualTo("localhost");
        assertThat(cb.getPort()).withFailMessage("Port incorrect").isEqualTo(7052);
        assertThat(cb.isTlsEnabled()).withFailMessage("TLS should be enabled").isTrue();
        assertThat(cb.getTlsClientRootCertPath()).withFailMessage("Root certificate file").isEqualTo("non_exist_path1");
        assertThat(cb.getTlsClientKeyPath()).withFailMessage("Client key file").isEqualTo("non_exist_path2");
        assertThat(cb.getTlsClientCertPath()).withFailMessage("Client certificate file").isEqualTo("non_exist_path3");

        environmentVariables.set("CORE_PEER_ADDRESS", "localhost1");
        cb.processEnvironmentOptions();
        assertThat(cb.getHost()).withFailMessage("Host incorrect").isEqualTo("localhost");
        assertThat(cb.getPort()).withFailMessage("Port incorrect").isEqualTo(7052);

        assertThatCode(cb::validateOptions).withFailMessage("Wrong arguments").doesNotThrowAnyException();

        cb.processCommandLineOptions(new String[] {"-i", "mycc1", "--peerAddress", "localhost.org:7053"});
        assertThat(cb.getId()).withFailMessage("CCId incorrect").isEqualTo("mycc1");
        assertThat(cb.getHost()).withFailMessage("Host incorrect").isEqualTo("localhost.org");
        assertThat(cb.getPort()).withFailMessage("Port incorrect").isEqualTo(7053);

        assertThatCode(cb::validateOptions).withFailMessage("Wrong arguments").doesNotThrowAnyException();

        cb.processCommandLineOptions(new String[] {"-i", "mycc1", "--peerAddress", "localhost1.org.7054"});
        assertThat(cb.getHost()).withFailMessage("Host incorrect").isEqualTo("localhost.org");
        assertThat(cb.getPort()).withFailMessage("Port incorrect").isEqualTo(7053);
    }

    @Test
    public void testUnsetOptionId() {
        final ChaincodeBase cb = new EmptyChaincode();
        assertThatThrownBy(cb::validateOptions)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("The chaincode id must be specified");
    }

    @Test
    public void testUnsetOptionClientCertPath() {
        final ChaincodeBase cb = new EmptyChaincode();
        environmentVariables.set("CORE_CHAINCODE_ID_NAME", "mycc");
        environmentVariables.set("CORE_PEER_TLS_ENABLED", "true");
        cb.processEnvironmentOptions();
        assertThatThrownBy(cb::validateOptions)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Client key certificate chain");
    }

    @Test
    public void testUnsetOptionClientKeyPath() {
        final ChaincodeBase cb = new EmptyChaincode();
        environmentVariables.set("CORE_CHAINCODE_ID_NAME", "mycc");
        environmentVariables.set("CORE_PEER_TLS_ENABLED", "true");
        environmentVariables.set("CORE_TLS_CLIENT_CERT_PATH", "non_exist_path3");
        cb.processEnvironmentOptions();
        cb.validateOptions();
        assertThatThrownBy(cb::validateOptions)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Client key (");
    }

    @Test
    @Disabled
    public void testNewChannelBuilder() throws Exception {
        final ChaincodeBase cb = new EmptyChaincode();

        environmentVariables.set("CORE_CHAINCODE_ID_NAME", "mycc");
        environmentVariables.set("CORE_PEER_ADDRESS", "localhost:7052");
        environmentVariables.set("CORE_PEER_TLS_ENABLED", "true");
        environmentVariables.set("CORE_PEER_TLS_ROOTCERT_FILE", "src/test/resources/ca.crt");
        environmentVariables.set("CORE_TLS_CLIENT_KEY_PATH", "src/test/resources/client.key.enc");
        environmentVariables.set("CORE_TLS_CLIENT_CERT_PATH", "src/test/resources/client.crt.enc");

        cb.processEnvironmentOptions();
        cb.validateOptions();
        assertThat(cb.newChannelBuilder()).isInstanceOf(ManagedChannelBuilder.class);
    }

    @Test
    public void testInitializeLogging() {
        final ChaincodeBase cb = new EmptyChaincode();

        cb.processEnvironmentOptions();
        cb.initializeLogging();
        assertThat(Logger.getLogger("org.hyperledger.fabric.shim").getLevel())
                .withFailMessage("Wrong log level for org.hyperledger.fabric.shim")
                .isEqualTo(Level.INFO);
        assertThat(Logger.getLogger(cb.getClass().getPackage().getName()).getLevel())
                .withFailMessage("Wrong log level for " + cb.getClass().getPackage().getName())
                .isEqualTo(Level.INFO);

        setLogLevelForChaincode(environmentVariables, cb, "WRONG", "WRONG");
        assertThat(Logger.getLogger("org.hyperledger.fabric.shim").getLevel())
                .withFailMessage("Wrong log level for org.hyperledger.fabric.shim")
                .isEqualTo(Level.INFO);
        assertThat(Logger.getLogger(cb.getClass().getPackage().getName()).getLevel())
                .withFailMessage("Wrong log level for " + cb.getClass().getPackage().getName())
                .isEqualTo(Level.INFO);

        setLogLevelForChaincode(environmentVariables, cb, "DEBUG", "NOTICE");
        assertThat(Logger.getLogger("org.hyperledger.fabric.shim").getLevel())
                .withFailMessage("Wrong log level for org.hyperledger.fabric.shim")
                .isEqualTo(Level.FINEST);
        assertThat(Logger.getLogger(cb.getClass().getPackage().getName()).getLevel())
                .withFailMessage("Wrong log level for " + cb.getClass().getPackage().getName())
                .isEqualTo(Level.CONFIG);

        setLogLevelForChaincode(environmentVariables, cb, "INFO", "WARNING");
        assertThat(Logger.getLogger("org.hyperledger.fabric.shim").getLevel())
                .withFailMessage("Wrong log level for org.hyperledger.fabric.shim")
                .isEqualTo(Level.INFO);
        assertThat(Logger.getLogger(cb.getClass().getPackage().getName()).getLevel())
                .withFailMessage("Wrong log level for " + cb.getClass().getPackage().getName())
                .isEqualTo(Level.WARNING);

        setLogLevelForChaincode(environmentVariables, cb, "CRITICAL", "ERROR");
        assertThat(Logger.getLogger("org.hyperledger.fabric.shim").getLevel())
                .withFailMessage("Wrong log level for org.hyperledger.fabric.shim")
                .isEqualTo(Level.SEVERE);
        assertThat(Logger.getLogger(cb.getClass().getPackage().getName()).getLevel())
                .withFailMessage("Wrong log level for " + cb.getClass().getPackage().getName())
                .isEqualTo(Level.SEVERE);
    }

    @Test
    public void testStartFailsWithoutValidOptions() {
        final String[] args = new String[0];
        final ChaincodeBase cb = new EmptyChaincode();

        Handler mockHandler = Mockito.mock(Handler.class);
        ArgumentCaptor<LogRecord> argumentCaptor = ArgumentCaptor.forClass(LogRecord.class);
        Logger logger = Logger.getLogger("org.hyperledger.fabric.shim.ChaincodeBase");
        logger.addHandler(mockHandler);
        cb.start(args);

        Mockito.verify(mockHandler, Mockito.atLeast(1)).publish(argumentCaptor.capture());
        LogRecord lr = argumentCaptor.getValue();
        String msg = lr.getMessage();

        assertThat(msg).doesNotContain("java.lang.NullPointerException");
        assertThat(msg).doesNotContain(
            "The chaincode id must be specified using either the -i or --i command line options or the CORE_CHAINCODE_ID_NAME environment variable.");
    }

    public static void setLogLevelForChaincode(final EnvironmentVariables environmentVariables, final ChaincodeBase cb, final String shimLevel,
            final String chaincodeLevel) {
        environmentVariables.set(ChaincodeBase.CORE_CHAINCODE_LOGGING_SHIM, shimLevel);
        environmentVariables.set(ChaincodeBase.CORE_CHAINCODE_LOGGING_LEVEL, chaincodeLevel);
        cb.processEnvironmentOptions();
        cb.initializeLogging();
    }

    @Test
    public void connectChaincodeBase() throws IOException {
        final ChaincodeBase cb = new EmptyChaincode();

        environmentVariables.set("CORE_CHAINCODE_ID_NAME", "mycc");
        environmentVariables.set("CORE_PEER_ADDRESS", "localhost:7052");
        environmentVariables.set("CORE_PEER_TLS_ENABLED", "false");

        cb.processEnvironmentOptions();
        cb.validateOptions();

        final Properties props = cb.getChaincodeConfig();
        Metrics.initialize(props);
        Traces.initialize(props);

        cb.connectToPeer(new StreamObserver<ChaincodeMessage>() {
            @Override
            public void onNext(final ChaincodeMessage value) {

            }

            @Override
            public void onError(final Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        });

        environmentVariables.clear("CORE_CHAINCODE_ID_NAME", "CORE_PEER_ADDRESS", "CORE_PEER_TLS_ENABLED");
    }

    @Test
    public void connectChaincodeBaseNull() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    final ChaincodeBase cb = new EmptyChaincode();
                    cb.connectToPeer(null);
                }
        );
    }
}
