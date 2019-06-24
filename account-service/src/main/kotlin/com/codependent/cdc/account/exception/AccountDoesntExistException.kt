package com.codependent.cdc.account.exception

class AccountDoesntExistException(sourceAccountId: Long) : RuntimeException(sourceAccountId.toString())
