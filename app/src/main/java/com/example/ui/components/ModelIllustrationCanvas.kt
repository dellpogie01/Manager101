package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ModelIllustration(illustrationType: String, modifier: Modifier = Modifier) {
  val primaryColor = MaterialTheme.colorScheme.primary
  val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
  val onSurface = MaterialTheme.colorScheme.onSurface
  val secondaryColor = MaterialTheme.colorScheme.secondary

  Box(modifier = modifier.fillMaxWidth().height(180.dp)) {
    Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
      when (illustrationType) {
        "smart_goals" -> drawSmartGoals(primaryColor, surfaceVariant, onSurface)
        "swot_matrix", "matrix_2x2" -> draw2x2Matrix(primaryColor, surfaceVariant, onSurface)
        "eisenhower" -> drawEisenhower(primaryColor, surfaceVariant, onSurface)
        "five_forces" -> drawFiveForces(primaryColor, surfaceVariant, onSurface)
        "pdca_cycle" -> drawPdcaCycle(primaryColor, surfaceVariant, onSurface)
        "fishbone_diagram" -> drawFishbone(primaryColor, surfaceVariant, onSurface)
        "balanced_scorecard" -> drawBalancedScorecard(primaryColor, surfaceVariant, onSurface)
        "seven_s" -> drawSevenS(primaryColor, surfaceVariant, onSurface)
        "pestle_grid" -> drawPestle(primaryColor, surfaceVariant, onSurface)
        "bcg_grid" -> drawBcg(primaryColor, surfaceVariant, onSurface)
        "kanban_board" -> drawKanban(primaryColor, surfaceVariant, onSurface)
        "decision_tree" -> drawDecisionTree(primaryColor, surfaceVariant, onSurface)
        "break_even_chart" -> drawBreakEven(primaryColor, surfaceVariant, onSurface)
        "bar_chart" -> drawBarChart(primaryColor, surfaceVariant, onSurface)
        "pie_chart" -> drawPieChart(primaryColor, surfaceVariant, onSurface)
        "value_chain_diagram" -> drawValueChain(primaryColor, surfaceVariant, onSurface)
        else -> drawGenericFlow(primaryColor, surfaceVariant, onSurface)
      }
    }
  }
}

private fun DrawScope.drawSmartGoals(primary: Color, surface: Color, text: Color) {
  val w = size.width
  val h = size.height
  val boxWidth = w / 5.2f
  val boxHeight = h * 0.5f
  val startY = h * 0.25f

  val labels = listOf("S", "M", "A", "R", "T")
  labels.forEachIndexed { index, label ->
    val startX = index * (boxWidth + 8f) + 16f
    drawRect(
      color = if (index % 2 == 0) primary.copy(alpha = 0.2f) else primary.copy(alpha = 0.4f),
      topLeft = Offset(startX, startY),
      size = Size(boxWidth, boxHeight)
    )
    drawRect(
      color = primary,
      topLeft = Offset(startX, startY),
      size = Size(boxWidth, boxHeight),
      style = Stroke(width = 2.dp.toPx())
    )
  }
}

private fun DrawScope.draw2x2Matrix(primary: Color, surface: Color, text: Color) {
  val w = size.width
  val h = size.height
  val cx = w / 2f
  val cy = h / 2f

  // Draw 4 quadrants
  drawRect(color = primary.copy(alpha = 0.15f), topLeft = Offset(16f, 16f), size = Size(cx - 24f, cy - 24f))
  drawRect(color = primary.copy(alpha = 0.3f), topLeft = Offset(cx + 8f, 16f), size = Size(cx - 24f, cy - 24f))
  drawRect(color = primary.copy(alpha = 0.3f), topLeft = Offset(16f, cy + 8f), size = Size(cx - 24f, cy - 24f))
  drawRect(color = primary.copy(alpha = 0.15f), topLeft = Offset(cx + 8f, cy + 8f), size = Size(cx - 24f, cy - 24f))

  // Cross lines
  drawLine(color = text.copy(alpha = 0.5f), start = Offset(cx, 10f), end = Offset(cx, h - 10f), strokeWidth = 2f)
  drawLine(color = text.copy(alpha = 0.5f), start = Offset(10f, cy), end = Offset(w - 10f, cy), strokeWidth = 2f)
}

