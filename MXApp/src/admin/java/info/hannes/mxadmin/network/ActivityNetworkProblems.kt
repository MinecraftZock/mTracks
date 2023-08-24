package info.hannes.mxadmin.network

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import info.hannes.mxadmin.adapter.NetworkProblemAdapter
import info.mx.tracks.base.ActivityRx
import info.mx.tracks.databinding.ActivityNetworkProblemsBinding

class ActivityNetworkProblems : ActivityRx() {

    private lateinit var binding: ActivityNetworkProblemsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNetworkProblemsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val networkProblemAdapter = NetworkProblemAdapter(this)

        val layoutManager = LinearLayoutManager(this)
        binding.contentActivityNetworkIssues.listNetworkProblems.layoutManager = layoutManager
        binding.contentActivityNetworkIssues.listNetworkProblems.adapter = networkProblemAdapter

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            // onBackPressed()
            finish()
        }
    }

}
