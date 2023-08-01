package com.example.happyplace.activites

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplace.R
import com.example.happyplace.databinding.ActivityHappyPlaceDetailBinding
import com.example.happyplace.databinding.ActivityMainBinding

import com.example.happyplace.models.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHappyPlaceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var happyPlaceDetailModel : HappyPlaceModel? = null

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            happyPlaceDetailModel = intent.getParcelableExtra(
                MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel?
        }

        if (happyPlaceDetailModel != null){
            setSupportActionBar(binding.toolBarHappyPlaceDetailActivity)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlaceDetailModel.title

            binding.toolBarHappyPlaceDetailActivity.setNavigationOnClickListener {
                onBackPressed()
            }

            binding.image.setImageURI(Uri.parse(happyPlaceDetailModel.image))
            binding.tilDescription.text = happyPlaceDetailModel.description
            binding.tilLocation.text = happyPlaceDetailModel.location


            binding.btnViewOnMap.setOnClickListener{
                val intent = Intent(this@HappyPlaceDetailActivity,
                MapActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS,
                happyPlaceDetailModel)
                startActivity(intent)
            }
        }
    }
}