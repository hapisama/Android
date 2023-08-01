package com.example.happyplace.activites
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.happyplace.R
import com.example.happyplace.database.DataBaseHandler
import com.example.happyplace.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplace.models.HappyPlaceModel
import com.example.happyplace.utils.GetAddressFromLatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest


class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage : Uri? = null
    private var mLatitude : Double = 0.0
    private var mLongitude : Double = 0.0

    private var happyPlaceDetail: HappyPlaceModel? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private lateinit var binding: ActivityAddHappyPlaceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(findViewById(R.id.toolBar_add_place))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolBarAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if(!Places.isInitialized()){
            Log.e("loc","1")
            Places.initialize(this@AddHappyPlaceActivity,
                resources.getString(R.string.MAPS_KEY_API))
            Log.e("loc","2")
        }

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            happyPlaceDetail = intent.getParcelableExtra(
                MainActivity.EXTRA_PLACE_DETAILS
            )as HappyPlaceModel?
            Log.e("loc","3")
        }

        dateSetListener = DatePickerDialog.OnDateSetListener{
            view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        updateDateInView()

        if (happyPlaceDetail != null){
            supportActionBar?.title = "Edit Happy Place"

            findViewById<TextView>(R.id.et_title).text = happyPlaceDetail!!.title
            findViewById<TextView>(R.id.et_description).text = happyPlaceDetail!!.description
            binding.etDate.setText(happyPlaceDetail!!.date)
            findViewById<TextView>(R.id.et_location).text = happyPlaceDetail!!.location
            mLatitude = happyPlaceDetail!!.latitude
            mLongitude = happyPlaceDetail!!.longitude
            saveImageToInternalStorage = Uri.parse(
                happyPlaceDetail!!.image
            )
            binding.image.setImageURI(saveImageToInternalStorage)

            binding.btnSave.text = "update"
        }

        binding.etDate.setOnClickListener(this)
        binding.addImageText.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.etLocation.setOnClickListener(this)
        binding.tvSelectCurrentLocation.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(this@AddHappyPlaceActivity,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.addImageText ->{
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pickerDialogItems = arrayOf(
                    "Select photo from Gallery",
                    "Capture photo from Camera")
                pictureDialog.setItems(pickerDialogItems){
                    dialog, which ->
                    when(which){
                        0 -> choosePhotoFromGallery()
                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save -> {
                when{
                    binding.etTitle.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
                    }
                    binding.etDescription.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show()
                    }
                    binding.etLocation.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter location", Toast.LENGTH_SHORT).show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "Please select or take a picture ", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val happyPlaceModel = HappyPlaceModel(
                            if(happyPlaceDetail == null) 0 else happyPlaceDetail!!.id,
                            binding.etTitle.text.toString(),
                            saveImageToInternalStorage.toString(),
                            binding.etDescription.text.toString(),
                            binding.etDate.text.toString(),
                            binding.etLocation.text.toString(),
                            mLatitude,
                            mLongitude
                        )

                        val dataBaseHandler = DataBaseHandler(this)
                        if ( happyPlaceDetail == null){
                            val addHappyPlace = dataBaseHandler.addHappyPlace(happyPlaceModel)

                            if(addHappyPlace > 0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }else{
                            val updateHappyPlace = dataBaseHandler.updateHappyPlace(happyPlaceModel)

                            if (updateHappyPlace>0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                    }
                }
            }
            R.id.et_location ->{
                try {
                    val fields = listOf(
                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                        Place.Field.ADDRESS
                    )
                    val intent =
                        Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN,
                        fields).build(this@AddHappyPlaceActivity)
                    startActivityForResult(intent, PLACES_REQUEST_CODE)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
            R.id.tv_select_current_location ->{
              if (!isLocationEnable()){
                  Toast.makeText(
                      this,
                      "Your location provider is turned off. Please turn it on.",
                      Toast.LENGTH_SHORT
                  ).show()

                  val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                  startActivity(intent)
              } else {
                  Dexter.withActivity(this).withPermissions(
                      android.Manifest.permission.ACCESS_FINE_LOCATION,
                      android.Manifest.permission.ACCESS_COARSE_LOCATION
                  ).withListener(object  : MultiplePermissionsListener{
                      override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                          if(report!!.areAllPermissionsGranted()){
                              requestNewLocationData()
                          }
                      }

                      override fun onPermissionRationaleShouldBeShown(
                          permissions: MutableList<PermissionRequest>?,
                          token: PermissionToken?
                      ) {
                          showRationalDialogForPermissions()
                      }
                  }).onSameThread().check()
              }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
                if(data != null){
                    val contentURI = data.data
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver, contentURI)
                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        binding.image.setImageBitmap(selectedImageBitmap)

                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlaceActivity,
                        "Failed to load the  image from gallery", Toast.LENGTH_SHORT).show()
                    }
                }
                }else if(requestCode == CAMERA){
                val thumbnail : Bitmap = data?.extras?.get("data") as Bitmap
                saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
                binding.image.setImageBitmap(thumbnail)

            }else if (requestCode == PLACES_REQUEST_CODE){
                val place: Place = Autocomplete.getPlaceFromIntent(data!!)
                binding.etLocation.setText(place.address)
                Log.e("loc","4")
                mLatitude = place.latLng!!.latitude
                Log.e("loc","5")
                mLongitude = place.latLng!!.longitude
                Log.e("loc","6")
            }
        }
    }

    private fun updateDateInView(){
        val myFormat = "yyyy.MM.dd"
        val sdf = SimpleDateFormat(
            myFormat,
            Locale.getDefault()
        )
        binding.etDate.setText(sdf.format(cal.time)).toString()
    }

    private fun choosePhotoFromGallery(){
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ).withListener(object : MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)

                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }
    private fun takePhotoFromCamera(){
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()){
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA)

                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }
    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("It looks like you have turend off permission required " +
                "for this feature. It can be enabled under the " +
                "Applications Settings ")
            .setPositiveButton("Go to SETTINGS")
            {
                _,_ ->
                try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){
                dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap):Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    private fun isLocationEnable(): Boolean{
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(
            LocationManager.GPS_PROVIDER
        ) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData(){
        val mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest.priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation : Location? = locationResult.lastLocation
            mLatitude = mLastLocation!!.latitude
            Log.e("location", "lat works")
            mLongitude = mLastLocation!!.longitude
            Log.e("location", "lot works")

            Thread {
                try {
                    val geocoder = Geocoder(this@AddHappyPlaceActivity, Locale.getDefault())
                    val list = geocoder.getFromLocation(40.7,74.0,1)
                    Log.d("Majid",list[0].countryCode)
                }catch (e: Exception){
                    Log.e("AddHappyPlaceAtivity","Majid",e)
                }
            }.start()
            return
            val addressTask =
                GetAddressFromLatLng(this@AddHappyPlaceActivity,
                mLatitude, mLongitude)

            addressTask.setAddressListener(object :
            GetAddressFromLatLng.AddressListener{
                override fun onAddressFound(address: String?) {
                    binding.etLocation.setText(address)
                }

                override fun onError() {
                    Log.e("Get adress", "something went wrong")
                }
            })
            addressTask.getAddress()
        }
    }

    companion object{
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlaceImage"
        private const val PLACES_REQUEST_CODE = 3
    }
}