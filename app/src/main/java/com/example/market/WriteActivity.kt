package com.example.market
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import android.webkit.MimeTypeMap
import com.example.market.R

@Suppress("DEPRECATION")
class WriteActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        val saveImageButton: Button = findViewById(R.id.addImageButton)
        saveImageButton.setOnClickListener {
            openFileChooser()
        }
        val saveButton = findViewById<Button>(R.id.button)
        saveButton.setOnClickListener {
            saveDataToFirestore()
            Toast.makeText(this@WriteActivity, "글쓰기 성공", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)

        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            saveDataToFirestore()
        }
    }

    private fun saveDataToFirestore() {
        val title = findViewById<EditText>(R.id.titleContent)
        val price = findViewById<EditText>(R.id.priceContent)
        val content = findViewById<EditText>(R.id.contentView)
        val name = findViewById<EditText>(R.id.nameContent)

        val db = FirebaseFirestore.getInstance()
        val itemsCollection: CollectionReference = db.collection("products")

        val itemData = hashMapOf<String, Any>(
            "title" to title.text.toString(),
            "price" to price.text.toString(),
            "content" to content.text.toString(),
            "name" to name.text.toString(),
            "sell" to "판매중" // Default status
        )

        if (imageUri != null) {
            uploadImageAndSaveData(itemData, itemsCollection)
        } else {
            saveDataWithoutImage(itemData, itemsCollection)
        }
    }

    private fun uploadImageAndSaveData(itemData: HashMap<String, Any>, itemsCollection: CollectionReference) {
        val storageRef: StorageReference = FirebaseStorage.getInstance().getReference("item_images")
        val imageName = "${System.currentTimeMillis()}.${getFileExtension(imageUri!!)}"

        storageRef.child(imageName).putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.child(imageName).downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        itemData["imageUrl"] = downloadUri.toString()
                        saveDataWithoutImage(itemData, itemsCollection)
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveDataWithoutImage(itemData: HashMap<String, Any>, itemsCollection: CollectionReference) {
        itemsCollection.add(itemData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Data saved to Firestore", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving data to Firestore", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }
}
