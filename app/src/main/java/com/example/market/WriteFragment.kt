package com.example.market
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.webkit.MimeTypeMap

class WriteFragment : Fragment() {

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_write, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveImageButton: Button = view.findViewById(R.id.addImageButton)
        saveImageButton.setOnClickListener {
            openFileChooser()
        }

        val saveButton = view.findViewById<Button>(R.id.button)
        saveButton.setOnClickListener {
            saveDataToFirestore()
            Toast.makeText(requireContext(), "글쓰기 성공", Toast.LENGTH_SHORT).show()

            // ListFragment로 전환
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, ListFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            saveDataToFirestore()
        }
    }

    private fun saveDataToFirestore() {
        val title = view?.findViewById<EditText>(R.id.titleContent)
        val price = view?.findViewById<EditText>(R.id.priceContent)
        val content = view?.findViewById<EditText>(R.id.contentView)
        val name = view?.findViewById<EditText>(R.id.nameContent)

        val db = FirebaseFirestore.getInstance()
        val itemsCollection: CollectionReference = db.collection("products")

        val itemData = hashMapOf(
            "title" to title?.text.toString(),
            "price" to price?.text.toString(),
            "content" to content?.text.toString(),
            "name" to name?.text.toString(),
            "sell" to "판매중" // Default status
        )

        if (imageUri != null) {
            uploadImageAndSaveData(itemData, itemsCollection)
        } else {
            saveDataWithoutImage(itemData, itemsCollection)
        }
    }

    private fun uploadImageAndSaveData(itemData: HashMap<String, String>, itemsCollection: CollectionReference) {
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
                Toast.makeText(requireContext(), "Error uploading image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveDataWithoutImage(itemData: HashMap<String, String>, itemsCollection: CollectionReference) {
        itemsCollection.add(itemData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(requireContext(), "Data saved to Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error saving data to Firestore", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = requireActivity().contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }
}
