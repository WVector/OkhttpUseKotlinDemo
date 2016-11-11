package com.vector.okhttpusekotlindemo

import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import okhttp3.*
import org.jetbrains.anko.*
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLConnection
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {
    val baseUrl = "http:10.222.5.115:8080"
    lateinit private var mTvResult: TextView
    private val client = OkHttpClient.Builder()
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .readTimeout(10000L, TimeUnit.MILLISECONDS)
            .writeTimeout(10000L, TimeUnit.MILLISECONDS)
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        scrollView {
            verticalLayout {
                button {
                    text = "get"
                }.onClick {
                    get()
                }
                button {
                    text = "post-json"
                }.onClick {
                    postJson()
                }
                button {
                    text = "post-params"
                }.onClick {
                    postParams()
                }

                button {
                    text = "post-file"
                }.onClick {
                    postFile()
                }


                mTvResult = textView {
                }.lparams {
                }

            }

        }
    }

    val stringCallBack: StringCallBack = object : StringCallBack() {
        override fun onResponse(response: Response) {
            mTvResult.text = response.body()?.string()
        }

        override fun onFailure(e: Exception) {
            toast("异常")
        }
    }

    private fun get() {

        val request: Request = Request.Builder().get().url("$baseUrl/users/3").build()
        val okHttpClient: OkHttpClient = OkHttpClient()
        val call = okHttpClient.newCall(request)
        call.enqueue(stringCallBack)
    }

    private fun postJson() {
        val json = "{\n" +
                "    \"mobile\": \"17704727321\",\n" +
                "    \"email\": \"xxxx@gmail.com\",\n" +
                "    \"realname\": \"vector\"\n" +
                "}";
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        val request: Request = Request.Builder().post(requestBody).url("$baseUrl/users/json").build()
        client.newCall(request).enqueue(stringCallBack)
    }

    private fun postParams() {
        val requestBody: RequestBody = FormBody
                .Builder()
                .add("mobile", "17704727320")
                .add("email", "xxxx@gmail.com")
                .add("realname", "vector")
                .build()
        val request: Request = Request.Builder().post(requestBody).url("$baseUrl/users/register").build()
        client.newCall(request).enqueue(stringCallBack)
    }

    private fun postFile() {
        val file = File(Environment.getExternalStorageDirectory(), "test.jpg")
        if (!file.exists()) {
            toast("文件不存在，请修改文件路径")
            return
        }
        val fileInput: FileInput = FileInput("image", "test.jpg", file)

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileInput.filename)), fileInput.file)
        builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody)


        val request: Request = Request.Builder().post(builder.build()).url("$baseUrl/users/imageUpload").build()
        client.newCall(request).enqueue(stringCallBack)
    }


    data class FileInput(var key: String, var filename: String, var file: File) {

        override fun toString(): String {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}'
        }
    }

    private fun guessMimeType(path: String): String {
        val fileNameMap = URLConnection.getFileNameMap()
        var contentTypeFor: String? = null
        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream"
        }
        return contentTypeFor
    }


}
