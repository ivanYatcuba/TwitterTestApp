package com.iyatsouba.twittertestapp.twitter;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import retrofit2.Call;

import static org.mockito.Mockito.mock;

public class MockCallAnswer implements Answer<Object> {

    @Override
    public Object answer(InvocationOnMock invocation) throws Throwable {
        if (invocation.getMethod().getReturnType().equals(Call.class)) {
            return mock(Call.class);
        } else {
            return Mockito.RETURNS_DEFAULTS.answer(invocation);
        }
    }
}