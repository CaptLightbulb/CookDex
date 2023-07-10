package com.lightbulb.android.cookdex

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.io.File
import java.util.*
import android.Manifest

private const val REQUEST_IMAGE = 2
private const val MEDIA_PERM_REQUEST = 3

class RecipeFragment : Fragment() {

    private lateinit var recipe: Recipe
    private lateinit var titleField: EditText
    private lateinit var urlField: EditText
    private lateinit var recipeImageView: ImageView
    private lateinit var setImageButton: Button
    private lateinit var viewPageButton: Button
    private lateinit var deleteButton: Button
    private lateinit var imageFile: File
    private lateinit var imageUri: Uri

    private val recipePageViewModel: RecipePageViewModel by lazy {
        ViewModelProviders.of(this).get(RecipePageViewModel::class.java)
    }

    //retrieves the recipe from the database based on the UUID passed in the args
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipe = Recipe()
        val recipeId: UUID = arguments?.getSerializable("recipeId") as UUID
        recipePageViewModel.loadRecipe(recipeId)
    }

    //Initialize some of the components used in the Fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recipe_page, container, false)

        titleField = view.findViewById(R.id.recipe_page_title)
        urlField = view.findViewById(R.id.recipe_page_url)
        recipeImageView = view.findViewById(R.id.recipe_page_image)
        setImageButton = view.findViewById(R.id.set_image)
        viewPageButton = view.findViewById(R.id.view_recipe)
        deleteButton = view.findViewById(R.id.delete_recipe)

        return view
    }

    //If we're editing a recipe that already exists, the EditText widget will be initialized to display the current title
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipePageViewModel.recipeData.observe(
            viewLifecycleOwner,
             Observer{ recipe ->
                 recipe?.let {
                     this.recipe = recipe
                     updateUI()
                 }
             }
        )
    }

    override fun onStart() {
        super.onStart()

        //This allows the recipe to be updated in real time any time the user makes a change to the EditText widget
        val titleUpdater = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                recipe.title = p0.toString()
            }
            override fun afterTextChanged(p0: Editable?) {}
        }

        titleField.addTextChangedListener(titleUpdater)

        //This allows the recipe to be updated in real time any time the user makes a change to the EditText widget
        val urlUpdater = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                recipe.url = p0.toString()
            }
            override fun afterTextChanged(p0: Editable?) {}
        }

        urlField.addTextChangedListener(urlUpdater)

        //This opens up the phone's image gallery for the user to select an image
        //Only works if the user has permitted the app to access their media files
        setImageButton.apply {

            val selectImage = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }

            setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireActivity().applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE) as Array<out String>, MEDIA_PERM_REQUEST)
                } else {
                    startActivityForResult(
                        Intent.createChooser(selectImage, "Select Image"),
                        REQUEST_IMAGE
                    )
                }
            }
        }

        //Will tell the database to delete the current recipe before returning to the recipe master list screen
        deleteButton.setOnClickListener{
            val warning = AlertDialog.Builder(requireContext()).apply {
                setMessage("Are you sure you want to delete this recipe?")
                setPositiveButton("Yes", DialogInterface.OnClickListener{dialog, id ->
                    recipePageViewModel.deleteRecipe(recipe)
                    activity?.supportFragmentManager?.popBackStack()
                })
                setNegativeButton("No"){_, _ ->
                }
            }
            warning.show()
        }

        //Will use the recipe's set Url to open a webview to view the recipe's website
        viewPageButton.setOnClickListener{
            val webUri = Uri.parse(recipe.url)
            val intent = RecipeWebActivity.newIntent(requireContext(), webUri)
            startActivity(intent)
        }
    }

    //stores the user's selected image as a filepath in the database to use that picture later
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK) return

        //after the user selects an image, the image Uri is assigned to the recipe
        if (requestCode == REQUEST_IMAGE) {
            var imageUri = data?.data
            if (imageUri != null){

                recipe.imagePath = ImageFilePath.getPath(activity?.applicationContext, imageUri) ?: ""
                recipePageViewModel.saveRecipe(recipe)
                updateUI()
            }
        }
    }

    //Ensures the database is updated with any changes whenever the user exits the recipe's detail page
    override fun onStop() {
        super.onStop()
        recipePageViewModel.saveRecipe(recipe)
    }

    //keeps the interface synced with the recipe's properties
    private fun updateUI(){
        titleField.setText(recipe.title)
        urlField.setText(recipe.url)
        recipeImageView.setImageResource(R.drawable.ic_blank_image)
        if(recipe.imagePath.isNotEmpty()){
            val imageFile = File(recipe.imagePath)
            if (imageFile.exists()) recipeImageView.setImageBitmap(BitmapFactory.decodeFile(imageFile.absolutePath))
        }
    }

    //This will return a new instance of this Fragment with the recipe property preset to whatever recipe is passed as an argument
    companion object {
        fun newInstance(recipeId: UUID): RecipeFragment{
            val args = Bundle().apply {
                putSerializable("recipeId", recipeId)
            }
            return RecipeFragment().apply {
                arguments = args
            }
        }
    }

}