package firebase.app.app_firebase

import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import firebase.app.app_firebase.Luchador

class Luchador : Parcelable {
    //crearemos ahora los get y set
    //tendra 3 datos
    var id = 0
    var nombre: String? = null
    var desc: String? = null

    //constructor
    constructor() {}
    constructor(id: Int, nombre: String?, desc: String?) {
        this.id = id
        this.nombre = nombre
        this.desc = desc
    }

    //creamos el toString
    override fun toString(): String {
        return "Luchador{" +
                "nombre='" + nombre + '\'' +
                ", desc='" + desc + '\'' +
                '}'
    }

    //crearemos el parcelable solo si se quieren mandar objetos de una ventana a otra
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(nombre)
        dest.writeString(desc)
    }

    fun readFromParcel(source: Parcel) {
        id = source.readInt()
        nombre = source.readString()
        desc = source.readString()
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readInt()
        nombre = `in`.readString()
        desc = `in`.readString()
    }


    companion object CREATOR : Creator<Luchador> {
        override fun createFromParcel(parcel: Parcel): Luchador {
            return Luchador(parcel)
        }

        override fun newArray(size: Int): Array<Luchador?> {
            return arrayOfNulls(size)
        }
    }
}