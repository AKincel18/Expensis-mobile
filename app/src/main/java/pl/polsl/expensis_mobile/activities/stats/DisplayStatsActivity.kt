package pl.polsl.expensis_mobile.activities.stats

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.display_stat_activity.*
import kotlinx.android.synthetic.main.stats_activity.backButton
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.dto.stats.StatResponseDTO
import pl.polsl.expensis_mobile.others.StatName
import pl.polsl.expensis_mobile.utils.ChartBuilder
import pl.polsl.expensis_mobile.utils.IntentKeys

class DisplayStatsActivity : AppCompatActivity() {

    private var statResponseDTOs: List<StatResponseDTO>? = null
    private var statName: StatName? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_stat_activity)
        fetchDataStats()
        ChartBuilder(this, chart, statResponseDTOs!!, statName).buildChart()
        onBackClicked()
    }

    private fun fetchDataStats() {
        val intent: Intent = intent
        if (intent.hasExtra(IntentKeys.DATA_NAME)) {
            val ordinalEnum = intent.getIntExtra(IntentKeys.DATA_NAME, -1)
            if (ordinalEnum >= 0 && ordinalEnum < StatName.values().size)
                statName = StatName.values()[ordinalEnum]
            displayStatTitle.text = statName.toString()
        }
        if (intent.hasExtra(IntentKeys.DATA_STATS)) {
            val dataStats = (intent.getStringExtra(IntentKeys.DATA_STATS))
            val type = object : TypeToken<List<StatResponseDTO>>() {}.type
            statResponseDTOs = Gson().fromJson<List<StatResponseDTO>>(dataStats, type)
        }
    }

    private fun onBackClicked() {
        backButton.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }
    }
}