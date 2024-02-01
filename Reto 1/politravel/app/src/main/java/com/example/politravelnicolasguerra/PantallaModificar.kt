package com.example.politravelnicolasguerra

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.File

class PantallaModificar : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var paisEditText: EditText
    private lateinit var lugaresInteresantesEditText: EditText
    private lateinit var precioEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var duracionEditText: EditText
    private lateinit var gridView: GridView
    private lateinit var puntuacionRatingBar: RatingBar
    private lateinit var transporte: Spinner
    private lateinit var inicioTourNombre: EditText
    private lateinit var finTourNombre: EditText
    private lateinit var inicioTourCoordenadasX: EditText
    private lateinit var inicioTourCoordenadasY: EditText
    private lateinit var finTourCoordenadasX: EditText
    private lateinit var finTourCoordenadasY: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_modificar)

        val adapterSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            arrayOf("avión", "barco", "coche")
        )
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
        inicioTourNombre= findViewById(R.id.inicioTourNombre)
        finTourNombre = findViewById(R.id.finTourNombre)

        inicioTourCoordenadasX = findViewById(R.id.inicioTourCoordenadasX)
        inicioTourCoordenadasY = findViewById(R.id.inicioTourCoordenadasY)
        finTourCoordenadasX = findViewById(R.id.finTourCoordenadasX)
        finTourCoordenadasY = findViewById(R.id.finTourCoordenadasY)


        gridView = findViewById(R.id.gridview)

        val adapter = PantallaDarDeAlta.ImageAdapter(this, readImagesFromDirectory())
        gridView.adapter = adapter

        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            view.setBackgroundResource(R.drawable.image_selected)

            val selectedImageName = adapter.getItem(position)

            adapter.selectedImageName = selectedImageName
        }

        val gson = Gson()
        val file = File(filesDir, "infoViajes.json")
        val paquetes = if (file.exists()) {
            gson.fromJson(file.readText(), Array<ClasePaquetes>::class.java).toMutableList()
        } else {
            mutableListOf()
        }

        val position = intent.getIntExtra("position", -1)
        val paquete = paquetes[position]

        nombreEditText.setText(paquete.nombre)
        paisEditText.setText(paquete.pais)
        lugaresInteresantesEditText.setText(paquete.lugaresInteresantes.joinToString())
        precioEditText.setText(paquete.precio)
        descripcionEditText.setText(paquete.descripcion)
        duracionEditText.setText(paquete.duracion.toString())
        transporte.setSelection(adapterSpinner.getPosition(paquete.transporte))
        puntuacionRatingBar.rating = paquete.puntuacion.toFloat()
        inicioTourNombre.setText(paquete.inicioTourNombre)
        finTourNombre.setText(paquete.finTourNombre)
        inicioTourCoordenadasX.setText(paquete.inicioTourCoordenadas[0].toString())
        inicioTourCoordenadasY.setText(paquete.inicioTourCoordenadas[1].toString())
        finTourCoordenadasX.setText(paquete.inicioTourCoordenadas[0].toString())
        finTourCoordenadasY.setText(paquete.inicioTourCoordenadas[1].toString())

        val ButtonGuardar = findViewById<Button>(R.id.botonGuardar)
        ButtonGuardar.setOnClickListener(){
            paquete.nombre = nombreEditText.text.toString()
            paquete.pais = paisEditText.text.toString()
            paquete.lugaresInteresantes = lugaresInteresantesEditText.text.split(",").map { it.trim() }.toTypedArray()
            paquete.precio = precioEditText.text.toString()
            paquete.descripcion = descripcionEditText.text.toString()
            paquete.duracion = duracionEditText.text.toString().toInt()
            paquete.transporte = transporte.selectedItem.toString()
            paquete.puntuacion = puntuacionRatingBar.rating.toDouble()
            paquete.inicioTourNombre = inicioTourNombre.text.toString()
            paquete.finTourNombre = finTourNombre.text.toString()
            paquete.inicioTourCoordenadas = listOf(
                inicioTourCoordenadasX.text.toString().toDouble(),
                inicioTourCoordenadasY.text.toString().toDouble()
            ).toTypedArray()
            paquete.finTourCoordenadas = listOf(
                finTourCoordenadasX.text.toString().toDouble(),
                finTourCoordenadasY.text.toString().toDouble()
            ).toTypedArray()

            paquetes[position] = paquete
            val paquetesJson = gson.toJson(paquetes)
            file.writeText(paquetesJson)
            this.finish()
        }
    }

    private fun readImagesFromDirectory(): List<String> {
        val imagesDirectory = File(filesDir, "img")
        return imagesDirectory.listFiles()?.filter { it.isFile }?.map { it.name } ?: emptyList()
    }
}