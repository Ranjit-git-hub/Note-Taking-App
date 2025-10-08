package com.example.notetakingapp.data.fragment.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider  // ADD THIS IMPORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notetakingapp.R
import com.example.notetakingapp.data.viewmodel.UserViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var emptyStateView: LinearLayout
    private lateinit var adapter: ListAdapter

    private val userViewModel: UserViewModel by viewModels()
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        initializeViews(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        setupMenu()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        floatingActionButton = view.findViewById(R.id.floatingActionButton)
        emptyStateView = view.findViewById(R.id.emptyStateView)
    }

    private fun setupRecyclerView() {
        adapter = ListAdapter(userViewModel)
        recyclerView.apply {
            adapter = this@ListFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true) // Optimize if item size is fixed
        }
    }

    private fun setupClickListeners() {
        floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }
    }

    private fun setupObservers() {
        userViewModel.readAllDataByRecent.observe(viewLifecycleOwner) { users ->
            adapter.submitList(users)
            showEmptyState(users.isEmpty())
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            emptyStateView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyStateView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.delete_menu, menu)
                menuInflater.inflate(R.menu.search_menu, menu)
                setupSearchView(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_delete -> {
                        deleteAllUsers()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView

        searchView.apply {
            queryHint = "Search notes..."
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { searchDatabase(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // Add debounce to avoid excessive database queries
                    searchJob?.cancel()
                    searchJob = lifecycleScope.launch {
                        newText?.let {
                            delay(300) // 300ms debounce
                            if (it.length >= 2) {
                                searchDatabase(it)
                            } else if (it.isEmpty()) {
                                // Show all data when search is cleared
                                userViewModel.readAllDataByRecent.observe(viewLifecycleOwner) { users ->
                                    adapter.submitList(users)
                                    showEmptyState(users.isEmpty())
                                }
                            }
                        }
                    }
                    return true
                }
            })
        }
    }

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"
        userViewModel.searchDatabase(searchQuery).observe(viewLifecycleOwner) { result ->
            adapter.submitList(result)
            showEmptyState(result.isEmpty())
        }
    }

    private fun deleteAllUsers() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete all notes?")
            .setMessage("Are you sure you want to delete all notes?")
            .setPositiveButton("Yes") { _, _ ->
                userViewModel.deleteAllUsers()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}