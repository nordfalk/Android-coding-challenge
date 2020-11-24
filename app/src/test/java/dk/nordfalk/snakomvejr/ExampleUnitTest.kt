package dk.nordfalk.snakomvejr

import dk.nordfalk.snakomvejr.model.WeatherData
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        val wd = WeatherData();
        wd.getDescription( { res: String? -> println("res = $res") })
        Thread.sleep(1000) // vent på svar

    }
}