package com.cusufcan.kotlinkursprojeodevi.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.cusufcan.kotlinkursprojeodevi.adapter.ArtAdapter
import com.cusufcan.kotlinkursprojeodevi.databinding.FragmentListBinding
import com.cusufcan.kotlinkursprojeodevi.db.ArtDao
import com.cusufcan.kotlinkursprojeodevi.db.ArtDatabase
import com.cusufcan.kotlinkursprojeodevi.model.Art
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ListFragment : Fragment() {
    private val disposable = CompositeDisposable()

    private lateinit var binding: FragmentListBinding
    private lateinit var dao: ArtDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(requireContext(), ArtDatabase::class.java, "ArtsKotlin").build()
        dao = db.artDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    private fun getData() {
        disposable.add(
            dao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse) { it.printStackTrace() }
        )
    }

    private fun handleResponse(data: List<Art>) {
        val adapter = ArtAdapter(data)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }
}