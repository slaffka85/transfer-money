package com.revolut.service;

import com.revolut.exception.JdbcException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class JdbcExceptionMapper implements ExceptionMapper<JdbcException> {

	private static Logger log = LogManager.getLogger(JdbcExceptionMapper.class);

	public Response toResponse(JdbcException exception) {
		if (log.isDebugEnabled()) {
			log.debug("Mapping exception to Response....");
		}
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(exception.getMessage())
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
}
