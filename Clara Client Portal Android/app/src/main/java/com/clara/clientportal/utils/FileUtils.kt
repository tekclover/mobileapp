package com.clara.clientportal.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {
    const val MULTI_PART_TYPE = "multipart/form-data"
    private const val PROVIDER = ".provider"
    private const val PACKAGE_NAME = "com.clara.clientportal"
    private const val CONTENT = "content"
    private const val FILE = "file"
    private const val DATA = "_data"
    private const val DOCUMENTS_DIR = "documents"

    private fun createImageFile(context: Context): File? {
        val timeStamp = SimpleDateFormat(
            Constants.YYYY_MM_DD_HH_MM_SS,
            Locale.getDefault()
        ).format(Date())
        val imageFileName = Constants.CLIENT_PORTAL + "_" + timeStamp + "_"
        val storageDir = context.cacheDir
        var photo: File? = null
        try {
            photo = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return photo
    }

    fun copyFileToAppDirectory(context: Context?, uri: Uri?): String? {
        var path: String? = null
        try {
            context?.let { ctx ->
                uri?.let {
                    val fileName = getFileName(ctx, it)
                    val cacheDir = getDocumentCacheDir(ctx)
                    val file = generateFileName(fileName, cacheDir)
                    file?.let { it1 ->
                        path = it1.absolutePath
                        saveFileFromUri(ctx, it, path)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return path
    }

    @SuppressLint("NewApi", "ObsoleteSdkInt")
    fun getPath(context: Context?, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if ((context != null) && isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4)
                }
                val contentUriPrefixesToTry = arrayOf(
                    "content://downloads/public_downloads",
                    "content://downloads/my_downloads",
                    "content://downloads/all_downloads"
                )
                for (contentUriPrefix: String? in contentUriPrefixesToTry) {
                    try {
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse(contentUriPrefix),
                            java.lang.Long.valueOf(id)
                        )
                        val path = getDataColumn(context, contentUri)
                        if (path != null) {
                            return path
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val fileName = getFileName(context, uri)
                val cacheDir = getDocumentCacheDir(context)
                val file = generateFileName(fileName, cacheDir)
                var destinationPath: String? = null
                if (file != null) {
                    destinationPath = file.absolutePath
                    saveFileFromUri(context, uri, destinationPath)
                }
                return destinationPath
            } else {
                var filePath = uri.path
                if ((CONTENT == uri.scheme)) {
                    val cursor = context.contentResolver.query(
                        uri,
                        arrayOf(MediaStore.Files.FileColumns.DATA),
                        null,
                        null,
                        null
                    )
                    if (cursor != null) {
                        cursor.moveToFirst()
                        filePath = cursor.getString(0)
                        cursor.close()
                    }
                }
                return filePath
            }
        } else if (context != null && CONTENT.equals(
                uri.scheme,
                ignoreCase = true
            )
        ) {
            return getDataColumn(context, uri)
        } else if (FILE.equals(
                uri.scheme,
                ignoreCase = true
            )
        ) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        val column: String = DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri, projection, null, null,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return ("com.android.providers.downloads.documents" == uri.authority)
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        val mimeType = context.contentResolver.getType(uri)
        var filename: String? = null
        if (mimeType == null) {
            val path = getPath(context, uri)
            filename = if (path == null) {
                getName(uri.toString())
            } else {
                val file = File(path)
                file.name
            }
        } else {
            val returnCursor = context.contentResolver.query(uri, null, null, null, null)
            if (returnCursor != null) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }
        return filename
    }

    private fun getDocumentCacheDir(context: Context): File {
        val dir = File(context.cacheDir, DOCUMENTS_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun generateFileName(fName: String?, directory: File): File? {
        var name = fName ?: return null
        var file = File(directory, name)
        if (file.exists()) {
            var fileName: String = name
            var extension = ""
            val dotIndex = name.lastIndexOf('.')
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex)
                extension = name.substring(dotIndex)
            }
            var index = 0
            while (file.exists()) {
                index++
                name = "$fileName($index)$extension"
                file = File(directory, name)
            }
        }
        try {
            if (!file.createNewFile()) {
                return null
            }
        } catch (e: IOException) {
            return null
        }
        return file
    }


    private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String?) {
        var `is`: InputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val buf = ByteArray(1024)
            `is`!!.read(buf)
            do {
                bos.write(buf)
            } while (`is`.read(buf) != -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getName(filename: String?): String? {
        if (filename == null) {
            return null
        }
        val index = filename.lastIndexOf('/')
        return filename.substring(index + 1)
    }

    fun getActionViewIntent(context: Context, fileName: String, path: String): Intent {
        val fileNameMap = URLConnection.getFileNameMap()
        val mimeTYpe = fileNameMap.getContentTypeFor(fileName)
        val actionIntent = Intent(Intent.ACTION_VIEW)
        actionIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        actionIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        val contentUri =
            FileProvider.getUriForFile(context, PACKAGE_NAME + PROVIDER, File(path))
        actionIntent.setDataAndType(contentUri, mimeTYpe)
        return actionIntent
    }

    fun downloadDocument(
        context: Context,
        downloadUrl: String,
        documentName: String,
        onDownloadCompleted: (String) -> Unit
    ) {
        try {
            var downloadDocumentFilePath = ""
            CoroutineScope(Dispatchers.IO).launch {
                var fileName = ""
                var contentType = ""

                fun copyInputStreamToFile(inputStream: InputStream, file: File?) {
                    try {
                        val outputStream: OutputStream = FileOutputStream(file)
                        val buffer = ByteArray(1024)
                        var len: Int
                        var total: Long = 0
                        while (inputStream.read(buffer).also { len = it } > 0) {
                            total += len.toLong()
                            outputStream.write(buffer, 0, len)
                        }
                        outputStream.close()
                        inputStream.close()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                fun renameFileExist(inputStream: InputStream?, filePath: String) {
                    val file = File(filePath + fileName)
                    if (inputStream != null) {
                        copyInputStreamToFile(inputStream, file)
                    }
                    if (!TextUtils.isEmpty(file.path)) downloadDocumentFilePath = file.path
                }

                fun getFileExtension(mimeType: String): String {
                    if (!TextUtils.isEmpty(mimeType)) {
                        val splitContentType = mimeType.split("/").toTypedArray()
                        return if (splitContentType.isNotEmpty()) splitContentType[1] else ""
                    }
                    return Constants.PDF_EXTENSION
                }

                fun convertStreamToFile(inputStream: InputStream) {
                    val fileExtension = getFileExtension(contentType)
                    val fileFormatIndex: Int = fileName.lastIndexOf('.')
                    val fileFormat =
                        if (fileFormatIndex < 0) "" else fileName.substring(fileFormatIndex)
                    if (TextUtils.isEmpty(fileFormat) && (TextUtils.isEmpty(fileExtension) || !fileName.endsWith(
                            fileExtension
                        ))
                    ) {
                        fileName += ".$fileExtension"
                    }
                    val downloadFilePath = "/client_portal/"
                    val folder = File(context.cacheDir.toString() + downloadFilePath)
                    folder.mkdirs()
                    renameFileExist(inputStream, context.cacheDir.toString() + downloadFilePath)
                }

                val inputStream: InputStream?
                val url = URL(downloadUrl)
                val connection = withContext(Dispatchers.IO) {
                    url.openConnection()
                } as HttpURLConnection
                connection.requestMethod = "GET"
                withContext(Dispatchers.IO) {
                    connection.connect()
                }

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK && connection.contentType != null) {
                    val disposition = connection.getHeaderField("Content-Disposition")
                    contentType = connection.contentType
                    if (disposition != null) {
                        val index = disposition.indexOf("filename=")
                        if (index > 0) {
                            fileName = disposition.substring(index + 10, disposition.length - 1)
                        }
                    } else {
                        fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1)
                    }
                    fileName = documentName
                    inputStream = connection.inputStream
                    inputStream?.let { convertStreamToFile(it) }
                } else {
                    Log.d("download document error", "" + responseCode)
                }
                connection.disconnect()
                withContext(Dispatchers.Main) {
                    onDownloadCompleted.invoke(downloadDocumentFilePath)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getContentUri(context: Context): Uri? {
        return try {
            createImageFile(context)?.let {
                FileProvider.getUriForFile(
                    context,
                    String.format("%s%s", PACKAGE_NAME, PROVIDER),
                    it
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}