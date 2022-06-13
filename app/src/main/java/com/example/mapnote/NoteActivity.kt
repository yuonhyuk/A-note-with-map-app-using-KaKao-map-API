package com.example.mapnote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.example.mapnote.Room.MarkerDataBase
import com.example.mapnote.Room.MarkerInfo
import com.example.mapnote.databinding.ActivityMainBinding
import com.example.mapnote.databinding.ActivityNoteBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityNoteBinding
    lateinit var db : MarkerDataBase
    var markerList = listOf<MarkerInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


    }
    //1.insert
    private fun insertData(markerInfo: MarkerInfo){
        CoroutineScope(Dispatchers.IO).launch {
            db.markerDAO().insert(markerInfo)
        }
        getAllData()
    }
    //2.getAlldata
    private fun getAllData(){
        CoroutineScope(Dispatchers.IO).launch {
            markerList = db.markerDAO().getAll()
        }
    }
    //3.delete
    private fun deleteData(markerInfo: MarkerInfo){
        CoroutineScope(Dispatchers.IO).launch {
            db.markerDAO().delete(markerInfo)
        }
        getAllData()
    }

    //4.deleteAll
    private fun deleteAll(){
        CoroutineScope(Dispatchers.IO).launch {
            db.markerDAO().deleteAll()
        }
        getAllData()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.map -> {
                binding.layoutDrawer.closeDrawers()
            }
            R.id.note -> Toast.makeText(applicationContext,"리스트", Toast.LENGTH_SHORT).show()
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
}