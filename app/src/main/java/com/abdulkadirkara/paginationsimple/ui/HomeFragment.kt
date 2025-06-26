package com.abdulkadirkara.paginationsimple.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdulkadirkara.paginationsimple.databinding.FragmentHomeBinding
import com.abdulkadirkara.paginationsimple.ui.adapter.UserPagingAdapter
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

        setupRecyclerView()
        loadData()
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.users.collectLatest {
                userPagingAdapter.submitData(it)
            }
        }

//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.userListFlow.collectLatest {
//                userPagingAdapter.submitData(it)
//            }
//        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = userPagingAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        userPagingAdapter.onItemClick = {
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(it)
            findNavController().navigate(action)
        }

        //Listener ile load state kullanımı
        //1)load state flow
        /*
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}