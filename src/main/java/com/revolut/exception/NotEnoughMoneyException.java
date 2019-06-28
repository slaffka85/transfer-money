package com.revolut.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotEnoughMoneyException extends RuntimeException implements ExceptionMapper<RuntimeException> {

    private static final String DEFAULT_MESSAGE = "it's impossible to transfer money, because it's not enough money";

    public NotEnoughMoneyException(String message) {
        super(message);
    }

    @Override
    public Response toResponse(RuntimeException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(DEFAULT_MESSAGE)
                .type("text/plain")
                .build();
    }
}
