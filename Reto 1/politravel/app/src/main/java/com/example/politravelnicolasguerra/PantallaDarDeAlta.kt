package com.example.politravelnicolasguerra

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.File


class PantallaDarDeAlta : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var paisEditText: EditText
    private lateinit var lugaresInteresantesEditText: EditText
    private lateinit var precioEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var duracionEditText: EditText
    private lateinit var gridView: GridView
    private lateinit var puntuacionRatingBar: RatingBar
    private lateinit var transporte: Spinner
    private var inicioTourCoordenadasX: Double = 0.0
    private var inicioTourCoordenadasY: Double = 0.0
    private var finTourCoordenadasX: Double = 0.0
    private var finTourCoordenadasY: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_dar_de_alta)

        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf("avión", "barco", "coche"))
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        transporte = findViewById(R.id.transporte)
        transporte.adapter = adapterSpinner

        nombreEditText = findViewById(R.id.nombre)
        paisEditText = findViewById(R.id.pais)
        lugaresInteresantesEditText = findViewById(R.id.lugaresInteresantes)
        precioEditText = findViewById(R.id.precio)
        descripcionEditText = findViewById(R.id.descripcion)
        duracionEditText = findViewById(R.id.duracion)
        gridView = findViewById(R.id.gridview)
        puntuacionRatingBar = findViewById(R.id.puntuacion)
        val inicioTourCoordenadasXText = findViewById<EditText>(R.id.inicioTourCoordenadasX).text.toString()
        val inicioTourCoordenadasYText = findViewById<EditText>(R.id.inicioTourCoordenadasY).text.toString()
        val finTourCoordenadasXText = findViewById<EditText>(R.id.finTourCoordenadasX).text.toString()
        val finTourCoordenadasYText = findViewById<EditText>(R.id.finTourCoordenadasY).text.toString()

        if (inicioTourCoordenadasXText.isNotEmpty() &&
            inicioTourCoordenadasYText.isNotEmpty() &&
            finTourCoordenadasXText.isNotEmpty() &&
            finTourCoordenadasYText.isNotEmpty()) {
            inicioTourCoordenadasX = inicioTourCoordenadasXText.toDouble()
            inicioTourCoordenadasY = inicioTourCoordenadasYText.toDouble()
            finTourCoordenadasX = finTourCoordenadasXText.toDouble()
            finTourCoordenadasY = finTourCoordenadasYText.toDouble()
        }

        gridView = findViewById(R.id.gridview)

        val adapter = ImageAdapter(this, readImagesFromDirectory())
        gridView.adapter = adapter

        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            view.setBackgroundResource(R.drawable.image_selected)

            val selectedImageName = adapter.getItem(position)

            adapter.selectedImageName = selectedImageName
        }

        val botonGuardar = findViewById<Button>(R.id.botonGuardar)
        botonGuardar.setOnClickListener {
            val selectedImageName = adapter.selectedImageName

            val gson = Gson()

            val file = File(filesDir, "infoViajes.json")
            val paquetes = if (file.exists()) {
                gson.fromJson(file.readText(), Array<ClasePaquetes>::class.java).toMutableList()
            } else {
                mutableListOf()
            }

            val lastId = paquetes.lastOrNull()?.id ?: 0

            val inicioTourCoordenadasXText = findViewById<EditText>(R.id.inicioTourCoordenadasX).text.toString()
            val inicioTourCoordenadasYText = findViewById<EditText>(R.id.inicioTourCoordenadasY).text.toString()
            val finTourCoordenadasXText = findViewById<EditText>(R.id.finTourCoordenadasX).text.toString()
            val finTourCoordenadasYText = findViewById<EditText>(R.id.finTourCoordenadasY).text.toString()

            if (inicioTourCoordenadasXText.isNotEmpty() &&
                inicioTourCoordenadasYText.isNotEmpty() &&
                finTourCoordenadasXText.isNotEmpty() &&
                finTourCoordenadasYText.isNotEmpty()) {
                inicioTourCoordenadasX = inicioTourCoordenadasXText.toDouble()
                inicioTourCoordenadasY = inicioTourCoordenadasYText.toDouble()
                finTourCoordenadasX = finTourCoordenadasXText.toDouble()
                finTourCoordenadasY = finTourCoordenadasYText.toDouble()

                val objetoPaquetes = ClasePaquetes(
                    lastId + 1,
                    nombreEditText.text.toString(),
                    paisEditText.text.toString(),
                    selectedImageName ?: "",
                    lugaresInteresantesEditText.text.toString().split(",").toTypedArray(),
                    puntuacionRatingBar.rating.toDouble(),
                    precioEditText.text.toString(),
                    descripcionEditText.text.toString(),
                    duracionEditText.text.toString().toInt(),
                    transporte.selectedItem.toString(),
                    findViewById<EditText>(R.id.inicioTourNombre).text.toString(),
                    findViewById<EditText>(R.id.finTourNombre).text.toString(),
                    arrayOf(inicioTourCoordenadasX, inicioTourCoordenadasY),
                    arrayOf(finTourCoordenadasX, finTourCoordenadasY)
                )

                paquetes.add(objetoPaquetes)

                file.writeText(gson.toJson(paquetes))

                this.finish()
            } else {
                Toast.makeText(this, "Error: el campo de coordenadas no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readImagesFromDirectory(): List<String> {
        val imagesDirectory = File(filesDir, "img")
        return imagesDirectory.listFiles()?.filter { it.isFile }?.map { it.name } ?: emptyList()
    }

    class ImageAdapter(private val context: Context, private val imageNames: List<String>) : BaseAdapter() {
        var selectedImageName: String? = null

        override fun getCount() = imageNames.size

        override fun getItem(position: Int) = imageNames[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val imageView = ImageView(context)
            imageView.layoutParams = AbsListView.LayoutParams(300, 300)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(0, 10, 0, 10)

            val imageName = imageNames[position]
            imageView.setImageURI(Uri.fromFile(File(context.filesDir, "img/$imageName")))

            return imageView
        }
    }
}
