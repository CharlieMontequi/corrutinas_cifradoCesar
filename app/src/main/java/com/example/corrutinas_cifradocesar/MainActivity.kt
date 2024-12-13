package com.example.corrutinas_cifradocesar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    // priemro indicar los elementos con los que se va a trabajar
    private lateinit var textoOringinal: EditText
    private lateinit var textoCifrado: TextView
    private lateinit var barraPorgreso : ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // dar a los componenetes las referencias

         textoCifrado = findViewById(R.id.textViewTextoConvertido)
        val botonConvertir = findViewById<Button>(R.id.buttonConvertir)
        textoOringinal = findViewById(R.id.editTextMultiOriginal)
        barraPorgreso= findViewById(R.id.progressBarCifrante)

        // evento para hacer el cifrado
        botonConvertir.setOnClickListener {


                cifrarConCorrutina(textoOringinal.text.toString())

        }

    }

    // funcion para establecer la corrutina
    private fun cifrarConCorrutina(texto: String) {
        // visibilizar la barra de carga
        barraPorgreso.visibility= android.view.View.VISIBLE


        // crear el ambito para las corrutinas a ejecutar
        // en el hilo principal to_do lo que ya dentro del launch
        // en el secundario es hilo distinto
        CoroutineScope(Dispatchers.Main).launch {
            val textoCodificandose = withContext(Dispatchers.Default) {
                codificarTexto(texto){ progreso ->
                    // Actualizar la ProgressBar en el hilo principal utilizando
                    // otra corrutina
                    launch(Dispatchers.Main) {
                        barraPorgreso.progress = progreso
                    }
                }
            }

            textoCifrado.text = textoCodificandose
            barraPorgreso.visibility= android.view.View.INVISIBLE
        }
    }

    private fun codificarTexto(texto: String, desplazamiento: Int = 4, actualizarProgreso: (Int) -> Unit): String {
        val cifrando = StringBuilder()

        while(cifrando.length < texto.length){

            actualizarProgreso(cifrando.length)
            for (char in texto) {
                if (char.isLetter()) {

                    // ver si las letras son mayusculas o minusculas con codiogo asci
                    val offset = if (char.isUpperCase()) 'A'.code else 'a'.code

                    // desplazar el numero de posiciones que se ha indicado
                    val newChar = ((char.code - offset + desplazamiento) % 26 + offset).toChar()
                    cifrando.append(newChar)

                } else {

                    // dejar caracteres no alfabéticos sin cambios
                    cifrando.append(char)
                }
            }

        }



        return cifrando.toString()
    }
    }