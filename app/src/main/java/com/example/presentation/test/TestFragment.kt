package com.example.presentation.test

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.service.TestService
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import org.koin.androidx.viewmodel.ext.android.viewModel

class TestFragment : Fragment() {

    private val viewModel: TestViewModel by viewModel()
    private var activeErrorDialog: androidx.appcompat.app.AlertDialog? = null

    private lateinit var tvTestStatus: TextView
    private lateinit var tvActiveCard: TextView
    private lateinit var testProgressBar: LinearProgressIndicator
    private lateinit var ivLiveScreenshot: ImageView
    private lateinit var pbLoadState: ProgressBar
    private lateinit var btnPause: MaterialButton
    private lateinit var btnCancel: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupListeners()
        observeServiceState()
    }

    private fun initializeViews(view: View) {
        tvTestStatus = view.findViewById(R.id.tv_test_status)
        tvActiveCard = view.findViewById(R.id.tv_active_card)
        testProgressBar = view.findViewById(R.id.test_progress_bar)
        ivLiveScreenshot = view.findViewById(R.id.iv_live_screenshot)
        pbLoadState = view.findViewById(R.id.pb_load_state)
        btnPause = view.findViewById(R.id.btn_test_pause)
        btnCancel = view.findViewById(R.id.btn_test_cancel)
    }

    private fun setupListeners() {
        btnPause.setOnClickListener {
            val isCurrentlyPaused = viewModel.serviceState.value.isPaused
            val serviceIntent = Intent(requireContext(), TestService::class.java).apply {
                action = if (isCurrentlyPaused) TestService.ACTION_RESUME else TestService.ACTION_PAUSE
            }
            requireContext().startService(serviceIntent)
        }

        btnCancel.setOnClickListener {
            com.example.util.DialogHelper.showCustomDialog(
                context = requireContext(),
                title = "إلغاء الاختبار",
                message = "هل أنت متأكد من رغبتك في إلغاء عملية فحص وتحليل البطاقات الجارية؟",
                dialogType = com.example.util.DialogHelper.DialogType.WARNING,
                positiveButtonText = "نعم، إلغاء",
                positiveAction = {
                    val serviceIntent = Intent(requireContext(), TestService::class.java).apply {
                        action = TestService.ACTION_CANCEL
                    }
                    requireContext().startService(serviceIntent)
                    findNavController().popBackStack()
                },
                negativeButtonText = "لا، استمرار"
            )
        }
    }

    private fun observeServiceState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                
                launch {
                    kotlinx.coroutines.flow.combine(
                        viewModel.serviceState,
                        com.example.service.TestService.isRunning
                    ) { state, isRunning ->
                        Pair(state, isRunning)
                    }.collectLatest { (state, isRunning) ->
                        // Status text
                        val statusText = when (state.status) {
                            "RUNNING" -> "حالة الاختبار: جاري الفحص النشط..."
                            "PAUSED" -> "حالة الاختبار: موقوف مؤقتاً"
                            "DONE" -> "حالة الاختبار: تم إكمال الفحص كلياً"
                            "LOAD_ERROR" -> "حالة الاختبار: فشل في تحميل الصفحة"
                            "CANCELLED" -> "حالة الاختبار: تم الإلغاء"
                            else -> "حالة الاختبار: تهيأة البيئة..."
                        }
                        tvTestStatus.text = statusText
                        tvActiveCard.text = "البطاقة الحالية للفحص: ${state.currentCard} (${state.progress}/${state.total})"

                        // Progress Indicator
                        if (state.total > 0) {
                            testProgressBar.max = state.total
                            testProgressBar.setProgress(state.progress, true)
                        } else {
                            testProgressBar.setProgress(0, false)
                        }

                         // Pause Button Label
                        btnPause.text = if (state.isPaused) "استئناف" else "إيقاف مؤقت"

                        val isProcessActive = isRunning && (state.status == "RUNNING" || state.status == "PAUSED" || state.status == "LOAD_ERROR")
                        btnPause.isEnabled = isProcessActive
                        btnCancel.isEnabled = isProcessActive

                        // Handle live stream screenshot on background dispatcher
                        state.screenshotBytes?.let { bytes ->
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                                val options = BitmapFactory.Options().apply {
                                    inSampleSize = 1
                                    inPreferredConfig = android.graphics.Bitmap.Config.RGB_565
                                }
                                val bitmap = try {
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                                } catch (e: Exception) {
                                    null
                                }
                                if (bitmap != null) {
                                    withContext(Dispatchers.Main) {
                                        if (isAdded && view != null) {
                                            ivLiveScreenshot.setImageBitmap(bitmap)
                                        }
                                    }
                                }
                            }
                        }

                        if (state.status == "DONE") {
                            val ctx = context
                            if (ctx != null && isAdded) {
                                try {
                                    com.example.util.ToastHelper.showSuccessToast(ctx, "اكتمل الاختبار الجاري بنجاح!")
                                    findNavController().popBackStack()
                                } catch (e: Exception) {
                                    Timber.e(e, "Failed to pop backstack in TestFragment")
                                }
                            }
                        }

                        if (state.status == "LOAD_ERROR") {
                            showLoadErrorDialog(state.error ?: "فشل تحميل الصفحة")
                        } else {
                            activeErrorDialog?.dismiss()
                            activeErrorDialog = null
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activeErrorDialog?.dismiss()
        activeErrorDialog = null
    }

    private fun showLoadErrorDialog(errorMessage: String) {
        if (activeErrorDialog != null && activeErrorDialog?.isShowing == true) {
            return
        }
        val ctx = context ?: return
        if (!isAdded) return

        activeErrorDialog = com.example.util.DialogHelper.showCustomDialog(
            context = ctx,
            title = "فشل تحميل الصفحة",
            message = "لم نتمكن من الوصول لصفحة تسجيل الدخول للراوتر.\nيرجى التأكد من أنك متصل بشبكة الواي فاي للراوتر وقريب منه، ثم أعد المحاولة.\n\nالخطأ: $errorMessage",
            dialogType = com.example.util.DialogHelper.DialogType.WARNING,
            positiveButtonText = "إعادة المحاولة",
            positiveAction = {
                val serviceIntent = Intent(requireContext(), TestService::class.java).apply {
                    action = TestService.ACTION_RETRY_LOAD
                }
                requireContext().startService(serviceIntent)
                activeErrorDialog = null
            },
            negativeButtonText = "إلغاء والعودة",
            negativeAction = {
                val serviceIntent = Intent(requireContext(), TestService::class.java).apply {
                    action = TestService.ACTION_CANCEL
                }
                requireContext().startService(serviceIntent)
                activeErrorDialog = null
                findNavController().popBackStack()
            },
            isCancelable = false
        )
    }
}
