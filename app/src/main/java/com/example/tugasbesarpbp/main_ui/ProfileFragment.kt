package com.example.tugasbesarpbp.main_ui

import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.tugasbesarpbp.CameraActivity
import com.example.tugasbesarpbp.CameraView
import com.example.tugasbesarpbp.HomeActivity
import com.example.tugasbesarpbp.UpdateProfileActivity
import com.example.tugasbesarpbp.api.UserApi
import com.example.tugasbesarpbp.api_models.User
import com.example.tugasbesarpbp.databinding.FragmentProfileBinding
import com.example.tugasbesarpbp.room.MainDB
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    }

    // onStart --> ketika fragment ini muncul atau di tampilkan (mis. ketika kita kembali dari fragment/activity lain)
    override fun onStart() {
        super.onStart()

        getUserDetails()
        // get session
//        val session = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
//        val db by lazy { MainDB(activity as HomeActivity) }
//        CoroutineScope(Dispatchers.IO).launch {
//            val user = db.UserDao().getUserById(session.getInt("id", 0))
//            if(user != null){
//                binding.tvDisplayName.text = user.nama
//                binding.tvVPUsername.text = user.username
//                binding.tvVPEmail.text = user.email
//                binding.tvVPTanggalLahir.text = user.tanggalLahir
//                binding.tvVPNomorTelepon.text = user.nomorTelepon
//            }
//        }

        // on click listener for profileBtnEdit
        binding.profileBtnEdit.setOnClickListener {
            val intent = Intent(activity as HomeActivity, UpdateProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnFloatSignOut.setOnClickListener {
            (activity as HomeActivity).signOut()
        }
        binding.profileImage.setOnClickListener {
            val intent = Intent(activity as HomeActivity, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getUserDetails() {
        // Dapatkan session untuk mengambil token dan id user
        val session = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)

        // set loading screen
        setLoadingScreen(true)
        val stringRequest: StringRequest = object: StringRequest(Method.GET, UserApi.GET_URL, {
            val gson = Gson()

            // Dapatkan data user dari response [struktur: response = { "data": { ... } }]
            val jsonRespose = JSONObject(it)
            val user = gson.fromJson(jsonRespose.getJSONObject("data").toString(), User::class.java)
            binding.tvDisplayName.text = user.nama
            binding.tvVPUsername.text = user.username
            binding.tvVPEmail.text = user.email
            binding.tvVPTanggalLahir.text = user.tanggalLahir
            binding.tvVPNomorTelepon.text = user.nomorTelepon
            setLoadingScreen(false)
        }, {
            // Kalau ada error
            var respObj: JSONObject? = null
            try {
                respObj = JSONObject(String(it.networkResponse.data))
                AlertDialog.Builder(requireActivity())
                    .setTitle("Terjadi Kesalahan!")
                    .setMessage(respObj.toString(4))
                    .setPositiveButton("OK", null)
                    .show()
            } catch (e: Exception) {
                val response = it.networkResponse
                var dialogContent = ""
                if(response != null) {
                    dialogContent = "Error ${response.statusCode}\r\nHubungi admin."
                } else {
                    dialogContent = "Tidak dapat terhubung ke server.\r\nPeriksa koneksi internet."
                }
                AlertDialog.Builder(requireActivity())
                    .setMessage(dialogContent)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                // Dapatkan token dari session dan tambahkan ke header
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + session.getString("token", "")
                return headers
            }
        }

        // Tambahkan request ke queue
        (activity as HomeActivity).queue!!.add(stringRequest)
    }

    private fun setLoadingScreen(state: Boolean) {
        if (state) {
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            binding.layoutLoader.layoutLoader.alpha = 1f

            // set flag to disable click
            binding.layoutLoader.layoutLoader.isClickable = true
        } else {
//            binding.layoutLoader.visibility = View.GONE
            // fade out
            binding.layoutLoader.layoutLoader.animate().alpha(0f).setDuration(250).withEndAction {
                binding.layoutLoader.layoutLoader.visibility = View.GONE
            }
            // set flag to enable click
            binding.layoutLoader.layoutLoader.isClickable = false
        }
    }
}