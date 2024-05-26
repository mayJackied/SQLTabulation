package com.jackied.backInterface;

public interface Increased {
    void Increase(int i);

    default String returnValue(int i, String... value) {
        return null;
    }
}
