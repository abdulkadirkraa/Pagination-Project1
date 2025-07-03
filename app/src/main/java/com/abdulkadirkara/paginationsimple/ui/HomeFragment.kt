package com.abdulkadirkara.paginationsimple.ui

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
    private val viewModel: HomeViewModel by activityViewModels()
    @Inject lateinit var userPagingAdapter: UserPagingAdapter
    // Kullanıcının swipe ile yenileyip yenilemediğini takip eder
    private var isUserSwipeRefreshing = false

    /*
    SwipeRefreshLayout, Google’ın destek kütüphanelerinde sunduğu bir ViewGroup’tur.
    İçerisine bir tane scrollable (kaydırılabilir) view alır (örneğin RecyclerView, ScrollView, ListView, vb.).
    Kullanıcı içeriği yukarıdan aşağı kaydırınca “refresh gesture” (yenileme hareketi) algılanır ve onRefresh() fonksiyonu tetiklenir.
    SwipeRefreshLayout'ın setRefreshing(true/false) metoduyla loading durumunu manuel kontrol ederiz.

    binding.swipeRefreshLayout.setOnRefreshListener {
        // Veri yenileme işlemleri başlatılır
        viewModel.refreshData()
    }
    --
    // Loading başlat
    binding.swipeRefreshLayout.isRefreshing = true

    // ViewModel üzerinden veri yüklenir
    viewModel.refreshData()
    --
    viewModel.data.observe(viewLifecycleOwner) { data ->
        adapter.submitList(data)

        // Yenileme tamamlandıktan sonra animasyon durdurulur
        binding.swipeRefreshLayout.isRefreshing = false
    }
    --
    Paging 3 kütüphanesiyle çalışıyorsan, swipe-to-refresh ile PagingData'yı yenilemek için:
    binding.swipeRefreshLayout.setOnRefreshListener {
        adapter.refresh() // PagingAdapter fonksiyonu
    }
    Ardından adapter'ın loadStateFlow'unu kullanarak animasyonu durdur:
    adapter.loadStateFlow.collectLatest {
        binding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
    }
    --
    adapter.refresh() (özellikle Paging 3 kütüphanesinde PagingDataAdapter ya da AsyncPagingDataDiffer kullanıyorsan), veri kaynağını baştan yüklemek anlamına gelir.
    Yani kullanıcının manuel olarak "yenileme" (refresh) yapması gibi davranır.
    Mevcut PagingData'yı yeniden tetikler.
    PagingSource içindeki load() fonksiyonunu LoadType.REFRESH ile tekrar çağırır.
    İlk sayfa verilerini yeniden yükler.
    Mevcut PagingData silinir ve UI'ya yeni veriler yüklenmeye başlanır.
    LoadStateListener varsa, önce Loading, sonra NotLoading ya da Error durumlarına geçilir.
     */

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
        setupSwipeRefresh()
        fabClickListener()
    }

    private fun fabClickListener(){
        binding.fabFilter.setOnClickListener {
            FilterBottomSheetFragment().show(childFragmentManager, "FilterDialogFragment")
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            isUserSwipeRefreshing = true
            userPagingAdapter.refresh() // PagingAdapter içindeki veri kaynağını yeniden başlatır

        }

        // Yüklenme durumuna göre swipe-to-refresh göstergesini kapat
        viewLifecycleOwner.lifecycleScope.launch {
            //adapter.refresh() → PagingSource.invalidate() tetikler, verileri baştan çeker.
            //loadStateFlow → arka planda olan Paging durumlarını izlemek için çok önemlidir.
            userPagingAdapter.loadStateFlow.collectLatest { loadStates ->
                // Sadece kullanıcı swipe ettiyse isRefreshing gösterilsin
                binding.swipeRefreshLayout.isRefreshing =
                    isUserSwipeRefreshing && loadStates.source.refresh is LoadState.Loading

                // Eğer yükleme tamamlandıysa flag sıfırla
                if (loadStates.source.refresh is LoadState.NotLoading) {
                    isUserSwipeRefreshing = false
                }
            }
        }
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
            val refreshState = loadState.source.refresh
            val appendState = loadState.source.append

            val isRefreshLoading = refreshState is LoadState.Loading
            val isRefreshError = refreshState is LoadState.Error
            val isAppendLoading = appendState is LoadState.Loading
            val isAppendError = appendState is LoadState.Error

            val isInitialLoad = userPagingAdapter.itemCount == 0

            // Her durumda önce görünürlükleri gizle
            binding.progressBarCenter.visibility = View.GONE
            binding.errorLayout.visibility = View.GONE
            // Normalde paddingBottom sıfırla
            setRecyclerViewPaddingBottom(0)

            when {
                // REFRESH YÜKLENİYOR
                isRefreshLoading -> {
                    if (isUserSwipeRefreshing){
                        // Kullanıcı swipe etmiş, bırak gösterilsin
                        // Center progressbar'ı göstermiyoruz
                        binding.progressBarCenter.visibility = View.GONE
                    } else {
                        // İlk açılış yüklemesi: swipe olmamış
                        binding.progressBarCenter.visibility = View.VISIBLE
                        setGravity(binding.progressBarCenter, Gravity.CENTER)

                        // SwipeRefreshLayout açıldıysa zorla kapat
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }

                // REFRESH HATA
                isRefreshError -> {
                    if (isUserSwipeRefreshing && !isInitialLoad) {
                        // Kullanıcı swipe etti ve önceden veri vardı => Snackbar göster
                        val error = (refreshState as LoadState.Error).error.localizedMessage ?: "Bir şeyler yanlış gitti"
                        Snackbar.make(binding.root, error, Snackbar.LENGTH_INDEFINITE)
                            .setAction("Yeniden Dene") {
                                userPagingAdapter.refresh()
                            }
                            .show()
                    } else {
                        // İlk açılışta hata olduysa error layout göster
                        binding.errorLayout.visibility = View.VISIBLE
                        setGravity(binding.errorLayout, Gravity.CENTER)
                        binding.textError.text = (refreshState as LoadState.Error).error.localizedMessage
                    }
                }

                // APPEND YÜKLENİYOR
                isAppendLoading -> {
                    binding.progressBarCenter.visibility = View.VISIBLE
                    setGravity(binding.progressBarCenter, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                    // ProgressBar yüksekliğini kullanarak padding ayarla
                    binding.progressBarCenter.post { //post { ... } bloğu görünümün layout sonrası ölçüsünü alabilmek için kullanılır.
                        val height = binding.progressBarCenter.height
                        setRecyclerViewPaddingBottom(height)
                    }
                }

                // APPEND HATA
                isAppendError -> {
                    binding.errorLayout.visibility = View.VISIBLE
                    setGravity(binding.errorLayout, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                    binding.textError.text = (appendState as LoadState.Error).error.localizedMessage
                    // Error layout yüksekliğini kullanarak padding ayarla
                    binding.errorLayout.post {
                        val height = binding.errorLayout.height
                        setRecyclerViewPaddingBottom(height)
                    }
                }
            }
        }
    }

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

    private fun setGravity(view: View, gravity: Int) {
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = gravity
        view.layoutParams = layoutParams
    }

    private fun setRecyclerViewPaddingBottom(paddingInPx: Int) {
        binding.recyclerView.setPadding(
            binding.recyclerView.paddingLeft,
            binding.recyclerView.paddingTop,
            binding.recyclerView.paddingRight,
            paddingInPx
        )
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