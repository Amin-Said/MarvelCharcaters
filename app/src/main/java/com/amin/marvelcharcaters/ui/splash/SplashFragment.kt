package com.amin.marvelcharcaters.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.amin.marvelcharcaters.R
import com.amin.marvelcharcaters.databinding.FragmentSplashBinding
import com.amin.marvelcharcaters.utils.Helper
import com.amin.marvelcharcaters.utils.extensions.toastFromResource

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (Helper.isOnline(requireActivity())) {
            goToHome()
        } else {
            requireActivity().toastFromResource(R.string.error_message_Network)
        }

    }

    private fun goToHome(){
        Handler(Looper.getMainLooper()).postDelayed({
            val action =
                SplashFragmentDirections.actionGoToHome()
            findNavController().navigate(action)
        }, 2000)
    }
}