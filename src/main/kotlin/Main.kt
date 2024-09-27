package org.ventanas

import java.nio.file.Files
import java.nio.file.Path


/*
* El fichero calificaciones.csv contiene las calificaciones de un curso. Durante el curso se realizaron dos exámenes parciales de teoría y un examen de prácticas. Los alumnos que tuvieron menos de 4 en alguno de estos exámenes pudieron repetirlo en la recuperación al final del curso (convocatoria ordinaria). Escribir un programa que contenga las siguientes funciones:

* Una función que reciba el fichero de calificaciones y devuelva una lista de diccionarios, donde cada diccionario contiene la información de los exámenes y la asistencia de un alumno. La lista tiene que estar ordenada por apellidos.

* Una función que reciba una lista de diccionarios como la que devuelve la función anterior y añada a cada diccionario un nuevo par con la nota final del curso. El peso de cada parcial de teoría en la nota final es de un 30% mientras que el peso del examen de prácticas es de un 40%.

* Una función que reciba una lista de diccionarios como la que devuelve la función anterior y devuelva dos listas, una con los alumnos aprobados y otra con los alumnos suspensos. Para aprobar el curso, la asistencia tiene que ser mayor o igual que el 75%, la nota de los exámenes parciales y de prácticas mayor o igual que 4 y la nota final mayor o igual que 5.*/


// Una función que reciba el fichero de calificaciones y devuelva una lista de diccionarios, donde cada diccionario contiene la información de los exámenes y la asistencia de un alumno. La lista tiene que estar ordenada por apellidos.
fun clasificaciones2(path: Path): MutableList<MutableMap<String,String>>{

    val datos = mutableListOf<MutableMap<String,String>>()
    val br = Files.newBufferedReader(path)

    br.use {reader ->
        val encabezado = reader.readLine().split(";")
        reader.forEachLine { linea ->
            val mapStudents = mutableMapOf<String,String>()

            val valores = linea.split(";")
            for (i in encabezado.indices){
                mapStudents[encabezado[i]] = valores[i]
            }
            datos.add(mapStudents)

            }
        }

    return datos.sortedBy { it["Apellidos"] }.toMutableList()
}


// Una función que reciba una lista de diccionarios como la que devuelve la función anterior y añada a cada diccionario un nuevo par con la nota final del curso. El peso de cada parcial de teoría en la nota final es de un 30% mientras que el peso del examen de prácticas es de un 40%.
fun notaFinal(clasif: MutableList<MutableMap<String, String>>): MutableList<MutableMap<String, String>> {
    clasif.forEach { estudiante ->
        var notaFinal = 0.0
        val parcialesCompletados = mutableSetOf<String>()
        var parcial1 = 0.0
        var parcial2 = 0.0
        var practica = 0.0


        estudiante.forEach { (clave, nota) ->

            val nota2 = nota.replace(",", ".").toDoubleOrNull() ?: 0.0
            if (clave == "Parcial1" && nota2 <= 5.0){
                parcial1 = nota2
            } else if (clave == "Parcial2" && nota2 <= 5.0){
                parcial2 = nota2
            } else if (clave == "Practicas" && nota2 <= 5.0){
                practica = nota2
            }
            when {
                clave == "Parcial1" && nota2 >= 5.0 -> {
                    notaFinal += (nota2 * 0.30)
                    parcialesCompletados.add("Parcial1")
                }
                clave == "Parcial2" && nota2 >= 5.0 -> {
                    notaFinal += (nota2 * 0.30)
                    parcialesCompletados.add("Parcial2")
                }
                clave == "Practicas" && nota2 >= 5.0 -> {
                    notaFinal += (nota2 * 0.40)
                    parcialesCompletados.add("Practicas")
                }

                clave == "Ordinario1" && "Parcial1" !in parcialesCompletados -> {
                    notaFinal += if (parcial1 > nota2) (parcial1 * 0.30) else (nota2 * 0.30)
                }
                clave == "Ordinario2" && "Parcial2" !in parcialesCompletados -> {
                    notaFinal += if (parcial2 > nota2) (parcial2 * 0.30) else (nota2 * 0.30)
                }
                clave == "OrdinarioPracticas" && "Practicas" !in parcialesCompletados -> {
                    notaFinal += if (practica > nota2) (practica * 0.30) else (nota2 * 0.40)
                }
            }
        }

        estudiante["Nota Final"] = String.format("%.2f", notaFinal)

    }

    return clasif
}


