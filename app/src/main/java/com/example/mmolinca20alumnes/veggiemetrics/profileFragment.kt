package com.example.mmolinca20alumnes.veggiemetrics

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class profileFragment : Fragment() {

    val TEST_REQUEST = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view: View = inflater!!.inflate(R.layout.fragment_profile, container, false)

        //Botó que porta al test setmanal:
        view.test_button.setOnClickListener {
            val intent = Intent(activity, testSetmanal::class.java)
            /*intent.putExtra("weight", weight.text.toString().toDouble())
            intent.putExtra("sex", sex.selectedItem.toString())
            intent.putExtra("diet", diet.selectedItem.toString())
            intent.putExtra("age", height.text.toString().toInt())   //Canviar més endavant
            intent.putExtra("pregnant", diet.selectedItem.toString())   //Canviar més endavant
*/
            intent.putExtra("weight", 65.0)
            intent.putExtra("sex", "Home")
            intent.putExtra("diet", "Flexitarià")
            intent.putExtra("age", 22)
            intent.putExtra("pregnant","No")
            startActivityForResult(intent, TEST_REQUEST)
        }

        // Inflate the layout for this fragment
        return view
    }

    //Funció que obté els resultats del test
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==TEST_REQUEST) {
            if (resultCode== Activity.RESULT_OK) {
                val results = data!!.getStringArrayListExtra("Resultats")
            }
        }
    }

}
