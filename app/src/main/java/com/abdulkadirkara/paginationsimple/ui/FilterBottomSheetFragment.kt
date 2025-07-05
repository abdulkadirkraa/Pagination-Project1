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
    /*
    filtreleri kaydettiğiniz SavedStateHandle’i okuyup kullanırken HomeFragment ile FilterBottomSheetFragment’ın farklı ViewModel örnekleri kullanıyor olabilirsiniz.
    FilterBottomSheetFragment’ta by activityViewModels() ile Activity scope’unda bir HomeViewModel alıyorsunuz.
    Muhtemelen HomeFragment’ta ise by viewModels() (fragment scope) kullanıyorsunuz, bu da FilterBottomSheetFragment’ın yazdığı state’i görmüyor.
    Sonuç olarak applyFilter(...) ile SavedStateHandle’e yazdığınız gender ve nat değerleri aslında bir başka ViewModel örneğine gidiyor, HomeFragment’ın kullandığı ViewModel’a değil. O yüzden PagingSource’a hep null, null gidiyor.
     */

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
            // (parentFragment as? HomeFragment)?.userPagingAdapter?.refresh()
            //Filtre uygulandığında adapter’ı yeniden başlatmak (yeniden PagingSource oluşturmak) için adapter.refresh() çağırın:
            //Eğer userPagingAdapter’ı doğrudan alamıyorsanız, HomeFragment içinde viewModel.users flow’u zaten collectLatest ile dinliyor ve yeni PagingData geldiğinde otomatik olarak submitData ediyor.
            //Yine de adapter.refresh() performansı arttırabilir.
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
