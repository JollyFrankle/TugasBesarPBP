package com.example.tugasbesarpbp.main_ui

import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.android.volley.toolbox.StringRequest
import com.example.tugasbesarpbp.CameraActivity
import com.example.tugasbesarpbp.CameraView
import com.example.tugasbesarpbp.HomeActivity
import com.example.tugasbesarpbp.UpdateProfileActivity
import com.example.tugasbesarpbp.api.http.UserApi
import com.example.tugasbesarpbp.api.models.User
import com.example.tugasbesarpbp.databinding.FragmentProfileBinding
import com.google.gson.Gson
import org.json.JSONObject

class ProfileFragment : Fragment() {
    // use view binding
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var mCamera: Camera? = null
    private var mCameraView: CameraView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set actionbar title
        (activity as HomeActivity).setActionBarTitle("Profile Management")

        // on click listener for profileBtnEdit
        binding.profileBtnEdit.setOnClickListener {
            val intent = Intent(activity, UpdateProfileActivity::class.java)
            startActivity(intent)
//            (activity as HomeActivity).resultLauncher.launch(intent)
        }

        binding.btnFloatSignOut.setOnClickListener {
            (activity as HomeActivity).signOut()
        }

        binding.profileImage.setOnClickListener {
            val intent = Intent(activity as HomeActivity, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    // onStart --> ketika fragment ini muncul atau ditampilkan kembali (mis. ketika kita kembali dari fragment/activity lain)
    override fun onStart() {
        super.onStart()

        // Load data
        (activity as HomeActivity).setLoadingScreen(true)
        UserApi.getProfile(requireActivity(), {
            val gson = Gson()

            // Dapatkan data user dari response [struktur: response = { "data": { ... } }]
            val jsonRespose = JSONObject(it)
            val user = gson.fromJson(jsonRespose.getJSONObject("data").toString(), User::class.java)
            binding.tvDisplayName.text = user.nama
            binding.tvVPUsername.text = user.username
            binding.tvVPEmail.text = user.email
            binding.tvVPTanggalLahir.text = user.tanggalLahir
            binding.tvVPNomorTelepon.text = user.nomorTelepon

            (activity as HomeActivity).setLoadingScreen(false)
        }, {
            (activity as HomeActivity).setLoadingScreen(false)
        })
    }
}