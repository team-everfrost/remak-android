package com.example.remak.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilitySystem
import com.example.remak.adapter.TagItemOffsetDecoration
import com.example.remak.adapter.TagRVAdapter
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.MainTagFragmentBinding
import com.example.remak.view.detail.TagDetailActivity

class MainTagFragment : Fragment(), TagRVAdapter.OnItemClickListener {
    private lateinit var binding: MainTagFragmentBinding
    private val viewModel: TagViewModel by activityViewModels { TagViewModelFactory(tokenRepository) }
    private lateinit var tokenRepository: TokenRepository
    private lateinit var adapter: TagRVAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)


        binding = MainTagFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }
        //뒤로가기 시 홈 프래그먼트로 이동
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (activity as MainActivity).binding.bottomNavigation.selectedItemId = R.id.homeFragment
            isEnabled = false
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TagRVAdapter(requireActivity(), listOf(), this)
        binding.tagRV.adapter = adapter
        binding.tagRV.layoutManager = LinearLayoutManager(requireContext())

        binding.tagRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = recyclerView.layoutManager?.itemCount
                val lastVisibleItemCount =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                if (totalItemCount!! <= (lastVisibleItemCount + 5)) {
                    viewModel.getNewTagList()
                }
            }
        })

        val itemDecoration = TagItemOffsetDecoration(30)
        binding.tagRV.addItemDecoration(itemDecoration)

        viewModel.getTagList()
        viewModel.tagList.observe(viewLifecycleOwner) {
            adapter.tagData = it
            adapter.notifyDataSetChanged()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("TagFragment", "onDestroy")
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetScrollData()

    }

    override fun onItemClick(position: Int) {
        val intent = Intent(requireContext(), TagDetailActivity::class.java)
        intent.putExtra("tagName", adapter.getTagName(position))
        intent.putExtra("tagCount", adapter.getTagCount(position))
        startActivity(intent)

    }
}