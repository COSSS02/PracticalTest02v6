package ro.pub.cs.systems.eim.practicaltest02v6

import android.util.Log
import android.widget.EditText
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket

class ServerThread(val port: Int) : Thread() {
    private var isRunning = false

    private var serverSocket: ServerSocket? = null
    private var data: HashMap<String, MoneyInfo> = HashMap()

    fun startServer() {
        isRunning = true
        start()
        Log.v(Constants.TAG, "startServer() method was invoked")
    }

    fun stopServer() {
        isRunning = false
        try {
            serverSocket!!.close()
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.message)
        }
        Log.v(Constants.TAG, "stopServer() method was invoked")
    }

    override fun run() {
        try {
            serverSocket = ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"))
            while (isRunning) {
                val socket = serverSocket!!.accept()
                Log.v(Constants.TAG, "accept()-ed: " + socket.getInetAddress() + ":" + socket.getPort())
                val communicationThread = CommunicationThread(this, socket)
                communicationThread.start()
            }
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.message)
        }
    }

    @Synchronized
    public fun getData(): HashMap<String, MoneyInfo> {
        return data
    }

    @Synchronized
    public fun setData(currency: String, data: MoneyInfo) {
        this.data = this.data.apply {
            put(currency, data)
        }
    }
}