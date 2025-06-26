package com.abdulkadirkara.paginationsimple.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.abdulkadirkara.paginationsimple.R
import com.abdulkadirkara.paginationsimple.databinding.FragmentDetailBinding
import com.abdulkadirkara.paginationsimple.util.ImageShape
import com.abdulkadirkara.paginationsimple.util.loadImage
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
            imageProfile.loadImage(
                url = user.picture.large,
                shape = ImageShape.ROUNDED,
                cornerRadius = 60f
            )

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