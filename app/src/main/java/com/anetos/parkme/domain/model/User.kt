package com.anetos.parkme.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.anetos.parkme.data.ConstantFirebase
import com.google.firebase.firestore.Exclude

class User() : Parcelable {
    var name: String? = null
    var countryNameCode: String? = null
    var countryCode: String? = null
    var mobileNumber: String? = null
    var emailAddress: String? = null
    var address: String? = null
    var role: String? = ConstantFirebase.ROLES.REGULAR.name
    var bankCard: BankCard? = null
    var walletCard: WalletCard? = null
    var userSubscribe: String? = ConstantFirebase.USER.FREE.name
    var bookedSpot: BookedSpot? = null
    var insertedAt: Long? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        countryNameCode = parcel.readString()
        countryCode = parcel.readString()
        mobileNumber = parcel.readString()
        emailAddress = parcel.readString()
        address = parcel.readString()
        role = parcel.readString()
        bankCard = parcel.readParcelable(BankCard::class.java.classLoader)
        walletCard = parcel.readParcelable(WalletCard::class.java.classLoader)
        userSubscribe = parcel.readString()
        bookedSpot = parcel.readParcelable(BookedSpot::class.java.classLoader)
        insertedAt = parcel.readValue(Long::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(countryNameCode)
        parcel.writeString(countryCode)
        parcel.writeString(mobileNumber)
        parcel.writeString(emailAddress)
        parcel.writeString(address)
        parcel.writeString(role)
        parcel.writeParcelable(bankCard, flags)
        parcel.writeParcelable(walletCard, flags)
        parcel.writeString(userSubscribe)
        parcel.writeParcelable(bookedSpot, flags)
        parcel.writeValue(insertedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

    @Exclude
    fun isAdmin(): Boolean {
        return role == ConstantFirebase.ROLES.ADMIN.name
    }

    @Exclude
    fun isUser(): Boolean {
        return role == ConstantFirebase.ROLES.REGULAR.name
    }
}