package com.trendingrepos.trimvideo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.getFilePath(uri: Uri, context: Context): String? {
    var cursor: Cursor? = null
    val column = "_data"

    val docId = DocumentsContract.getDocumentId(uri)
    val split = docId.split(":").toTypedArray()
    val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    val selection = "_id=?"
    val selectionArgs = arrayOf(
        split[1]
    )

    val projection = arrayOf(
        column
    )
    try {
        cursor = context.contentResolver.query(
            contentUri, projection, selection, selectionArgs,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

fun Fragment.checkPermissionForVideo(requestCode: Int): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED)
            && (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED)
        ) {
            val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            val permissionCoarse = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, requestCode)
            requestPermissions(permissionCoarse, requestCode)
        } else {
            return true
        }
    }
    return false
}

fun Fragment.showToast(message: String){
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}