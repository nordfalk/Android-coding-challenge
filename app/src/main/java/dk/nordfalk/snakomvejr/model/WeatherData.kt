package dk.nordfalk.snakomvejr.model

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class WeatherData {

    fun interface WeatherCallback {
        fun callback(res: String?)
    }
/*
Ville være en del bedre: https://www.dmi.dk/dmidk_byvejrWS/rest/texts/2610803
Skyet vejr med lidt regn af og til, især først på dagen, og temp. op omkring 8 grader. Jævn til frisk vind fra sydvest, ved kysterne stedvis op til hård vind, som aftager lidt sidst på dagen. I aften efterhånden mest tørt vejr, og i nat klarer det noget op sydfra. Temp. mellem 6 og 8 grader og let til frisk vind fra sydvest og syd.

 */



    fun getDescription(callback: WeatherCallback) {


        // http://api.openweathermap.org/data/2.5/weather?q=Copenhagen,dk&lang=da&APPID=37b3dc24f334b47e8a0bc838110e119a
        val url = "http://api.openweathermap.org/data/2.5/weather?q=Copenhagen,dk&lang=da&APPID=37b3dc24f334b47e8a0bc838110e119a"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.callback("Fejl: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body!!.string()
                println("res = $res")
                try {
                    val obj = JSONObject(res)
                    val des = obj.getJSONArray("weather").getJSONObject(0).getString("description")
                    println("des = $des")
                    callback.callback(des)
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback.callback("Fejl: $e")
                }
            }
        })

    }
}