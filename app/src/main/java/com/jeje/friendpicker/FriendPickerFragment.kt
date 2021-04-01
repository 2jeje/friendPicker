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
class FriendPickerFragment : Fragment(), FriendSelectedAdapterListener,
    FriendPickerAdapterListener {

    private lateinit var pickerAdapter: FriendPickerAdapter
    private lateinit var selectedAdapter: FriendSelectedAdapter

    private val viewModel: FriendPickerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.friends.observe(viewLifecycleOwner, Observer {
            pickerAdapter.notifyDataSetChanged()
        })

        viewModel.selectedFriends.observe(viewLifecycleOwner, Observer {
            selectedAdapter.notifyDataSetChanged()
        })

        return inflater.inflate(R.layout.fragment_friend_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search_bar.doOnTextChanged { text, start, before, count ->
            if (text.toString() == viewModel.searchText) {
                return@doOnTextChanged
            }

            viewModel.searchText = text.toString()

            if (text.isNullOrEmpty()) {
                viewModel.friends.value = viewModel.originFriends.toMutableList()
            } else {
                viewModel.friends.value = viewModel.originFriends.filterIndexed { index, friend ->
                    KoreanSoundSearchUtils.isMatchString(
                        friend.nickName.toString(),
                        text.toString()
                    ) != null
                }.toMutableList()
            }
        }

        done_btn.setOnClickListener {
            this.activity?.finish()
        }

        back_btn.setOnClickListener {
            this.activity?.finish()
        }

        selectedAdapter = FriendSelectedAdapter(requireContext(), viewModel, selected_friends_view)
        selected_friends_view.layoutManager =
            LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        selected_friends_view.adapter = selectedAdapter
        selectedAdapter.listener = this

        pickerAdapter = FriendPickerAdapter(requireContext(), viewModel)
        friends_view.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        friends_view.adapter = pickerAdapter
        pickerAdapter.listener = this

        viewModel.fetch { startPos, numberOfItem, error ->
            if (error == null) {
                if (startPos != null && numberOfItem != null) {
                    pickerAdapter.notifyItemRangeInserted(startPos, numberOfItem)
                }
            }
        }

        updateSelectedFriendView()
        updateSearchView()
        updateHeaderView()
    }

    override fun onSelectedFriendRemoved(friend: Friend?) {
        friend?.let {
            val pos = viewModel.friends.value?.indexOf(it)
            if (pos != null) {
                pickerAdapter.notifyItemChanged(pos + 1)
            }
        }
    }

    override fun onClickFriend(friend: Friend?) {

        friend?.let {
            if (friend.checked) {
                val removedPos = viewModel.selectedFriends.value?.indexOf(friend)

                if (removedPos != null) {
                    viewModel.selectedFriends.value?.removeAt(removedPos)
                    selectedAdapter.notifyItemRemoved(removedPos)
                }

            } else {
                viewModel.selectedFriends.value?.add(0, friend)
                selectedAdapter.notifyItemInserted(0)
                selected_friends_view.scrollToPosition(0)
            }

            updateHeaderView()
        }
    }

    fun updateSelectedFriendView() {
        if (viewModel.selectedFriends.value.isNullOrEmpty()) {
            selected_friends_view.visibility = View.GONE
        } else {
            selected_friends_view.visibility = View.VISIBLE
        }
    }

    fun updateHeaderView() {
        viewModel.selectedFriends.value?.let {
            if (it.size <= 0) {
                selected_friends_view.visibility = View.GONE
                count_view.text = ""
                done_btn.isEnabled = false
            } else {
                selected_friends_view.visibility = View.VISIBLE
                count_view.text = it.size.toString()
                done_btn.isEnabled = true
            }
        }
    }

    fun updateSearchView() {
        search_bar.setText(viewModel.searchText)
    }


}
