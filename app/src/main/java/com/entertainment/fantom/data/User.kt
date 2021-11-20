package com.entertainment.fantom.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val fbLink: String="",val webLink:String="", val id:String="",val image:String="",
                val email:String=""):Parcelable {
                    var name:String =""
                }

