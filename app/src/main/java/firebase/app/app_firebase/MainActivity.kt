package firebase.app.app_firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import firebase.app.app_firebase.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import firebase.app.app_firebase.Luchador
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ChildEventListener
import android.widget.AdapterView.OnItemClickListener
import android.content.DialogInterface
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.util.ArrayList

class MainActivity() : AppCompatActivity() {
    //Definicion de variables
    private var txtid: EditText? = null
    private var txtnom: EditText? = null
    private var txtdesc: EditText? = null
    private var btnbus: Button? = null
    private var btnmod: Button? = null
    private var btnreg: Button? = null
    private var btneli: Button? = null
    private var lvDatos: ListView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //inicializacion de variables
        txtid = findViewById<View>(R.id.txtid) as EditText
        txtnom = findViewById<View>(R.id.txtnom) as EditText
        txtdesc = findViewById<View>(R.id.txtdesc) as EditText
        btnbus = findViewById<View>(R.id.btnbus) as Button
        btnmod = findViewById<View>(R.id.btnmod) as Button
        btnreg = findViewById<View>(R.id.btnreg) as Button
        btneli = findViewById<View>(R.id.btneli) as Button
        lvDatos = findViewById<View>(R.id.lvDatos) as ListView

        //funciones para la logica de accion de cada boton
        botonBuscar()
        botonModificar()
        botonRegistrar()
        botonEliminar()
        ListarProductos()
    }

    //creacion de los metodos para los botones
    private fun botonBuscar() {
        btnbus!!.setOnClickListener(View.OnClickListener {
            if (txtid!!.text.toString().trim { it <= ' ' }.isEmpty()) {
                ocultarTeclado()
                Toast.makeText(
                    this@MainActivity,
                    "Dijite el ID del Producto a buscar",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val id = txtid!!.text.toString().toInt()
                val db = FirebaseDatabase.getInstance()
                val dbref = db.getReference(Luchador::class.java.simpleName)
                dbref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val aux = Integer.toString(id)
                        var res = false
                        for (x: DataSnapshot in snapshot.children) {
                            if (aux.equals(x.child("id").value.toString(), ignoreCase = true)) {
                                res = true
                                ocultarTeclado()
                                txtnom!!.setText(x.child("nombre").value.toString())
                                txtdesc!!.setText(x.child("desc").value.toString())
                                break
                            }
                        }
                        if (res == false) {
                            ocultarTeclado()
                            Toast.makeText(
                                this@MainActivity,
                                "ID ($aux) No ha Sido Encontrado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        })
    }

    private fun botonModificar() {
        btnmod!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (txtid!!.text.toString().trim { it <= ' ' }
                        .isEmpty() || txtnom!!.text.toString()
                        .isEmpty() || txtdesc!!.text.toString().isEmpty()) {
                    ocultarTeclado()
                    Toast.makeText(
                        this@MainActivity,
                        "Complete los Datos Faltantes para Actualizar",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val id = txtid!!.text.toString().toInt()
                    val nom = txtnom!!.text.toString()
                    val desc = txtdesc!!.text.toString()

                    //conectamos con Firebase
                    val db = FirebaseDatabase.getInstance()
                    //referencia
                    val dbref = db.getReference(Luchador::class.java.simpleName)
                    //evento de accion para insercion en firebase
                    dbref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var res2 = false
                            for (x: DataSnapshot in snapshot.children) {
                                if (x.child("nombre").value.toString()
                                        .equals(nom, ignoreCase = true)
                                ) {
                                    res2 = true
                                    ocultarTeclado()
                                    Toast.makeText(
                                        this@MainActivity,
                                        "El Nombre ($nom) ya Existe, No se Puede Modificar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    break
                                }
                            }
                            if (res2 == false) {
                                val aux = Integer.toString(id)
                                var res = false
                                for (x: DataSnapshot in snapshot.children) {
                                    if (x.child("id").value.toString()
                                            .equals(aux, ignoreCase = true)
                                    ) {
                                        res = true
                                        ocultarTeclado()
                                        x.ref.child("nombre").setValue(nom)
                                        x.ref.child("desc").setValue(desc)
                                        txtid!!.setText("")
                                        txtnom!!.setText("")
                                        txtdesc!!.setText("")
                                        ListarProductos()
                                        break
                                    }
                                }
                                if (res == false) {
                                    ocultarTeclado()
                                    Toast.makeText(
                                        this@MainActivity,
                                        "ID ($aux) no Encontrado, No se Puede Modificar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    txtid!!.setText("")
                                    txtnom!!.setText("")
                                    txtdesc!!.setText("")
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
        })
    } //boton mod

    private fun botonRegistrar() {
        btnreg!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                //para ver que dato falta
                if (txtid!!.text.toString().trim { it <= ' ' }
                        .isEmpty() || txtnom!!.text.toString()
                        .isEmpty() || txtdesc!!.text.toString().isEmpty()) {
                    ocultarTeclado()
                    Toast.makeText(
                        this@MainActivity,
                        "Complete los campos faltantes",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val id = txtid!!.text.toString().toInt()
                    val nom = txtnom!!.text.toString()
                    val desc = txtdesc!!.text.toString()

                    //conectamos con Firebase
                    val db = FirebaseDatabase.getInstance()
                    //referencia
                    val dbref = db.getReference(Luchador::class.java.simpleName)
                    //evento de accion para insercion en firebase
                    dbref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val aux = Integer.toString(id)
                            var res = false
                            for (x: DataSnapshot in snapshot.children) {
                                if (x.child("id").value.toString().equals(aux, ignoreCase = true)) {
                                    res = true
                                    ocultarTeclado()
                                    Toast.makeText(
                                        this@MainActivity,
                                        "!Error!, el ID ($aux) ya Existe",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    break
                                }
                            }
                            //para evaluar que el nombre no se repita
                            val res2 = false
                            for (x: DataSnapshot in snapshot.children) {
                                if (x.child("nombre").value.toString()
                                        .equals(nom, ignoreCase = true)
                                ) {
                                    res = true
                                    ocultarTeclado()
                                    Toast.makeText(
                                        this@MainActivity,
                                        "!Error!, el Nombre ($nom) ya Existe",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    break
                                }
                            }
                            if (res == false && res2 == false) {
                                val luc = Luchador(id, nom, desc)
                                //para realizaar la insercion
                                dbref.push().setValue(luc)
                                ocultarTeclado()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Cancion Registrada Correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                txtid!!.setText("")
                                txtnom!!.setText("")
                                txtdesc!!.setText("")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
        })
    } //boton registrar

    //Funcion para el listado
    private fun ListarProductos() {
        var db = FirebaseDatabase.getInstance()
        var dbref = db.getReference(Luchador::class.java.simpleName)
        var lisluc = ArrayList<String>()
        var ada = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, lisluc)
        lvDatos?.adapter = ada
        dbref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var luc = snapshot.child("nombre").getValue().toString()
                var idluc= snapshot.child("id").getValue().toString()
                var descLuc = snapshot.child("desc").getValue().toString()
                var datos :Luchador?=Luchador(idluc.toInt(),luc,descLuc)
                lisluc.add(datos.let { it?.nombre }.toString())
                ada.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                ada.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
        lvDatos?.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var luch = lisluc
                var aux:String=luch.get(p2)
                dbref.addListenerForSingleValueEvent(object  : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for( x in snapshot.children){
                            if(x.child("nombre").getValue().toString().equals(aux)){
                                var a = AlertDialog.Builder(this@MainActivity)
                                a.setTitle("Cancion seleccionada  \n$aux")
                                var mensaje:String="ID de la cancion: ${x.child("id").getValue().toString()}\n" +
                                        "Descripcion: ${x.child("desc").getValue().toString()}"
                                a.setMessage(mensaje)
                                a.show()
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }

        })
    }

    private fun botonEliminar() {
        btneli!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (txtid!!.text.toString().trim { it <= ' ' }.isEmpty()) {
                    ocultarTeclado()
                    Toast.makeText(
                        this@MainActivity,
                        "escriba el ID de la cancion a buscar",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val id = txtid!!.text.toString().toInt()
                    val db = FirebaseDatabase.getInstance()
                    val dbref = db.getReference(Luchador::class.java.simpleName)
                    dbref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val aux = Integer.toString(id)
                            val res = booleanArrayOf(false)
                            for (x: DataSnapshot in snapshot.children) {
                                if (aux.equals(x.child("id").value.toString(), ignoreCase = true)) {
                                    val a = AlertDialog.Builder(this@MainActivity)
                                    a.setCancelable(false)
                                    a.setTitle("Pregunta")
                                    a.setMessage("¿Seguro que Quieres Eliminar El Registro?")
                                    a.setNegativeButton(
                                        "Cancelar",
                                        object : DialogInterface.OnClickListener {
                                            override fun onClick(
                                                dialogInterface: DialogInterface,
                                                i: Int
                                            ) {
                                            }
                                        })
                                    a.setPositiveButton(
                                        "Aceptar",
                                        object : DialogInterface.OnClickListener {
                                            override fun onClick(
                                                dialogInterface: DialogInterface,
                                                i: Int
                                            ) {
                                                res[0] = true
                                                ocultarTeclado()
                                                x.ref.removeValue()
                                                ListarProductos()
                                                txtid!!.setText("")
                                                txtnom!!.setText("")
                                                txtdesc!!.setText("")
                                            }
                                        })
                                    a.show()
                                    break
                                }
                            }
                            if (res[0] == false) {
                                ocultarTeclado()
                                Toast.makeText(
                                    this@MainActivity,
                                    "ID ($aux) No ha Sido Encontrado, no se Puede Eliminar",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
        })
    }

    //metodo para ocultar teclado
    private fun ocultarTeclado() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}