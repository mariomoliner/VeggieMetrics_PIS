package logic_deficiencies

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import models.Ingredient
import models.Unitat
import models.recepta_detall
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow

class Engine(recepta: recepta_detall, c: Context, listener: OnFinishListener) {


    class report(nom: String, id: String, qty: String){
        val nom: String
        val id: String
        var qty_percent: String

        init{
            this.nom = nom
            this.id = id
            this.qty_percent = qty
        }

        public fun add_precentatge(nou: String){
            var x = (nou.toFloat() + qty_percent.toFloat()).toString()
            this.qty_percent = x
        }
    }

    interface OnFinishListener {
        fun ONfinish(a: ArrayList<report>)
    }

    var recepta: recepta_detall
    var c: Context
    var variableJSONrdi: JSONArray
    lateinit var llista_report:  ArrayList<report>
    lateinit var top3: ArrayList<report>
    var numero_request: Int
    val listener: OnFinishListener


    init{
        this.recepta = recepta
        this.c = c
        this.listener = listener


        val file_name = "rdi.json"
        val json_string = c.assets.open(file_name).bufferedReader().use{
            it.readText()
        }//no fa falta tancar el fitxer es tanca sol

        variableJSONrdi = JSONObject(json_string).getJSONArray("rdi")

        Log.d("prints", recepta.llista_ingredients.size.toString())
        Log.d("prints", variableJSONrdi.length().toString())
        numero_request = recepta.llista_ingredients.size * variableJSONrdi.length()
        Log.d("prints", numero_request.toString())
        init_llista_report()
    }

    private fun init_llista_report(){
        llista_report = arrayListOf()
        var x : JSONObject
        for(j in 0..variableJSONrdi.length()-1){
            x = variableJSONrdi.getJSONObject(j)
            llista_report.add(report(x.getString("nom"),x.getString("id_db"),0.toString()))
        }
    }

    private fun print_llista(){
        for(i in llista_report){
            Log.e("printllista", i.nom +" percentatge " + i.qty_percent + "%")
        }
        top_3()
    }

    private fun top_3(){
        //var a = llista_report.map { it.qty_percent.toFloat() }.toTypedArray()
        val sortedList = llista_report.sortedWith(compareBy({ it.qty_percent.toFloat() }))

        for(i in sortedList){
            Log.e("printllista", i.nom +" percentatge " + i.qty_percent + "%")
        }


        val top3list = sortedList.subList(sortedList.size-3,sortedList.size)
        top3 = ArrayList(top3list)
        Collections.reverse(top3)

        listener.ONfinish(top3)
    }

    public fun print_rdis(){
        Log.e("pitf","printeamos los rdis")
        var x : JSONObject

        for(i in recepta.llista_ingredients){

            if(!i.aliment.codi.equals("-1")){
                for(j in 0..variableJSONrdi.length()-1){
                    x = variableJSONrdi.getJSONObject(j)
                    var s = request(i.aliment.codi, x.getString("id_db"), x.getString("nom"), Unitat(x.getString("unitat"),x.getString("qty")))
                    Log.e("numero req", numero_request.toString())
                }
                //Log.e("dfdf","porcentaje calculado " + s.grams + " " + s.name )
                /*x = variableJSONrdi.getJSONObject(10)
                var s = request(i.aliment.codi, "1178", x.getString("nom"), Unitat(x.getString("unitat"),x.getString("qty")))
                x = variableJSONrdi.getJSONObject(2)
                var ss = request(i.aliment.codi, "1090", x.getString("nom"), Unitat(x.getString("unitat"),x.getString("qty")))*/

            }
        }


    }


    /*private fun nutrient_data(){
        for(i in recepta.llista_ingredients){
            val id_nutr = i.aliment.codi
        }
    }*/


    private fun request(id: String, id_nutrient: String, nom: String, rdi: Unitat){
        val url = "https://api.nal.usda.gov/fdc/v1/food/$id?api_key=WegyBxbDgsKNbAlSnCerYOogsyuwetZtuNolIz28"


        var llista: ArrayList<Unitat> = arrayListOf()
        var gramsMunitats :Unitat = Unitat("error", "error")
        val cache = DiskBasedCache(c.cacheDir, 1024 * 1024) // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())

        // Instantiate the RequestQueue with the cache and network. Start the queue.
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }


