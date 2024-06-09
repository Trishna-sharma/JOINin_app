package com.example.joinin

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: CardAdapter
    private lateinit var cardDataList: ArrayList<CardData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonShowDetails = findViewById<ImageButton>(R.id.buttonShowDetails)
        val btnInfo: ImageButton = findViewById(R.id.btnInfo)
        val btnSearch: ImageButton = findViewById(R.id.btnSearch)
        val btnFirst: ImageButton = findViewById(R.id.btnFirst)
        val btnSecond: ImageButton = findViewById(R.id.btnSecond)

        buttonShowDetails.setOnClickListener { //Onclick retrieving the data's from the shared preferences

            val sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
            val name = sharedPreferences.getString("name", "N/A")
            val age = sharedPreferences.getString("age", "N/A")

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Your Details")
            builder.setMessage("Name: $name\nAge: $age")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val itemSpacingDecoration = ItemSpacingDecoration(spaceHeight = 16)
        recyclerView.addItemDecoration(itemSpacingDecoration)

        cardDataList = ArrayList() //some trending categories are listed here as carddatalist.
        cardDataList.add(CardData(R.drawable.a, "Business", "Explore businesses and know more", "https://chat.whatsapp.com/JexFRSjuI5cEawVgYwdZEj"))
        cardDataList.add(CardData(R.drawable.b, "Movies", "Do you like watching movies!!","https://chat.whatsapp.com/BXmZ5GAnNaPAZlt6GNVqmC"))
        cardDataList.add(CardData(R.drawable.c, "Games", "Gamers ..this group is for you","https://chat.whatsapp.com/Jl8WpOJrtd7APcxtxXrXIG"))
        cardDataList.add(CardData(R.drawable.d, "Fashion", "Would you like to learn more about designing?","https://chat.whatsapp.com/KJdBe8tsSrQKIN6q8SQDBm"))
        cardDataList.add(CardData(R.drawable.e, "Cooking", "Daily posts and recipes are available","https://chat.whatsapp.com/Iph0VTaHSAC2DIVJ580bTG"))
        cardDataList.add(CardData(R.drawable.f, "Vlog", "Day to day life of vloggers","https://chat.whatsapp.com/LZ0rmhPPTZt4Uc5MXpWBKw"))
        cardDataList.add(CardData(R.drawable.make, "Makeup", "Makeup enhances beauty","https://chat.whatsapp.com/KHezaEJ2Ivu34HZbgP51Oc"))
        cardDataList.add(CardData(R.drawable.gardening, "Gardening ", "Gardening is my passion .","https://chat.whatsapp.com/LE2Ow6PFdb3C2MlPk0riDs"))
        cardDataList.add(CardData(R.drawable.h, "Crypto", "Crypto,Bitcoin,NFT,make money online","https://chat.whatsapp.com/GduAH0gHwei9CJeHOyCm0L"))
        cardDataList.add(CardData(R.drawable.books, "Books", "Book worms this group is for you.","https://chat.whatsapp.com/Keq2vN3qUXm2KWrcSXEQCX"))
        cardDataList.add(CardData(R.drawable.g, "Beauty/SKIN care", "Beauty comes with care","https://chat.whatsapp.com/Ee3vmejfKve4POePzQ1Hxn")) // Add other card data here...

        adapter = CardAdapter(cardDataList, this)
        recyclerView.adapter = adapter


        btnInfo.setOnClickListener {
            showInformationDialog() //handles the information pop up
        }


        btnSearch.setOnClickListener {
            showSearchDialog() //handles the search functionality
        }


        btnFirst.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish() //Onclick starts the mainactivity
        }

        btnSecond.setOnClickListener {
            startActivity(Intent(this, Categories::class.java))
            finish() //Onclick starts the category activity
        }

    }

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

    private fun handleSearch(query: String) {
        val filteredList = cardDataList.filter { cardData ->
            cardData.title.contains(query, ignoreCase = true) || cardData.description.contains(query, ignoreCase = true)
        }
        adapter.updateData(filteredList)
    }

    data class CardData(val imageResource: Int, val title: String, val description: String, val whatsappGroupLink: String)

    inner class CardAdapter(private var cardDataList: MutableList<CardData>, private val context: Context) :
        RecyclerView.Adapter<CardAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_row, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val cardData = cardDataList[position]
            holder.bind(cardData)
            holder.joinButton.setOnClickListener {

                val whatsappGroupLink = cardData.whatsappGroupLink
                launchWhatsAppGroup(whatsappGroupLink)
            }
        }

        override fun getItemCount(): Int {
            return cardDataList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val imageView: ImageView = itemView.findViewById(R.id.imageView)
            private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
            private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
            val joinButton: ImageButton = itemView.findViewById(R.id.joinButton)
            val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

            fun bind(cardData: CardData) {
                imageView.setImageResource(cardData.imageResource)
                titleTextView.text = cardData.title
                descriptionTextView.text = cardData.description

                deleteButton.setOnClickListener { //deletes the card data
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        cardDataList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, cardDataList.size)
                    }
                }
            }
        }

        fun updateData(newData: List<CardData>) { //update the card list data
            cardDataList.clear()
            cardDataList.addAll(newData)
            notifyDataSetChanged()
        }

        private fun launchWhatsAppGroup(link: String) { //retrive the whatsapp group link

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link)
            context.startActivity(intent)
        }
    }

    class ItemSpacingDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() { // to decorate (create space between) the card data view items

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = spaceHeight
        }
    }
}
