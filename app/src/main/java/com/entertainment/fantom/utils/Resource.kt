package com.entertainment.fantom.utils

import com.entertainment.fantom.DetailObject


sealed class Resource {
    data class Loading(val data: Any? = null) : Resource()
    data class Success(val data : DetailObject) : Resource()
    data class Error(val error: String = "") : Resource()
}
