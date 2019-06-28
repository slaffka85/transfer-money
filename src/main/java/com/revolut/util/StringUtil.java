package com.revolut.util;

public class StringUtil {

    private StringUtil() {
        //preventing instance creation
    }

    public static StringBuilder deleteLastCharacters(StringBuilder builder, String characters) {
        int lastIndex = builder.lastIndexOf(characters);
        builder.delete(lastIndex, builder.length());
        return builder;
    }
}
