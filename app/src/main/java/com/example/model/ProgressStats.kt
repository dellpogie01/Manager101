package com.example.model

data class ProgressStats(
    val totalEligible: Int = 0,
    val itemsRead: Int = 0,
    val itemsRemaining: Int = 0,
    val completionPercentage: Int = 0
) {
    companion object {
        fun calculate(totalEligible: Int, distinctCompletedCount: Int): ProgressStats {
            if (totalEligible <= 0) {
                return ProgressStats(
                    totalEligible = 0,
                    itemsRead = 0,
                    itemsRemaining = 0,
                    completionPercentage = 0
                )
            }
            val safeCompleted = distinctCompletedCount.coerceIn(0, totalEligible)
            val remaining = (totalEligible - safeCompleted).coerceAtLeast(0)
            val percentage = when {
                safeCompleted == 0 -> 0
                safeCompleted == totalEligible -> 100
                else -> {
                    val raw = (safeCompleted.toDouble() / totalEligible.toDouble()) * 100.0
                    val rounded = Math.round(raw).toInt()
                    when {
                        rounded >= 100 -> 99
                        rounded <= 0 -> 1
                        else -> rounded
                    }
                }
            }
            return ProgressStats(
                totalEligible = totalEligible,
                itemsRead = safeCompleted,
                itemsRemaining = remaining,
                completionPercentage = percentage
            )
        }
    }
}
