package com.anetos.parkme.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.PrimaryKey

class BookedSpot() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var bookedParkingId: String? = null
    var bookedFrom: Long? = null
    var bookedTill: Long? = null
    var bookedHours: Double? = 0.0

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        bookedParkingId = parcel.readString()
        bookedFrom = parcel.readValue(Long::class.java.classLoader) as? Long
        bookedTill = parcel.readValue(Long::class.java.classLoader) as? Long
        bookedHours = parcel.readValue(Double::class.java.classLoader) as? Double
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(bookedParkingId)
        parcel.writeValue(bookedFrom)
        parcel.writeValue(bookedTill)
        parcel.writeValue(bookedHours)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookedSpot> {
        override fun createFromParcel(parcel: Parcel): BookedSpot {
            return BookedSpot(parcel)
        }

        override fun newArray(size: Int): Array<BookedSpot?> {
            return arrayOfNulls(size)
        }
    }

}