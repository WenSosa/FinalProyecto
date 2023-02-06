package com.andrea.finalproyectott.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PagerAdapter (fm : FragmentManager) : FragmentPagerAdapter(fm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    private val fragmentList = ArrayList<Fragment>()

    override fun getItem(position: Int)= fragmentList[position]

    override fun getCount() = fragmentList.size

    fun addFragment(fragment : Fragment) = fragmentList.add(fragment)

}