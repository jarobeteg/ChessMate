package com.example.chessmate.ui.fragment

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.adapter.Item
import com.example.chessmate.adapter.MoreAdapter
import com.example.chessmate.ui.ViewModel.MoreViewModel
import com.example.chessmate.ui.activity.SettingsActivity

class MoreFragment : Fragment() {

    companion object {
        fun newInstance() = MoreFragment()
    }

    private lateinit var viewModel: MoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)
        val textView: TextView = requireActivity().findViewById(R.id.main_toolbar_title)
        textView.text = getString(R.string.bottom_nav_more)
        val recyclerView: RecyclerView = view.findViewById(R.id.moreFragment_recyclerView)
        val items = listOf(
            Item("Settings Menu") { startActivity(SettingsActivity::class.java) }
        )
        val adapter = MoreAdapter(items)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return view
    }

    private fun startActivity(activityClass: Class<*>){
        val context = requireContext()
        context.startActivity(Intent(context, activityClass))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MoreViewModel::class.java)
        // TODO: Use the ViewModel
    }
}