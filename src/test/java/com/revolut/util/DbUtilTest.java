package com.revolut.util;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class DbUtilTest {


    @Test
    public void testPrivateConstructor() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<DbUtil> constructor = (Constructor<DbUtil>) DbUtil.class.getDeclaredConstructors()[0];
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
