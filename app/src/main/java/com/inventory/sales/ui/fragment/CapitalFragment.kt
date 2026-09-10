package com.inventory.sales.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.inventory.sales.databinding.FragmentCapitalBinding
import com.inventory.sales.ui.adapter.MonthlyTotalAdapter
import com.inventory.sales.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

class CapitalFragment : Fragment() {
    private lateinit var binding: FragmentCapitalBinding
    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: MonthlyTotalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCapitalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)
        setupRecyclerView()
        setupObservers()
        viewModel.loadMonthlyTotals()
    }

    private fun setupRecyclerView() {
        adapter = MonthlyTotalAdapter()
        binding.monthlyTotalsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.monthlyTotalsRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collect { products ->
                val capital = viewModel.calculateCapital(products)
                binding.capitalAmount.text = String.format("%.2f", capital)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.monthlyTotals.collect { totals ->
                val items = totals.map { (month, total) ->
                    MonthlyTotal(month, total)
                }.sortedByDescending { it.month }
                adapter.submitList(items)
            }
        }
    }
}

data class MonthlyTotal(val month: String, val total: Double)