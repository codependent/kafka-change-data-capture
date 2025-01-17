package com.codependent.cdc.account.service

import com.codependent.cdc.account.Movement
import com.codependent.cdc.account.TransferApproved
import com.codependent.cdc.account.dto.Account
import com.codependent.cdc.account.dto.MovementType
import com.codependent.cdc.account.dto.Transfer
import com.codependent.cdc.account.entity.AccountEntity
import com.codependent.cdc.account.entity.MovementEntity
import com.codependent.cdc.account.exception.AccountDoesntExistException
import com.codependent.cdc.account.exception.FundsNotAvailableException
import com.codependent.cdc.account.mapper.ObjectMapper
import com.codependent.cdc.account.repository.AccountRepository
import com.codependent.cdc.account.repository.MovementRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Transactional
@Service
class AccountServiceImpl(private val accountRepository: AccountRepository,
                         private val movementRepository: MovementRepository,
                         private val mapper: ObjectMapper) : AccountService {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun receiveTransfer(transfer: TransferApproved) {

        if (movementRepository.findByTransactionIdAndTypeAndAccountEntityId(transfer.getTransferId(), MovementType.PAYMENT, transfer.getDestinationAccountId()).isEmpty()) {
            val destinationAccount = accountRepository.findById(transfer.getDestinationAccountId())
            destinationAccount.ifPresent {
                val funds = BigDecimal(it.funds.toString())
                val transferAmmount = BigDecimal(transfer.getAmmount().toString())
                it.funds = funds.add(transferAmmount).toFloat()

                val movementEntity = MovementEntity(0, transfer.getTransferId(), MovementType.PAYMENT,
                        it, transfer.getSourceAccountId(), transfer.getAmmount(), Date())
                it.movements.add(movementEntity)

                accountRepository.save(it)
            }
        } else {
            logger.warn("Ignoring duplicated received transfer {}", transfer)
        }
    }

    override fun cancelTransfer(movement: Movement) {

        if (movementRepository.findByTransactionIdAndTypeAndAccountEntityId(movement.transactionId, MovementType.CHARGE,
                        movement.accountEntityId).isNotEmpty()) {
            val sourceAccount = accountRepository.findById(movement.accountEntityId)
            sourceAccount.ifPresent { sAccount ->
                val funds = BigDecimal(sAccount.funds.toString())
                val transferAmmount = BigDecimal(movement.getAmmount().toString())
                sAccount.funds = funds.add(transferAmmount).toFloat()
                sAccount.movements.removeIf { it.transactionId == movement.transactionId }
                accountRepository.save(sAccount)
            }
        } else {
            logger.warn("Ignoring duplicated transfer cancelation {}", movement)
        }
    }

    override fun getAll(): List<Account> {
        return mapper.map(accountRepository.findAll(), Account::class.java)
    }

    override fun save(account: Account) {
        accountRepository.save(mapper.map(account, AccountEntity::class.java))
    }

    override fun transfer(transfer: Transfer) {
        val account = accountRepository.findById(transfer.sourceAccountId)
        when (account.isPresent) {
            true -> {
                val sourceAccount = account.get()
                if (sourceAccount.funds >= transfer.ammount) {
                    val funds = BigDecimal(sourceAccount.funds.toString())
                    val transferAmmount = BigDecimal(transfer.ammount.toString())
                    sourceAccount.funds = funds.subtract(transferAmmount).toFloat()

                    val movement = MovementEntity(0, UUID.randomUUID().toString(),
                            MovementType.CHARGE, sourceAccount, transfer.destinationAccountId, transfer.ammount, Date())
                    sourceAccount.movements.add(movement)

                    accountRepository.save(sourceAccount)
                } else {
                    throw FundsNotAvailableException(transfer.sourceAccountId)
                }
            }
            false -> throw AccountDoesntExistException(transfer.sourceAccountId)
        }
    }
}
