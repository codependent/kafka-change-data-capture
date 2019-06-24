package com.codependent.cdc.account.entity

import com.codependent.cdc.account.dto.MovementType
import java.util.*
import javax.persistence.*

@Entity
data class MovementEntity(@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Int,
                          var transactionId: String,
                          @Enumerated(EnumType.STRING) val type: MovementType,
                          @ManyToOne var accountEntity: AccountEntity,
                          var relatedAccountId: Long,
                          var ammount: Float,
                          @Temporal(TemporalType.TIMESTAMP) var date: Date)
