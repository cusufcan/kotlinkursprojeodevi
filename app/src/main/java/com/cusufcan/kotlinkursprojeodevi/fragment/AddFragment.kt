package com.cusufcan.kotlinkursprojeodevi.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.room.Room
import com.cusufcan.kotlinkursprojeodevi.R
import com.cusufcan.kotlinkursprojeodevi.databinding.FragmentAddBinding
import com.cusufcan.kotlinkursprojeodevi.db.ArtDao
import com.cusufcan.kotlinkursprojeodevi.db.ArtDatabase
import com.cusufcan.kotlinkursprojeodevi.helper.AppHelper
import com.cusufcan.kotlinkursprojeodevi.model.Art
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream

class AddFragment : Fragment() {
    private val disposable = CompositeDisposable()

    private lateinit var binding: FragmentAddBinding

    private lateinit var dao: ArtDao

    private lateinit var activityLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private lateinit var selectedImage: Bitmap

    private lateinit var artFromMain: Art

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLaunchers()

        val db = Room.databaseBuilder(requireContext(), ArtDatabase::class.java, "ArtsKotlin").build()
        dao = db.artDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.getString("info").equals("new")) {
                binding.artImage.setImageResource(R.drawable.select_image)

                binding.artNameText.text.clear()
                binding.artistNameText.text.clear()
                binding.artYearText.text.clear()

                binding.saveButton.visibility = View.VISIBLE
                binding.deleteButton.visibility = View.GONE
            } else {
                binding.saveButton.visibility = View.GONE
                binding.deleteButton.visibility = View.VISIBLE

                val id = AddFragmentArgs.fromBundle(requireArguments()).artId
                disposable.add(
                    dao.getArtById(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponseWithOldArt) { error -> error.printStackTrace() }
                )
            }
        }

        binding.artImage.setOnClickListener { pickImage(view) }

        binding.saveButton.setOnClickListener { save() }

        binding.deleteButton.setOnClickListener { delete() }
    }

    private fun registerLaunchers() {
        activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultIntent = result.data ?: return@registerForActivityResult
                val imageData = resultIntent.data ?: return@registerForActivityResult
                try {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(requireActivity().contentResolver, imageData)
                        selectedImage = ImageDecoder.decodeBitmap(source)
                    } else {
                        selectedImage = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageData)
                    }
                    binding.artImage.setImageBitmap(selectedImage)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                AppHelper.toGallery(activityLauncher)
            } else {
                Toast.makeText(requireContext(), "Permission Needed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImage(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(view, android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            requestPermission(view, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun requestPermission(view: View, permission: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission") {
                        permissionLauncher.launch(permission)
                    }.show()
            } else {
                permissionLauncher.launch(permission)
            }
        } else {
            AppHelper.toGallery(activityLauncher)
        }
    }

    private fun save() {
        val artName = binding.artNameText.text.toString()
        val artistName = binding.artistNameText.text.toString()
        val artYear = binding.artYearText.text.toString()

        val artImage = AppHelper.imageSmaller(selectedImage, 300, true)

        val outputStream = ByteArrayOutputStream()
        artImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
        val byteArray = outputStream.toByteArray()

        val art = Art(artName, artistName, artYear, byteArray)

        disposable.add(
            dao.insert(art).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse) { it.printStackTrace() }
        )
    }

    private fun delete() {
        disposable.add(
            dao.delete(artFromMain).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse) { it.printStackTrace() }
        )
    }

    private fun handleResponse() {
        val action = AddFragmentDirections.actionAddFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun handleResponseWithOldArt(art: Art) {
        artFromMain = art

        binding.artNameText.setText(art.artName)
        binding.artistNameText.setText(art.artistName)
        binding.artYearText.setText(art.artYear)

        if (art.image == null) return
        val bitmap = BitmapFactory.decodeByteArray(art.image, 0, art.image!!.size)
        binding.artImage.setImageBitmap(bitmap)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        disposable.clear()
    }
}