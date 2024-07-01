/*
 * Copyright 2019 IBM DTCC All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.contract.execution.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.contract.execution.InvocationRequest;
import org.hyperledger.fabric.shim.ChaincodeStub;

public class ContractInvocationRequest implements InvocationRequest {
    private final String namespace;
    private final String method;
    private final List<byte[]> args;

    private static final Log LOG = LogFactory.getLog(ContractInvocationRequest.class);

    /**
     * @param context
     */
    public ContractInvocationRequest(final ChaincodeStub context) {
        final String func = context.getStringArgs().isEmpty() ? null : context.getStringArgs().get(0);
        final String[] funcParts = func.split(":");
        LOG.debug(func);
        if (funcParts.length == 2) {
            namespace = funcParts[0];
            method = funcParts[1];
        } else {
            namespace = DEFAULT_NAMESPACE;
            method = funcParts[0];
        }

        args = context.getArgs().stream().skip(1).collect(Collectors.toList());
        if (LOG.isDebugEnabled()) {
            LOG.debug(namespace + " " + method + " " + args);
        }
    }

    /**
     *
     */
    @Override
    public String getNamespace() {
        return namespace;
    }

    /**
     *
     */
    @Override
    public String getMethod() {
        return method;
    }

    /**
     *
     */
    @Override
    public List<byte[]> getArgs() {
        return args;
    }

    /**
     *
     */
    @Override
    public String getRequestName() {
        return namespace + ":" + method;
    }

    /**
     *
     */
    @Override
    public String toString() {
        return namespace + ":" + method + " @" + Integer.toHexString(System.identityHashCode(this));
    }

}
