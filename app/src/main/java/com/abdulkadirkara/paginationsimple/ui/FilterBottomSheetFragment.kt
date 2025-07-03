package com.abdulkadirkara.paginationsimple.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.abdulkadirkara.paginationsimple.databinding.FragmentFilterBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFilterBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStateFromViewModel()
        setupListeners()
    }

    private fun initStateFromViewModel() {
        // Gender
        when (viewModel.gender) {
            "male" -> binding.chipMale.isChecked = true
            "female" -> binding.chipFemale.isChecked = true
            else -> binding.chipGroupGender.clearCheck()
        }

        // Nationalities
        val selectedNats = viewModel.nationalities
        val chipGroup = binding.chipGroupNationality
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.isChecked = selectedNats.contains(chip.text.toString())
        }
    }

    private fun setupListeners() {
        binding.btnApply.setOnClickListener {
            val gender = when {
                binding.chipMale.isChecked -> "male"
                binding.chipFemale.isChecked -> "female"
                else -> null
            }

            val selectedNats = mutableListOf<String>()
            for (i in 0 until binding.chipGroupNationality.childCount) {
                val chip = binding.chipGroupNationality.getChildAt(i) as Chip
                if (chip.isChecked) selectedNats.add(chip.text.toString())
            }

            viewModel.applyFilter(gender, selectedNats)
            (parentFragment as? HomeFragment)?.userPagingAdapter?.refresh()
            dismiss()
        }

        binding.btnReset.setOnClickListener {
            viewModel.resetFilter()
            dismiss()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
