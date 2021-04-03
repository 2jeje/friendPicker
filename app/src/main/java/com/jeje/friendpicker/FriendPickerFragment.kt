package com.jeje.friendpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jeje.friendpicker.model.Friend
import com.jeje.friendpicker.viewmodel.FriendPickerViewModel
import kotlinx.android.synthetic.main.fragment_friend_picker.*


/**
 * A simple [Fragment] subclass.
 * Use the [FriendPickerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendPickerFragment : Fragment() {

    private lateinit var pickerAdapter: FriendPickerAdapter
    private lateinit var selectedAdapter: FriendSelectedAdapter

    private val viewModel: FriendPickerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.friends.observe(viewLifecycleOwner, Observer {
            pickerAdapter.setFriends(it)
        })

        viewModel.selectedFriend.observe(viewLifecycleOwner, Observer {
            pickerAdapter.setSelectedFriends(it)
            selectedAdapter.setSelectedFriends(it)
            updateHeaderView(it)
            updateSelectedFriendView(it)
        })

        return inflater.inflate(R.layout.fragment_friend_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search_bar.doOnTextChanged { text, start, before, count ->
            viewModel.searchName(text)
        }

        done_btn.setOnClickListener {
            this.activity?.finish()
        }

        back_btn.setOnClickListener {
            this.activity?.finish()
        }

        selectedAdapter =
            FriendSelectedAdapter(requireContext(), selected_friends_view) { friend, list ->
                viewModel.setSelectedFriends(list)
                pickerAdapter.removeSelectedFriend(friend, list)
                updateHeaderView(list)
                updateSelectedFriendView(list)
            }
        selected_friends_view.layoutManager =
            LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        selected_friends_view.adapter = selectedAdapter

        pickerAdapter = FriendPickerAdapter(requireContext(), listCallback = {
            viewModel.setSelectedFriends(it)
        }, addCallback = { selectedAdapter.addFriend(it) }, removeCallback = {
            selectedAdapter.removeFriend(it)
        })
        friends_view.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        friends_view.adapter = pickerAdapter

        viewModel.fetch { startPos, numberOfItem, error ->
            if (error == null) {
                if (startPos != null && numberOfItem != null) {
                    pickerAdapter.notifyItemRangeInserted(startPos, numberOfItem)
                }
            }
        }
        updateSearchView()
    }

    private fun updateSelectedFriendView(selectedFriends: List<Friend>) {
        if (selectedFriends.isNullOrEmpty()) {
            selected_friends_view.visibility = View.GONE
        } else {
            selected_friends_view.visibility = View.VISIBLE
        }
        selected_friends_view.scrollToPosition(0)
    }

    private fun updateHeaderView(selectedFriends: List<Friend>) {
        selectedFriends.let {
            if (it.isEmpty()) {
                count_view.text = ""
                done_btn.isEnabled = false
            } else {
                count_view.text = it.size.toString()
                done_btn.isEnabled = true
            }
        }
    }

    private fun updateSearchView() {
        search_bar.setText(viewModel.searchText)
    }
}
