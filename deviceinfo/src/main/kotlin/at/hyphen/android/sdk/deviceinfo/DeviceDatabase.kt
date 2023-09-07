@file:Suppress("NAME_SHADOWING")

package at.hyphen.android.sdk.deviceinfo

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.TextUtils
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class DeviceDatabase(context: Context) :
    SQLiteOpenHelper(context, NAME, null, VERSION) {
    private val file: File
    private val context: Context

    init {
        this.context = context.applicationContext
        file = context.getDatabasePath(NAME)
        if (!file.exists()) {
            create()
        }
    }

    fun query(codename: String?, model: String?): String? {
        val database = readableDatabase
        val columns = arrayOf(COLUMN_NAME)
        val selection: String
        val selectionArgs: Array<String>
        if (codename != null && model != null) {
            selection = "$COLUMN_CODENAME LIKE ? OR $COLUMN_MODEL LIKE ?"
            selectionArgs = arrayOf(codename, model)
        } else if (codename != null) {
            selection = "$COLUMN_CODENAME LIKE ?"
            selectionArgs = arrayOf(codename)
        } else if (model != null) {
            selection = "$COLUMN_MODEL LIKE ?"
            selectionArgs = arrayOf(model)
        } else {
            return null
        }
        val cursor = database.query(TABLE_DEVICES, columns, selection, selectionArgs, null, null, null)
        var name: String? = null
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        }
        close(cursor)
        close(database)
        return name
    }

    fun queryToDevice(codename: String?, model: String?): DeviceName.DeviceInfo? {
        val database = readableDatabase
        val columns = arrayOf(COLUMN_NAME, COLUMN_CODENAME, COLUMN_MODEL)
        val selection: String
        val selectionArgs: Array<String?>
        if (!TextUtils.isEmpty(codename) && !TextUtils.isEmpty(model)) {
            selection = "$COLUMN_CODENAME LIKE ? OR $COLUMN_MODEL LIKE ?"
            selectionArgs = arrayOf(codename, model)
        } else if (!TextUtils.isEmpty(codename)) {
            selection = "$COLUMN_CODENAME LIKE ?"
            selectionArgs = arrayOf(codename)
        } else if (TextUtils.isEmpty(model)) {
            selection = "$COLUMN_MODEL LIKE ?"
            selectionArgs = arrayOf(model)
        } else {
            return null
        }
        val cursor = database.query(TABLE_DEVICES, columns, selection, selectionArgs, null, null, null)
        var deviceInfo: DeviceName.DeviceInfo? = null
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val codename = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODENAME))
            val model = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODEL))
            deviceInfo = DeviceName.DeviceInfo(name, codename, model)
        }
        close(cursor)
        close(database)
        return deviceInfo
    }

    override fun onCreate(db: SQLiteDatabase) {
        // no-op
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion) {
            if (context.deleteDatabase(NAME) || file.delete() || !file.exists()) {
                create()
            }
        }
    }

    @Throws(SQLException::class)
    private fun create() {
        try {
            readableDatabase // Create an empty database that we will overwrite.
            close() // Close the empty database
            transferDatabaseAsset() // Copy the database from assets to the application's database dir
        } catch (e: IOException) {
            throw SQLException("Error creating $NAME database", e)
        }
    }

    @Throws(IOException::class)
    private fun transferDatabaseAsset() {
        val input = context.assets.open(NAME)
        val output: OutputStream = FileOutputStream(file)
        val buffer = ByteArray(2048)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }
        output.flush()
        close(output)
        close(input)
    }

    private fun close(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (ignored: IOException) {
            }
        }
    }

    companion object {
        private const val TABLE_DEVICES = "devices"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_CODENAME = "codename"
        private const val COLUMN_MODEL = "model"
        private const val NAME = "android-devices.db"
        private const val VERSION = 1
    }
}
