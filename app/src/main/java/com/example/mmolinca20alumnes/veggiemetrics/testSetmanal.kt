package com.example.mmolinca20alumnes.veggiemetrics

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_test_setmanal.*
import kotlinx.android.synthetic.main.fragment_home.*


class testSetmanal : AppCompatActivity() {

    var pes: Double = 0.0
    var edat: Int = 0
    var embaras: String = ""
    var sexe = ""
    var dieta = ""

    var ferro = 0.0
    var omega = 0.0
    var calci = 0.0
    var proteines = 0.0

    var avisos: MutableList<String> = mutableListOf()

    var ferroDieta = 0.0
    var omegaDieta = 0.0
    var calciDieta = 0.0
    var proteinesDieta = 0.0

    //Relaciona un String de resultat amb l'obtingut al test
    fun test(requisit: Double, resultat: Double): String {
        var conclusio: String
        if (resultat < 0.5 * requisit)
            conclusio = "Bad"
        else if (resultat < 0.9 * requisit)
            conclusio = "Not good"
        else conclusio = "Ok"
        return conclusio
    }

    //Concatena en un String els elements de la llista
    fun concatenaAvisos(llista: MutableList<String>): String {
        var concatenacio = ""
        for (num in llista)
            concatenacio += num
        return concatenacio
    }

    // Comprova que es responen totes les preguntes
    fun totesRespostes(preguntes: List<RadioGroup>): Boolean {
        var ok = true
        for (radio in preguntes) {
            if (radio.checkedRadioButtonId == -1) {
                ok = false
                break
            }
        }
        return ok
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_setmanal)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = getString(R.string.test_setmanal)
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        pes = intent.getDoubleExtra("weight", 0.0)
        edat = intent.getIntExtra("age", 0)
        embaras = intent.getStringExtra("pregnant")
        sexe = intent.getStringExtra("sex")
        dieta = intent.getStringExtra("diet")

        //Comprovació que les dades del perfil estan bé
        if (pes <= 0 || edat < 4) {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            Toast.makeText(this, getString(R.string.dades_incorrectes), Toast.LENGTH_LONG).show()
            finish()
        }

        //Càlcul dels requisits nutricionals
        //Ferro
        if (embaras.equals(getString(R.string.no))) {
            if (edat in 4..8)
                ferro = 10.0
            else if (edat in 9..13)
                ferro = 8.0
            else if (sexe.equals(getString(R.string.man))) {
                if (edat in 14..18)
                    ferro = 11.0
                else if (edat > 18)
                    ferro = 8.0
            } else if (sexe.equals(getString(R.string.woman))) {
                if (edat in 14..18)
                    ferro = 15.0
                else if (edat in 19..50)
                    ferro = 18.0
                else if (edat > 50)
                    ferro = 8.0
            } else if (embaras.equals(getString(R.string.si)))
                ferro = 27.0
        }
        ferro *= 7.0

        //Omega 3
        if (embaras.equals(getString(R.string.no))) {
            if (edat in 4..8)
                omega = 0.9
            else if (sexe.equals(getString(R.string.man))) {
                if (edat in 9..13)
                    omega = 1.2
                else if (edat > 13)
                    omega = 1.6
            } else if (sexe.equals(getString(R.string.woman))) {
                if (edat in 9..13)
                    omega = 1.0
                else if (edat > 13)
                    omega = 1.1
            }
        } else if (embaras.equals(getString(R.string.si)))
            omega = 1.4
        omega *= 7.0

        //Calci
        if (edat in 4..8)
            calci = 1000.0
        else if (edat in 9..18)
            calci = 1300.0
        else if (edat in 19..50)
            calci = 1000.0
        else if (edat > 70)
            calci = 1200.0
        else if (sexe.equals(getString(R.string.man))) {
            if (edat in 51..70)
                calci = 1000.0
        } else if (sexe.equals(getString(R.string.woman))) {
            if (edat in 51..70)
                calci = 1200.0
        }

        calci *= 7.0

        //Proteïnes
        if (dieta.equals(getString(R.string.vegetariana)) || dieta.equals(getString(R.string.vegana))) {
            if (embaras.equals(getString(R.string.no)))
                proteines = pes
            else if (embaras.equals(getString(R.string.si)))
                proteines = 1.5 * pes
        } else if (embaras.equals(getString(R.string.no)))
            proteines = 0.8 * pes
        else if (embaras.equals(getString(R.string.si)))
            proteines = 1.4 * pes

        proteines *= 7.0

