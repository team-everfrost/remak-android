package com.example.remak.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.remak.App
import com.example.remak.adapter.CollectionRVAdapter
import com.example.remak.adapter.SpacingItemDecorator
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.MainCollectionFragmentBinding

class MainCollectionFragment : Fragment(), CollectionRVAdapter.OnItemClickListener {
    private lateinit var binding: MainCollectionFragmentBinding
    private val viewModel: CollectionViewModel by viewModels {
        CollectionViewModelFactory(
            tokenRepository
        )
    }
    private lateinit var tokenRepository: TokenRepository
    private lateinit var adapter: CollectionRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)
        binding = MainCollectionFragmentBinding.inflate(inflater, container, false)
        viewModel.getCollectionList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isCollectionEmpty.observe(viewLifecycleOwner) {
            if (it) {
                binding.emptyCollectionLayout.visibility = View.VISIBLE
                binding.collectionLayout.visibility = View.GONE
            } else {
                binding.emptyCollectionLayout.visibility = View.GONE
                binding.collectionLayout.visibility = View.VISIBLE
            }
        }

        val recyclerView = binding.collectionRecyclerView
        adapter = CollectionRVAdapter(listOf(), this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpacingItemDecorator(30))
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)


        viewModel.collectionList.observe(viewLifecycleOwner) {
            adapter.collectionData = it
            adapter.notifyDataSetChanged()
        }

    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

}