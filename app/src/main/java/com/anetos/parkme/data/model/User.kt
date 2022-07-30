package com.anetos.parkme.data.model

import com.anetos.parkme.data.ConstantFirebase
import com.google.firebase.firestore.Exclude
import java.io.Serializable
import java.util.*

class User : Serializable {
    var name: String? = null
    var countryNameCode: String? = null
    var countryCode: String? = null
    var mobileNumber: String? = null
    var emailAddress: String? = null
    var address: String? = null
    var role: String? = ConstantFirebase.ROLES.REGULAR.name
    var bankCard: BankCard? = null
    var userSubscribe: String? = ConstantFirebase.USER.FREE.name
    var service: String? = null
    var insertedAt: Long? = null

    constructor()

    constructor(
        name: String,
        countryNameCode: String,
        countryCode: String,
        mobile: String,
        email: String,
        address: String,
        role: String,
        bankCard: BankCard,
        userSubscribe: String,
        service: String,
    ) {
        this.name = name
        this.countryNameCode = countryNameCode
        this.countryCode = countryCode
        this.mobileNumber = mobile
        this.emailAddress = email
        this.address = address
        this.role = role
        this.bankCard = bankCard
        this.userSubscribe = userSubscribe
        this.service = service
        this.insertedAt = Calendar.getInstance().timeInMillis
    }

    constructor(
        name: String,
        countryNameCode: String,
        countryCode: String,
        mobile: String,
        email: String,
        address: String,
        role: String
    ) {
        this.name = name
        this.countryNameCode = countryNameCode
        this.countryCode = countryCode
        this.mobileNumber = mobile
        this.emailAddress = email
        this.address = address
        this.role = role
        this.insertedAt = Calendar.getInstance().timeInMillis
    }

    constructor(
        name: String,
        countryNameCode: String,
        countryCode: String,
        mobile: String,
        email: String,
        role: String
    ) {
        this.name = name
        this.countryNameCode = countryNameCode
        this.countryCode = countryCode
        this.mobileNumber = mobile
        this.emailAddress = email
        this.role = role
        this.insertedAt = Calendar.getInstance().timeInMillis
    }

    @Exclude
    fun isAdmin(): Boolean {
        return role == ConstantFirebase.ROLES.ADMIN.name
    }

    @Exclude
    fun isUser(): Boolean {
        return role == ConstantFirebase.ROLES.REGULAR.name
    }

    /*@Exclude
    fun isTechnician(): Boolean {
        return role == Constants.ROLES.SERVICE_PROVIDER.name
    }*/

    fun updateDetails(
        name: String, countryCode: String, countryNameCode: String, mobile: String,
        email: String, address: String
    ) {
        this.name = name
        this.countryNameCode = countryNameCode
        this.countryCode = countryCode
        this.mobileNumber = mobile
        this.emailAddress = email
        this.address = address
    }

    override fun toString(): String {
        return "$name, Role => $role, $countryCode-$mobileNumber, $emailAddress, $address, $insertedAt"
    }
}