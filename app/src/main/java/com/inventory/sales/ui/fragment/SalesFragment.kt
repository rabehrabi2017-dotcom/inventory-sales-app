package com.inventory.sales.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.inventory.sales.databinding.FragmentSalesBinding
import com.inventory.sales.ui.adapter.SaleAdapter
import com.inventory.sales.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

class SalesFragment : Fragment() {
    private lateinit var binding: FragmentSalesBinding
    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: SaleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = SaleAdapter()
        binding.salesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.salesRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sales.collect { sales ->
                adapter.submitList(sales)
            }
        }
    }
}