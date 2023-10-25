package com.github.lilinsong3.learnview

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.github.lilinsong3.learnview.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityMainBinding
    private val sharedMainViewModel: SharedMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        NavigationUI.setupWithNavController(dataBinding.toolbarTop, navController, dataBinding.drawer)
        dataBinding.drawerNav.setupWithNavController(navController)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                sharedMainViewModel.event.collect {
                    when (it) {
                        // 工具栏显示控制
                        is Event.AppBarVisibilityEvent -> {
                            dataBinding.toolbarTop.visibility = if (it.shown) {
                                View.VISIBLE
                            } else {
                                View.GONE
                            }
                        }
                    }
                }
            }
        }
    }
}