package com.example.happyplace.activites
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplace.R
import com.example.happyplace.adapter.HappyPlaceAdapter
import com.example.happyplace.database.DataBaseHandler
import com.example.happyplace.databinding.ActivityMainBinding
import com.example.happyplace.models.HappyPlaceModel
import com.example.happyplace.utils.SwipeToDeleteCallback
import pl.kitek.rvswipetodelete.SwipeToEditCallback
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getHappyPlaceListFromDB()

        binding.addButton.setOnClickListener{
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        Log.d("main", "1")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                getHappyPlaceListFromDB()
            }else{
                Log.e("activity", "cancelled or back pressed")
            }
        }
    }


    private fun getHappyPlaceListFromDB(){
        val dbHandler = DataBaseHandler(this)
        val getHappyPlaceList = dbHandler.getHappyPlaceList()

        if (getHappyPlaceList.size > 0){
            findViewById<RecyclerView>(R.id.rv_happyPlaceList).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tv_noHappyPlace).visibility = View.GONE
            setupHappyPlaceRecycleView(getHappyPlaceList)
            Log.d("main", "db1")
        }else{
            findViewById<RecyclerView>(R.id.rv_happyPlaceList).visibility = View.GONE
            findViewById<TextView>(R.id.tv_noHappyPlace).visibility = View.VISIBLE

        }
    }

    private fun setupHappyPlaceRecycleView(
        happyPlaceList: ArrayList<HappyPlaceModel>){

        findViewById<RecyclerView>(R.id.rv_happyPlaceList).layoutManager =
            LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.rv_happyPlaceList).setHasFixedSize(true)

        val placeAdapter = HappyPlaceAdapter(
            this, happyPlaceList)
        findViewById<RecyclerView>(R.id.rv_happyPlaceList).adapter = placeAdapter

        placeAdapter.setOnClickListener(object : HappyPlaceAdapter.OnClickListener {
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity,
                    HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }
        })
        val editSwipeHandler = object : SwipeToEditCallback(this){

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = findViewById<RecyclerView>(R.id.rv_happyPlaceList).adapter as HappyPlaceAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(findViewById(R.id.rv_happyPlaceList))


        val deleteSwipeHandler = object : SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = findViewById<RecyclerView>(R.id.rv_happyPlaceList).adapter as HappyPlaceAdapter
                adapter.removeAt(viewHolder.adapterPosition)

                getHappyPlaceListFromDB()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(findViewById(R.id.rv_happyPlaceList))
    }



companion object{
     var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
    var EXTRA_PLACE_DETAILS = "extra place details"
}
}