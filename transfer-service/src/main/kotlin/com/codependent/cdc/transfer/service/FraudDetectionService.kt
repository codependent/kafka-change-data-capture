package com.codependent.cdc.transfer.service

import com.codependent.cdc.account.Movement

interface FraudDetectionService {

    fun isFraudulent(transfer: Movement): Boolean

}
