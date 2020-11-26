package dk.nordfalk.snakomvejr

import dk.nordfalk.snakomvejr.model.WeatherData
import dk.nordfalk.snakomvejr.service.WeatherDataFetcher
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testWeather() {
        assertEquals(4, 2 + 2)

        var wd : WeatherData? = null;
        val ws = WeatherDataFetcher();
        val sem = java.util.concurrent.Semaphore(0)
        ws.getWeatherData {
            res: WeatherData -> println("res = $res")
            wd = res
            sem.release()
        }


        sem.acquire() // vent på svar
        assertEquals("København", wd!!.placename)

    }
}