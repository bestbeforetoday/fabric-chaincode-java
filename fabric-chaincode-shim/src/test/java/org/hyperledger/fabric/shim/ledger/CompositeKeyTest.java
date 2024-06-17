/*
 * Copyright 2019 IBM All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.shim.ledger;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;

public class CompositeKeyTest {
    @Test
    public void testValidateSimpleKeys() {
        CompositeKey.validateSimpleKeys("abc", "def", "ghi");
    }

    @Test
    public void testValidateSimpleKeysException() throws Exception {
        assertThatThrownBy(() -> CompositeKey.validateSimpleKeys("\u0000abc"))
                .isInstanceOf(CompositeKeyFormatException.class);
    }

    @Test
    public void testCompositeKeyStringStringArray() {
        final CompositeKey key = new CompositeKey("abc", "def", "ghi", "jkl", "mno");
        assertThat(key.getObjectType()).isEqualTo("abc");
        assertThat(key.getAttributes()).hasSize(4);
        assertThat(key.toString()).isEqualTo("\u0000abc\u0000def\u0000ghi\u0000jkl\u0000mno\u0000");
    }

    @Test
    public void testCompositeKeyStringListOfString() {
        final CompositeKey key = new CompositeKey("abc", Arrays.asList("def", "ghi", "jkl", "mno"));
        assertThat(key.getObjectType()).isEqualTo("abc");
        assertThat(key.getAttributes()).hasSize(4);
        assertThat(key.toString()).isEqualTo("\u0000abc\u0000def\u0000ghi\u0000jkl\u0000mno\u0000");
    }

    @Test
    public void testEmptyAttributes() {
        final CompositeKey key = new CompositeKey("abc");
        assertThat(key.getObjectType()).isEqualTo("abc");
        assertThat(key.getAttributes()).hasSize(0);
        assertThat(key.toString()).isEqualTo("\u0000abc\u0000");
    }

    @Test
    public void testCompositeKeyWithInvalidObjectTypeDelimiter() {
        assertThatThrownBy(() -> new CompositeKey("ab\u0000c", Arrays.asList("def", "ghi", "jkl", "mno")))
                .isInstanceOf(CompositeKeyFormatException.class);
    }

    @Test
    public void testCompositeKeyWithInvalidAttributeDelimiter() {
        assertThatThrownBy(() -> new CompositeKey("abc", Arrays.asList("def", "ghi", "j\u0000kl", "mno")))
                .isInstanceOf(CompositeKeyFormatException.class);
    }

    @Test
    public void testCompositeKeyWithInvalidObjectTypeMaxCodePoint() {
        assertThatThrownBy(() -> new CompositeKey("ab\udbff\udfffc", Arrays.asList("def", "ghi", "jkl", "mno")))
                .isInstanceOf(CompositeKeyFormatException.class);
    }

    @Test
    public void testCompositeKeyWithInvalidAttributeMaxCodePoint() {
        assertThatThrownBy(() -> new CompositeKey("abc", Arrays.asList("def", "ghi", "jk\udbff\udfffl", "mno")))
                .isInstanceOf(CompositeKeyFormatException.class);
    }

    @Test
    public void testGetObjectType() {
        final CompositeKey key = new CompositeKey("abc", Arrays.asList("def", "ghi", "jkl", "mno"));
        assertThat(key.getObjectType()).isEqualTo("abc");
    }

    @Test
    public void testGetAttributes() {
        final CompositeKey key = new CompositeKey("abc", Arrays.asList("def", "ghi", "jkl", "mno"));
        assertThat(key.getObjectType()).isEqualTo("abc");
        assertThat(key.getAttributes()).hasSize(4);
        assertThat(key.getAttributes()).contains("def", "ghi", "jkl", "mno");
    }

    @Test
    public void testToString() {
        final CompositeKey key = new CompositeKey("abc", Arrays.asList("def", "ghi", "jkl", "mno"));
        assertThat(key.toString()).isEqualTo("\u0000abc\u0000def\u0000ghi\u0000jkl\u0000mno\u0000");
    }

    @Test
    public void testParseCompositeKey() {
        final CompositeKey key = CompositeKey.parseCompositeKey("\u0000abc\u0000def\u0000ghi\u0000jkl\u0000mno\u0000");
        assertThat(key.getObjectType()).isEqualTo("abc");
        assertThat(key.getAttributes()).hasSize(4);
        assertThat(key.getAttributes()).contains("def", "ghi", "jkl", "mno");
        assertThat(key.toString()).isEqualTo("\u0000abc\u0000def\u0000ghi\u0000jkl\u0000mno\u0000");
    }

    @Test
    public void testParseCompositeKeyInvalidObjectType() {
        assertThatThrownBy(() -> CompositeKey.parseCompositeKey("ab\udbff\udfffc\u0000def\u0000ghi\u0000jkl\u0000mno\u0000"))
                .isInstanceOf(CompositeKeyFormatException.class);
    }

    @Test
    public void testParseCompositeKeyInvalidAttribute() {
        assertThatThrownBy(() -> CompositeKey.parseCompositeKey("abc\u0000def\u0000ghi\u0000jk\udbff\udfffl\u0000mno\u0000"))
                .isInstanceOf(CompositeKeyFormatException.class);
    }

}
