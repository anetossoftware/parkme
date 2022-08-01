package com.anetos.parkme.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.PrimaryKey

class BankCard() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var nameOnCard: String? = null
    var cardNumber: String? = null
    var cvv: String? = null
    var expiryDate: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        nameOnCard = parcel.readString()
        cardNumber = parcel.readString()
        cvv = parcel.readString()
        expiryDate = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nameOnCard)
        parcel.writeString(cardNumber)
        parcel.writeString(cvv)
        parcel.writeString(expiryDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BankCard> {
        override fun createFromParcel(parcel: Parcel): BankCard {
            return BankCard(parcel)
        }

        override fun newArray(size: Int): Array<BankCard?> {
            return arrayOfNulls(size)
        }
    }
}