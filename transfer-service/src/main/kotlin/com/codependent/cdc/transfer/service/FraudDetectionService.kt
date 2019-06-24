package com.codependent.cdc.transfer.service

import movement_entity

interface FraudDetectionService {

    fun isFraudulent(transfer: movement_entity): Boolean

}