        llista.add(Unitat("grams","0"))
        var unidad: String

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                var sub = response.getJSONArray("foodNutrients")
                for(i in 0..sub.length()-1){

                    if ( sub.getJSONObject(i).getJSONObject("nutrient").getString("id").equals(id_nutrient) ){

                        if(sub.getJSONObject(i).getJSONObject("nutrient").getString("unitName").equals("µg")){ //el bug con microgramos que no se explicaba
                            unidad = "mcg"
                        }
                        else{
                            unidad = sub.getJSONObject(i).getJSONObject("nutrient").getString("unitName")
                        }

                        sub.getJSONObject(i).getJSONObject("nutrient").getString("unitName")
                        //Log.e("ddfdf",sub.getJSONObject(i).getString("amount"))
                        gramsMunitats = calculate_grams_micro(id ,sub.getJSONObject(i).getJSONObject("nutrient").getString("id"), sub.getJSONObject(i).getString("amount"),
                            unidad,response.getJSONArray("foodPortions").getJSONObject(0).getString("gramWeight"))
                        //return gramsiunitats
                        //calculate_percentatge_rdi(gramsMunitats, nom, rdi)
                        //Log.e("dfd",gramsMunitats.grams + " "+ gramsMunitats.name)
                        calculate_percentatge_rdi(gramsMunitats, nom, rdi)
                        break
                    }

                }
                numero_request--
                if(numero_request == 0){
                    Log.e("ENTRO AQUI", "sdsfdfdfdfdf")
                    print_llista()
                }

            },
            Response.ErrorListener { error ->
                // TODO: Handle error
                numero_request--
                if(numero_request == 0){
                    Log.e("ENTRO AQUI", "sdsfdfdfdfdf")
                    print_llista()
                }


            }
        )

        requestQueue.add(jsonObjectRequest)

    }


    private fun calculate_grams_micro(id_ingredient: String, id_nutrient: String, amount: String, unit: String, denominador: String) : Unitat{
        var ingred = recepta.getIngredient_ID(id_ingredient)

        //Log.e("d", id_nutrient + amount + unit)

        //Log.e("dfdf", recepta.getIngredient_ID(id_ingredient)?.unitat)
        //Log.e("dfdf", recepta.getIngredient_ID(id_ingredient)?.qty.toString())


        var cantidad_de_micro = amount.toFloat() * (getgrams_ingredient(ingred).toFloat().div(denominador.toFloat()))

        //Log.e("dfdf", "la cantidad de micro es " + cantidad_de_micro.toString())

        return Unitat(unit,cantidad_de_micro.toString())

    }

    //donat un ingredient dona els grams total del ingredient
    private fun getgrams_ingredient(i : Ingredient?): String{

        if (i != null) {
            for(q in i.aliment.units!!){
                if(q.name.equals(i.unitat)){
                    val num = i.qty.toFloat() * q.grams.toFloat()
                    return num.toString()
                }
            }
        }
        return "no 'sha pogut calcular"

    }

    private fun calculate_percentatge_rdi(gramsM: Unitat, nombre: String, rdi: Unitat){
        var numgramsrecepta = gramsM.grams.toFloat()
        var numgramsrdi = rdi.grams.toFloat()




        Log.e("d",gramsM.name.trim())


        normalitza(gramsM)
        normalitza(rdi)





        Log.e("fdf", (gramsM.grams.toFloat().div(rdi.grams.toFloat())*100).toString() + "%")
        val percentatge = (gramsM.grams.toFloat().div(rdi.grams.toFloat())*100).toString()

        for(i in 0..llista_report.size-1){
            if(llista_report.get(i).nom.equals(nombre)){
                llista_report.get(i).add_precentatge(percentatge)
            }
        }
    }


    private fun normalitza(unit: Unitat){
        var numgrams = unit.grams.toFloat()

        //Log.e("dfd","lo que me llega "+ unit.name + "a  " + unit.grams)

        /*if(unit.name.trim().equals("µg")){
            Log.e("ds","fdffLo detecto!")
        }*/

        if(unit.name.trim().equals("μg")){
            Log.e("ds","Lo detecto!")
            unit.grams = numgrams.div(10.toFloat().pow(6)).toString()
        }

        if(unit.name.trim().equals("mcg")){
            Log.e("ds","Lo detecto!")
            unit.grams = numgrams.div(10.toFloat().pow(6)).toString()
        }

        when {
            unit.name.equals("pg") -> {
                unit.grams = numgrams.div(10.toFloat().pow(12)).toString()
            }
            unit.name.equals("mg") -> {
                //Log.e("ds","entro en mg")
                unit.grams = numgrams.div(10.toFloat().pow(3)).toString()
            }
            unit.name.equals("ng") -> {
                unit.grams = numgrams.div(10.toFloat().pow(9)).toString()
            }
        }
    }



}