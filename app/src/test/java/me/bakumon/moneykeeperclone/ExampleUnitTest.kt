/*
 * Copyright 2018 Bakumon. https://github.com/Bakumon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package me.bakumon.moneykeeperclone

import me.bakumon.moneykeeperclone.utill.BigDecimalUtil
import me.bakumon.moneykeeperclone.utill.DateUtils
import me.bakumon.moneykeeperclone.utill.Pi
import org.junit.Test
import java.math.BigDecimal

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
//        assertEquals(4, (2 + 2).toLong())
        val index = DateUtils.month2Index("2018-07")
        System.out.print(index)
    }

    @Test
    @Throws(Exception::class)
    fun inputFilter(){
        val text = "1234590.09"
        var result = ""
        if (text.contains(".")) {
            val splitList = text.split(".")
            if (splitList[1].length > 2) {
                result = splitList[0] + "." + splitList[1].substring(0, 2)
            } else {
                result = text
            }
        } else {
            result = text
        }

        System.out.print(result)
    }

    @Test
    @Throws(Exception::class)
    fun decimalTest(){
        val result = BigDecimalUtil.fen2Yuan(BigDecimal("566699"))
        System.out.print(result)
    }
}