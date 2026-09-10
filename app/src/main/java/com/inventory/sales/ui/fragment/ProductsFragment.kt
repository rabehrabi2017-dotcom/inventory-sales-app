package com.inventory.sales.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.inventory.sales.R
import com.inventory.sales.data.entity.Product
import com.inventory.sales.databinding.FragmentProductsBinding
import com.inventory.sales.ui.adapter.ProductAdapter
import com.inventory.sales.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

class ProductsFragment : Fragment() {
    private lateinit var binding: FragmentProductsBinding
    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)
        setupRecyclerView()
        setupObservers()
        setupAddButton()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            onEdit = { product -> editProduct(product) },
            onDelete = { product -> deleteProduct(product) },
            onSell = { product -> showSellDialog(product) }
        )
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.productsRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collect { products ->
                adapter.submitList(products)
            }
        }
    }

    private fun setupAddButton() {
        binding.addProductButton.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun showAddProductDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_product, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.productNameInput)
        val priceInput = dialogView.findViewById<EditText>(R.id.productPriceInput)
        val quantityInput = dialogView.findViewById<EditText>(R.id.productQuantityInput)
        val thresholdInput = dialogView.findViewById<EditText>(R.id.productThresholdInput)

        AlertDialog.Builder(requireContext())
            .setTitle("إضافة منتج جديد")
            .setView(dialogView)
            .setPositiveButton("إضافة") { _, _ ->
                val name = nameInput.text.toString().trim()
                val price = priceInput.text.toString().toDoubleOrNull()
                val quantity = quantityInput.text.toString().toIntOrNull()
                val threshold = thresholdInput.text.toString().toIntOrNull()

                if (name.isEmpty() || price == null || quantity == null || threshold == null) {
                    Toast.makeText(requireContext(), "يرجى ملء جميع الحقول بشكل صحيح", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                viewModel.addProduct(name, price, quantity, threshold)
                Toast.makeText(requireContext(), "تم إضافة المنتج بنجاح", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    private fun editProduct(product: Product) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_product, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.productNameInput)
        val priceInput = dialogView.findViewById<EditText>(R.id.productPriceInput)
        val quantityInput = dialogView.findViewById<EditText>(R.id.productQuantityInput)
        val thresholdInput = dialogView.findViewById<EditText>(R.id.productThresholdInput)

        nameInput.setText(product.name)
        priceInput.setText(product.salePrice.toString())
        quantityInput.setText(product.currentQuantity.toString())
        thresholdInput.setText(product.lowStockThreshold.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("تعديل المنتج")
            .setView(dialogView)
            .setPositiveButton("تحديث") { _, _ ->
                val name = nameInput.text.toString().trim()
                val price = priceInput.text.toString().toDoubleOrNull()
                val quantity = quantityInput.text.toString().toIntOrNull()
                val threshold = thresholdInput.text.toString().toIntOrNull()

                if (name.isEmpty() || price == null || quantity == null || threshold == null) {
                    Toast.makeText(requireContext(), "يرجى ملء جميع الحقول بشكل صحيح", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updatedProduct = product.copy(
                    name = name,
                    salePrice = price,
                    currentQuantity = quantity,
                    lowStockThreshold = threshold,
                    updatedAt = System.currentTimeMillis()
                )
                viewModel.updateProduct(updatedProduct)
                Toast.makeText(requireContext(), "تم تحديث المنتج بنجاح", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    private fun deleteProduct(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("حذف المنتج")
            .setMessage("هل أنت متأكد من حذف ${product.name}؟")
            .setPositiveButton("حذف") { _, _ ->
                viewModel.deleteProduct(product)
                Toast.makeText(requireContext(), "تم حذف المنتج", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    private fun showSellDialog(product: Product) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_sell, null)
        val quantityInput = dialogView.findViewById<EditText>(R.id.sellQuantityInput)
        quantityInput.setText("1")

        AlertDialog.Builder(requireContext())
            .setTitle("بيع منتج")
            .setView(dialogView)
            .setPositiveButton("تنفيذ البيع") { _, _ ->
                val quantity = quantityInput.text.toString().toIntOrNull() ?: 1
                if (quantity <= 0 || quantity > product.currentQuantity) {
                    Toast.makeText(requireContext(), "كمية غير صحيحة", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewModel.recordSale(product, quantity)
                Toast.makeText(requireContext(), "تم تسجيل البيع بنجاح", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }
}