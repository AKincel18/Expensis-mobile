package pl.polsl.expensis_mobile.utils

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat.getColor
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.dto.stats.StatsResponseDTO
import pl.polsl.expensis_mobile.others.StatsName
import kotlin.math.round

class ChartBuilder(private val context: Context, private val chart: BarChart,
                   private val statsResponseDTOS: List<StatsResponseDTO>,
                   private val statsName: StatsName?
) {
    private val userColor = Color.rgb(0, 170, 116)
    private val othersColor = Color.rgb(255, 166, 0)

    private val textSize = 12f

    fun buildChart() {
        if (statsName == StatsName.SEPARATED) {
            buildSeparatedChart()
        } else {
            buildOthersChart()
        }
    }

    private fun buildOthersChart() {
        val userDataEntries: MutableList<BarEntry> = arrayListOf()
        val allDataEntries: MutableList<BarEntry> = arrayListOf()
        val groupNames: MutableList<String> = arrayListOf()
        var iterator = 0.0f
        for (statsResponseDTO in statsResponseDTOS) {
            userDataEntries.add(BarEntry(iterator, statsResponseDTO.userValue))
            allDataEntries.add(BarEntry(iterator, statsResponseDTO.allValue))

            groupNames.add(statsResponseDTO.nameValue)
            iterator += 3f
        }

        val userSet = prepareChartSet(userDataEntries, "Me", userColor)

        val othersSet = prepareChartSet(allDataEntries, "Others", othersColor)

        val barData = BarData(userSet, othersSet)

        val groupSpace = 0.10f
        val barSpace = 0.0f
        val barWidth = 0.45f
        barData.barWidth = barWidth

        chart.data = barData
        chart.groupBars(0.toFloat(), groupSpace, barSpace)

        prepareXAxisSettings(groupNames)
        prepareChartSettings()
    }

    private fun buildSeparatedChart() {
        val allDataEntries: MutableList<BarEntry> = arrayListOf()
        val barNames: MutableList<String> = arrayListOf()
        var userValue = 0.0f
        var iterator = 0.5f
        for (statsResponseDTO in statsResponseDTOS) {
            if (statsResponseDTO.userValue != 0.0f)
                userValue = statsResponseDTO.userValue
            allDataEntries.add(BarEntry(iterator, statsResponseDTO.allValue))
            barNames.add(statsResponseDTO.nameValue)
            iterator += 1f
        }

        val othersSet = prepareChartSet(allDataEntries, "Others", othersColor)

        val barData = BarData(othersSet)

        val line = LimitLine(userValue)
        line.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        line.lineColor = userColor
        line.lineWidth = 1.5f
        line.textSize = textSize
        line.yOffset = 5f
        chart.axisLeft.addLimitLine(line)
        chart.data = barData
        val lineLegend = LegendEntry()
        lineLegend.label = "Me: ${round(userValue).toInt()}"
        lineLegend.formColor = userColor
        val legends = chart.legend.entries.toMutableList()
        legends.add(lineLegend)
        legends.reverse()
        chart.legend.setCustom(legends)
        prepareXAxisSettings(barNames)
        prepareChartSettings()
    }

    private fun prepareChartSet(userDataEntries: MutableList<BarEntry>, textLabel: String, color: Int): BarDataSet {
        val userSet = BarDataSet(userDataEntries, textLabel)
        userSet.color = color
        userSet.valueTextColor = color
        userSet.valueTextSize = textSize
        userSet.axisDependency = YAxis.AxisDependency.LEFT
        userSet.valueFormatter = DefaultValueFormatter(0)
        return userSet
    }

    private fun prepareXAxisSettings(labelNames: MutableList<String>) {
        val xAxis = chart.xAxis
        xAxis.setCenterAxisLabels(true)
        xAxis.axisMinimum = 0.0f
        xAxis.axisMaximum = statsResponseDTOS.size.toFloat()
        xAxis.valueFormatter = IndexAxisValueFormatter(labelNames)
        xAxis.textSize = textSize
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.textColor = getColor(context, R.color.primaryColor)
    }

    private fun prepareChartSettings() {
        chart.axisLeft.textColor = getColor(context, R.color.primaryColor)
        chart.axisLeft.axisMinimum = 0f
        chart.axisLeft.axisMaximum = getRoundedMaxValue()
        chart.axisRight.isEnabled = false
        chart.legend.textColor = getColor(context, R.color.primaryColor)
        chart.legend.textSize = textSize
        chart.description.isEnabled = false
        chart.setVisibleXRangeMaximum(4f)
        chart.extraBottomOffset = 10f
        chart.axisLeft.textSize = textSize
        chart.animateY(2500)
        chart.invalidate()
    }

    private fun getRoundedMaxValue(): Float {
        val maxAllValue = statsResponseDTOS.maxBy { response -> response.allValue }!!.allValue
        val maxUserValue = statsResponseDTOS.maxBy { response -> response.userValue }!!.userValue
        return if (maxAllValue >= maxUserValue)
            maxAllValue + maxAllValue / 10
        else
            maxUserValue + maxUserValue / 10
    }
}