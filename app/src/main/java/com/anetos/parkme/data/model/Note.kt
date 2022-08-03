package com.anetos.parkme.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anetos.parkme.core.helper.NoteColor


@Entity
class Note() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var documentId: String? = null
    var userId: String? = null
    var noteTitle: String? = null
    var noteBody: String = String()
    var completed = false
    var creationDate: String? = null
    var noteColor: NoteColor = NoteColor.Gray

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        documentId = parcel.readString()
        userId = parcel.readString()
        noteTitle = parcel.readString()
        noteBody = parcel.readString().toString()
        completed = parcel.readByte() != 0.toByte()
        creationDate = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(documentId)
        parcel.writeString(userId)
        parcel.writeString(noteTitle)
        parcel.writeString(noteBody)
        parcel.writeByte(if (completed) 1 else 0)
        parcel.writeString(creationDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}