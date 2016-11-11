package com.vector.okhttpusekotlindemo

import okhttp3.Call
import okhttp3.Response
import java.io.IOException

/**
 * Created by Vector
 * on 2016/11/7 0007.
 */
abstract class StringCallBack : okhttp3.Callback {
    companion object {
        private val platform: Platform = Platform.get()
    }

    override fun onResponse(call: Call?, response: Response?) = platform.execute {
        if (response != null && response.isSuccessful) {
            onResponse(response)
        } else {
            onFailure(Exception(""))
        }
    }

    override fun onFailure(call: Call?, e: IOException?) = platform.execute {
        if (e != null) {
            onFailure(e)
        } else {
            onFailure(Exception(""))
        }
    }


    abstract fun onResponse(response: Response)
    abstract fun onFailure(e: Exception)

}