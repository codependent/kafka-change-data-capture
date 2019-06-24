package com.codependent.cdc.transfer.service

import com.codependent.cdc.account.Movement
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FraudDetectionServiceImpl : FraudDetectionService {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun isFraudulent(transfer: Movement): Boolean {
        val isFraudulentDestination = fraudulentDestinations.contains(transfer.relatedAccountId)
        logger.info("****** Is {} a fraudulent transfer? -> {}", transfer, isFraudulentDestination)
        return isFraudulentDestination
    }

    private val fraudulentDestinations = setOf<Long>(900, 1000, 2000, 3000)

}
