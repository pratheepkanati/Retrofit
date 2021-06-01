package com.example.retrofit

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.retrofit.databinding.ActivityMainBinding
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var mDataBindingView: ActivityMainBinding
    var filePath : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBindingView = DataBindingUtil.setContentView(this, R.layout.activity_main)

        filePath = File(Environment.getExternalStorageDirectory().path + "/Almarai/sample.pdf").toString()

        mDataBindingView.btnUpload.setOnClickListener {
// uploading the file to server
            // uploading the file to server
            UploadFileToServer(mDataBindingView, this, filePath!!).execute()
        }
    }


    private class UploadFileToServer(
        mDataBindingView: ActivityMainBinding,
        context: Context,
        filePath: String
    ) : AsyncTask<String?, String?, String?>() {
        var viewBinding = mDataBindingView
        var context1 = context
        var filePath = filePath
        var totalSize: Long = 0



        override fun onPreExecute() {
            Toast.makeText(context1, "Uploading", Toast.LENGTH_SHORT).show()
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String? {
            return uploadFile()!!
        }

        private fun uploadFile(): String? {
            var responseString: String? = null
            val httpclient: HttpClient = DefaultHttpClient()
            val httppost = HttpPost("http://192.168.42.293/AndroidFileUpload/fileUpload.php")

            try {

                val entity = AndroidMultiPartEntity(object : AndroidMultiPartEntity.ProgressListener{
                    override fun transferred(num: Long) {
                        fun transferred(num: Long) {
                            publishProgress(((num / totalSize as Float * 100).toInt()).toString())
                        }
                    }

                })
                val sourceFile = File(filePath)

                // Adding file data to http body
                entity.addPart("image", FileBody(sourceFile))

                // Extra parameters if you want to pass to server
                entity.addPart(
                    "website",
                    StringBody("www.almarai.com")
                )
                entity.addPart("email", StringBody("rajeshpj594@gmail.com"))
                totalSize = entity.contentLength
                httppost.entity = entity

                // Making server call
                val response = httpclient.execute(httppost)
                val r_entity = response.entity
                val statusCode = response.statusLine.statusCode
                responseString = if (statusCode == 200) {
                    // Server response
                    EntityUtils.toString(r_entity)
                } else {
                    ("Error occurred! Http Status Code: "
                            + statusCode)
                }
            } catch (e: ClientProtocolException) {
                responseString = e.toString()
            } catch (e: IOException) {
                responseString = e.toString()
            }
            return responseString
        }

        override fun onPostExecute(result: String?) {
            Toast.makeText(context1, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
            super.onPostExecute(result)
        }

    }

 }