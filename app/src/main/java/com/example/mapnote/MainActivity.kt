package com.example.mapnote

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mapnote.Room.MarkerDataBase
import com.example.mapnote.Room.MarkerEntity
import com.example.mapnote.databinding.ActivityMainBinding
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

@SuppressLint("StaticFieldLeak")
class MainActivity : AppCompatActivity(), MapView.POIItemEventListener,
MapView.MapViewEventListener {
    private lateinit var binding: ActivityMainBinding
    lateinit var db : MarkerDataBase
    var markerList = listOf<MarkerEntity>()
    private val ACCESS_FINE_LOCATION = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))

        binding.mapView.setMapViewEventListener(this)
        binding.mapView.setPOIItemEventListener(this)

        db = MarkerDataBase.getInstance((this))!!

        if (checkLocationService()) {
            permissionCheck()
        }
        else {
            Toast.makeText(this, "현재 위치를 확인하기 위해서는 GPS를 켜주세요", Toast.LENGTH_SHORT).show()
            binding.mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.5663, 126.9779),3,true)
            val marker = MapPOIItem()
            marker.itemName = "서울 시청"
            marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.5663, 126.9779)
            marker.markerType = MapPOIItem.MarkerType.BluePin
            marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
            binding.mapView.addPOIItem(marker)
        }

        if (MapView.isMapTilePersistentCacheEnabled()) {
            MapView.setMapTilePersistentCacheEnabled(true)
        }

        binding.zoomout.setOnClickListener(){
            binding.mapView.zoomIn(true)
        }
        binding.zoomin.setOnClickListener(){
            binding.mapView.zoomOut(true)
        }
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
            // 말풍선 클릭 시
            address.text = ""
            return mCalloutBalloon
        }
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    // 앱 위치 권한 확인
    private fun permissionCheck() {
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 권한 거절
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                }
                builder.setNegativeButton("취소") { dialog, which ->
                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { dialog, which ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") {
                            dialog, which ->
                    }
                    builder.show()
                }
            }
        }
    }

    // 권한 요청
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
                startTracking()
            } else {
                Toast.makeText(this, "위치 권한이 거절되었습니다", Toast.LENGTH_SHORT).show()
                permissionCheck()
            }
        }
    }
    // 현재 위치 트래킹
    private fun startTracking() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    private fun insertInfo(marker_Info: MarkerEntity){
        val insertTask = (object : AsyncTask<Unit,Unit,Unit>(){
            override fun doInBackground(vararg p0: Unit?) {
                db.markerDAO().insert(marker_Info)
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                getAllInfo()
            }
        }).execute()
    }

    private fun getAllInfo(){
        val getTask = (object : AsyncTask<Unit,Unit,Unit>(){
            override fun doInBackground(vararg p0: Unit?) {
                markerList = db.markerDAO().getAll()
            }
            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)

            }
        }).execute()
    }

    private fun deleteInfo(){
    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {
        val builder = AlertDialog.Builder(this)
        val itemList = arrayOf("해당 마커 정보 보기","마커 정보 수정", "마커 삭제", "취소")
        builder.setTitle("마커 설정")
        builder.setItems(itemList) { dialog, which ->
            when(which) {
                0 -> {
                    val dialogView = View.inflate(this@MainActivity, R.layout.information_dialog, null)
                    var dlg = AlertDialog.Builder(this@MainActivity)
                    dlg.setView(dialogView)

                    dialogView.findViewById<TextView>(R.id.location_name).text = p1?.itemName
                    //db에 저장된 poiItem 메모정보 가져오기 dialogView.findViewById<TextView>(R.id.memocontent).text = p1?.메모
                    //db에 저장된 poiItem 시간 가져오기 dialogView.findViewById<TextView>(R.id.memocontent).text = p1?.시간ㅇㅁ
                    dlg.setPositiveButton("확인",null)
                    dlg.show()
                }
                1 -> {
                    val dialogView = View.inflate(this@MainActivity, R.layout.input_information_dialog, null)
                    var dlg = AlertDialog.Builder(this@MainActivity)
                    dlg.setView(dialogView)

                    dlg.setPositiveButton("확인") { dialog, which ->
                        val loc = p1?.mapPoint
                        val itemname = dialogView.findViewById<EditText>(R.id.location_name).text.toString()
                        val memo = dialogView.findViewById<EditText>(R.id.memocontent).text.toString()
                        val time = dialogView.findViewById<EditText>(R.id.editTextTime).text.toString()
                        val lat = p1?.mapPoint?.mapPointGeoCoord?.latitude
                        val lng = p1?.mapPoint?.mapPointGeoCoord?.longitude
                        val marker = MapPOIItem()
                        val marker_Info = MarkerEntity(null,itemname,memo,time,lat,lng)

                        p0?.removePOIItem(p1)
                        marker.itemName = itemname
                        marker.mapPoint = loc
                        marker.markerType = MapPOIItem.MarkerType.BluePin
                        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                        binding.mapView.addPOIItem(marker)
                        insertInfo(marker_Info)
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
}