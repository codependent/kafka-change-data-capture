package com.codependent.cdc.account.web

import com.codependent.cdc.account.dto.Account
import com.codependent.cdc.account.dto.Transfer
import com.codependent.cdc.account.service.AccountService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/accounts")
class AccountRestController(private val accountService: AccountService) {

    @PostMapping
    fun save(@RequestBody accountEntity: Account) {
        accountService.save(accountEntity)
    }

    @PutMapping("/transfers")
    fun update(@RequestBody transfer: Transfer) {
        accountService.transfer(transfer)
    }

    @GetMapping
    fun getAll(): List<Account> {
        return accountService.getAll()
    }
}
