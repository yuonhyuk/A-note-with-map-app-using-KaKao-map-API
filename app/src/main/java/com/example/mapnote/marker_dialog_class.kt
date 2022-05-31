package com.example.mapnote

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText

class InfoDialog(context : Context) {
    private val dialog = Dialog(context)

    fun show(){
        dialog.setContentView(R.layout.input_information_dialog)
        dialog.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함

        val locaName = dialog.findViewById<EditText>(R.id.location_name)
        val memotext = dialog.findViewById<EditText>(R.id.memocontent)
        val editedTime = dialog.findViewById<EditText>(R.id.editTextTime)
        val posBtn = dialog.findViewById<Button>(R.id.btn_positive)
        val negBtn = dialog.findViewById<Button>(R.id.btn_negative)

        dialog.show()

        posBtn.setOnClickListener{
            onClickedListner.onClicked(locaName.text.toString(),memotext.text.toString(),editedTime.text.toString())
            dialog.dismiss()
        }

        //cancel 버튼 동작
        negBtn.setOnClickListener{
            dialog.dismiss()
        }
    }
    interface BtnOnClickListner{
        fun onClicked(localname : String, memotext : String, deadTime : String)
    }
    private  lateinit var onClickedListner: BtnOnClickListner
    fun setOnClickedListner(listener : BtnOnClickListner){
        onClickedListner = listener
    }
}
