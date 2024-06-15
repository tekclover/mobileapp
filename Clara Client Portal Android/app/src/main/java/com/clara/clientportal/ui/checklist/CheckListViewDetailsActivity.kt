package com.clara.clientportal.ui.checklist

import android.content.ActivityNotFoundException
import android.content.pm.ActivityInfo
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.clara.clientportal.ActivityResultContractDocument
import com.clara.clientportal.ActivityResultContractImage
import com.clara.clientportal.BaseActivity
import com.clara.clientportal.R
import com.clara.clientportal.databinding.ActivityCheckListViewDetailsBinding
import com.clara.clientportal.model.MatterDocLists
import com.clara.clientportal.network.APIConstant
import com.clara.clientportal.ui.checklist.adapter.CheckListViewDetailsAdapter
import com.clara.clientportal.ui.documentupload.FragmentUploadDocument
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class CheckListViewDetailsActivity : BaseActivity(), FragmentUploadDocument.UploadDocumentListener {
    private lateinit var binding: ActivityCheckListViewDetailsBinding
    private val viewModel: CheckListViewDetailsViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckListViewDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        initIntentValue()
        setListener()
        setObserver()
        viewModel.getAuthToken(APIConstant.CHECK_LIST_Id)
    }

    private fun initIntentValue() {
        intent?.extras?.let {
            viewModel.checkListNo = it.getInt(Constants.CHECK_LIST_NO, -1)
            viewModel.matterHeaderId = it.getInt(Constants.MATTER_HEADER_ID, -1)
            viewModel.matterNo = it.getString(Constants.MATTER_NO, "")
        }
    }

    private fun setObserver() {
        viewModel.checkListMutableLiveData.observe(this) {
            if (it.isNullOrEmpty().not()) {
                val linearLayoutManager = LinearLayoutManager(this)
                binding.checkListDocumentUploadList.layoutManager = linearLayoutManager
                binding.checkListDocumentUploadList.addItemDecoration(
                    DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                    )
                )
                it.firstOrNull()?.matterDocLists?.let { docList ->
/*                    val sortedList = docList.sortedByDescending { date ->
                        date.updatedOn?.let { it1 ->
                            commonUtils.formatDate(Constants.YYYY_MM_DD_T_HH_MM_SS_SSS_Z).parse(
                                it1
                            )
                        }
                    } */
                    val sortedList = docList.sortedByDescending { it1 ->
                        it1.matterNumber
                    }
                    val checkListViewDetailsAdapter =
                        CheckListViewDetailsAdapter(
                            sortedList,
                            commonUtils,
                            preferenceHelper,
                            ::onViewClick
                        )
                    binding.checkListDocumentUploadList.adapter = checkListViewDetailsAdapter
                    setHeaders()
                }
            }
        }
        viewModel.loaderMutableLiveData.observe(this) {
            if (it) {
                binding.lytProgressParent.lytProgress.visibility = View.VISIBLE
                binding.lytProgressParent.lytProgress.isEnabled = false
                binding.lytProgressParent.lytProgress.isClickable = false
                binding.lytProgressParent.imgProgress.setBackgroundResource(R.drawable.progress_frame_animation)
                frameAnimation =
                    binding.lytProgressParent.imgProgress.background as AnimationDrawable
                frameAnimation.start()
            } else {
                binding.lytProgressParent.lytProgress.visibility = View.GONE
                if (::frameAnimation.isInitialized && frameAnimation.isRunning) {
                    frameAnimation.stop()
                }
            }
        }
        viewModel.uploadMutableLiveData.observe(this) {
            it?.status?.let { uploadStatus ->
                if (uploadStatus == Constants.UPLOADED) {
                    viewModel.getAuthToken(APIConstant.DOCUMENT_UPLOAD_UPDATE_ID)
                }
            }
        }
        viewModel.documentUploadUpdateMutableLiveData.observe(this) {
            it?.let {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.upload_success_message)
                )
                viewModel.getAuthToken(APIConstant.CHECK_LIST_Id)
            }
        }
        viewModel.documentSubmitMutableLiveData.observe(this) {
            it?.let {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.submit_success_message)
                )
                viewModel.getAuthToken(APIConstant.CHECK_LIST_Id)
            }
        }
        viewModel.networkMutableLiveData.observe(this) {
            if (it) {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.no_network)
                )
            }
        }
        viewModel.apiFailureMutableLiveData.observe(this) {
            if (it) {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.api_failure_message)
                )
            }
        }
        viewModel.downloadMutableLiveData.observe(this) {
            viewModel.loaderMutableLiveData.value = false
            if (it.isNotEmpty()) {
                try {
                    startActivity(FileUtils.getActionViewIntent(this, viewModel.documentName, it))
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            } else {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.no_document_to_display)
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val notificationView = menu.findItem(R.id.action_notification)?.actionView
        val notificationCount = notificationView?.findViewById(R.id.notification_count) as? TextView
        notificationCount?.text = preferenceHelper.getNotificationCount().toString()
        notificationView?.setOnClickListener {
            navigateToNotification()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            R.id.action_logout -> {
                logout()
                true
            }

            else -> false
        }
    }

    private fun onViewClick(matterDocList: MatterDocLists, isUpload: Boolean) {
        viewModel.matterDocLists = matterDocList
        viewModel.matterNo = matterDocList.matterNumber ?: ""
        viewModel.checkListNo = matterDocList.checkListNo ?: -1
        viewModel.documentName = matterDocList.documentUrl ?: ""
        when {
            isUpload -> {
                if ((viewModel.checkListMutableLiveData.value?.firstOrNull()?.statusId
                        ?: -1) == 23
                ) {
                    commonUtils.showToastMessage(
                        this,
                        this.resources.getString(R.string.upload_error_message)
                    )
                } else {
                    showUploadDialog()
                }
            }

            viewModel.documentName.isEmpty() -> {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.no_document_to_display)
                )
            }

            else -> {
                initDownload()
            }
        }
    }


    private fun showUploadDialog() {
        if (supportFragmentManager.findFragmentByTag(TAG_UPLOAD_FRAGMENT) == null) {
            val uploadFragment = FragmentUploadDocument()
            supportFragmentManager.beginTransaction().add(uploadFragment, TAG_UPLOAD_FRAGMENT)
                .commitAllowingStateLoss()
        }
    }

    override fun onCamera() {
        if (isCameraPermissionGranted()) {
            initCameraIntent()
        } else {
            requestPermission(Constants.CAMERA_PERMISSION)
        }
    }

    override fun onGallery() {
        launchGalleryIntent()
    }

    override fun onFiles() {
        launchDocumentIntent(arrayOf("*/*"))
    }

    private fun launchCameraIntent(uri: Uri?) {
        try {
            cameraLauncher.launch(uri)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun launchGalleryIntent() {
        try {
            galleryLauncher.launch(Constants.GALLERY_IMAGE_TYPE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchDocumentIntent(mimeType: Array<String>) {
        documentLauncher.launch(mimeType)
    }

    private var cameraLauncher: ActivityResultLauncher<Uri> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                getFilePath(viewModel.tempCameraUri)
            }
        }
    private var galleryLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContractImage()) { result ->
            if (result != null) {
                getFilePath(result)
            }
        }
    private var documentLauncher =
        registerForActivityResult(ActivityResultContractDocument()) { result ->
            if (result != null) {
                getFilePath(result)
            }
        }

    private fun getFilePath(uri: Uri?) {
        uri?.let {
            FileUtils.copyFileToAppDirectory(this, it)?.let { filePath ->
                viewModel.uploadList.value = File(filePath)
                viewModel.uploadList.value?.name?.let { fileName ->
                    viewModel.getAuthToken(APIConstant.UPLOAD)
                }
            }
        }
    }

    private fun initCameraIntent() {
        viewModel.tempCameraUri = FileUtils.getContentUri(this)
        viewModel.tempCameraUri?.let {
            launchCameraIntent(it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isPermissionGranted(grantResults)) {
            when (requestCode) {
                Constants.CAMERA_PERMISSION -> {
                    viewModel.tempCameraUri = FileUtils.getContentUri(this)
                    launchCameraIntent(viewModel.tempCameraUri)
                }

                Constants.GALLERY_PERMISSION -> {
                    launchGalleryIntent()
                }
            }
        }
    }

    private fun initDownload() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU || isExternalStoragePermissionGranted(
                this
            )
        ) {
            val url = if ((viewModel.checkListMutableLiveData.value?.firstOrNull()?.statusId
                    ?: -1) == 23
            ) {
                APIConstant.BASE_URL + APIConstant.DOWNLOAD + Constants.QUESTION_MARK_SYMBOL + Constants.FILE_NAME + Constants.ASSIGNMENT_SYMBOL + viewModel.documentName + Constants.AMPERSAND_SYMBOL + Constants.LOCATION + Constants.ASSIGNMENT_SYMBOL + Constants.PARAM_CLIENT_PORTAL + Constants.SLASH_SYMBOL + preferenceHelper.getClientId() + Constants.SLASH_SYMBOL + viewModel.matterNo + Constants.SLASH_SYMBOL + viewModel.checkListNo
            } else {
                APIConstant.BASE_URL + APIConstant.DOWNLOAD + Constants.QUESTION_MARK_SYMBOL + Constants.FILE_NAME + Constants.ASSIGNMENT_SYMBOL + viewModel.documentName + Constants.AMPERSAND_SYMBOL + Constants.LOCATION + Constants.ASSIGNMENT_SYMBOL + Constants.PARAM_CLIENT_PORTAL + Constants.SLASH_SYMBOL + Constants.TEMP + Constants.SLASH_SYMBOL + preferenceHelper.getClientId() + Constants.SLASH_SYMBOL + viewModel.matterNo + Constants.SLASH_SYMBOL + viewModel.checkListNo
            }
            viewModel.loaderMutableLiveData.value = true
            viewModel.downloadDocument(this, url, viewModel.documentName)
        } else {
            requestPermission(Constants.DOWNLOAD_PERMISSION)
        }
    }

    private fun showUploadSubmitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(this.resources.getString(R.string.do_you_want_to_submit))
        /*
                builder.setMessage(
                    String.format(
                        Locale.getDefault(),
                        this.resources.getString(R.string.file_name),
                        fileName
                    )
                )
        */
        builder.setMessage(this.resources.getString(R.string.submit_subtitle))
        builder.setPositiveButton(this.resources.getString(R.string.yes)) { dialogInterface, _ ->
            dialogInterface.dismiss()
            viewModel.getAuthToken(APIConstant.CHECK_LIST_DOCUMENT_SUBMIT_ID)
        }

        builder.setNegativeButton(this.resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.color_red_dark))
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.color_devil_blue))
    }

    companion object {
        const val TAG_UPLOAD_FRAGMENT = "UPLOAD_FRAGMENT"
    }

    private fun setHeaders() {
        if (preferenceHelper.isTablet()) {
            with(binding) {
                lytHeaderView?.textTitle1?.text =
                    this@CheckListViewDetailsActivity.resources.getString(R.string.document)
                lytHeaderView?.textTitle2?.text =
                    this@CheckListViewDetailsActivity.resources.getString(R.string.attachment)
                lytHeaderView?.textTitle3?.text =
                    this@CheckListViewDetailsActivity.resources.getString(R.string.status)
                lytHeaderView?.textTitle4?.text =
                    this@CheckListViewDetailsActivity.resources.getString(R.string.date)
                lytHeaderView?.textTitle5?.text =
                    this@CheckListViewDetailsActivity.resources.getString(R.string.download)
                lytHeaderView?.textTitle6?.text =
                    this@CheckListViewDetailsActivity.resources.getString(R.string.upload)
                lytHeaderView?.lytHeader6Parent?.visibility = View.VISIBLE
            }
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun setListener() {
        binding.btnSubmit.setOnClickListener {
            when {
                (viewModel.checkListMutableLiveData.value?.firstOrNull()?.statusId
                    ?: -1) == 23 -> {
                    commonUtils.showToastMessage(
                        this,
                        this.resources.getString(R.string.submit_error_message)
                    )
                }

                isDocumentUploaded() -> {
                    showUploadSubmitDialog()
                }

                else -> {
                    commonUtils.showToastMessage(
                        this,
                        this.resources.getString(R.string.please_upload_file_to_submit)
                    )
                }
            }
        }
        binding.btnCancel?.setOnClickListener {
            finish()
        }
    }

    private fun isDocumentUploaded(): Boolean {
        val statusIdList =
            viewModel.checkListMutableLiveData.value?.firstOrNull()?.matterDocLists?.filter { it.statusId == 60 || it.statusId == 63 }
        return statusIdList.isNullOrEmpty().not()
    }
}