package com.example.mapnote

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.mapnote.Room.MarkerDataBase
import com.example.mapnote.Room.MarkerInfo
import com.example.mapnote.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

@SuppressLint("StaticFieldLeak")
class MainActivity : AppCompatActivity(), MapView.POIItemEventListener,
MapView.MapViewEventListener,NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    lateinit var db : MarkerDataBase
    var markerList = listOf<MarkerInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))

        binding.mapView.setMapViewEventListener(this)
        binding.mapView.setPOIItemEventListener(this)

        db = MarkerDataBase.getInstance((this))!!

        binding.mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.5663, 126.9779),3,true)
        val marker = MapPOIItem()
        marker.itemName = "서울 시청"
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.5663, 126.9779)
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        binding.mapView.addPOIItem(marker)

        if (MapView.isMapTilePersistentCacheEnabled()) {
            MapView.setMapTilePersistentCacheEnabled(true)
        }
        binding.button.setOnClickListener(){
            for(i in 1..2) {
                getAllData()
            }
            binding.mapView.removeAllPOIItems()
            for(i in markerList.indices)
            {
                val marker = MapPOIItem()
                marker.itemName = markerList[i].name
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(markerList[i].lat!!, markerList[i].lng!!)
                marker.markerType = MapPOIItem.MarkerType.YellowPin
                marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                binding.mapView.addPOIItem(marker)
            }
        }
        binding.zoomout.setOnClickListener(){
            binding.mapView.zoomIn(true)
        }
        binding.zoomin.setOnClickListener(){
            binding.mapView.zoomOut(true)
        }
        binding.btnNavi.setOnClickListener(){
            binding.layoutDrawer.openDrawer(GravityCompat.START)
        }
        binding.naviView.setNavigationItemSelectedListener(this)
    }

    //말풍선 레이아웃
    class CustomBalloonAdapter(inflater: LayoutInflater): CalloutBalloonAdapter {
        val mCalloutBalloon: View = inflater.inflate(R.layout.balloon, null)
        val name: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_name)
        val address: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_address)

        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            // 마커 클릭 시 나오는 말풍선
            name.text = poiItem?.itemName   // 해당 마커의 정보 이용 가능
            address.text = "정보 없음."
            return mCalloutBalloon
        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            return mCalloutBalloon
        }
    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
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

    @SuppressLint("CutPasteId")
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {
        val builder = AlertDialog.Builder(this)
        val itemList = arrayOf("해당 마커 정보 보기","마커 정보 수정", "마커 삭제", "취소")
        builder.setTitle("마커 설정")
        builder.setItems(itemList) { dialog, which ->
            when(which) {
                0 -> {
                    val dialogView = View.inflate(this@MainActivity, R.layout.information_dialog, null)
                    var dlg = AlertDialog.Builder(this@MainActivity)
                    val lat = p1?.mapPoint?.mapPointGeoCoord?.latitude
                    val lng = p1?.mapPoint?.mapPointGeoCoord?.longitude
                    dlg.setView(dialogView)
                    for(i in 1..2){
                        getAllData()
                    }

                    if(markerList.isEmpty())
                    {
                        dialogView.findViewById<TextView>(R.id.location_name).text = p1?.itemName
                        dialogView.findViewById<TextView>(R.id.memocontent).text = "정보 없음"
                        dialogView.findViewById<TextView>(R.id.deaddate).text = "정보 없음"
                        dialogView.findViewById<TextView>(R.id.deadline).text = "정보 없음"
                    }
                    else{
                        for(i in markerList.indices)
                        {
                            if ((markerList[i].lat==lat)&&(markerList[i].lng==lng)){
                                dialogView.findViewById<TextView>(R.id.location_name).text = p1?.itemName
                                dialogView.findViewById<TextView>(R.id.memocontent).text = markerList[i].content
                                dialogView.findViewById<TextView>(R.id.deaddate).text = markerList[i].date
                                dialogView.findViewById<TextView>(R.id.deadline).text = markerList[i].time
                                break
                            }
                            else{
                                dialogView.findViewById<TextView>(R.id.location_name).text = p1?.itemName
                                dialogView.findViewById<TextView>(R.id.memocontent).text = "정보 없음"
                                dialogView.findViewById<TextView>(R.id.deaddate).text = "정보 없음"
                                dialogView.findViewById<TextView>(R.id.deadline).text = "정보 없음"
                            }
                        }
                    }

                    dlg.setPositiveButton("확인",null)
                    dlg.show()
                }
                1 -> {
                    val dialogView = View.inflate(this@MainActivity, R.layout.input_information_dialog, null)
                    var dlg = AlertDialog.Builder(this@MainActivity)
                    dlg.setView(dialogView)

                    dlg.setPositiveButton("확인") { dialog, which ->
                        //입력하지 않으면 경고창 뜨게 하는 기능 추가하기(에러 방지)
                        val loc = p1?.mapPoint
                        val itemname = dialogView.findViewById<EditText>(R.id.location_name).text.toString()
                        val memo = dialogView.findViewById<EditText>(R.id.memocontent).text.toString()
                        val date = dialogView.findViewById<EditText>(R.id.edittextdate).text.toString()
                        val time = dialogView.findViewById<EditText>(R.id.edittexttime).text.toString()
                        val lat = p1?.mapPoint?.mapPointGeoCoord?.latitude
                        val lng = p1?.mapPoint?.mapPointGeoCoord?.longitude
                        val markerInfo = MarkerInfo(null,itemname,memo,date,time,lat,lng)
                        val marker = MapPOIItem()

                        //poiItem 추가
                        p0?.removePOIItem(p1)
                        marker.itemName = itemname
                        marker.mapPoint = loc
                        marker.markerType = MapPOIItem.MarkerType.YellowPin
                        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                        binding.mapView.addPOIItem(marker)
                        for(i in 1..2){
                            insertData(markerInfo)
                            getAllData()
                        }
                    }
                    dlg.setNegativeButton("취소", null)
                    dlg.show()
                }
                2 -> p0?.removePOIItem(p1)    // 마커 삭제
                3 -> dialog.dismiss()   // 대화상자 닫기
            }
        }
        builder.show()
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }

    override fun onMapViewInitialized(p0: MapView?) {
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        binding.mapView.removeAllPOIItems()
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
        binding.mapView.removeAllPOIItems()
        val marker = MapPOIItem()
        marker.itemName = "클릭해서 정보 입력"
        marker.mapPoint = p1
        marker.markerType = MapPOIItem.MarkerType.YellowPin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        marker.showAnimationType = MapPOIItem.ShowAnimationType.DropFromHeaven
        binding.mapView.addPOIItem(marker)
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.map -> {}
            R.id.note -> {
                val intent = Intent(this,NoteActivity::class.java)
                startActivity(intent)
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
}