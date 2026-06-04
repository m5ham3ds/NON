package com.example.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.local.entity.RouterProfileEntity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RouterFormFragment : Fragment() {

    private val viewModel: RouterManagerViewModel by viewModel()
    private var targetRouterId: Long = -1L
    private var existingProfile: RouterProfileEntity? = null

    // UI elements
    private lateinit var toolbar: MaterialToolbar
    private lateinit var etName: TextInputEditText
    private lateinit var spinnerProtocol: Spinner
    private lateinit var etIp: TextInputEditText
    private lateinit var etLoginPath: TextInputEditText
    private lateinit var etUserSel: TextInputEditText
    private lateinit var etPwdSel: TextInputEditText
    private lateinit var etSubSel: TextInputEditText
    private lateinit var etUser: TextInputEditText
    private lateinit var etSuccessInd: TextInputEditText
    private lateinit var etFailureInd: TextInputEditText
    private lateinit var etLogoutSel: TextInputEditText
    private lateinit var btnCancel: MaterialButton
    private lateinit var btnSave: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        targetRouterId = arguments?.getLong("routerId", -1L) ?: -1L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_router_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupProtocolSpinner()
        setupListeners()
        observeProfile()
    }

    private fun initializeViews(view: View) {
        toolbar = view.findViewById(R.id.toolbar_router_form)
        etName = view.findViewById(R.id.et_router_name)
        spinnerProtocol = view.findViewById(R.id.spinner_protocol)
        etIp = view.findViewById(R.id.et_ip)
        etLoginPath = view.findViewById(R.id.et_login_path)
        etUserSel = view.findViewById(R.id.et_username_selector)
        etPwdSel = view.findViewById(R.id.et_password_selector)
        etSubSel = view.findViewById(R.id.et_submit_selector)
        etUser = view.findViewById(R.id.et_username)
        etSuccessInd = view.findViewById(R.id.et_success_indicator)
        etFailureInd = view.findViewById(R.id.et_failure_indicator)
        etLogoutSel = view.findViewById(R.id.et_logout_selector)
        btnCancel = view.findViewById(R.id.btn_cancel_form)
        btnSave = view.findViewById(R.id.btn_save_form)

        toolbar.title = if (targetRouterId == -1L) "إضافة راوتر مستهدف جديد" else "تعديل بيانات الراوتر"
    }

    private fun setupProtocolSpinner() {
        val protocols = listOf("http", "https")
        val protAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, protocols).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerProtocol.adapter = protAdapter
    }

    private fun setupListeners() {
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        btnSave.setOnClickListener {
            saveRouterProfile()
        }
    }

    private fun observeProfile() {
        if (targetRouterId == -1L) return

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.routers.collectLatest { list ->
                    val profile = list.firstOrNull { it.id == targetRouterId }
                    if (profile != null) {
                        existingProfile = profile
                        prefillForm(profile)
                    }
                }
            }
        }
    }

    private fun prefillForm(profile: RouterProfileEntity) {
        etName.setText(profile.name)
        val protocols = listOf("http", "https")
        val selection = protocols.indexOf(profile.protocol).coerceAtLeast(0)
        spinnerProtocol.setSelection(selection)
        etIp.setText(profile.ip)
        etLoginPath.setText(profile.loginPath)
        etUserSel.setText(profile.usernameSelector)
        etPwdSel.setText(profile.passwordSelector)
        etSubSel.setText(profile.submitSelector)
        etUser.setText(profile.username)
        etSuccessInd.setText(profile.successIndicator)
        etFailureInd.setText(profile.failureIndicator)
        etLogoutSel.setText(profile.logoutSelector)
    }

    private fun saveRouterProfile() {
        val name = etName.text?.toString()?.trim() ?: ""
        val protocol = spinnerProtocol.selectedItem?.toString() ?: "http"
        val ip = etIp.text?.toString()?.trim() ?: ""
        val loginPath = etLoginPath.text?.toString()?.trim() ?: ""
        val userSel = etUserSel.text?.toString()?.trim() ?: ""
        val pwdSel = etPwdSel.text?.toString()?.trim() ?: ""
        val subSel = etSubSel.text?.toString()?.trim() ?: ""
        val user = etUser.text?.toString()?.trim() ?: ""
        val successInd = etSuccessInd.text?.toString()?.trim() ?: ""
        val failureInd = etFailureInd.text?.toString()?.trim() ?: ""
        val logoutSel = etLogoutSel.text?.toString()?.trim() ?: ""

        if (name.isBlank() || ip.isBlank() || userSel.isBlank() || pwdSel.isBlank() || subSel.isBlank()) {
            com.example.util.ToastHelper.showErrorToast(requireContext(), "الرجاء تعبئة كافة الحقول المطلوبة بنجاح")
            return
        }

        val item = RouterProfileEntity(
            id = existingProfile?.id ?: 0L,
            name = name,
            ip = ip,
            protocol = protocol,
            loginPath = loginPath,
            usernameSelector = userSel,
            passwordSelector = pwdSel,
            submitSelector = subSel,
            username = user,
            successIndicator = successInd,
            failureIndicator = failureInd,
            logoutSelector = logoutSel,
            isDefault = existingProfile?.isDefault ?: false,
            createdAt = existingProfile?.createdAt ?: System.currentTimeMillis()
        )

        if (targetRouterId == -1L) {
            viewModel.addRouter(item)
        } else {
            viewModel.updateRouter(item)
        }

        com.example.util.ToastHelper.showSuccessToast(requireContext(), "تم حفظ ملف تعريف الراوتر بنجاح")
        findNavController().popBackStack()
    }
}
