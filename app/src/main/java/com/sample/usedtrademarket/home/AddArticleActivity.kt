package com.sample.usedtrademarket.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.sample.usedtrademarket.DBKey.Companion.DB_ARTICLES
import com.sample.usedtrademarket.R

class AddArticleActivity:AppCompatActivity() {

    private var selectedUri : Uri? = null // 파베에 저장하기 위해 전역변수로 지정
    private val auth:FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    private val articleDB:DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_article)

        findViewById<ImageView>(R.id.img_back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.imageAddButton).setOnClickListener {
            when{
                ContextCompat.checkSelfPermission( // 특정 권한이 부여됬는지 여부 확인 (여기서는 외부 저장소를 읽어도 되는지에 대해)
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED ->{ // 권한이 이미 부여되었을때
                    Log.d("test","권한 부여 완료")
                    startContentProvider() // 사진 가져오기

                }
                // 교육용 팝업이 필요한 경우
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)-> {
                    showPermissionContextPopup()// 팝업 작성 코드

                }
                else ->{ // 권한이 부여되지 않았으므로 권한 요청
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1010)
                }
            }
        }

        findViewById<TextView>(R.id.tv_submit).setOnClickListener { // 등록하기 버튼을 눌렀을때
            val title = findViewById<EditText>(R.id.titleEditText).text.toString()
            val price = findViewById<EditText>(R.id.priceEditText).text.toString()
            val sellerId = auth.currentUser?.uid.orEmpty()

            // 중간에 이미지가 있으면 업로드 과정을 추가
            if(selectedUri !=null){ // 예외 처리
                val photoUri = selectedUri?: return@setOnClickListener // 굳이 필요 없는 구문
                uploadPhoto(photoUri,  // 람다식 이용
                    successHandler = { uri ->
                        uploadArticle(sellerId, title, price,uri )
                    },
                    errorHandler = {
                        Toast.makeText(this,"사진 업로드에 실패했습니다.",Toast.LENGTH_SHORT).show()
                    }
                )
            }else{ // 이미지가 없을때
                uploadArticle(sellerId,title,price, "")
            }

        }
    }
    private fun uploadPhoto(uri:Uri , successHandler:(String) ->Unit,errorHandler:()->Unit){
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("article/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener{
                if(it.isSuccessful){ // 업로드 완료
                    storage.reference.child("article/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri->  // 다운로드 uri를 성공적으로 가져왔을 경우
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                        }

                }else{ // 업로드 실패
                    errorHandler()
                }
            }

    }

    private fun uploadArticle(sellerId:String, title:String, price:String, imageUrl : String ){

        val model = ArticleModel(sellerId,title,System.currentTimeMillis(),"$price 원",imageUrl)
        articleDB.push().setValue(model) // model 아이템 하나를 firebase 에 넣음
        finish()
    }

    // 권한 요청에 대한 결과처리 함수
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            1010->{
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    startContentProvider()
                }else{
                    Toast.makeText(this,"사진 권한을 승낙해야 이미지 업로드가 가능합니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    } // end of onRequestPermissionsResult

    private fun startContentProvider(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type="image/*" // 이미지 타입의 모든것 허용
        // todo 추후 startActivityForResult 변경
        startActivityForResult(intent,2020)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("test","onActivityResult")
        Log.d("test","requestCode : "+requestCode)

        // 예외처리
//        if(requestCode!= Activity.RESULT_OK){
//            return
//        }
        when(requestCode){
            2020->{
                // 넘어온 사진 uri 처리
                val uri = data?.data
                Log.d("test","uri 1:"+uri)
                if(uri!=null){
                    findViewById<ImageView>(R.id.photoImageView).setImageURI(uri)
                    Log.d("test","uri 2:"+uri)
                    selectedUri = uri // 파베에 저장하기 위함.
                }else{
                    Toast.makeText(this,"사진을 가져오는데 실패했습니다.",Toast.LENGTH_SHORT).show()
                }

            }else->{
                Toast.makeText(this,"사진을 가져오는데 실패했습니다.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 교육용 팝업 생성 함수
    private fun showPermissionContextPopup(){
        AlertDialog.Builder(this)
            .setTitle("권한 요청")
            .setMessage("사진을 가져오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의"){_,_->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1000)
            }
            .create()
            .show()

    }

} // end of class