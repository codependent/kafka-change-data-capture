package com.codependent.cdc.account.entity

import javax.persistence.*

@Entity
@Table(name = "account")
data class AccountEntity(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long,
                         val ownerId: String,
                         var ownerName: String,
                         var funds: Float,
                         @OneToMany(cascade = [CascadeType.ALL]) var movements: MutableList<MovementEntity> = mutableListOf())