private fun DrawScope.drawEisenhower(primary: Color, surface: Color, text: Color) {
  draw2x2Matrix(primary, surface, text)
}

private fun DrawScope.drawFiveForces(primary: Color, surface: Color, text: Color) {
  val cx = size.width / 2f
  val cy = size.height / 2f
  val radius = 35f

  // Center core
  drawCircle(color = primary, radius = radius, center = Offset(cx, cy))

  // 4 surrounding circles
  val positions = listOf(
    Offset(cx, cy - 60f),
    Offset(cx, cy + 60f),
    Offset(cx - 90f, cy),
    Offset(cx + 90f, cy)
  )
  positions.forEach { pos ->
    drawCircle(color = primary.copy(alpha = 0.4f), radius = 20f, center = pos)
    drawLine(color = primary, start = Offset(cx, cy), end = pos, strokeWidth = 2f)
  }
}

private fun DrawScope.drawPdcaCycle(primary: Color, surface: Color, text: Color) {
  val cx = size.width / 2f
  val cy = size.height / 2f
  val radius = 55f

  drawCircle(color = primary.copy(alpha = 0.2f), radius = radius, center = Offset(cx, cy), style = Stroke(width = 24.dp.toPx()))
  drawArc(
    color = primary,
    startAngle = 0f,
    sweepAngle = 90f,
    useCenter = false,
    topLeft = Offset(cx - radius, cy - radius),
    size = Size(radius * 2, radius * 2),
    style = Stroke(width = 6.dp.toPx())
  )
}

private fun DrawScope.drawFishbone(primary: Color, surface: Color, text: Color) {
  val startX = 20f
  val endX = size.width - 20f
  val cy = size.height / 2f

  // Spine
  drawLine(color = primary, start = Offset(startX, cy), end = Offset(endX, cy), strokeWidth = 3f)

  // Ribs
  for (i in 1..3) {
    val x = startX + i * 50f
    drawLine(color = primary.copy(alpha = 0.7f), start = Offset(x, cy), end = Offset(x - 20f, cy - 40f), strokeWidth = 2f)
    drawLine(color = primary.copy(alpha = 0.7f), start = Offset(x, cy), end = Offset(x - 20f, cy + 40f), strokeWidth = 2f)
  }
}

private fun DrawScope.drawBalancedScorecard(primary: Color, surface: Color, text: Color) {
  draw2x2Matrix(primary, surface, text)
}

private fun DrawScope.drawSevenS(primary: Color, surface: Color, text: Color) {
  val cx = size.width / 2f
  val cy = size.height / 2f
  drawCircle(color = primary.copy(alpha = 0.3f), radius = 50f, center = Offset(cx, cy))
  drawCircle(color = primary, radius = 50f, center = Offset(cx, cy), style = Stroke(width = 2f))
}

private fun DrawScope.drawPestle(primary: Color, surface: Color, text: Color) {
  val w = size.width
  val h = size.height
  val colWidth = w / 3.2f
  val rowHeight = h * 0.4f

  for (i in 0..5) {
    val x = (i % 3) * (colWidth + 8f) + 16f
    val y = (i / 3) * (rowHeight + 8f) + 20f
    drawRect(color = primary.copy(alpha = 0.25f), topLeft = Offset(x, y), size = Size(colWidth, rowHeight))
  }
}

private fun DrawScope.drawBcg(primary: Color, surface: Color, text: Color) {
  draw2x2Matrix(primary, surface, text)
}

private fun DrawScope.drawKanban(primary: Color, surface: Color, text: Color) {
  val w = size.width
  val h = size.height
  val colWidth = w / 3.5f

  for (i in 0..2) {
    val x = i * (colWidth + 12f) + 16f
    drawRect(color = primary.copy(alpha = 0.2f), topLeft = Offset(x, 20f), size = Size(colWidth, h - 40f))
    drawRect(color = primary, topLeft = Offset(x + 8f, 35f), size = Size(colWidth - 16f, 25f))
    drawRect(color = primary.copy(alpha = 0.5f), topLeft = Offset(x + 8f, 70f), size = Size(colWidth - 16f, 25f))
  }
}

