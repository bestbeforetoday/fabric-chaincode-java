/*
 * Copyright 2019 IBM All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.fabric.contract.routing;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.ContractRuntimeException;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.metadata.TypeSchema;
import org.hyperledger.fabric.contract.routing.impl.TxFunctionImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

final class TxFunctionTest {
    @Contract()
    class TestObject implements ContractInterface {

        @Transaction()
        public void testMethod1(final Context ctx) {}

        @Transaction()
        public void testMethod2(final Context ctx, @Property(schema = {"a", "b"}) final int arg) {}

        @Transaction()
        public void wibble(final String arg1) {}
    }

    @Test
    void constructor() throws NoSuchMethodException, SecurityException {
        final TestObject test = new TestObject();
        final ContractDefinition cd = mock(ContractDefinition.class);
        Mockito.when(cd.getAnnotation()).thenReturn(test.getClass().getAnnotation(Contract.class));

        final TxFunction txfn =
                new TxFunctionImpl(test.getClass().getMethod("testMethod1", new Class<?>[] {Context.class}), cd);
        final String name = txfn.getName();
        assertEquals(name, "testMethod1");

        assertThat(txfn.toString(), startsWith("testMethod1"));
    }

    @Test
    void property() throws NoSuchMethodException, SecurityException {
        final TestObject test = new TestObject();
        final ContractDefinition cd = mock(ContractDefinition.class);
        Mockito.when(cd.getAnnotation()).thenReturn(test.getClass().getAnnotation(Contract.class));
        final TxFunction txfn = new TxFunctionImpl(
                test.getClass().getMethod("testMethod2", new Class<?>[] {Context.class, int.class}), cd);
        final String name = txfn.getName();
        assertEquals(name, "testMethod2");

        assertThat(txfn.toString(), startsWith("testMethod2"));
        assertFalse(txfn.isUnknownTx());
        txfn.setUnknownTx(true);
        assertTrue(txfn.isUnknownTx());

        final TypeSchema ts = new TypeSchema();
        txfn.setReturnSchema(ts);
        final TypeSchema rts = txfn.getReturnSchema();
        assertEquals(ts, rts);
    }

    @Test
    void invaldtxfn() throws NoSuchMethodException, SecurityException {
        final TestObject test = new TestObject();
        final ContractDefinition cd = mock(ContractDefinition.class);
        Mockito.when(cd.getAnnotation()).thenReturn(test.getClass().getAnnotation(Contract.class));

        assertThatThrownBy(() -> new TxFunctionImpl(test.getClass().getMethod("wibble", String.class), cd))
                .isInstanceOf(ContractRuntimeException.class);
    }
}
