package com.revolut.controller;

import com.revolut.service.TransferService;
import com.revolut.service.impl.TransferServiceSyncImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

public class AbstractTransferController {

    private Logger logger = LogManager.getLogger(this.getClass().getName());

    /**
     * Method provides to transfer money
     * @param accNumberFrom from account number
     * @param accNumberTo to account number
     * @param amount amount of money
     * @return {@link Response}
     */
    @POST
    @Path("/{accNumberFrom}/{accNumberTo}/{amount}")
    public Response transferMoney(
            @PathParam("accNumberFrom") long accNumberFrom,
            @PathParam("accNumberTo") long accNumberTo,
            @PathParam("amount") BigDecimal amount
    ) {
        logger.debug("transfer money controller");
        TransferService transferService = new TransferServiceSyncImpl();
        transferService.transferMoney(accNumberFrom, accNumberTo, amount);
        return Response.ok().build();
    }
}
