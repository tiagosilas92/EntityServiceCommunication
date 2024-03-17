package br.edu.ifsp.scl.sdm.entityservicecommunication

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message

class IncrementService : Service() {
    //Qunado e criado o event handler ele rrecebe a mennsagem com o valor da intent
    //Apos receber o valor ele vai incrementar e colocar dentro do vaalue live data
    //Utilizada para rodar em thread separada a arrea de memoria heap ainda e compartilhada entre elas
    //o local e acessiivel para ambas as threads
    private inner class IncrementHandler(looper: Looper):Handler(looper){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            msg.data.getInt("VALUE").also {
                InterEntityCommunication.valueLiveData.postValue(it + 1)
            }
            stopSelf()
        }
    }
    //Metodo recebe o mais importante que e a intent onde sera carregado o valor que sera incrementado
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getIntExtra("VALUE", -1)?.also {value ->
            HandlerThread("IncrementThrread").apply {
                start()
                IncrementHandler(looper).apply {
                    obtainMessage().apply {
                        data.putInt("VALUE", value)
                        sendMessage(this)
                    }
                }
            }
//            InterEntityCommunication.valueLiveData.postValue(it + 1)
        }
        return START_NOT_STICKY
    }

    //Serviço iniciado nao necessita do metodo onBind, porem e obrigado a ter declarado
    override fun onBind(intent: Intent): IBinder? = null
}