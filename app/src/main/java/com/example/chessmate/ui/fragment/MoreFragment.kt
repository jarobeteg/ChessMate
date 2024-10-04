package com.example.chessmate.ui.fragment

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.adapter.Item
import com.example.chessmate.adapter.MoreAdapter
import com.example.chessmate.ui.activity.BoardEditorActivity
import com.example.chessmate.ui.viewmodel.MoreViewModel
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
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.moreFragment_recyclerView)
        val items = listOf(
            Item(getString(R.string.title_settings_menu)) { startActivity(SettingsActivity::class.java) },
            Item(getString(R.string.board_editor_title)) { startActivity(BoardEditorActivity::class.java) },
            Item(getString(R.string.chess_com_title), true) { openUrl("https://www.chess.com/")},
            Item(getString(R.string.lichess_org_title), true) { openUrl("https://lichess.org/") },
            Item(getString(R.string.agadmator_title), true) { openUrl("https://www.youtube.com/@agadmator") },
            Item(getString(R.string.gotham_chess_title), true) { openUrl("https://www.youtube.com/@GothamChess") },
            Item(getString(R.string.find_me_on_chess_com_title), true) { openUrl("https://www.chess.com/member/jarobeteg") }
        )
        val adapter = MoreAdapter(items, requireContext())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    private fun startActivity(activityClass: Class<*>){
        val context = requireContext()
        context.startActivity(Intent(context, activityClass))
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MoreViewModel::class.java)
        // TODO: Use the ViewModel
    }
}