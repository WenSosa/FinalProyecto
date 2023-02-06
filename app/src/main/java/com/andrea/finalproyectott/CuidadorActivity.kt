package com.andrea.finalproyectott

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.andrea.finalproyectott.activities.LoginActivity
import com.andrea.finalproyectott.databinding.ActivityCuidadorBinding
import com.andrea.finalproyectott.fragments.*
import com.andrea.finalproyectott.others.ToolbarActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class CuidadorActivity : ToolbarActivity() , NavigationView.OnNavigationItemSelectedListener{

    private val mAuth : FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var binding: ActivityCuidadorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuidadorBinding.inflate(layoutInflater)
        val view = binding.root

        setNavDrawer()
        setUserHeaderInformation()

        if (savedInstanceState == null) {
            fragmentTransaction(PerfilFragment())
            binding.navView2.menu.getItem(0).isChecked = true
        }
        setContentView(view)
    }
    private fun setNavDrawer(){
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, _toolbar , R.string.open_drawer, R.string.close_drawer)
        toggle.isDrawerIndicatorEnabled = true
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView2.setNavigationItemSelectedListener(this)
    }
    private fun fragmentTransaction(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun loadFragmentById(id: Int) {
        when(id){
            R.id.nav_home->fragmentTransaction(PerfilFragment())
            R.id.nav_levels->fragmentTransaction(NivelesCuidadorFragment())//lista
            R.id.nav_insulina->fragmentTransaction(InsulinaCuidadorFragment())//vista
            R.id.nav_medicamento->fragmentTransaction(MedicamentoCuidador())
            R.id.nav_alimentos->fragmentTransaction(AlimenticiaFragment())
            R.id.nav_recordatorios->fragmentTransaction(RecordatoriosCuidadorFragment())
            R.id.nav_profile->fragmentTransaction(DatosPerfil())
        }
    }
    private fun setUserHeaderInformation() {
        //val name = nav_view.getHeaderView(0).findViewById<TextView>(R.id.textViewName)
        val email = binding.navView2.getHeaderView(0).findViewById<TextView>(R.id.textViewEmail)
        val foto = binding.navView2.getHeaderView(0).findViewById<ImageView>(R.id.imageViewAvatar)
        val user = mAuth.currentUser
        var usermail = ""
        //var username = ""
        //var photoURL =""
        user?.let {
            usermail = user.email.toString()
            //username?.let{ user.displayName.toString()}
            user.photoUrl?.let {
                Picasso.get().load(user.photoUrl).resize(100, 100).into(foto)
            }?: kotlin.run {
                Picasso.get().load(R.drawable.ic_home).resize(100, 100).into(foto)
            }
        }
        email?.let { email.text = usermail }
        //name?.let { name.text = username }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        loadFragmentById(item.itemId)
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.general_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_log_out -> {
                FirebaseAuth.getInstance().signOut()
                goToActivity<LoginActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
            R.id.menu_perfil -> {
                goToActivity<MainActivity>(){
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
