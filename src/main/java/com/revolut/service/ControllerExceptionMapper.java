package com.revolut.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ControllerExceptionMapper implements ExceptionMapper<Exception> {

	private static Logger log = LogManager.getLogger(ControllerExceptionMapper.class);

	public ControllerExceptionMapper() {
	}

	public Response toResponse(Exception exception) {
		if (log.isDebugEnabled()) {
			log.debug("Mapping exception to Response....");
		}
		return Response.status(Response.Status.BAD_REQUEST)
				.entity(exception.getMessage())
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
}
