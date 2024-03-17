package br.edu.ifsp.scl.sdm.entityservicecommunication

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log

class IncrementBound : Service() {
    private inner class IncrementBoundServiceHandler(looper: Looper): Handler(looper){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            msg.replyTo?.also {
                Log.v(this.javaClass.simpleName,"Return incremented value.")

                clientMessenger = it
            }

            msg.data.getInt("VALUE").also {
                clientMessenger.send(Message.obtain().apply {data.putInt("VALUE", it + 1)})
            }
            stopSelf()
        }
    }
    private lateinit var ibsMessenger: Messenger
    private lateinit var ibsHandler: IncrementBoundServiceHandler
    private lateinit var clientMessenger : Messenger
    override fun onCreate() {
        super.onCreate()
        HandlerThread(this.javaClass.simpleName).apply {
            start()
            ibsHandler = IncrementBoundServiceHandler(looper)
        }
    }
    override fun onBind(intent: Intent): IBinder {
        Log.v(this.javaClass.simpleName,"Entity bound to the Service")
        ibsMessenger = Messenger(ibsHandler)
        return ibsMessenger.binder
    }


    override fun unbindService(conn: ServiceConnection) {
        Log.v(this.javaClass.simpleName,"Entity unbound to the Service")
        super.unbindService(conn)
    }
}