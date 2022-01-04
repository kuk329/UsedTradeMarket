package com.sample.usedtrademarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sample.usedtrademarket.chatlist.ChatListFragment
import com.sample.usedtrademarket.home.HomeFragment
import com.sample.usedtrademarket.mypage.MyPageFragment
import com.sample.usedtrademarket.near.NearMyFragment
import com.sample.usedtrademarket.neighbor.NeighborFragment

class MainActivity : AppCompatActivity() {

    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private var isFabOpen : Boolean = false
    private lateinit var  fab1: FloatingActionButton
    private lateinit var  fab2: FloatingActionButton
    private lateinit var  fab3: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val chatListFragment = ChatListFragment()
        val myPageFragment = MyPageFragment()
        val nearMyFragment = NearMyFragment()
        val neighborFragment = NeighborFragment()

//        fab1 = findViewById(R.id.addFloatingButton)
//        fab2 = findViewById(R.id.fb_write)
//        fab3 = findViewById(R.id.fb_promotion)
//        fab1.setOnClickListener()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // 초기 프래그먼트 지정
        replaceFragment(homeFragment)
        bottomNavigationView.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.home-> replaceFragment(homeFragment)
                R.id.neighborhood->replaceFragment(neighborFragment)
                R.id.nearMe->replaceFragment(nearMyFragment)
                R.id.chatList->replaceFragment(chatListFragment)
                R.id.myPage->replaceFragment(myPageFragment)
            }
            true
        }

    } // end of onCreate

    private fun replaceFragment(fragment: Fragment){
        // 프래그먼트 replace 하는 method
        // 반복되는 일이므로 따로 빼서 작성

        supportFragmentManager.beginTransaction()
            .apply { // 어떤 작업을 할지 작성
                replace(R.id.fragmentContainer , fragment)
                commit()
            }
    }
}// end of class