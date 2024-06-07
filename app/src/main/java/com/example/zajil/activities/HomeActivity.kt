package com.example.zajil.activities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.zajil.R
import com.example.zajil.databinding.ActivityHomeBinding
import com.example.zajil.databinding.DialogLogoutBinding
import com.example.zajil.databinding.DialogOnlineOfflineDialogBinding
import com.example.zajil.fragments.AboutUsFragment
import com.example.zajil.fragments.HelpSupportFragment
import com.example.zajil.fragments.HomeFragment
import com.example.zajil.fragments.NotificationsFragment
import com.example.zajil.fragments.OrdersFragment
import com.example.zajil.fragments.PrivacyPolicyFragment
import com.example.zajil.fragments.RequestFragment
import com.example.zajil.fragments.SelectLanguageFragment
import com.example.zajil.fragments.SettingsFragment
import com.example.zajil.fragments.TermsAndConditionsFragment
import com.example.zajil.fragments.WalletFragment
import com.example.zajil.services.LocationService
import com.example.zajil.util.App
import com.example.zajil.util.Commons
import com.example.zajil.util.Commons.loadCircleCrop
import com.example.zajil.util.Commons.showLogoutDialog
import com.example.zajil.util.Constants
import com.example.zajil.viewModel.NetworkViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class HomeActivity : BaseActivity<ActivityHomeBinding>(), View.OnClickListener {

    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }

    override fun getLayout() = ActivityHomeBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Commons.setTransparentStatusBarOnly(this)
        binding.viewPagerHome.offscreenPageLimit = 1
        binding.viewPagerHome.adapter = ViewPagerAdapter()
        binding.viewPagerHome.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayoutHome, binding.viewPagerHome) { tab, position ->
            when (position) {
                0 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.home)
                    tab.text = getString(R.string.home)
                }


                1 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.requests)
                    tab.text = getString(R.string.request)
                }

                2 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.orders)
                    tab.text = getString(R.string.my_orders)
                }


                3 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.wallet)
                    tab.text = getString(R.string.wallet)
                }
            }
        }.attach()

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.viewPagerHome.registerOnPageChangeCallback(onPageCallback)

        binding.includedLayout.ivNotification.setOnClickListener(this)
        binding.includedDrawer.tvAboutUs.setOnClickListener(this)
        binding.includedLayout.ivLanguage.setOnClickListener(this)