// Una función que reciba una lista de diccionarios como la que devuelve la función anterior y devuelva dos listas, una con los alumnos aprobados y otra con los alumnos suspensos. Para aprobar el curso, la asistencia tiene que ser mayor o igual que el 75%, la nota de los exámenes parciales y de prácticas mayor o igual que 4 y la nota final mayor o igual que 5.*/
fun passSusp(clasif: MutableList<MutableMap<String, String>>): Pair<MutableList<String>,MutableList<String>>{
    val pass = mutableListOf<String>()
    val suspense = mutableListOf<String>()
    clasif.forEach { estudiante ->
        val parcialesCompletados = mutableSetOf<String>()

        estudiante.forEach { (clave, nota) ->
            val nota2 = nota.replace(",", ".").replace("%", "").toDoubleOrNull() ?: 0.0
            when {
                clave == "Asistencia" && nota2 >= 75.0 -> {
                    parcialesCompletados.add("Asistencia")
                }
                clave == "Nota Final" && nota2 >= 5.0 -> {
                    parcialesCompletados.add("Nota Final")
                }
                clave == "Parcial1" && nota2 >= 4.0 -> {
                    parcialesCompletados.add("Parcial1")
                }
                clave == "Parcial2" && nota2 >= 4.0 -> {
                    parcialesCompletados.add("Parcial2")
                }
                clave == "Practicas" && nota2 >= 4.0 -> {
                    parcialesCompletados.add("Practicas")
                }
                clave == "Ordinario1" && "Parcial1" !in parcialesCompletados && nota2 >= 5 -> parcialesCompletados.add("Parcial1")
                clave == "Ordinario2" && "Parcial2" !in parcialesCompletados && nota2 >= 5 -> parcialesCompletados.add("Parcial2")
                clave == "OrdinarioPracticas" && "Practicas" !in parcialesCompletados && nota2 >= 5 -> parcialesCompletados.add("Practicas")
            }
        }
        if (parcialesCompletados.size >= 5) pass.add(estudiante["Apellidos"].toString()) else suspense.add(estudiante["Apellidos"].toString())
    }

    return Pair(pass, suspense)
}


// Esto es otra forma de sacar la lista filtrada por el apellido pero que no he utilizado para este ejecicio
fun clasificaciones(path: Path): MutableList<MutableMap<String,MutableList<String>>>{

    var datos = mutableListOf<MutableMap<String,MutableList<String>>>()
    val br = Files.newBufferedReader(path)

    br.use {reader ->
        reader.forEachLine { linea ->
            val valores = linea.split(";").toMutableList()
            val clave = valores.first()

            if (clave != "Apellidos") {
                val mapStudents = mutableMapOf<String,MutableList<String>>()

                valores.remove(clave)
                mapStudents[clave] = valores
                datos.add(mapStudents)
            }
        }
    }
    datos = datos.sortedBy {
        val nombreCompleto = it.keys.first()
        val apellido = nombreCompleto.split(" ").last()
        apellido
    }.toMutableList()
    return datos
}

fun main() {
    val path = Path.of("C:\\2ºDAM\\ADA\\Ejerccicio2-ADA\\src\\main\\resources\\calificaciones.csv")
    val classif = clasificaciones2(path)
    val finalNota = notaFinal(classif)
    val passSuspense = passSusp(finalNota)
    val pass = passSuspense.first
    val suspense = passSuspense.second

    println("Han aprobado (${pass.size}): $pass")
    println("Han suspendidos (${suspense.size}): $suspense")


}