        sendTest.setOnClickListener() {

            val preguntes = listOf<RadioGroup>(
                radio1,
                radio2,
                radio3,
                radio4,
                radio5,
                radio6,
                radio7,
                radio8,
                radio9,
                radio10,
                radio11,
                radio12,
                radio13,
                radio14,
                radio15
            )

            // Calcula els resultats si es responen totes les preguntes
            if (totesRespostes(preguntes)) {

                //Pregunta 1
                if (radio1_b.isChecked) {
                    ferroDieta += 10
                    proteinesDieta += 70
                } else if (radio1_c.isChecked) {
                    ferroDieta += 25
                    proteinesDieta += 175
                } else if (radio1_d.isChecked) {
                    ferroDieta += 45
                    proteinesDieta += 315
                }

                //Pregunta 2
                if (radio2_b.isChecked) {
                    proteinesDieta += 50
                    omegaDieta += 8
                    calciDieta += 280
                } else if (radio2_c.isChecked) {
                    proteinesDieta += 125
                    omegaDieta += 20
                    calciDieta += 700
                } else if (radio2_d.isChecked) {
                    proteinesDieta += 225
                    omegaDieta += 45
                    calciDieta += 1260
                }

                //Pregunta 3
                if (radio3_b.isChecked) {
                    ferroDieta += 20
                    proteinesDieta += 100
                    omegaDieta += 2
                    calciDieta += 280
                } else if (radio3_c.isChecked) {
                    ferroDieta += 50
                    proteinesDieta += 250
                    omegaDieta += 5
                    calciDieta += 700
                } else if (radio3_d.isChecked) {
                    ferroDieta += 90
                    proteinesDieta += 450
                    omegaDieta += 9
                    calciDieta += 1260
                }

                //Pregunta 4
                if (radio4_a.isChecked) {
                    proteinesDieta += 105
                    calciDieta += 300
                } else if (radio4_b.isChecked) {
                    proteinesDieta += 315
                    calciDieta += 900
                } else if (radio4_c.isChecked) {
                    proteinesDieta += 525
                    calciDieta += 1500
                } else if (radio4_d.isChecked) {
                    proteinesDieta += 735
                    calciDieta += 2100
                }


                //Pregunta 5
                if (radio5_b.isChecked) {
                    ferroDieta += 14
                    proteinesDieta += 40
                    calciDieta += 400
                } else if (radio5_c.isChecked) {
                    ferroDieta += 35
                    proteinesDieta += 100
                    calciDieta += 1000
                } else if (radio5_d.isChecked) {
                    ferroDieta += 63
                    proteinesDieta += 220
                    calciDieta += 1800
                }

                //Pregunta 6
                if (radio6_b.isChecked) {
                    calciDieta += 800
                } else if (radio6_c.isChecked) {
                    calciDieta += 2200
                } else if (radio6_d.isChecked) {
                    calciDieta += 4600
                }

                //Pregunta 7
                if (radio7_a.isChecked) {
                    ferroDieta += 3
                    proteinesDieta += 12
                    calciDieta += 140
                } else if (radio7_b.isChecked) {
                    ferroDieta += 7.5
                    proteinesDieta += 30
                    calciDieta += 350
                } else if (radio7_c.isChecked) {
                    ferroDieta += 10.5
                    proteinesDieta += 66
                    calciDieta += 490
                } else if (radio7_d.isChecked) {
                    ferroDieta += 21
                    proteinesDieta += 132
                    calciDieta += 980
                }

                //Pregunta 8
                if (radio8_b.isChecked) {
                    omegaDieta += 5
                } else if (radio8_c.isChecked) {
                    omegaDieta += 12.5
                } else if (radio8_d.isChecked) {
                    omegaDieta += 17.5
                }

                //Preguntes 9 i 11
                if (radio9_a.isChecked || radio11_a.isChecked) {
                    avisos.add("1")
                } else if (radio9_b.isChecked && (radio11_a.isChecked || radio11_b.isChecked)) {
                    avisos.add("1")
                } else if (radio11_b.isChecked && (radio9_a.isChecked || radio9_b.isChecked)) {
                    avisos.add("1")
                } else if (radio9_d.isChecked && radio11_d.isChecked) {
                    avisos.add("2")
                }

                //Pregunta 10
                if (radio10_a.isChecked) {
                    ferroDieta += 6
                    calciDieta += 80
                    if (dieta.equals(getString(R.string.vegetariana)) || dieta.equals(getString(R.string.vegana)))
                        avisos.add("3")
                } else if (radio10_b.isChecked) {
                    ferroDieta += 15
                    calciDieta += 200
                } else if (radio10_c.isChecked) {
                    ferroDieta += 21
                    calciDieta += 280
                } else if (radio10_d.isChecked) {
                    ferroDieta += 42
                    calciDieta += 560
                }

                //Preguntes 12, 13, 14, 15
                if (dieta.equals(getString(R.string.vegetariana)) || dieta.equals(getString(R.string.vegana))) {
                    if (radio12_c.isChecked) {
                        avisos.add("4")
                    }

                    if (radio13_c.isChecked) {
                        avisos.add("5")
                    }

                    if (radio14_c.isChecked) {
                        avisos.add("6")
                    }

                    if (radio15_c.isChecked) {
                        avisos.add("7")
                    }

                    if (dieta.equals(getString(R.string.vegetariana)))
                        avisos.add("8")
                    else if (dieta.equals(getString(R.string.vegana)))
                        avisos.add("9")
                }

                if (radio12_d.isChecked) {
                    avisos.add("4")
                }

                if (radio13_d.isChecked) {
                    avisos.add("5")
                }

                if (radio14_d.isChecked) {
                    avisos.add("6")
                }

                if (radio15_d.isChecked) {
                    avisos.add("7")
                }

                var dades = ArrayList<String>(5)
                dades.add(0, test(proteines, proteinesDieta))
                dades.add(1, test(ferro, ferroDieta))
                dades.add(2, test(omega, omegaDieta))
                dades.add(3, test(calci, calciDieta))
                dades.add(4, concatenaAvisos(avisos))

                val intent = Intent()
                intent.putExtra("Resultats", dades)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else
                Toast.makeText(this, getString(R.string.falten_preguntes), Toast.LENGTH_LONG).show()
        }
    }
}