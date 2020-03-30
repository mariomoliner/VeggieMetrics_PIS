package com.example.mmolinca20alumnes.veggiemetrics

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class profileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view: View = inflater!!.inflate(R.layout.fragment_profile, container, false)

        //Botó que porta al test setmanal:
        view.test_button.setOnClickListener {
            val intent = Intent(activity, testSetmanal::class.java)
            startActivity(intent)
        }

        // Inflate the layout for this fragment
        return view
    }

}
