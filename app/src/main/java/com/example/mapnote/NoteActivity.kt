package com.example.mapnote

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mapnote.Room.MarkerDataBase
import com.example.mapnote.Room.MarkerInfo
import com.example.mapnote.databinding.ActivityNoteBinding
import com.google.android.material.navigation.NavigationView

@SuppressLint("StaticFieldLeak")
class NoteActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,OnDeleteListner {
    private lateinit var binding: ActivityNoteBinding
    lateinit var db : MarkerDataBase
    var markerList = listOf<MarkerInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        db = MarkerDataBase.getInstance((this))!!
        for (i in 1..2)
            getAllData()


        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.btnNavi.setOnClickListener(){
            binding.layoutDrawer.openDrawer(GravityCompat.START)
        }
        binding.naviView.setNavigationItemSelectedListener(this)
        binding.deleteAll.setOnClickListener(){
            deleteAllData()
        }
    }


    //1.insert
    private fun insertData(markerInfo: MarkerInfo){
        val insertTask = object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                db.markerDAO().insert(markerInfo)
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                getAllData()
            }
        }
        insertTask.execute()
    }
    //2.getAlldata
    private fun getAllData(){
        val getTask = object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                markerList = db.markerDAO().getAll()
            }
            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                setRecyclerView(markerList)
            }
        }
        getTask.execute()
    }
    //3.delete
    private fun deleteData(markerInfo: MarkerInfo){
        val deleteTask = object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                db.markerDAO().delete(markerInfo)
            }
            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                getAllData()
            }
        }
        deleteTask.execute()
    }

    //4.deleteAll
    private fun deleteAllData(){
        val deleteallTask = object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                db.markerDAO().deleteAll()
            }
            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                getAllData()
            }
        }
        deleteallTask.execute()
    }

    fun setRecyclerView(markerList: List<MarkerInfo>){
        binding.recyclerView.adapter = MyAdapter(this,markerList,this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.map -> {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
            R.id.note -> {
            }
        }
        binding.layoutDrawer.closeDrawers()
        return false
    }

    override fun onBackPressed() {
        if(binding.layoutDrawer.isDrawerOpen(GravityCompat.START)){
            binding.layoutDrawer.closeDrawers()
        }
        else {
            super.onBackPressed()
        }
    }

    override fun OnDeleteListner(markerInfo: MarkerInfo) {
        for(i in 1..2){
            deleteData(markerInfo)
        }
    }
}