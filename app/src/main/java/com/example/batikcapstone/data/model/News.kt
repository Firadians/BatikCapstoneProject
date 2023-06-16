package com.example.batikcapstone.data.model

import android.os.Parcel
import android.os.Parcelable


data class News(
    val name: String? = null,
    val description: String? = null,
    val postDate: String? = null,
    val photoUrl: String? = null,
    val webUrl: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
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
        dest.writeString(description)
        dest.writeString(postDate)
        dest.writeString(photoUrl)
        dest.writeString(webUrl)
    }

    companion object CREATOR : Parcelable.Creator<News> {
        override fun createFromParcel(parcel: Parcel): News {
            return News(parcel)
        }

        override fun newArray(size: Int): Array<News?> {
            return arrayOfNulls(size)
        }
    }
}
