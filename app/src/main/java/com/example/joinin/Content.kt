package com.example.joinin

import CardData
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Content : AppCompatActivity(){

    private lateinit var adapter: CardDataAdapter
    private var selectedCategory: String? = null
    private val categoryButtonPairs: MutableList<Pair<Int, String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)

        val buttonBack = findViewById<ImageButton>(R.id.Buttonback)
        val buttonShowDetails = findViewById<ImageButton>(R.id.buttonShowDetails)
        val receivedCardDataList = intent.getParcelableArrayListExtra<CardData>("cardDataList")
        val btnFirst: ImageButton = findViewById(R.id.btnFirst)
        val btnSecond: ImageButton = findViewById(R.id.btnSecond)
        val btnSearch: ImageButton = findViewById(R.id.btnSearch)
        val btnInfo: ImageButton = findViewById(R.id.btnInfo)

        adapter = CardDataAdapter(mutableListOf())
        adapter.setData(receivedCardDataList ?: emptyList())

        buttonBack.setOnClickListener { // Onclick returns back to the previous page
            onBackPressed()
        }

        buttonShowDetails.setOnClickListener {
                  //Retrieving data from shared preferences
            val sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
            val name = sharedPreferences.getString("name", "N/A")
            val age = sharedPreferences.getString("age", "N/A")

            val builder = android.app.AlertDialog.Builder(this)
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
        recyclerView.adapter = adapter

        val itemSpacingDecoration = ItemSpacingDecoration(spaceHeight = 16)
        recyclerView.addItemDecoration(itemSpacingDecoration) //used to create space between my card datas


        btnFirst.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish() //onclick opens the mainactivity
        }


        btnSecond.setOnClickListener {
            startActivity(Intent(this, Categories::class.java))
            finish()//onclick opens the category activity
        }


        btnSearch.setOnClickListener {
            showSearchDialog()//onclick calls the search function
        }


        btnInfo.setOnClickListener {
            showInfoDialog() // onclick calls the information pop up function
        }
    }


    private fun showSearchDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Search")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("Search") { dialog, _ ->
            val query = input.text.toString()
            performSearch(query)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun performSearch(query: String) { //handles the search function
        val filteredList = adapter.getData().filter { cardData ->
            cardData.title.contains(query, ignoreCase = true) ||
                    cardData.description.contains(query, ignoreCase = true)
        }
        adapter.setData(filteredList)
    }

    private fun showInfoDialog() { //handles the information pop up
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

    inner class CardDataAdapter(private var cardDataList: MutableList<CardData>) :
        RecyclerView.Adapter<CardDataAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
            private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
            private val joinButton: ImageButton = itemView.findViewById(R.id.joinButton)
            private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
            private val imageView: ImageView = itemView.findViewById(R.id.imageView)

            init {

                joinButton.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val cardData = cardDataList[position]
                        launchWhatsAppGroup(itemView.context, cardData.whatsappGroupLink) //user can join any whatsapp group of their choice using join button
                    }
                }

                deleteButton.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        cardDataList.removeAt(position)
                        notifyItemRemoved(position) //deletes the card data
                    }
                }
            }

            fun bind(cardData: CardData) {
                titleTextView.text = cardData.title
                descriptionTextView.text = cardData.description
                imageView.setImageResource(cardData.imageResource)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_row, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(cardDataList[position])
        }

        override fun getItemCount(): Int {
            return cardDataList.size
        }

        fun setData(newData: List<CardData>) {
            cardDataList.clear()
            cardDataList.addAll(newData)
            notifyDataSetChanged()
        }

        fun getData(): List<CardData> {
            return cardDataList
        }

        private fun launchWhatsAppGroup(context: Context, whatsappGroupLink: String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappGroupLink))
            context.startActivity(intent)
        }
    }
}

class ItemSpacingDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = spaceHeight
    }
}

