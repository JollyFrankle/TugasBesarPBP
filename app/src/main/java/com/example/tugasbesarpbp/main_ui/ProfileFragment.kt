package com.example.tugasbesarpbp.main_ui

import android.annotation.SuppressLint
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import com.example.tugasbesarpbp.*
import com.example.tugasbesarpbp.databinding.FragmentProfileBinding
import com.example.tugasbesarpbp.room.MainDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        // get session
        val session = (activity as HomeActivity).getSession()
        val db by lazy { MainDB(activity as HomeActivity) }
        CoroutineScope(Dispatchers.IO).launch {
            val user = db.UserDao().getUserById(session.getInt("id", 0))
            if(user != null){
                binding.tvDisplayName.text = user.nama
                binding.tvVPUsername.text = user.username
                binding.tvVPEmail.text = user.email
                binding.tvVPTanggalLahir.text = user.tanggalLahir
                binding.tvVPNomorTelepon.text = user.nomorTelepon
            }
        }

        // on click listener for profileBtnEdit
        binding.profileBtnEdit.setOnClickListener {
            (activity as HomeActivity).goToActivity(UpdateActivity::class.java)
        }

        binding.btnFloatSignOut.setOnClickListener {
            (activity as HomeActivity).signOut()
        }
        binding.profileImage.setOnClickListener {
            (activity as HomeActivity).goToActivity(CameraActivity::class.java)
        }
    }
}