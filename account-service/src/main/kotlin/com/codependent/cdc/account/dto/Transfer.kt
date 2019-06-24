package com.codependent.cdc.account.dto

data class Transfer (val sourceAccountId: Long, val destinationAccountId: Long, val ammount: Float)
