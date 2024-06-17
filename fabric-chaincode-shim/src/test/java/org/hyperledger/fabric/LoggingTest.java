/*
 * Copyright 2019 IBM All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.fabric;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import static org.assertj.core.api.Assertions.assertThat;


public final class LoggingTest {
    @Test
    public void testMapLevel() {

        assertThat(proxyMapLevel("ERROR")).withFailMessage("Error maps").isEqualTo(Level.SEVERE);
        assertThat(proxyMapLevel("critical")).withFailMessage("Critical maps").isEqualTo(Level.SEVERE);
        assertThat(proxyMapLevel("INFO")).withFailMessage("Info maps").isEqualTo(Level.INFO);
        assertThat(proxyMapLevel(" notice")).withFailMessage("Config maps").isEqualTo(Level.CONFIG);
        assertThat(proxyMapLevel(" info")).withFailMessage("Info maps").isEqualTo(Level.INFO);
        assertThat(proxyMapLevel("debug          ")).withFailMessage("Debug maps").isEqualTo(Level.FINEST);
        assertThat(proxyMapLevel("wibble          ")).withFailMessage("Info maps").isEqualTo(Level.INFO);
        assertThat(proxyMapLevel(new Object[] {null})).withFailMessage("Info maps").isEqualTo(Level.INFO);
    }

    public Object proxyMapLevel(final Object... args) {

        try {
            final Method m = Logging.class.getDeclaredMethod("mapLevel", String.class);
            m.setAccessible(true);
            return m.invoke(null, args);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testFormatError() {
        final Exception e1 = new Exception("Computer says no");

        assertThat(Logging.formatError(e1)).contains("Computer says no");

        final NullPointerException npe1 = new NullPointerException("Nothing here");
        npe1.initCause(e1);

        assertThat(Logging.formatError(npe1)).contains("Computer says no");
        assertThat(Logging.formatError(npe1)).contains("Nothing here");

        assertThat(Logging.formatError(null)).isNull();
    }

    @Test
    public void testSetLogLevel() {

        final java.util.logging.Logger l = java.util.logging.Logger.getLogger("org.hyperledger.fabric.test");
        final java.util.logging.Logger another = java.util.logging.Logger.getLogger("acme.wibble");

        final Level anotherLevel = another.getLevel();
        Logging.setLogLevel("debug");
        assertThat(l.getLevel()).isEqualTo(Level.FINEST);
        assertThat(another.getLevel()).isEqualTo(anotherLevel);

        Logging.setLogLevel("dsomethoig");
        assertThat(l.getLevel()).isEqualTo(Level.INFO);
        assertThat(another.getLevel()).isEqualTo(anotherLevel);

        Logging.setLogLevel("ERROR");
        assertThat(l.getLevel()).isEqualTo(Level.SEVERE);
        assertThat(another.getLevel()).isEqualTo(anotherLevel);

        Logging.setLogLevel("debug");
    }
}
