package com.example.notetakingapp.data.fragment.add

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notetakingapp.R
import com.example.notetakingapp.data.model.User
import com.example.notetakingapp.data.viewmodel.UserViewModel

class AddFragment : Fragment() {

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var titleEdit: EditText
    private lateinit var descEdit: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        val btn = view.findViewById<Button>(R.id.add_btn)

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        titleEdit = view.findViewById(R.id.addTitle)
        descEdit  = view.findViewById(R.id.addDescription)


        btn.setOnClickListener{
            insertDataToDatabase()
        }

        return view
    }

    private fun insertDataToDatabase() {
        val title = titleEdit.text.toString()
        val description = descEdit.text.toString()

        if (inputCheck(title,description)){
            // Create User Object
            val user = User(0, title, description)
            // Add data to database
            mUserViewModel.addUser(user)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()
            // Navigate Back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(), "Please fill out all fields!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(title: String, description: String): Boolean{
        return !(TextUtils.isEmpty(title) && TextUtils.isEmpty(description))
    }


}




