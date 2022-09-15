package com.example.charttopdf

import android.util.Log
import org.junit.Test

import org.junit.Assert.*
import kotlin.reflect.KProperty

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val TAG = "ExampleUnitTest"
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    data class TestReflection(
        val timeInRange : Float,
        val numRecords : Int
    )
    @Test
    fun reflection_stuff() {
        for (member in TestReflection::class.members) {
            when(member) {
                is KProperty -> {
                    if (member == TestReflection::numRecords) {

                    }
                    println(TAG + "Property name ${member.name}")
                }
            }
        }

    }
}