//        binding.includedDrawer.tvContactUs.setOnClickListener(this)
        binding.includedDrawer.tvHelpSupport.setOnClickListener(this)
        binding.includedDrawer.tvTerms.setOnClickListener(this)
        binding.includedDrawer.tvPrivacyPolicy.setOnClickListener(this)
        binding.includedLayout.ivMenu.setOnClickListener(this)
        binding.includedDrawer.ivProfile.setOnClickListener(this)
        binding.includedDrawer.tvSettings.setOnClickListener(this)

        binding.tabLayoutHome.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (binding.tabLayoutHome.selectedTabPosition == 1) {
                    if (App.preferenceManager.user?.is_online != true) {
                        isUserOnlineDialog()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }


    fun isUserOnlineDialog() {
        Dialog(this, R.style.dialog_style).apply {
            DialogOnlineOfflineDialogBinding.inflate(layoutInflater).let {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCanceledOnTouchOutside(false)
                setCancelable(false)
                setContentView(it.root)
                window?.setDimAmount(0.8F)
                it.tvOkay.setOnClickListener {
                    dismiss()
                    binding.tabLayoutHome.selectTab(binding.tabLayoutHome.getTabAt(0), true)
                }
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserDetail()
        App.preferenceManager.user?.let {
            binding.includedDrawer.tvName.text = it.name
            binding.includedDrawer.ivProfile.loadCircleCrop(it.idImage)
            binding.includedDrawer.tvPhone.text = it.phoneNumber
        }
    }


    override fun onClick(p0: View?) {
        when (p0) {

            binding.includedLayout.ivLanguage -> {
                startActivity(Intent(this, CommonActivity::class.java).apply {
                    putExtra(Constants.FRAGMENT_NAME, SelectLanguageFragment::class.java.simpleName)
                })
            }

            binding.includedDrawer.ivProfile -> {
                /*showMediaOptionDialog({
                    // camera option
                }, {
                    // gallery option
                })*/
            }

            binding.includedLayout.ivNotification -> {
                startActivity(Intent(this, CommonActivity::class.java).apply {
                    putExtra(Constants.FRAGMENT_NAME, NotificationsFragment::class.java.simpleName)
                })
            }

            binding.includedDrawer.tvAboutUs -> {
                startActivity(Intent(this, CommonActivity::class.java).apply {
                    putExtra(Constants.FRAGMENT_NAME, AboutUsFragment::class.java.simpleName)
                    putExtra(Constants.URL_TO_OPEN, Constants.ABOUT_US)
                })
            }

            /*binding.includedDrawer.tvContactUs -> {
                startActivity(Intent(this, CommonActivity::class.java).apply {
                    putExtra(Constants.FRAGMENT_NAME, ContactUsFragment::class.java.simpleName)
                })
            }*/

            binding.includedDrawer.tvHelpSupport -> {
                startActivity(Intent(this, CommonActivity::class.java).apply {
                    putExtra(Constants.FRAGMENT_NAME, HelpSupportFragment::class.java.simpleName)
                })
            }

            binding.includedDrawer.tvTerms -> {
                startActivity(Intent(this, CommonActivity::class.java).apply {
                    putExtra(
                        Constants.FRAGMENT_NAME,
                        TermsAndConditionsFragment::class.java.simpleName
                    )
                    putExtra(Constants.URL_TO_OPEN, Constants.TERMS_AND_CONDITION)
                })
            }

            binding.includedDrawer.tvPrivacyPolicy -> {
                startActivity(Intent(this, CommonActivity::class.java).apply {
                    putExtra(Constants.FRAGMENT_NAME, PrivacyPolicyFragment::class.java.simpleName)
                    putExtra(Constants.URL_TO_OPEN, Constants.PRIVACY_POLICY)
                })
            }

            binding.includedDrawer.tvSettings -> {
                startActivity(Intent(this, CommonActivity::class.java).apply {
                    putExtra(Constants.FRAGMENT_NAME, SettingsFragment::class.java.simpleName)
                })
            }

            binding.includedLayout.ivMenu -> {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }


    private val onPageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            binding.includedLayout.ivLanguage.isSelected = position != 0
//            binding.includedLayout.ivProfile.isSelected = position != 0
            binding.includedLayout.ivNotification.isSelected = position != 0
            when (position) {
                0 -> {
                    binding.includedLayout.tvCenter.visibility = View.GONE
                    binding.includedLayout.llLocation.visibility = View.VISIBLE
                    binding.includedLayout.ivMenu.setImageResource(R.drawable.hamburger_white)
                    binding.includedLayout.root.setBackgroundColor(getColor(R.color.primary))
                    window.statusBarColor = getColor(R.color.primary)
                    Commons.clearLightStatusBar(this@HomeActivity)
                }

                1 -> {
                    binding.includedLayout.ivMenu.setImageResource(R.drawable.hamburger_primary)
                    binding.includedLayout.tvCenter.visibility = View.VISIBLE
                    binding.includedLayout.tvCenter.text = getString(R.string.new_request)
                    binding.includedLayout.llLocation.visibility = View.GONE
                    binding.includedLayout.root.setBackgroundColor(getColor(R.color.white))
                    window.statusBarColor = getColor(R.color.white)
                    Commons.setLightStatusBar(this@HomeActivity)
                }

                2 -> {
                    binding.includedLayout.ivMenu.setImageResource(R.drawable.hamburger_primary)
                    binding.includedLayout.tvCenter.visibility = View.VISIBLE
                    binding.includedLayout.tvCenter.text = getString(R.string.my_orders)
                    binding.includedLayout.llLocation.visibility = View.GONE
                    binding.includedLayout.root.setBackgroundColor(getColor(R.color.white))
                    window.statusBarColor = getColor(R.color.white)
                    Commons.setLightStatusBar(this@HomeActivity)
                }

                else -> {
                    binding.includedLayout.ivMenu.setImageResource(R.drawable.hamburger_primary)
                    binding.includedLayout.tvCenter.visibility = View.VISIBLE
                    binding.includedLayout.tvCenter.text = getString(R.string.wallet)
                    binding.includedLayout.llLocation.visibility = View.GONE
                    binding.includedLayout.root.setBackgroundColor(getColor(R.color.white))
                    window.statusBarColor = getColor(R.color.white)
                    Commons.setLightStatusBar(this@HomeActivity)
                }
            }
        }
    }

    inner class ViewPagerAdapter : FragmentStateAdapter(this) {
        override fun getItemCount() = 4
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> RequestFragment()
                2 -> OrdersFragment()
                else -> WalletFragment()
            }
        }
    }


    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    baseContext,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // start choosing images
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                // show permission dialog
            } else {
                requestPermission()
            }
        } else {

            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    baseContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    baseContext,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // start choosing images
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                // show permission info dialog
            } else {
                requestPermission()
            }

        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            checkPermission()
        }


}
