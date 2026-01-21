package ro.pub.cs.systems.eim.practicaltest02v6

import android.util.Log
import android.widget.TextView
import ro.pub.cs.systems.eim.practicaltest02v6.Utilities.getReader
import ro.pub.cs.systems.eim.practicaltest02v6.Utilities.getWriter
import java.io.IOException
import java.net.Socket

class ClientThread(val clientAddress: String, val port:Int, val currency:String,
                   var moneyTextView: TextView) : Thread() {

    override fun run() {
        var socket: Socket? = null
        try {
            Log.v(Constants.TAG, "ClientThread started")
            socket = Socket(clientAddress, port)

            val bufferedReader = getReader(socket)
            val printWriter = getWriter(socket)

            printWriter.println(currency)
            printWriter.flush()

            var moneyInformation: String? = null

            while (true) {
                val line = bufferedReader.readLine() ?: break
                moneyInformation = if (moneyInformation == null) {
                    line
                } else {
                    moneyInformation + "\n" + line
                }
            }

            Log.v(Constants.TAG, "Weather information received: $moneyInformation")
            moneyTextView.post {
                moneyTextView.text = moneyInformation ?: "No money information received"
            }

        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.message)

        } finally {
            try {
                socket?.close()
            } catch (ioException: IOException) {
                Log.e(Constants.TAG, "An exception has occurred: " + ioException.message)
            }
        }
    }
}