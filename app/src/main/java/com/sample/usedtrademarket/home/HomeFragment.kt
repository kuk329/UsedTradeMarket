package com.sample.usedtrademarket.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sample.usedtrademarket.DBKey.Companion.DB_ARTICLES
import com.sample.usedtrademarket.R
import com.sample.usedtrademarket.databinding.FragmentHomeBinding

class HomeFragment:Fragment(R.layout.fragment_home) {

    private lateinit var articleDB : DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter

    private val articleList = mutableListOf<ArticleModel>()
    private val listener = object:ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            // model class 자체를 받음
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel?:return // null일때 예외 처리

            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}
    }

    private var binding: FragmentHomeBinding? = null
    private val auth: FirebaseAuth by lazy{
        Firebase.auth
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        articleList.clear()
        articleDB = Firebase.database.reference.child(DB_ARTICLES)


        articleAdapter = ArticleAdapter()
//        articleAdapter.submitList(mutableListOf<ArticleModel>().apply {
//            add(ArticleModel("0","aaa",100000,"5000원",""))
//            add(ArticleModel("1","bbb",200000,"6000원",""))
//        })

        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter

        fragmentHomeBinding.addFloatingButton.setOnClickListener{
            val intent=Intent(requireContext(),AddArticleActivity::class.java)
            startActivity(intent)

        }

        articleDB.addChildEventListener(listener)
    }

    override fun onResume() { // 다시 home tab을 했을때
        super.onResume()

        articleAdapter.notifyDataSetChanged()
    }
    override fun onDestroy() {
        super.onDestroy()
        articleDB.removeEventListener(listener)
    }
}