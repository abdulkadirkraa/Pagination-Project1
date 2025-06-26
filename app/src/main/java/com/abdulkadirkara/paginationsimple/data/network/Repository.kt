package com.abdulkadirkara.paginationsimple.data.network

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.abdulkadirkara.paginationsimple.data.model.Result
import com.abdulkadirkara.paginationsimple.data.service.ApiService
import com.abdulkadirkara.paginationsimple.util.Constants.INITIAL_LOAD_SIZE
import com.abdulkadirkara.paginationsimple.util.Constants.MAX_SIZE
import com.abdulkadirkara.paginationsimple.util.Constants.PAGE_SIZE
import com.abdulkadirkara.paginationsimple.util.Constants.PREFETCH_DISTANCE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(private val apiService: ApiService) {

     fun getUsers() : Flow<PagingData<Result>> {
        return Pager(
            config = PagingConfig(
                //Zorunludur. Ekranda görünenin 3-5 katı yüklesin her yüklemede.
                pageSize = PAGE_SIZE,
                //Scroll sırasında kullanıcı henüz o verileri görmeden arkadan yükleme yapılmasını sağlar. Scroll takılmasın diye.
                prefetchDistance = PREFETCH_DISTANCE,
                //Henüz yüklenmemiş öğeler yerine ekranda boş (null) placeholder gösterilsin mi?
                //Evetse: Henüz verisi yüklenmemiş öğeler görünür ama boş gösterilir. Yani örn: 100 veri varsa ama 20'si yüklüyse, kalan 80'i gri kutularla gösterilebilir.
                //Hayırsa: Henüz gelmeyen öğeler UI’da hiç görünmez.
                enablePlaceholders = true,
                //Scroll başlamadan önce ilk yüklemede yüklenecek veri miktarıdır. Varsayılan olarak pageSize * 3 alınır. (Yani hızlı scroll’da boşluk görmeyelim diye)
                initialLoadSize = INITIAL_LOAD_SIZE,
                //Sayfalama mantığıyla sürekli veri geldikçe, eski sayfalar silinebilir.
                //MAX_SIZE_UNBOUNDED verilirse, sınır yoktur.
                //Ama bellek yönetimi için maxSize = 200 gibi sınırlama verilebilir.
                //maxSize >= pageSize + 2 * prefetchDistance olmalı.
                maxSize = MAX_SIZE
            ),
            pagingSourceFactory = {
                RandomUserPagingSource(apiService)
            }
        ).flow
    }
    /*
    Pager = Sayfalanabilir veri akışı üretir (Flow<PagingData>)
    PagingData = Ekrana gösterilecek veri parçaları.
    PagingConfig = Android’de Pager sınıfıyla kullanılır ve veri sayfalama davranışını yapılandırmak için kullanılır.
    config = Sayfa boyutu, önbellek ayarları gibi PagingConfig nesnesi
    pagingSourceFactory = Veriyi nereden (Room, Retrofit vs) alacağını söyleyen fonksiyon

    enablePlaceholders = true dersen:
    RecyclerView’in adaptörü, veriler henüz yüklenmemiş olsa bile o veriler için boş hücreler (placeholder itemlar) oluşturur.
    Örneğin toplam 1000 eleman varsa, Paging henüz sadece 0-20 arasını yüklediyse, kalan 980 eleman için de boş satırlar oluşturulur ve bunlar null veri olarak gelir. UI'da progress veya iskelet loading görünümü veya shimmer efektle gösterilebilir.
    Toplam veri sayısının biliyorsan bu özelliği kullanabilirsin ama bilmiyorsan kullanmamalısın.
    Eğer enablePlaceholders = true yaparsan:
    Adapter’ın onBindViewHolder metodunda getItem(position) çağrısı null dönebilir.
    Bu durumda null kontrolü yapıp:
    val item = getItem(position)
    if (item == null) {
        // placeholder görünümünü ayarla (örnek: shimmer efekt)
    } else {
        // normal görünüm
    }
    prefetchDistance: Ne Zaman Önceden Yüklemeye Başlasın?
    Kullanıcı listenin sonuna yaklaştığında, önceden veri yüklemeye başlar.
    Amaç: Scroll sırasında "yükleniyor" ekranı hiç göstermemek ya da minimize etmek.
     */
}