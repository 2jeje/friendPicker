package com.jeje.friendpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jeje.friendpicker.databinding.FragmentFriendPickerBinding
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

    private lateinit var binding : FragmentFriendPickerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.friends.observe(viewLifecycleOwner, Observer {
            pickerAdapter.setFriends(it)
        })

        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_friend_picker, container,false)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search_bar.doOnTextChanged { text, start, before, count ->
            pickerAdapter.setFriends(viewModel.search(text))
            pickerAdapter.notifyDataSetChanged()
        }

        done_btn.setOnClickListener {
            this.activity?.finish()
        }

        back_btn.setOnClickListener {
            this.activity?.finish()
        }

        selectedAdapter =
            FriendSelectedAdapter(requireContext()) { friend, list ->
                pickerAdapter.removeSelectedFriend(friend, list)
                updateHeaderView()
                updateSelectedFriendsVisibility()
            }

        viewModel.friends.value?.filter { it.checked == true }?.let {
            selectedAdapter.setSelectedFriends(
                it
            )
        }
        selected_friends_view.layoutManager =
            LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        selected_friends_view.adapter = selectedAdapter

        pickerAdapter = FriendPickerAdapter(requireContext(),
            addCallback = {
                selectedAdapter.addFriend(it)
                updateHeaderView()
                updateSelectedFriendsVisibility()
            }, removeCallback = {
                selectedAdapter.removeFriend(it)
                updateHeaderView()
                updateSelectedFriendsVisibility()
            })

        friends_view.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        friends_view.adapter = pickerAdapter

        viewModel.fetch { startPos, numberOfItem, error ->
            if (error == null) {
                if (startPos != null && numberOfItem != null) {
                    viewModel.friends.value?.toMutableList()?.let { pickerAdapter.setFriends(it) }
                    pickerAdapter.notifyItemRangeInserted(startPos, numberOfItem)
                }
            }
        }

        updateSelectedFriendsVisibility()
    }

    private fun updateHeaderView() {
        selectedAdapter.friends?.let {
            if (it.isEmpty()) {
                count_view.text = ""
                done_btn.isEnabled = false
            } else {
                count_view.text = it.size.toString()
                done_btn.isEnabled = true
            }
        }
    }

    private fun updateSelectedFriendsVisibility() {
        if (selectedAdapter.friends.isNullOrEmpty()) {
            selected_friends_view.visibility = View.GONE
        }
        else {
            selected_friends_view.visibility = View.VISIBLE
        }
        selected_friends_view.scrollToPosition(0)
    }
}
