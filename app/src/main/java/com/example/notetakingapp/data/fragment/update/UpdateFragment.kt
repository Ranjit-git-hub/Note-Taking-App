package com.example.notetakingapp.data.fragment.update

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notetakingapp.R
import com.example.notetakingapp.data.model.User
import com.example.notetakingapp.data.viewmodel.UserViewModel
import android.content.Intent


class UpdateFragment : Fragment() {

    private val args: UpdateFragmentArgs by navArgs()
    private lateinit var mUserViewModel: UserViewModel

    private lateinit var titleEdit: EditText
    private lateinit var descEdit: EditText
    private lateinit var updateBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val view = inflater.inflate(R.layout.fragment_update, container, false)

        titleEdit = view.findViewById(R.id.updateTitle)
        descEdit = view.findViewById(R.id.updateDescription)
        updateBtn = view.findViewById(R.id.update_btn)

        titleEdit.setText(args.currentUser.title)
        descEdit.setText(args.currentUser.description)

        updateBtn.setOnClickListener {
            updateItem()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Register menu using MenuProvider for lifecycle safety
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.delete_menu, menu) // ensure this file exists
                menuInflater.inflate(R.menu.share_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.menu_delete -> {
                        deleteUser()
                        true
                    }
                    R.id.menu_share -> {
                        shareNote()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }



    private fun updateItem() {
        val title = titleEdit.text.toString().trim()
        val description = descEdit.text.toString().trim()

        if (inputCheck(title, description)) {
            val updatedUser = User(args.currentUser.id, title, description)
            mUserViewModel.updateUser(updatedUser)
            Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(title: String, description: String): Boolean {
        return !(TextUtils.isEmpty(title) && TextUtils.isEmpty(description))
    }

    private fun deleteUser() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete ${args.currentUser.title}?")
        builder.setMessage("Are you sure you want to delete ${args.currentUser.title}?")
        builder.setPositiveButton("Yes") { _, _ ->
            mUserViewModel.deleteUser(args.currentUser)
            Toast.makeText(requireContext(), "Successfully removed: ${args.currentUser.title}", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun shareNote() {
        val title = titleEdit.text.toString().trim()
        val description = descEdit.text.toString().trim()

        val textToShare = buildString {
            if (title.isNotEmpty()) append("Title: ").append(title)
            if (description.isNotEmpty()) {
                if (isNotEmpty()) append("\n\n")
                append("Description: ").append(description)
            }
        }

        if (textToShare.isBlank()) {
            Toast.makeText(requireContext(), "Nothing to share", Toast.LENGTH_SHORT).show()
            return
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToShare)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share note via")
        startActivity(shareIntent)
    }

}
