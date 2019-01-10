package me.bakumon.moneykeeper

import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)
        val today = LocalDate.of(2019, 1, 31)
        val nextWeek = today.minusMonths(-1)

        val nextWeek1 = today.minusMonths(2)

        System.out.println("Today is : " + today)
        System.out.println("Date after 1 month : " + nextWeek)
        System.out.println("Date after 2 month : " + nextWeek1)



    }
}
