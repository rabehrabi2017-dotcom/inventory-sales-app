package com.inventory.sales.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.inventory.sales.databinding.FragmentSearchBinding
import com.inventory.sales.ui.adapter.ProductAdapter
import com.inventory.sales.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)
        setupRecyclerView()
        setupSearchInput()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            onEdit = { },
            onDelete = { },
            onSell = { }
        )
        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchResultsRecyclerView.adapter = adapter
    }

    private fun setupSearchInput() {
        binding.searchInput.addTextChangedListener { text ->
            viewModel.updateSearchQuery(text.toString())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collect { results ->
                adapter.submitList(results)
            }
        }
    }
}