package ro.pub.cs.systems.eim.practicaltest02v6

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PracticalTest02v6MainActivity : AppCompatActivity() {
    private var serverPortText: EditText? = null
    private var clientAddressText: EditText? = null
    private var clientPortText: EditText? = null
    private var moneyTextView: TextView? = null
    private var serverConnectButton: Button? = null
    private var USDButton: Button? = null
    private var EURButton: Button? = null
    private var serverThread: ServerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test02v6_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        serverPortText = findViewById(R.id.server_port_edit_text)
        clientAddressText = findViewById(R.id.client_address_edit_text)
        clientPortText = findViewById(R.id.client_port_edit_text)
        moneyTextView = findViewById(R.id.money_text_view)

        serverConnectButton = findViewById(R.id.connect_button)
        serverConnectButton?.setOnClickListener(ButtonClickListener())

        USDButton = findViewById(R.id.get_usd)
        USDButton?.setOnClickListener(ButtonClickListener())

        EURButton = findViewById(R.id.get_eur)
        EURButton?.setOnClickListener(ButtonClickListener())
    }

    private inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(view: android.view.View?) {
            when (view?.id) {
                R.id.connect_button -> {
                    val serverPort = serverPortText?.getText().toString()
                    if (serverPort.isEmpty()) {
                        return
                    }
                    serverThread = ServerThread(serverPort.toInt())
                    serverThread!!.startServer()
                    Log.v(Constants.TAG, "Server started on port: $serverPort")
                }
                R.id.get_usd -> {
                    Log.v(Constants.TAG, "Get USD button clicked")

                    val clientAddress = clientAddressText?.getText().toString()
                    val clientPort = clientPortText?.getText().toString()
                    if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                        return
                    }

                    moneyTextView?.text = ""

                    val clientThread = ClientThread(clientAddress, clientPort.toInt(),"BTC-USD", moneyTextView!!)

                    clientThread.start()
                }
                R.id.get_eur ->{
                    Log.v(Constants.TAG, "Get EUR button clicked")

                    val clientAddress = clientAddressText?.getText().toString()
                    val clientPort = clientPortText?.getText().toString()
                    if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                        return
                    }

                    moneyTextView?.text = ""

                    val clientThread = ClientThread(clientAddress, clientPort.toInt(),"BTC-EUR", moneyTextView!!)

                    clientThread.start()
                }
            }
        }
    }

    override fun onDestroy() {
        serverThread!!.stopServer()
        super.onDestroy()
    }
}