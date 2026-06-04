package com.example.presentation.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.R
import com.example.data.local.entity.RouterProfileEntity
import com.example.presentation.adapter.LogAdapter
import com.example.service.TestService
import com.example.widget.ConnectionStatusView
import com.example.widget.CustomProgressBar
import com.example.widget.StatisticCard
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()

    private lateinit var spinnerRouter: Spinner
    private lateinit var tvRouterInfo: TextView
    private lateinit var etPrefix: TextInputEditText
    private lateinit var etLength: TextInputEditText
    private lateinit var etCharset: TextInputEditText
    private lateinit var etCount: TextInputEditText
    private lateinit var connectionStatusView: ConnectionStatusView

    private lateinit var cardSent: StatisticCard
    private lateinit var cardSuccess: StatisticCard
    private lateinit var cardFailed: StatisticCard

    private lateinit var layoutProgress: View
    private lateinit var tvProgress: TextView
    private lateinit var progressBar: CustomProgressBar

    private lateinit var btnStart: MaterialButton
    private lateinit var btnStop: MaterialButton
    private lateinit var btnClearLog: MaterialButton

    private lateinit var logAdapter: LogAdapter
    private lateinit var recyclerLog: RecyclerView

    private var routerList: List<RouterProfileEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupLogRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun initializeViews(view: View) {
        spinnerRouter = view.findViewById(R.id.spinner_router)
        tvRouterInfo = view.findViewById(R.id.tv_router_info)
        etPrefix = view.findViewById(R.id.et_prefix)
        etLength = view.findViewById(R.id.et_length)
        etCharset = view.findViewById(R.id.et_charset)
        etCount = view.findViewById(R.id.et_count)
        connectionStatusView = view.findViewById(R.id.connection_status_view)

        cardSent = view.findViewById(R.id.card_sent)
        cardSuccess = view.findViewById(R.id.card_success)
        cardFailed = view.findViewById(R.id.card_failed)

        layoutProgress = view.findViewById(R.id.layout_progress)
        tvProgress = view.findViewById(R.id.tv_progress)
        progressBar = view.findViewById(R.id.progress_bar)

        btnStart = view.findViewById(R.id.btn_start_test)
        btnStop = view.findViewById(R.id.btn_stop_test)
        btnClearLog = view.findViewById(R.id.btn_clear_log_extra)
    }

    private fun setupLogRecyclerView() {
        recyclerLog = view?.findViewById(R.id.recycler_log) ?: return
        logAdapter = LogAdapter()
        recyclerLog.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = logAdapter
        }
    }

    private fun setupListeners() {
        spinnerRouter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position in routerList.indices) {
                    val router = routerList[position]
                    viewModel.selectRouter(router.id)
                    tvRouterInfo.text = "IP: ${router.ip} | Path: ${router.loginPath}"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnStart.setOnClickListener {
            val prefix = etPrefix.text?.toString() ?: ""
            val length = etLength.text?.toString()?.toIntOrNull() ?: 6
            val charset = etCharset.text?.toString() ?: "0123456789"
            val count = etCount.text?.toString()?.toIntOrNull() ?: 10

            // Check if connected to Wi-Fi!
            if (viewModel.connectionState.value == HomeViewModel.ConnectionState.DISCONNECTED) {
                com.example.util.DialogHelper.showCustomDialog(
                    context = requireContext(),
                    title = "تنبيه: غير متصل بالواي فاي",
                    message = "أنت غير متصل بشبكة الواي فاي حالياً. يرجى الاتصال بالشبكة لكي تتمكن من الوصول لصفحة تسجيل دخول الراوتر واختبار البطاقات بنجاح.",
                    dialogType = com.example.util.DialogHelper.DialogType.ERROR,
                    positiveButtonText = "حسناً، فهمت"
                )
                return@setOnClickListener
            }

            if (viewModel.isServiceRunning().value) {
                com.example.util.DialogHelper.showCustomDialog(
                    context = requireContext(),
                    title = "عملية جارية بالفعل",
                    message = "هناك عملية فحص جارية حالياً على الراوتر المختار. هل تود إلغاء العملية الجارية والبدء مجدداً بعملية فحص جديدة، أم تود المتابعة ومراقبة الفحص الحالي؟",
                    dialogType = com.example.util.DialogHelper.DialogType.WARNING,
                    positiveButtonText = "إلغاء والبدء من جديد",
                    positiveAction = {
                        // 1. Cancel previous service
                        val serviceIntent = Intent(requireContext(), TestService::class.java).apply {
                            action = TestService.ACTION_CANCEL
                        }
                        requireContext().startService(serviceIntent)

                        // 2. Start a new test
                        viewModel.generateAndStart(
                            prefix = prefix,
                            length = length,
                            count = count,
                            charset = charset,
                            delayMs = 2000L
                        )
                    },
                    negativeButtonText = "المتابعة ومراقبة الفحص الحالي",
                    negativeAction = {
                        // Go to test fragment monitoring page
                        val routerId = viewModel.selectedRouterId.value
                        val bundle = Bundle().apply {
                            putLong("routerId", routerId)
                            putStringArray("cardList", emptyArray())
                            putLong("delayMs", 2000L)
                        }
                        try {
                            findNavController().navigate(R.id.action_home_fragment_to_test_fragment, bundle)
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to navigate to test fragment safely")
                        }
                    },
                    neutralButtonText = "إغلاق"
                )
            } else {
                viewModel.generateAndStart(
                    prefix = prefix,
                    length = length,
                    count = count,
                    charset = charset,
                    delayMs = 2000L
                )
            }
        }

        btnStop.setOnClickListener {
            com.example.util.DialogHelper.showCustomDialog(
                context = requireContext(),
                title = "تأكيد إلغاء الفحص الجاري",
                message = "هل أنت متأكد من رغبتك في إلغاء عملية الفحص الجارية للبطاقات؟ سيتم إيقاف فحص وتجربة البطاقات كلياً.",
                dialogType = com.example.util.DialogHelper.DialogType.WARNING,
                positiveButtonText = "نعم، إلغاء الفحص",
                positiveAction = {
                    val serviceIntent = Intent(requireContext(), TestService::class.java).apply {
                        action = TestService.ACTION_CANCEL
                    }
                    requireContext().startService(serviceIntent)
                    com.example.util.ToastHelper.showSuccessToast(requireContext(), "تم إلغاء عملية الفحص بنجاح!")
                },
                negativeButtonText = "لا، استمرار العمل"
            )
        }

        btnClearLog.setOnClickListener {
            com.example.util.DialogHelper.showCustomDialog(
                context = requireContext(),
                title = "تأكيد حذف السجلات",
                message = "هل أنت متأكد من رغبتك في حذف جميع سجلات الفحص والبطاقات وتصفير الإحصائيات بالكامل؟ لا يمكن التراجع عن هذا الإجراء.",
                dialogType = com.example.util.DialogHelper.DialogType.WARNING,
                positiveButtonText = "نعم، احذف وصفر الإحصائيات",
                positiveAction = {
                    viewModel.clearLogs()
                    com.example.util.ToastHelper.showSuccessToast(requireContext(), "تم مسح جميع السجلات وتصفير الإعدادات بنجاح!")
                },
                negativeButtonText = "إلغاء وإبقاء"
            )
        }

        view?.findViewById<View>(R.id.btn_manage_routers)?.setOnClickListener {
            try {
                findNavController().navigate(R.id.nav_router_manager_fragment)
            } catch (e: Exception) {
                Timber.e(e, "Failed navigating to router manager fragment safely")
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                
                launch {
                    viewModel.routers.collectLatest { routers ->
                        val ctx = context ?: return@collectLatest
                        routerList = routers
                        val names = routers.map { it.name }
                        val adapter = ArrayAdapter(
                            ctx,
                            android.R.layout.simple_spinner_item,
                            names
                        ).apply {
                            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        }
                        spinnerRouter.adapter = adapter
                    }
                }

                launch {
                    viewModel.connectionState.collectLatest { state ->
                        connectionStatusView.setConnected(state == HomeViewModel.ConnectionState.CONNECTED)
                    }
                }

                launch {
                    viewModel.logEntries.collectLatest { logs ->
                        logAdapter.submitList(logs) {
                            if (logs.isNotEmpty()) {
                                recyclerLog.post { recyclerLog.scrollToPosition(logs.size - 1) }
                            }
                        }
                    }
                }

                launch {
                    viewModel.statistics.collectLatest { stats ->
                        val ctx = context ?: return@collectLatest
                        cardSent.bind("الإجمالي المولد", stats.total.toString())
                        cardSuccess.bind("البطاقات الناجحة", stats.success.toString(), ContextCompat.getColor(ctx, android.R.color.holo_green_dark))
                        cardFailed.bind("البطاقات الفاشلة", stats.failure.toString(), ContextCompat.getColor(ctx, android.R.color.holo_red_dark))

                        if (stats.total > 0) {
                            val pct = (stats.success + stats.failure) * 100 / stats.total
                            tvProgress.text = "$pct%"
                            progressBar.setProgressWithAnimation(pct)
                        } else {
                            tvProgress.text = "0%"
                            progressBar.progress = 0
                        }
                    }
                }

                launch {
                    viewModel.uiState.collectLatest { state ->
                        when (state) {
                            is HomeViewModel.UiState.Testing -> {
                                btnStart.isEnabled = true
                                btnStop.isEnabled = true
                                layoutProgress.visibility = View.VISIBLE
                            }
                            else -> {
                                btnStart.isEnabled = true
                                btnStop.isEnabled = false
                                layoutProgress.visibility = View.GONE
                            }
                        }
                    }
                }

                launch {
                    viewModel.errorFlow.collectLatest { err ->
                        val ctx = context ?: return@collectLatest
                        com.example.util.ToastHelper.showErrorToast(ctx, err)
                    }
                }

                launch {
                    viewModel.startTestEvent.collect { config ->
                        val ctx = context ?: return@collect
                        val serviceIntent = Intent(ctx, TestService::class.java).apply {
                            action = TestService.ACTION_START
                            putExtra(TestService.EXTRA_ROUTER_ID, config.routerId)
                            putStringArrayListExtra(TestService.EXTRA_CARD_LIST, config.cardList)
                            putExtra(TestService.EXTRA_DELAY_MS, config.delayMs)
                        }
                        try {
                            ContextCompat.startForegroundService(ctx, serviceIntent)
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to start TestService in foreground")
                            com.example.util.ToastHelper.showErrorToast(ctx, "فشل بدء خدمة المزامنة بالخلفية: ${e.localizedMessage}")
                        }

                        // Navigate to TestFragment using bundle for maximum compile safety
                        val bundle = Bundle().apply {
                            putLong("routerId", config.routerId)
                            putStringArray("cardList", config.cardList.toTypedArray())
                            putLong("delayMs", config.delayMs)
                        }
                        if (isAdded) {
                            try {
                                findNavController().navigate(R.id.action_home_fragment_to_test_fragment, bundle)
                            } catch (e: Exception) {
                                Timber.e(e, "Failed to navigate to test fragment safely")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startMonitoringConnection(requireContext())
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopMonitoringConnection()
    }
}
