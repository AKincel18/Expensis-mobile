package pl.polsl.expensis_mobile.activities.stats

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.display_stats_activity.*
import kotlinx.android.synthetic.main.stats_activity.backButton
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.dto.stats.StatsResponseDTO
import pl.polsl.expensis_mobile.others.StatsName
import pl.polsl.expensis_mobile.utils.ChartBuilder
import pl.polsl.expensis_mobile.utils.IntentKeys

class DisplayStatsActivity : AppCompatActivity() {

    private var statsResponseDTOS: List<StatsResponseDTO>? = null
    private var statsName: StatsName? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_stats_activity)
        fetchDataStats()
        ChartBuilder(this, chart, statsResponseDTOS!!, statsName).buildChart()
        onBackClicked()
    }

    private fun fetchDataStats() {
        val intent: Intent = intent
        if (intent.hasExtra(IntentKeys.DATA_NAME)) {
            val ordinalEnum = intent.getIntExtra(IntentKeys.DATA_NAME, -1)
            if (ordinalEnum >= 0 && ordinalEnum < StatsName.values().size)
                statsName = StatsName.values()[ordinalEnum]
            displayStatsTitle.text = statsName.toString()
        }
        if (intent.hasExtra(IntentKeys.DATA_STATS)) {
            val dataStats = (intent.getStringExtra(IntentKeys.DATA_STATS))
            val type = object : TypeToken<List<StatsResponseDTO>>() {}.type
            statsResponseDTOS = Gson().fromJson<List<StatsResponseDTO>>(dataStats, type)
        }
    }

    private fun onBackClicked() {
        backButton.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }
    }
}