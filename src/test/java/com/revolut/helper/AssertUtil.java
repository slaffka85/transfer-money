package com.revolut.helper;

import org.junit.Assert;

import java.math.BigDecimal;

public class AssertUtil {

    public static void assertEquals(BigDecimal val1, BigDecimal val2) {
        Assert.assertEquals(0, val1.compareTo(val2));
    }
}
