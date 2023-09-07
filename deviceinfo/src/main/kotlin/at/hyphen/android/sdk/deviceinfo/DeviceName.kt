package at.hyphen.android.sdk.deviceinfo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.annotation.WorkerThread
import org.json.JSONException
import org.json.JSONObject

@SuppressLint("StaticFieldLeak")
object DeviceName {
    private const val SHARED_PREF_NAME = "device_names"

    @SuppressLint("StaticFieldLeak") // application context is safe
    private var context: Context? = null

    fun init(context: Context) {
        DeviceName.context = context.applicationContext
    }

    fun with(context: Context): Request {
        return Request(context.applicationContext)
    }

    val deviceName: String?
        get() = getDeviceName(Build.DEVICE, Build.MODEL, capitalize(Build.MODEL))

    fun getDeviceName(codename: String?, fallback: String?): String? {
        return getDeviceName(codename, codename, fallback)
    }

    fun getDeviceName(codename: String?, model: String?, fallback: String?): String? {
        val marketName = getDeviceInfo(context(), codename, model).marketName
        return marketName ?: fallback
    }

    @WorkerThread
    fun getDeviceInfo(context: Context): DeviceInfo {
        return getDeviceInfo(context.applicationContext, Build.DEVICE, Build.MODEL)
    }

    @WorkerThread
    fun getDeviceInfo(context: Context?, codename: String?): DeviceInfo {
        return getDeviceInfo(context, codename, null)
    }

    @WorkerThread
    fun getDeviceInfo(context: Context?, codename: String?, model: String?): DeviceInfo {
        val prefs = context!!.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val key = String.format("%s:%s", codename, model)
        val savedJson = prefs.getString(key, null)
        if (savedJson != null) {
            try {
                return DeviceInfo(JSONObject(savedJson))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        try {
            DeviceDatabase(context).use { database ->
                val info: DeviceInfo? = database.queryToDevice(codename, model)
                if (info != null) {
                    val json = JSONObject()
                    json.put("manufacturer", info.manufacturer)
                    json.put("codename", info.codename)
                    json.put("model", info.model)
                    json.put("market_name", info.marketName)
                    val editor = prefs.edit()
                    editor.putString(key, json.toString())
                    editor.apply()
                    return info
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (codename == Build.DEVICE && Build.MODEL == model) {
            DeviceInfo(Build.MANUFACTURER, codename, codename, model) // current device
        } else DeviceInfo(null, null, codename, model)
        // unknown device
    }

    private fun capitalize(str: String?): String? {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str!!.toCharArray()
        var capitalizeNext = true
        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(c.uppercaseChar())
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }
        return phrase.toString()
    }

    @SuppressLint("PrivateApi")
    private fun context(): Context? {
        if (context != null) return context

        // We didn't use to require holding onto the application context so let's cheat a little.
        try {
            return Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication")
                .invoke(null, null) as Application
        } catch (ignored: Exception) {
        }

        // Last attempt at hackery
        try {
            return Class.forName("android.app.AppGlobals")
                .getMethod("getInitialApplication")
                .invoke(null, null) as Application
        } catch (ignored: Exception) {
        }
        throw RuntimeException("DeviceName must be initialized before usage.")
    }

    class Request internal constructor(val context: Context) {
        val handler = Handler(context.mainLooper)
        var codename: String? = null
        var model: String? = null

        fun setCodename(codename: String?): Request {
            this.codename = codename
            return this
        }

        fun setModel(model: String?): Request {
            this.model = model
            return this
        }


        fun request(callback: Callback) {
            if (codename == null && model == null) {
                codename = Build.DEVICE
                model = Build.MODEL
            }
            val runnable = GetDeviceRunnable(callback)
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Thread(runnable).start()
            } else {
                runnable.run()
            }
        }

        private inner class GetDeviceRunnable(val callback: Callback) : Runnable {
            var deviceInfo: DeviceInfo? = null
            var error: Exception? = null
            override fun run() {
                try {
                    deviceInfo = getDeviceInfo(context, codename, model)
                } catch (e: Exception) {
                    error = e
                }
                handler.post { callback.onFinished(deviceInfo, error) }
            }
        }
    }

    interface Callback {
        fun onFinished(info: DeviceInfo?, error: Exception?)
    }

    class DeviceInfo {
        val manufacturer: String?
        val marketName: String?
        val codename: String?

        val model: String?

        constructor(marketName: String?, codename: String?, model: String?) : this(null, marketName, codename, model)
        constructor(manufacturer: String?, marketName: String?, codename: String?, model: String?) {
            this.manufacturer = manufacturer
            this.marketName = marketName
            this.codename = codename
            this.model = model
        }

        internal constructor(jsonObject: JSONObject) {
            manufacturer = jsonObject.getString("manufacturer")
            marketName = jsonObject.getString("market_name")
            codename = jsonObject.getString("codename")
            model = jsonObject.getString("model")
        }

        val name: String?
            get() = if (!TextUtils.isEmpty(marketName)) {
                marketName
            } else capitalize(model)
    }
}

