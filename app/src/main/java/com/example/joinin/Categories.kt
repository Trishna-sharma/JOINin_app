package com.example.joinin

import CardAdapter
import CardData
import UserInputData
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Categories : AppCompatActivity(), InputDialogFragment.InputModalListener {

    private lateinit var categorySpinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var cardAdapter: CardAdapter
    private val cardDataMap: MutableMap<String, MutableList<CardData>> = mutableMapOf()
    private var categoryButtonPairs: MutableList<Pair<Int, String>> = mutableListOf()
    private lateinit var categoryButtonsRecyclerView: RecyclerView
    private lateinit var adapter: CategoryButtonsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        recyclerView = findViewById(R.id.categoryButtonsRecyclerView)
        cardAdapter = CardAdapter(emptyList(), this)
        recyclerView.adapter = cardAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        categoryButtonPairs = getCategoryButtons().toMutableList()
        categoryButtonsRecyclerView = findViewById(R.id.categoryButtonsRecyclerView)
        categoryButtonsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = CategoryButtonsAdapter(categoryButtonPairs)
        categoryButtonsRecyclerView.adapter = adapter


        val inputModalView = layoutInflater.inflate(R.layout.input_modal, null)
        val categorySpinner: Spinner = inputModalView.findViewById(R.id.categorySpinner)
        val initialCardDataList = generateCardDataListForCategory("Books")

        val btnInfo: ImageButton = findViewById(R.id.btnInfo)
        val btnFirst: ImageButton = findViewById(R.id.btnFirst)
        val btnSecond: ImageButton = findViewById(R.id.btnSecond)
        val btnSearch: ImageButton = findViewById(R.id.btnSearch)
        val add: ImageButton = findViewById(R.id.add)
        val categories = getCategoryButtons().map { it.second }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        cardAdapter.updateData(initialCardDataList)

        btnSearch.setOnClickListener {
            showSearchDialog()  // On click calls the search function
        }

        btnInfo.setOnClickListener {
            showInformationDialog() //On click calls the information pop up function
        }

        btnFirst.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)) //Onclick opens the mainactivity
            finish()
        }

        btnSecond.setOnClickListener {
            startActivity(Intent(this, Categories::class.java))
            finish() //Onclick opens the category activity
        }

        add.setOnClickListener {
            val inputDialog = InputDialogFragment()
            inputDialog.setInputModalListener(this)
            inputDialog.show(supportFragmentManager, "InputDialog") //onclick displays a modal with the informations to be entered by the user.
        }

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                updateRecyclerView(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    override fun onSubmitButtonClicked(link: String, title: String, description: String, category: String) { //when user wants to add new data in the app
        val message = "Submitting card data...\nGroup Title: $title\nDescription: $description\nCategory: $category"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        val cardData = CardData(R.drawable.success, title, description, link)
        val cardList = cardDataMap.getOrPut(category) { mutableListOf() }
        cardList.add(cardData)
        updateRecyclerView(category)
    }

    private fun updateRecyclerView(category: String) { //to update the card data's after taking the user input.
        val cardList = cardDataMap[category] ?: emptyList()
        cardAdapter.updateData(cardList)
    }


    private fun getCategoryButtons(): List<Pair<Int, String>> { // the main category buttons to take all my sub categories,paired with images and titles
        return listOf(
            Pair(R.drawable.books, "Books"),
            Pair(R.drawable.h, "Crypto"),
            Pair(R.drawable.a, "Business"),
            Pair(R.drawable.makeup, "Makeup"),
            Pair(R.drawable.g, "Beauty"),
            Pair(R.drawable.e, "Cooking"),
            Pair(R.drawable.c, "Games"),
            Pair(R.drawable.d, "Fashion"),
            Pair(R.drawable.f, "Vlogs"),
            Pair(R.drawable.b, "Movies")
        )
    }

    inner class CategoryButtonsAdapter(private var categoryButtonPairs: List<Pair<Int, String>>) :
        RecyclerView.Adapter<CategoryButtonsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.category_buttons, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val categoryButtonPair = categoryButtonPairs[position]
            holder.bind(categoryButtonPair)
        }

        override fun getItemCount(): Int {
            return categoryButtonPairs.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val categoryImageButton: ImageButton = itemView.findViewById(R.id.categoryImageButton)
            private val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryTextView)

            init {

                categoryImageButton.setOnClickListener {
                    val category = categoryButtonPairs[adapterPosition].second
                    openContentActivity(category, generateCardDataListForCategory(category))
                }
            }

            fun bind(pair: Pair<Int, String>) {
                val (imageResource, categoryName) = pair
                Glide.with(itemView.context)
                    .load(imageResource)
                    .into(categoryImageButton)
                categoryNameTextView.text = categoryName
            }
        }

        fun updateData(newList: List<Pair<Int, String>>) {
            categoryButtonPairs = newList
            notifyDataSetChanged()
        }
    }



    private fun generateCardDataListForCategory(category: String, userInputData: UserInputData? = null): List<CardData> {
        val cardDataList = mutableListOf<CardData>()

        when (category) { //holds all my sub-categories and displays them as per user click.
            "Books" -> {
                cardDataList.add(CardData(R.drawable.books, "Your BOOKS", "Do you love books? Join us, this group is for you.", "https://chat.whatsapp.com/0AL1QonTrhX1jPASSxUUEr"))
                cardDataList.add(CardData(R.drawable.success, "Book is life", "No description", "https://chat.whatsapp.com/JexFRSjuI5cEawVgYwdZEj"))
            }
            "Crypto" -> {
                cardDataList.add(CardData(R.drawable.crypto, "Investment", "Invest and earn more", "https://chat.whatsapp.com/invite/6WZAFy0jhj9DnhRWVmZlU3"))
                cardDataList.add(CardData(R.drawable.h, "Crypto Stop Signals 786", "Crypto is passion", "https://chat.whatsapp.com/FfJfOI6y1xB0v7TjeD2Ywi"))
            }
            "Business" -> {
                cardDataList.add(CardData(R.drawable.tech, "Home Based Job for Women", "Join us and earn more!", "https://chat.whatsapp.com/CCaF4rpm4wpGtadXsL3Zjq"))
                cardDataList.add(CardData(R.drawable.f, "Forage Earning", "we are here to earn", "https://chat.whatsapp.com/invite/22JL7Yil0b6D1AtmTf2P87"))
                cardDataList.add(CardData(R.drawable.h, "Business Group", "Join in if interested", "https://chat.whatsapp.com/invite/GElOC4tAjys9UQ5ipGW1CD"))
                cardDataList.add(CardData(R.drawable.grow, "Earning with Irtaza", "....", "https://chat.whatsapp.com/invite/GElOC4tAjys9UQ5ipGW1CD"))
            }
            "Makeup" -> {
                cardDataList.add(CardData(R.drawable.makeup, "Brand Beauty", "We share makeup ideas and sell makeup", "https://chat.whatsapp.com/L1h1p2ADEHaFUC0oWxRQiX"))
                cardDataList.add(CardData(R.drawable.make, "Mishti Creations", "Be bold with makeup", "https://chat.whatsapp.com/invite/LVl1QUayush8NaZDJ7VYT8"))
            }
            "Beauty" -> {
                cardDataList.add(CardData(R.drawable.fashion, "Makeup and Adah", "..>!!!!", "https://chat.whatsapp.com/invite/HUuXptgEs532XQzZQe2PEU"))
                cardDataList.add(CardData(R.drawable.success, "Ekyzglam", "", "https://chat.whatsapp.com/BcdcjwgxAzRBAZKtaipPWJ"))
                cardDataList.add(CardData(R.drawable.success, "The fashion world", "", "https://chat.whatsapp.com/invite/LASPduLYkBz5tB7horkMf0"))
            }
            "Cooking" -> {
                cardDataList.add(CardData(R.drawable.village, "Village Food Experts", "Kerala", "https://chat.whatsapp.com/invite/5qJ3O60nlh8737qSuUGY2D"))
                cardDataList.add(CardData(R.drawable.manbhashi, "Chef Manbhashi Guruji", "Kooc", "https://chat.whatsapp.com/invite/CGzYMh0y23N5Hu3pGI450Q"))
                cardDataList.add(CardData(R.drawable.chocolate, "Labdhi Chocolates", "Labhdi Chocolates", "https://chat.whatsapp.com/invite/ChYUqIld3AbFBCyBdaLc6D"))
                cardDataList.add(CardData(R.drawable.chefjob, "Chef Job Worldwide", "Get a job as CHEF!!", "https://chat.whatsapp.com/invite/EhDo9JYUt1A7L nWtlZrro"))
                cardDataList.add(CardData(R.drawable.chef, "Cook with me", "Group for cookers", "https://chat.whatsapp.com/invite/3YciytU8MRELOUG1hJCwAJ"))
            }
            "Games" -> {
                cardDataList.add(CardData(R.drawable.pubg, "PUBG Mobiles", "Play Pubg", "https://chat.whatsapp.com/Dj3BOjADEgoABqGzlh3wzH"))
                cardDataList.add(CardData(R.drawable.ryl, "Wixzerd GAME", "...", "https://chat.whatsapp.com/GgJWo4MJRX0LBcdRCQAvLT"))
                cardDataList.add(CardData(R.drawable.ghost, "Kourav Gaming", "Join in if interested", "https://chat.whatsapp.com/G4R2JxNzvDrH565OYIjzWK"))
            }
            "Fashion" -> {
                cardDataList.add(CardData(R.drawable.make, "PAKZONE store", "Shop your favourite products here", "https://chat.whatsapp.com/LBLo5ZKr9Kd36C4qNMngLQ"))
                cardDataList.add(CardData(R.drawable.fashion, "Faiza Fashion Clothing", ">-<", "https://chat.whatsapp.com/J871cyrByBtCOb2X0rKTbp"))
            }
            "Vlogs" -> {
                cardDataList.add(CardData(R.drawable.grow, "Ougoz", "Day to day life stories", "https://chat.whatsapp.com/invite/IkYYA9ayU54HvkTI0Pv7tb"))
            }
            "Movies" -> {
                cardDataList.add(CardData(R.drawable.funny, "Funny Group", "Fun everywhere", "https://chat.whatsapp.com/invite/34b7u9B2EDEJD6FxYnsGWG"))
                cardDataList.add(CardData(R.drawable.success, "Stream India", "get the new released movies webiste links here", "https://chat.whatsapp.com/invite/FQx21w4Rtab7ZOSmplSINt"))
            }
            else -> {
                // Add other categories
            }
        }

        userInputData?.let {
            val newCardData = CardData(it.imageResource, it.title, it.description, it.whatsappGroupLink)
            cardDataList.add(newCardData)
        }

        return cardDataList
    }

    // Handles the user click and takes initiative to move the app to the next activity
    private fun openContentActivity(category: String, cardDataList: List<CardData>) {
        Toast.makeText(this, "Clicked category: $category", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Content::class.java).apply {
            putExtra("category", category)
            putParcelableArrayListExtra("cardDataList", ArrayList(cardDataList))
        }
        startActivity(intent)
    }

    //handles the pop up for instructing on how the app works.

    private fun showInformationDialog() {
        val infoBoardView = layoutInflater.inflate(R.layout.overlay_info, null)

        val titleTextView: TextView = infoBoardView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = infoBoardView.findViewById(R.id.descriptionTextView)
        titleTextView.text = "Information"
        descriptionTextView.text = "Look for your desired interest and join in groups and learn more."

        val dialog = AlertDialog.Builder(this)
            .setView(infoBoardView)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }


    //handles the search

    private fun showSearchDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Search")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("Search") { dialog, _ ->
            val query = input.text.toString().trim()
            handleSearch(query)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    //handles the search query of the user

    private fun handleSearch(query: String) {
        if (::adapter.isInitialized) {
            val filteredList = categoryButtonPairs.filter { (_, categoryName) ->
                categoryName.contains(query, ignoreCase = true)
            }
            adapter.updateData(filteredList)
        } else {
            Log.e("Categories", "Adapter not initialized")
        }
    }
}

