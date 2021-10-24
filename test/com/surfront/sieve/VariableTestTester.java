package com.surfront.sieve;

import com.surfront.sieve.variable.VariableExpander;
import com.surfront.sieve.variable.VariableHelper;

public class VariableTestTester {
    public static void main(String[] args) {
        String[] patterns = new String[] {
                "test",
                "test $test",
                "test ${ test",
                "test ${test",
                "test ${test test",
                "${message}",
                "${message.subject}",
                "${message.headers['test']}",
                "${message.headers['Reply-To']}",
                "${message.headers[\"Reply-To\"]}",
                "prefix ${message}",
                "${message} surfix",
                "prefix ${message} surfix",
        };
        for (String pattern : patterns) {
            System.out.println(pattern + " = " + VariableHelper.containsVariable(pattern));
        }
    }
}
