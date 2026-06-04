package com.example.presentation.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.R
import com.example.presentation.adapter.SessionAdapter
import com.example.presentation.adapter.TestResultAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class HistoryFragment : Fragment() {

    private val viewModel: HistoryViewModel by viewModel()

    private lateinit var layoutFiltersActions: View
    private lateinit var btnBackToSessions: MaterialButton
    private lateinit var btnExportSession: MaterialButton
    private lateinit var chipGroupFilters: ChipGroup

    private lateinit var recyclerSessions: RecyclerView
    private lateinit var recyclerResults: RecyclerView
    private lateinit var tvEmpty: TextView

    private lateinit var sessionAdapter: SessionAdapter
    private lateinit var resultAdapter: TestResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupRecyclerViews()
        setupListeners()
        observeViewModel()
    }

    private fun initializeViews(view: View) {
        layoutFiltersActions = view.findViewById(R.id.layout_filters_actions)
        btnBackToSessions = view.findViewById(R.id.btn_back_to_sessions)
        btnExportSession = view.findViewById(R.id.btn_export_session)
        chipGroupFilters = view.findViewById(R.id.chip_group_filters)

        recyclerSessions = view.findViewById(R.id.recycler_sessions)
        recyclerResults = view.findViewById(R.id.recycler_results)
        tvEmpty = view.findViewById(R.id.tv_empty_history)
    }

    private fun setupRecyclerViews() {
        sessionAdapter = SessionAdapter { session ->
            viewModel.selectSession(session.id)
            layoutFiltersActions.visibility = View.VISIBLE
            recyclerSessions.visibility = View.GONE
            recyclerResults.visibility = View.VISIBLE
        }

        resultAdapter = TestResultAdapter()

        recyclerSessions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sessionAdapter
        }

        recyclerResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = resultAdapter
        }
    }

    private fun setupListeners() {
        btnBackToSessions.setOnClickListener {
            layoutFiltersActions.visibility = View.GONE
            recyclerResults.visibility = View.GONE
            recyclerSessions.visibility = View.VISIBLE
        }

        btnExportSession.setOnClickListener {
            val sessionId = viewModel.selectedSessionId.value ?: return@setOnClickListener
            val fileName = "session_${sessionId}_results.json"
            viewModel.exportToFile(requireContext(), fileName)
        }

        chipGroupFilters.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: R.id.chip_filter_all
            val filter = when (checkedId) {
                R.id.chip_filter_success -> "success"
                R.id.chip_filter_failure -> "failure"
                else -> "all"
            }
            viewModel.setFilter(filter)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                
                launch {
                    viewModel.sessions.collectLatest { sessions ->
                        sessionAdapter.submitList(sessions)
                        tvEmpty.visibility = if (sessions.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.filteredResults.collectLatest { results ->
                        resultAdapter.submitList(results)
                    }
                }

                launch {
                    viewModel.exportStatus.collect { message ->
                        val ctx = context ?: return@collect
                        if (message.contains("نجاح") || message.contains("تم")) {
                            com.example.util.ToastHelper.showSuccessToast(ctx, message)
                        } else {
                            com.example.util.ToastHelper.showErrorToast(ctx, message)
                        }
                    }
                }
            }
        }
    }
}
