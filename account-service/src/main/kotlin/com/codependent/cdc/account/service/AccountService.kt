package com.codependent.cdc.account.service

import com.codependent.cdc.account.Movement
import com.codependent.cdc.account.TransferApproved
import com.codependent.cdc.account.dto.Account
import com.codependent.cdc.account.dto.Transfer

interface AccountService {

    fun save(account: Account)
    fun getAll(): List<Account>
    fun transfer(transfer: Transfer)
    fun receiveTransfer(transfer: TransferApproved)
    fun cancelTransfer(movement: Movement)
}
