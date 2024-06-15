package com.clara.clientportal.ui.documentupload

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.clara.clientportal.ActivityResultContractDocument
import com.clara.clientportal.ActivityResultContractImage
import com.clara.clientportal.BaseActivity
import com.clara.clientportal.R
import com.clara.clientportal.databinding.ActivityDocumentUploadBinding
import com.clara.clientportal.model.DocumentUploadedResponse
import com.clara.clientportal.network.APIConstant
import com.clara.clientportal.ui.documentupload.adapter.UploadedDocumentAdapter
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class DocumentUploadActivity : BaseActivity(), FragmentUploadDocument.UploadDocumentListener,
    FragmentChooseFile.ChooseFileListener {
    private lateinit var binding: ActivityDocumentUploadBinding
    private val viewModel: DocumentUploadViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        setObservers()
        setListener()
    }

    private fun setObservers() {
        viewModel.uploadedDocumentMutableLiveData.observe(this) {
            if (it.isNullOrEmpty().not()) {
                val linearLayoutManager = LinearLayoutManager(this)
                binding.documentUploadedList.layoutManager = linearLayoutManager
                binding.documentUploadedList.addItemDecoration(
                    DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                    )
                )
                val filterList =
                    it.filter { name -> name.referenceField5 == Constants.CLIENT_PORTAL || name.referenceField2 == Constants.CLIENT_PORTAL_CAPS }
              /*  val sortedList = filterList.sortedByDescending { date ->
                    date.createdOn?.let { it1 ->
                        commonUtils.formatDate(Constants.YYYY_MM_DD_T_HH_MM_SS_SSS_Z).parse(
                            it1
                        )
                    }
                }*/
                val sortedList = filterList.sortedByDescending { it1 ->
                    it1.createdOn
                }
                val uploadedDocumentAdapter =
                    UploadedDocumentAdapter(
                        sortedList,
                        commonUtils,
                        preferenceHelper,
                        ::onViewDocument
                    )
                binding.documentUploadedList.adapter = uploadedDocumentAdapter
                setHeaders()
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
        viewModel.matterIdMutableLiveData.observe(this) {
            if (it.isNullOrEmpty().not()) {
                val matterIdList: ArrayList<String> = arrayListOf()
                matterIdList.add(Constants.SELECT)
                for (id in it) {
                    matterIdList.add(id.matterNumber ?: "")
                }
                viewModel.matterNoList = matterIdList
                if (supportFragmentManager.findFragmentByTag(TAG_FILE_CHOOSER_FRAGMENT) == null) {
                    val fileChooserFragment =
                        FragmentChooseFile()
                    supportFragmentManager.beginTransaction()
                        .add(fileChooserFragment, TAG_FILE_CHOOSER_FRAGMENT)
                        .commitAllowingStateLoss()
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
                viewModel.getAuthToken(APIConstant.UPLOAD_DOCUMENT)
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

    private fun onViewDocument(documentUploaded: DocumentUploadedResponse) {
        viewModel.documentName = documentUploaded.documentUrl ?: ""
        if (viewModel.documentName.isNotEmpty()) {
            initDownload(
                documentUploaded.clientId ?: "",
                documentUploaded.matterNumber ?: "",
                documentUploaded.classId ?: -1
            )
        } else {
            commonUtils.showToastMessage(
                this,
                this.resources.getString(R.string.no_document_to_display)
            )
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

    private fun setListener() {
        binding.btnUpload.setOnClickListener {
            viewModel.getAuthToken(APIConstant.MATTER_IDS)
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

    override fun showUploadFragment() {
        showUploadDialog()
    }

    private fun initDownload(clientId: String, matterNo: String, classId: Number) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU || isExternalStoragePermissionGranted(
                this
            )
        ) {
            val url =
                APIConstant.BASE_URL + APIConstant.DOWNLOAD + Constants.QUESTION_MARK_SYMBOL + Constants.FILE_NAME + Constants.ASSIGNMENT_SYMBOL + viewModel.documentName + Constants.AMPERSAND_SYMBOL + Constants.LOCATION + Constants.ASSIGNMENT_SYMBOL + Constants.PARAM_CLIENT_PORTAL + Constants.SLASH_SYMBOL + clientId + Constants.SLASH_SYMBOL + matterNo + Constants.AMPERSAND_SYMBOL + Constants.CLASS_ID + Constants.ASSIGNMENT_SYMBOL + classId
            viewModel.loaderMutableLiveData.value = true
            viewModel.downloadDocument(this, url, viewModel.documentName)
        } else {
            requestPermission(Constants.DOWNLOAD_PERMISSION)
        }
    }

    companion object {
        const val TAG_FILE_CHOOSER_FRAGMENT = "FILE_CHOOSER_FRAGMENT"
        const val TAG_UPLOAD_FRAGMENT = "UPLOAD_FRAGMENT"
    }

    private fun setHeaders() {
        if (preferenceHelper.isTablet()) {
            with(binding) {
                lytHeaderView?.textTitle1?.text =
                    this@DocumentUploadActivity.resources.getString(R.string.matter_no)
                lytHeaderView?.textTitle2?.text =
                    this@DocumentUploadActivity.resources.getString(R.string.document_name)
                lytHeaderView?.textTitle3?.text =
                    this@DocumentUploadActivity.resources.getString(R.string.received_date)
                lytHeaderView?.textTitle4?.text =
                    this@DocumentUploadActivity.resources.getString(R.string.uploaded_by)
                lytHeaderView?.textTitle5?.text =
                    this@DocumentUploadActivity.resources.getString(R.string.details)
                lytHeaderView?.lytHeader5Parent?.visibility = View.VISIBLE
            }
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}