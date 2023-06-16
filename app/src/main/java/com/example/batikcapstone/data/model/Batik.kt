package com.example.batikcapstone.data.model

import android.os.Parcel
import android.os.Parcelable

data class Batik(
    val name: String? = null,
    val origin: String? = null,
    val description: String? = null,
    val photoUrl: String? = null,
    val display1: String? = null,
    val display2: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(origin)
        dest.writeString(description)
        dest.writeString(photoUrl)
        dest.writeString(display1)
        dest.writeString(display2)
    }

    companion object CREATOR : Parcelable.Creator<Batik> {
        override fun createFromParcel(parcel: Parcel): Batik {
            return Batik(parcel)
        }

        override fun newArray(size: Int): Array<Batik?> {
            return arrayOfNulls(size)
        }
    }
}