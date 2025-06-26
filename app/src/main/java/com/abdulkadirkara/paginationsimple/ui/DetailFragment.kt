package com.abdulkadirkara.paginationsimple.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.RoundedCornersTransformation
import com.abdulkadirkara.paginationsimple.R
import com.abdulkadirkara.paginationsimple.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailFragmentArgs by navArgs() // SafeArgs ile gelen veriyi al

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = args.user

        // XML'e bind işlemleri
        binding.apply {
            // Coil ile yuvarlatılmış profil resmi yükle
            imageProfile.load(user.picture.large) {
                crossfade(true)
                transformations(RoundedCornersTransformation(60f)) // oval köşeler
            }

            textFullName.text = "${user.name.title} ${user.name.first} ${user.name.last}"

            textEmail.text = "Email: ${user.email}"
            textPhone.text = "Phone: ${user.phone}"
            textCell.text = "Cell: ${user.cell}"

            textGender.text = "Gender: ${user.gender}"
            textAge.text = "Age: ${user.dob.age}"
            textDob.text = "Doğum Tarihi: ${user.dob.date.substring(0, 10)}"
            textNat.text = "Nationality: ${user.nat}"

            textLocation.text = "Location: ${user.location.city}, ${user.location.country}"
            textStreet.text = "Street: ${user.location.street.number} ${user.location.street.name}"
            textCity.text = "City: ${user.location.city}"
            textState.text = "State: ${user.location.state}"
            textCountry.text = "Country: ${user.location.country}"
            textPostcode.text = "Postcode: ${user.location.postcode}"

            textUsername.text = "Username: ${user.login.username}"
            textRegisteredDate.text = "Registered: ${user.registered.date.substring(0, 10)}"
            textLoginUUID.text = "UUID: ${user.login.uuid}"

            // Cinsiyete göre arka plan rengini ayarla
            val backgroundColor = when (user.gender.lowercase()) {
                "female" -> R.color.female_background
                "male" -> R.color.male_background
                else -> R.color.default_background
            }

            // Arka planı container seviyesinde değiştir
            detailContainer.setBackgroundColor(
                ContextCompat.getColor(requireContext(), backgroundColor)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
/*
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/detailContainer"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/bg_male_female"
    tools:context=".ui.DetailFragment">

    <androidx.cardview.widget.CardView
        android:id="@+id/cardUserDetail"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_margin="32dp"
        android:background="@android:color/white"
        app:cardCornerRadius="24dp"
        app:cardElevation="8dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="16dp">

            <!-- Profile Image -->
            <ImageView
                android:id="@+id/imageProfile"
                android:layout_width="120dp"
                android:layout_height="120dp"
                android:layout_gravity="center"
                android:scaleType="centerCrop"
                android:layout_marginBottom="12dp"
                tools:src="https://randomuser.me/api/portraits/women/1.jpg" />

            <!-- Name -->
            <TextView
                android:id="@+id/textFullName"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:textAppearance="?attr/textAppearanceHeadline6"
                android:textColor="#222222"
                android:textStyle="bold"
                tools:text="Ms. Jane Doe" />

            <!-- Divider -->
            <View
                android:layout_width="match_parent"
                android:layout_height="1dp"
                android:layout_marginVertical="12dp"
                android:background="#CCCCCC" />

            <!-- User Info Scroll -->
            <ScrollView
                android:id="@+id/scrollUserInfo"
                android:layout_width="match_parent"
                android:layout_height="200dp"
                android:fillViewport="true">

                <LinearLayout
                    android:id="@+id/linearUserInfo"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical">

                    <TextView
                        android:id="@+id/textEmail"
                        style="@style/UserInfoText"
                        tools:text="Email: jane.doe@example.com" />

                    <TextView
                        android:id="@+id/textPhone"
                        style="@style/UserInfoText"
                        tools:text="Phone: 123-456-7890" />

                    <TextView
                        android:id="@+id/textGender"
                        style="@style/UserInfoText"
                        tools:text="Gender: female" />

                    <TextView
                        android:id="@+id/textAge"
                        style="@style/UserInfoText"
                        tools:text="Age: 29" />

                    <TextView
                        android:id="@+id/textLocation"
                        style="@style/UserInfoText"
                        tools:text="Location: New York, USA" />

                    <TextView
                        android:id="@+id/textUsername"
                        style="@style/UserInfoText"
                        tools:text="Username: janedoe123" />

                    <TextView
                        android:id="@+id/textRegisteredDate"
                        style="@style/UserInfoText"
                        tools:text="Registered: 2015-06-15" />
                </LinearLayout>
            </ScrollView>
        </LinearLayout>
    </androidx.cardview.widget.CardView>
</FrameLayout>

 */