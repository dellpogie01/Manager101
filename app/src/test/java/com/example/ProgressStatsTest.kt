package com.example

import com.example.model.ProgressStats
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressStatsTest {

    @Test
    fun `zero completed items yields zero percent`() {
        val stats = ProgressStats.calculate(totalEligible = 159, distinctCompletedCount = 0)
        assertEquals(159, stats.totalEligible)
        assertEquals(0, stats.itemsRead)
        assertEquals(159, stats.itemsRemaining)
        assertEquals(0, stats.completionPercentage)
    }

    @Test
    fun `zero eligible items yields zero percent and prevents division by zero`() {
        val stats = ProgressStats.calculate(totalEligible = 0, distinctCompletedCount = 0)
        assertEquals(0, stats.totalEligible)
        assertEquals(0, stats.itemsRead)
        assertEquals(0, stats.itemsRemaining)
        assertEquals(0, stats.completionPercentage)
    }

    @Test
    fun `fifteen of twenty items yields seventy five percent`() {
        val stats = ProgressStats.calculate(totalEligible = 20, distinctCompletedCount = 15)
        assertEquals(20, stats.totalEligible)
        assertEquals(15, stats.itemsRead)
        assertEquals(5, stats.itemsRemaining)
        assertEquals(75, stats.completionPercentage)
    }

    @Test
    fun `all items completed yields exactly one hundred percent`() {
        val stats = ProgressStats.calculate(totalEligible = 159, distinctCompletedCount = 159)
        assertEquals(159, stats.totalEligible)
        assertEquals(159, stats.itemsRead)
        assertEquals(0, stats.itemsRemaining)
        assertEquals(100, stats.completionPercentage)
    }

    @Test
    fun `almost all items completed does not prematurely round to one hundred percent`() {
        val stats = ProgressStats.calculate(totalEligible = 1000, distinctCompletedCount = 999)
        assertEquals(999, stats.itemsRead)
        assertEquals(1, stats.itemsRemaining)
        assertEquals(99, stats.completionPercentage)
    }

    @Test
    fun `single item completed in large library does not round down to zero percent`() {
        val stats = ProgressStats.calculate(totalEligible = 1000, distinctCompletedCount = 1)
        assertEquals(1, stats.itemsRead)
        assertEquals(999, stats.itemsRemaining)
        assertEquals(1, stats.completionPercentage)
    }

    @Test
    fun `completed count exceeding total eligible is clamped safely`() {
        val stats = ProgressStats.calculate(totalEligible = 50, distinctCompletedCount = 60)
        assertEquals(50, stats.itemsRead)
        assertEquals(0, stats.itemsRemaining)
        assertEquals(100, stats.completionPercentage)
    }
}
