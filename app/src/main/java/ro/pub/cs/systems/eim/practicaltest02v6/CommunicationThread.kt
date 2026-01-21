package ro.pub.cs.systems.eim.practicaltest02v6

import android.util.Log
import android.widget.EditText
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException
import ro.pub.cs.systems.eim.practicaltest02v6.Utilities.getReader
import ro.pub.cs.systems.eim.practicaltest02v6.Utilities.getWriter
import java.net.Socket

class CommunicationThread(private val serverThread: ServerThread ,private val socket: Socket) :
    Thread() {
    override fun run() {
        try {
            Log.v(
                Constants.TAG,
                "Connection opened to " + socket.getLocalAddress() + ":" + socket.getLocalPort() + " from " + socket.getInetAddress()
            )
            val bufferedReader = getReader(socket)
            val printWriter = getWriter(socket)

            Log.v(Constants.TAG, "Waiting for parameters from client")

            val currency = bufferedReader.readLine()

            if(currency == null) {
                Log.e(Constants.TAG, "Error receiving parameters from client")
                socket.close()
                return
            }

            var data = serverThread.getData()
            var moneyInfo : MoneyInfo? = null

            if(data.containsKey(currency)) {
                val temp = data[currency]

                val old_time = temp?.time
                val current_time = System.currentTimeMillis() / 1000

                if(current_time - old_time!! <= Constants.WEB_SERVICE_CACHE_TIME) {
                    moneyInfo = temp
                    Log.v(Constants.TAG, "Getting the information from the cache...")
                }

            }

            if (moneyInfo == null) {
                Log.v(Constants.TAG, "Getting the information from the webservice...")

                val client = OkHttpClient()

                val request = Request.Builder()
                    .url(Constants.WEB_SERVICE_ADDRESS
                            + "?market=cadli"
                            + "&instruments=" + currency)
                    .build()

                val response = client.newCall(request).execute()
                var result : JSONObject? = null
                if (response.isSuccessful) {
                    result = JSONObject(response.body.string()).getJSONObject("Data").getJSONObject(currency)
                } else {
                    Log.e(Constants.TAG, "The request was not successful: " + response.message)
                    return
                }

                Log.v(Constants.TAG, "Response from webservice: $result")

                val value = result.getDouble("VALUE").toString()

                moneyInfo = MoneyInfo(currency, value, System.currentTimeMillis() / 1000)

                serverThread.setData(currency,moneyInfo)
            }

            Log.v(Constants.TAG, "Sending the information to the client...${moneyInfo.value}")
            printWriter.println(moneyInfo.value)
            printWriter.flush()

            socket.close()
            Log.v(Constants.TAG, "Connection closed")
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.message)
        }
    }
}