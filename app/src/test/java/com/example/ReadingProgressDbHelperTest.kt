package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ReadingProgressDbHelper
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ReadingProgressDbHelperTest {

    private lateinit var context: Context
    private lateinit var dbHelper: ReadingProgressDbHelper

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        dbHelper = ReadingProgressDbHelper(context)
        dbHelper.clearProgress("user_alice")
        dbHelper.clearProgress("user_bob")
    }

    @After
    fun tearDown() {
        dbHelper.clearProgress("user_alice")
        dbHelper.clearProgress("user_bob")
        dbHelper.close()
    }

    @Test
    fun `mark completed records progress and returns true`() {
        val success = dbHelper.markCompleted(userId = "user_alice", modelId = "smart_goals")
        assertTrue(success)
        assertTrue(dbHelper.isCompleted(userId = "user_alice", modelId = "smart_goals"))
        assertFalse(dbHelper.isCompleted(userId = "user_alice", modelId = "swot_analysis"))
    }

    @Test
    fun `marking duplicate item updates record without creating duplicate entries`() {
        dbHelper.markCompleted(userId = "user_alice", modelId = "smart_goals", completedAt = 1000L)
        dbHelper.markCompleted(userId = "user_alice", modelId = "smart_goals", completedAt = 2000L)

        val completedIds = dbHelper.getCompletedModelIds("user_alice")
        assertEquals(1, completedIds.size)
        assertTrue(completedIds.contains("smart_goals"))

        val records = dbHelper.getAllRecords("user_alice")
        assertEquals(1, records.size)
        assertEquals(2000L, records[0].completedAt)
    }

    @Test
    fun `reading progress is isolated between different users`() {
        dbHelper.markCompleted(userId = "user_alice", modelId = "smart_goals")
        dbHelper.markCompleted(userId = "user_bob", modelId = "swot_analysis")

        val aliceCompleted = dbHelper.getCompletedModelIds("user_alice")
        val bobCompleted = dbHelper.getCompletedModelIds("user_bob")

        assertEquals(setOf("smart_goals"), aliceCompleted)
        assertEquals(setOf("swot_analysis"), bobCompleted)
    }

    @Test
    fun `getCompletedModelIds returns empty set when no items completed`() {
        val completed = dbHelper.getCompletedModelIds("user_non_existent")
        assertTrue(completed.isEmpty())
    }
}
