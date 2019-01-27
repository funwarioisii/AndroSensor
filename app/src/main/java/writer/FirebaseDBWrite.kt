package writer

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseDBWrite {
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var ref: DatabaseReference

    // Jsonのキー
    private val path = "/sensor/light"

    init {
        ref = database.getReference(path)
    }

    fun updateChild(childName: String, childElement: Float) {
        ref.child(childName).setValue(childElement).addOnCompleteListener {
            Log.d("completed", "key:{$childName}, value: $childElement")
        }
    }

}