private fun DrawScope.drawDecisionTree(primary: Color, surface: Color, text: Color) {
  val cx = size.width / 2f
  drawCircle(color = primary, radius = 12f, center = Offset(cx, 30f))
  drawLine(color = primary, start = Offset(cx, 42f), end = Offset(cx - 60f, 90f), strokeWidth = 2f)
  drawLine(color = primary, start = Offset(cx, 42f), end = Offset(cx + 60f, 90f), strokeWidth = 2f)
  drawCircle(color = primary.copy(alpha = 0.7f), radius = 10f, center = Offset(cx - 60f, 100f))
  drawCircle(color = primary.copy(alpha = 0.7f), radius = 10f, center = Offset(cx + 60f, 100f))
}

private fun DrawScope.drawBreakEven(primary: Color, surface: Color, text: Color) {
  val startX = 30f
  val endX = size.width - 30f
  val startY = size.height - 30f
  val endY = 30f

  // Axes
  drawLine(color = text.copy(alpha = 0.5f), start = Offset(startX, startY), end = Offset(endX, startY), strokeWidth = 2f)
  drawLine(color = text.copy(alpha = 0.5f), start = Offset(startX, startY), end = Offset(startX, endY), strokeWidth = 2f)

  // Cost line
  drawLine(color = Color.Red, start = Offset(startX, startY - 20f), end = Offset(endX, endY + 20f), strokeWidth = 2f)
  // Revenue line
  drawLine(color = primary, start = Offset(startX, startY - 10f), end = Offset(endX, endY), strokeWidth = 2f)
}

private fun DrawScope.drawBarChart(primary: Color, surface: Color, text: Color) {
  val w = size.width
  val h = size.height
  val barWidth = 28f
  val spacing = 24f
  val startX = 40f
  val heights = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f)

  heights.forEachIndexed { index, ratio ->
    val x = startX + index * (barWidth + spacing)
    val barH = (h - 50f) * ratio
    val y = (h - 30f) - barH
    drawRect(color = primary, topLeft = Offset(x, y), size = Size(barWidth, barH))
  }
}

private fun DrawScope.drawPieChart(primary: Color, surface: Color, text: Color) {
  val cx = size.width / 2f
  val cy = size.height / 2f
  val radius = 50f

  drawArc(
    color = primary,
    startAngle = 0f,
    sweepAngle = 220f,
    useCenter = true,
    topLeft = Offset(cx - radius, cy - radius),
    size = Size(radius * 2, radius * 2)
  )
  drawArc(
    color = primary.copy(alpha = 0.4f),
    startAngle = 220f,
    sweepAngle = 140f,
    useCenter = true,
    topLeft = Offset(cx - radius, cy - radius),
    size = Size(radius * 2, radius * 2)
  )
}

private fun DrawScope.drawValueChain(primary: Color, surface: Color, text: Color) {
  val w = size.width
  val h = size.height
  val count = 5
  val boxW = (w - 40f) / count - 6f
  val boxH = h * 0.5f
  val y = h * 0.25f

  for (i in 0 until count) {
    val x = 20f + i * (boxW + 6f)
    drawRect(color = primary.copy(alpha = 0.3f + i * 0.12f), topLeft = Offset(x, y), size = Size(boxW, boxH))
  }
}

private fun DrawScope.drawGenericFlow(primary: Color, surface: Color, text: Color) {
  val w = size.width
  val cy = size.height / 2f
  val boxW = 70f
  val boxH = 40f

  for (i in 0..2) {
    val x = 30f + i * 110f
    drawRect(color = primary.copy(alpha = 0.4f + i * 0.2f), topLeft = Offset(x, cy - boxH / 2f), size = Size(boxW, boxH))
    if (i < 2) {
      drawLine(color = primary, start = Offset(x + boxW, cy), end = Offset(x + boxW + 40f, cy), strokeWidth = 2f)
    }
  }
}
