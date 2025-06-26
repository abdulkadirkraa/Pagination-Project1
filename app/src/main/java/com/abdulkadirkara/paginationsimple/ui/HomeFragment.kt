package com.abdulkadirkara.paginationsimple.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdulkadirkara.paginationsimple.databinding.FragmentHomeBinding
import com.abdulkadirkara.paginationsimple.ui.adapter.UserPagingAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    @Inject lateinit var userPagingAdapter: UserPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setupAdapterListeners()
        observeData()
        handleRetry()
        observeLoadStates()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.users.collectLatest {
                userPagingAdapter.submitData(it)
            }
        }
    }

    private fun initRecyclerView() = binding.recyclerView.apply {
        adapter = userPagingAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupAdapterListeners(){
        userPagingAdapter.onItemClick = {
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun observeLoadStates() {
        userPagingAdapter.addLoadStateListener { loadState ->
            val isLoading = loadState.source.refresh is LoadState.Loading
            val isError = loadState.source.refresh is LoadState.Error
            val isEmpty = loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    userPagingAdapter.itemCount == 0

            updateUIState(isLoading, isError, isEmpty, loadState)
            /*
        //Listener ile load state kullanımı
        //1)load state flow
        LoadState(
            refresh = LoadState.Loading/NotLoading/Error,
            prepend = LoadState.Loading/NotLoading/Error,
            append = LoadState.Loading/NotLoading/Error
        )
        LoadState Alanı	Ne Zaman Kullanılır?
        refresh =	İlk yükleme veya listeyi yenilemede / Uygulama ilk açıldığında ya da yenilemede
        append =    Sonraki sayfaları yüklerken, Listeye aşağıdan yeni sayfa ekleme / Liste aşağı kaydırıldığında yeni veri yüklenirken
        prepend =	(Kullanılmaz genelde ama) başa veri eklerken, Yukarıya yeni veri ekleme

        adapter.addLoadStateListener { loadState ->
            // Loading göster
            progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            // Hata durumunu yakala
            val errorState = loadState.source.refresh as? LoadState.Error
                ?: loadState.source.append as? LoadState.Error
            errorState?.let {
                Toast.makeText(context, "Hata: ${it.error.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
            // Retry butonunu göster/gizle
            retryButton.isVisible = loadState.source.refresh is LoadState.Error
        }

        Hataları ya da yükleniyor göstergelerini listeye item gibi eklemek istersen:
        val adapter = MyPagingAdapter().withLoadStateFooter(footer = MyLoadStateAdapter { adapter.retry() })


        viewLifecycleOwner.lifecycleScope.launch {
            userPagingAdapter.loadStateFlow.collectLatest {
                when(it) {
                    is LoadState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is LoadState.NotLoading -> {
                        binding.progressBar.visibility = View.GONE

                    }
                    is LoadState.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }

                    else -> {}
                }
            }

            //2) addLoadStateListener
            userPagingAdapter.addLoadStateListener {
                when(it.refresh) {
                    is LoadState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is LoadState.NotLoading -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is LoadState.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    else -> {}

                }
            }
            */
            //3) LoadStateAdapter kullanmak
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUIState(
        isLoading: Boolean,
        isError: Boolean,
        isEmpty: Boolean,
        loadState: CombinedLoadStates
    ) {
        binding.progressBarCenter.isVisible = isLoading
        binding.errorLayout.isVisible = isError || isEmpty

        when {
            isError -> {
                val error = (loadState.source.refresh as? LoadState.Error)?.error
                binding.textError.text = error?.localizedMessage ?: "Bilinmeyen bir hata"
            }

            isEmpty -> {
                binding.textError.text = "Hiç kullanıcı bulunamadı"
            }
        }
    }

    private fun handleRetry() {
        binding.buttonRetry.setOnClickListener {
            //retry() ile tekrar yükleme yapılır bunu da pagingsource'daki loglarla görebiliriz.
            //önceki başarısız olmuş veri yükleme işlemini tekrar denemek için
            userPagingAdapter.retry()
            Snackbar.make(it, "Retry called", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}