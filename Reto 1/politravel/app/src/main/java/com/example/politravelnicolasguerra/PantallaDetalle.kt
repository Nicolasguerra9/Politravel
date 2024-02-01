package com.example.politravelnicolasguerra

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.io.File

class PantallaDetalle : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap

    private var inicioTourCoordenadas: Array<Double>? = null
    private var finTourCoordenadas: Array<Double>? = null
    private var inicioTourNombre: String = ""
    private var finTourNombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_detalle)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Obtener los datos del intent
        val id = intent.getIntExtra("id", 0)
        val nombreI = intent.getStringExtra("nombre") ?: ""
        val paisI = intent.getStringExtra("pais") ?: ""
        val imgI = intent.getStringExtra("img") ?: ""
        val lugaresInteresantesI = intent.getStringArrayExtra("lugaresInteresantes") ?: emptyArray()
        val puntuacionI = intent.getDoubleExtra("puntuacion", 0.0)
        val precioI = intent.getStringExtra("precio") ?: ""
        val descripcionI = intent.getStringExtra("descripcion") ?: ""
        val duracionI = intent.getIntExtra("duracion", 0)
        val transporteI = intent.getStringExtra("transporte") ?: ""
        inicioTourNombre = intent.getStringExtra("inicioTourNombre") ?: ""
        finTourNombre = intent.getStringExtra("finTourNombre") ?: ""

         inicioTourCoordenadas =
            intent.getStringArrayExtra("inicioTourCoordenadas")?.map { it.toDouble() }
                ?.toTypedArray()
         finTourCoordenadas =
            intent.getStringArrayExtra("finTourCoordenadas")?.map { it.toDouble() }?.toTypedArray()

        Log.d("COORDENADAS", "Inicio: ${inicioTourCoordenadas?.get(0)}, ${inicioTourCoordenadas?.get(1)}")
        Log.d("COORDENADAS", "Fin: ${finTourCoordenadas?.get(0)}, ${finTourCoordenadas?.get(1)}")


        // Actualizar las vistas con los datos del intent
        val nombre = findViewById<TextView>(R.id.nombre)
        nombre.text = nombreI

        val pais = findViewById<TextView>(R.id.pais)
        pais.text = paisI

        val puntuacion = findViewById<TextView>(R.id.puntuacion)
        puntuacion.text = String.format("%.2f", puntuacionI)

        val precio = findViewById<TextView>(R.id.precio)
        precio.text = precioI

        val duracion = findViewById<TextView>(R.id.duracion)
        duracion.text = duracionI.toString()

        val transporte = findViewById<TextView>(R.id.transporte)
        transporte.text = transporteI

        val descripcion = findViewById<TextView>(R.id.descripcion)
        descripcion.text = descripcionI

        val itinerario = findViewById<TextView>(R.id.itinerario)
        val lugaresInteresantesStr = lugaresInteresantesI.joinToString(separator = "\n")
        itinerario.text = lugaresInteresantesStr

        val imgTransporte = findViewById<ImageView>(R.id.imgTransporte)
        when (transporteI) {
            "avión" -> imgTransporte.setImageResource(R.drawable.avion)
            "barco" -> imgTransporte.setImageResource(R.drawable.barco)
            "coche" -> imgTransporte.setImageResource(R.drawable.coche)
        }

        val img = findViewById<ImageView>(R.id.image)
        val imagePath = "${filesDir.absolutePath}/img/$imgI"

        val file = File(imagePath)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            img.setImageBitmap(bitmap)
        } else {
            Log.e("TAG", "La imagen no existe en la ruta: $imagePath")
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Agregar un listener para esperar a que el mapa esté listo
        map.setOnMapLoadedCallback {
            // Agregar marcador para inicioTourCoordenadas
            val inicioTourMarker = LatLng(
                inicioTourCoordenadas?.get(0) ?: 0.0, // Si es null, utiliza 0.0 como valor por defecto
                inicioTourCoordenadas?.get(1) ?: 0.0
            )
            map.addMarker(MarkerOptions().position(inicioTourMarker).title("Inicio del Tour").snippet("Nombre: $inicioTourNombre"))

            // Agregar marcador para finTourCoordenadas
            val finTourMarker = LatLng(
                finTourCoordenadas?.get(0) ?: 0.0,
                finTourCoordenadas?.get(1) ?: 0.0
            )
            map.addMarker(MarkerOptions().position(finTourMarker).title("Fin del Tour").snippet("Nombre: $finTourNombre"))

            // Configurar la cámara para mostrar los marcadores
            val builder = LatLngBounds.Builder()
            builder.include(inicioTourMarker)
            builder.include(finTourMarker)
            val bounds = builder.build()
            val padding = 50 // En px
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            map.moveCamera(cu)
        }
    }


    // Métodos del ciclo de vida del MapView
    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}