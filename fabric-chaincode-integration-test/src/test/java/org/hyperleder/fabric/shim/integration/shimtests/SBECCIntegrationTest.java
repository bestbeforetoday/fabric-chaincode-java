/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/
package org.hyperleder.fabric.shim.integration.shimtests;


import org.hyperleder.fabric.shim.integration.util.FabricState;
import org.hyperleder.fabric.shim.integration.util.InvokeHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SBECCIntegrationTest {

    @BeforeAll
    public static void setUp() throws Exception {
        FabricState.getState().start();
    }

    @Test
    public void RunSBE_pub_setget() {
        final String mode = "pub";

        final InvokeHelper helper = InvokeHelper.newHelper("shimcc", "sachannel");

        String text;

        text = helper.invoke("org1", "EndorsementCC:setval", mode, "foo");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:getval", mode);
        assertThat(text).contains("foo");

        text = helper.invoke("org1", "EndorsementCC:addorgs", mode, "org1MSP");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:listorgs", mode);
        assertThat(text).contains("org1MSP");

        text = helper.invoke("org1", "EndorsementCC:setval", mode, "val1");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:getval", mode);
        assertThat(text).contains("val1");

        text = helper.invoke("org1", "EndorsementCC:setval", mode, "val2");

        text = helper.invoke("org1", "EndorsementCC:getval", mode);
        assertThat(text).contains("val2");

        text = helper.invoke("org1", "EndorsementCC:addorgs", mode, "org2MSP");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:listorgs", mode);
        assertThat(text).contains("org2MSP");
        assertThat(text).contains("org1MSP");


        text = helper.invoke("org1", "EndorsementCC:setval", mode, "val3");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:getval", mode);
        assertThat(text).contains("val3");

        text = helper.invoke("org1", "EndorsementCC:setval", mode, "val4");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:getval", mode);
        assertThat(text).contains("val4");

        text = helper.invoke("org1", "EndorsementCC:delorgs", mode, "org1MSP");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:listorgs", mode);
        assertThat(text).contains("org2MSP");
        assertThat(text).doesNotContain("org1MSP");

        text = helper.invoke("org1", "EndorsementCC:deleteval", mode);
        assertThat(text).contains("success");
        text = helper.invoke("org1", "EndorsementCC:recordExists", mode);
        assertThat(text).contains("false");

    }

   @Test
    public void RunSBE_priv() {
        final String mode = "priv";

        final InvokeHelper helper = InvokeHelper.newHelper("shimcc", "sachannel");

        String text;

        text = helper.invoke("org1", "EndorsementCC:setval", mode, "foo");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:getval", mode);
        assertThat(text).contains("foo");

        text = helper.invoke("org1", "EndorsementCC:addorgs", mode, "org1MSP");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:listorgs", mode);
        assertThat(text).contains("org1MSP");

        text = helper.invoke("org1", "EndorsementCC:setval", mode, "val1");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:getval", mode);
        assertThat(text).contains("val1");

        text = helper.invoke("org1", "EndorsementCC:setval", mode, "val2");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:getval", mode);
        assertThat(text).contains("val2");

        text = helper.invoke("org1", "EndorsementCC:addorgs", mode, "org2MSP");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:listorgs", mode);
        assertThat(text).contains("org2MSP");
        assertThat(text).contains("org1MSP");
      

        text = helper.invoke("org1", "EndorsementCC:setval", mode, "val3");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:getval", mode);
        assertThat(text).contains("val3");

        text = helper.invoke("org1", "EndorsementCC:setval", mode, "val4");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:getval", mode);
        assertThat(text).contains("val4");

        text = helper.invoke("org1", "EndorsementCC:delorgs", mode, "org1MSP");
        assertThat(text).contains("success");

        text = helper.invoke("org1", "EndorsementCC:listorgs", mode);
        assertThat(text).contains("org2MSP");
        assertThat(text).doesNotContain("org1MSP");

        text = helper.invoke("org1", "EndorsementCC:deleteval", mode);
        assertThat(text).contains("success");
        text = helper.invoke("org1", "EndorsementCC:recordExists", mode);
        assertThat(text).contains("false");

    }

}
