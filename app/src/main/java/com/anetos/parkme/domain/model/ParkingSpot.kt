package com.anetos.parkme.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.anetos.parkme.data.ConstantFirebase
import java.util.*

class ParkingSpot() : Parcelable {
    var documentId: String? = null
    var parkingId: String? = null
    var address: String? = null
    var availabilityStatus: String? = ConstantFirebase.AVAILABILITY_STATUS.AVAILABLE.name
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var pricePerHr: Double = 0.0
    var insertedAt: Long? = Calendar.getInstance().timeInMillis

    constructor(parcel: Parcel) : this() {
        documentId = parcel.readString()
        parkingId = parcel.readString()
        address = parcel.readString()
        availabilityStatus = parcel.readString()
        latitude = parcel.readDouble()
        longitude = parcel.readDouble()
        pricePerHr = parcel.readDouble()
        insertedAt = parcel.readValue(Long::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(documentId)
        parcel.writeString(parkingId)
        parcel.writeString(address)
        parcel.writeString(availabilityStatus)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeDouble(pricePerHr)
        parcel.writeValue(insertedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParkingSpot> {
        override fun createFromParcel(parcel: Parcel): ParkingSpot {
            return ParkingSpot(parcel)
        }

        override fun newArray(size: Int): Array<ParkingSpot?> {
            return arrayOfNulls(size)
        }
    }
}