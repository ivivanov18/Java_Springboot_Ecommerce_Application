package com.example.demo;

import org.junit.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class HelperParametrizedTest {
    private String input;
    private String output;

    public HelperParametrizedTest(String input) {
        super();
        this.input = input;
    }

    @Parameterized.Parameters
    public static Collection initData() {
        String empNames[][] = {{"sareeta", "sareeta"}, {"sareeta", "Jeff"}};
        return Arrays.asList(empNames);
    }
}
