package com.example.joinin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.joinin.databinding.InputModalBinding

class InputDialogFragment : DialogFragment() {

    private var _binding: InputModalBinding? = null
    private val binding get() = _binding!!

    interface InputModalListener {
        fun onSubmitButtonClicked(link: String, title: String, description: String, category: String)

    }

    private var listener: InputModalListener? = null
    private var selectedCategory: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = InputModalBinding.inflate(inflater, container, false)

        // Defining the categories
        val categories = listOf("Books", "Crypto", "Business", "Makeup", "Beauty", "Cooking", "Games", "Fashion", "Vlogs", "Movies")


        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
                Toast.makeText(requireContext(), "Selected category: $selectedCategory", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        binding.submitButton.setOnClickListener { //when clicked on submit button , the informations are collected.
            val link = binding.linkEditText.text.toString()
            val title = binding.titleEditText.text.toString()
            val description = binding.descriptionEditText.text.toString()

            listener?.onSubmitButtonClicked(link, title, description, selectedCategory)
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setInputModalListener(listener: InputModalListener) {
        this.listener = listener
    }
}
