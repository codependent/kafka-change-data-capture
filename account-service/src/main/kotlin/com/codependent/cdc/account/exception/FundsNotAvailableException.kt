package com.codependent.cdc.account.exception

class FundsNotAvailableException(val sourceAccountId: Long) : RuntimeException(sourceAccountId.toString())
