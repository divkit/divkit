package tech.divkit.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivViewFacade
import tech.divkit.sample.databinding.ActivityMainPageBinding
import com.yandex.div2.DivAction

class MainPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding
    private val assetReader = AssetReader(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val divJson = assetReader.read("main_feed.json")
        val templatesJson = divJson.optJSONObject("templates")
        val cardsJson = divJson.getJSONArray("cards")

        val divContext = Div2Context(baseContext = this, configuration = createDivConfiguration())
        val listAdapter = DivListAdapter(divContext, templatesJson, cardsJson)
        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
    }

    fun createDivConfiguration(): DivConfiguration {
        return DivConfiguration.Builder(DemoDivImageLoader(this))
            .actionHandler(DemoDivActionHandler())
            .supportHyphenation(true)
            .visualErrorsEnabled(true)
            .build()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main_page, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    class DemoDivActionHandler: DivActionHandler() {
        override fun handleAction(action: DivAction, view: DivViewFacade): Boolean {
            return super.handleAction(action, view)
        }
    }
}
