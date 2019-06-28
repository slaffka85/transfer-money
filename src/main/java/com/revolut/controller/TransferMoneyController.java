package com.revolut.controller;

import com.revolut.service.TransferService;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

/**
 * Transfer money controller provides REST-API for transferring money
 *
 * @author v.tsapaev
 */
@Path("/transfer-money")
public class TransferMoneyController  {

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
        TransferService transferService = new TransferService();
        transferService.transferMoney(accNumberFrom, accNumberTo, amount);
        return Response.ok().build();
    }
}
