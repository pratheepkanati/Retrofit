package com.example.retrofit

import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntity
import java.io.FilterOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.Charset

class AndroidMultiPartEntity : MultipartEntity {
    private val listener: ProgressListener

    constructor(listener: ProgressListener) : super() {
        this.listener = listener
    }

    constructor(
        mode: HttpMultipartMode?,
        listener: ProgressListener
    ) : super(mode) {
        this.listener = listener
    }

    constructor(
        mode: HttpMultipartMode?, boundary: String?,
        charset: Charset?, listener: ProgressListener
    ) : super(mode, boundary, charset) {
        this.listener = listener
    }

    @Throws(IOException::class)
    override fun writeTo(outstream: OutputStream) {
        super.writeTo(CountingOutputStream(outstream, listener))
    }

    interface ProgressListener {
        fun transferred(num: Long)
    }

    class CountingOutputStream(
        out: OutputStream?,
        private val listener: ProgressListener
    ) : FilterOutputStream(out) {
        private var transferred: Long = 0
        @Throws(IOException::class)
        override fun write(b: ByteArray, off: Int, len: Int) {
            out.write(b, off, len)
            transferred += len.toLong()
            listener.transferred(transferred)
        }

        @Throws(IOException::class)
        override fun write(b: Int) {
            out.write(b)
            transferred++
            listener.transferred(transferred)
        }
    }
}