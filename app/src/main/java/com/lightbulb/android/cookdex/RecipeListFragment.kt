package com.lightbulb.android.cookdex

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.*

class RecipeListFragment : Fragment(){

    //This is needed for activity hosting
    interface Callbacks {
        fun onRecipeSelected(recipeId: UUID)
    }
    private var callbacks: Callbacks? = null

    //These components are needed to display the list of recipes
    private lateinit var recipeRecyclerView: RecyclerView
    private var adapter: RecipeAdapter? = RecipeAdapter(emptyList())

    //Get an instance of my recipeViewModel class
    private val recipeListViewModel: RecipeListViewModel by lazy {
        ViewModelProviders.of(this).get(RecipeListViewModel::class.java)
    }

    //Allow my custom menu to be used in place of the default action bar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    //This is need for interactions with the Main Activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    //This will initialize the display for the recipe list when the fragment is loading up
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recipe_list, container, false)

        recipeRecyclerView = view.findViewById(R.id.recipe_recycler_view) as RecyclerView
        recipeRecyclerView.adapter = adapter
        recipeRecyclerView.layoutManager = GridLayoutManager(context, 4)

        return view
    }

    //Updates the interface any time the database changes its data to remain up to date
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipeListViewModel.recipes.observe(
            viewLifecycleOwner,
            Observer{recipes ->
                recipes?.let {
                    updateUI(recipes)
                }
            }
        )
    }

    //Set the action bar to display the custom menu which has the add button
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.recipe_list, menu)

        val searchItem: MenuItem = menu.findItem(R.id.search_recipe)
        val searchView = searchItem.actionView as SearchView

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    requireActivity().hideKeyboard()
                    searchView.onActionViewCollapsed()
                    updateUI(recipeListViewModel.recipes.value?.filter { recipe -> recipe.title.lowercase().contains(p0?.lowercase() ?: "") } ?: emptyList())
                    return true
                }
                override fun onQueryTextChange(p0: String?): Boolean {
                    updateUI(recipeListViewModel.recipes.value?.filter { recipe -> recipe.title.lowercase().contains(p0?.lowercase() ?: "") } ?: emptyList())
                    return false
                }
            })
        }
    }

    //Create a new recipe and add it to the list whenever the add button is used from the action bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_recipe){
            val recipe = Recipe()
            recipeListViewModel.add(recipe)
            callbacks?.onRecipeSelected(recipe.id)
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    //Method to ensure the recyclerview is displaying the up-to-date information from the ViewModel
    private fun updateUI(recipes: List<Recipe>){
        adapter = RecipeAdapter(recipes)
        recipeRecyclerView.adapter = adapter
    }

    //This class will allow us to update the information in each recipe list item correctly
    private inner class RecipeHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{

        private lateinit var recipe: Recipe

        private val recipeTitle: TextView = itemView.findViewById(R.id.recipe_title)
        private val recipeIcon: ImageView = itemView.findViewById(R.id.recipe_icon)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(recipe: Recipe){ //This is called by the adapter, it sets the appearance of this item in the RecyclerView
            this.recipe = recipe
            recipeTitle.text = this.recipe.title
            recipeIcon.setImageResource(R.drawable.ic_blank_image)
            if(recipe.imagePath.isNotEmpty()){
                val imageFile = File(recipe.imagePath)
                if (imageFile.exists()) recipeIcon.setImageBitmap(BitmapFactory.decodeFile(imageFile.absolutePath))
            }
        }

        override fun onClick(p0: View?) { //This calls a method in the Main Activity to open up a personalized page for the given recipe
            callbacks?.onRecipeSelected(recipe.id)
        }
    }

    //This class is needed to implant the recipes into the recycler view as list items
    private inner class RecipeAdapter(var recipes: List<Recipe>): RecyclerView.Adapter<RecipeHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_recipe, parent, false)
            return RecipeHolder(view)
        }

        override fun getItemCount() = recipes.size

        override fun onBindViewHolder(holder: RecipeHolder, position: Int) {
            val recipe = recipes[position]
            holder.bind(recipe)
        }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }
    private fun Context.hideKeyboard(view: View){
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //Used by other objects to get an instance of this Fragment
    companion object{
        fun newInstance(): RecipeListFragment {
            return RecipeListFragment()
        }
    }
}