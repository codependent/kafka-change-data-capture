package com.codependent.cdc.account.consumer

import com.codependent.cdc.account.TransferApproved
import com.codependent.cdc.account.TransferEmitted
import com.codependent.cdc.account.service.AccountService
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.stereotype.Component

@Component
class TransferReceivedListener(private val accountService: AccountService) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @StreamListener("input")
    fun handleApproved(transfer: TransferApproved) {
        logger.info("****** Processing approved transfer {}", transfer)
        accountService.receiveTransfer(transfer)
    }

    @StreamListener("inputFraudulent")
    fun handleFraudulent(transfer: TransferEmitted) {
        logger.info("****** Cancelling fraudulent transfer {}", transfer)
        accountService.cancelTransfer(transfer)
    }

}
