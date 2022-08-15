package com.anetos.parkme.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.PrimaryKey

class WalletCard() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var nameOnCard: String? = null
    var avilableBalance: Double? = 0.0

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        nameOnCard = parcel.readString()
        avilableBalance = parcel.readValue(Double::class.java.classLoader) as? Double
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nameOnCard)
        parcel.writeValue(avilableBalance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WalletCard> {
        override fun createFromParcel(parcel: Parcel): WalletCard {
            return WalletCard(parcel)
        }

        override fun newArray(size: Int): Array<WalletCard?> {
            return arrayOfNulls(size)
        }
    }

}