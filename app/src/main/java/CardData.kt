

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.joinin.R


data class UserInputData( //works to pass the data using the parcel from one activity to another
    val imageResource: Int,
    val title: String,
    val description: String,
    val whatsappGroupLink: String
)

data class CardData (val imageResource: Int,
val title: String,
val description: String,
val whatsappGroupLink: String)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(imageResource)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(whatsappGroupLink)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardData> {
        override fun createFromParcel(parcel: Parcel): CardData {
            return CardData(parcel)
        }

        override fun newArray(size: Int): Array<CardData?> {
            return arrayOfNulls(size)
        }
    }
}

class CardAdapter(private var cardDataList: List<CardData>, private val context: Context) :
    RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_row, parent, false)
        return ViewHolder(itemView)
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

        fun bind(cardData: CardData) {
            imageView.setImageResource(cardData.imageResource)
            titleTextView.text = cardData.title
            descriptionTextView.text = cardData.description
        }
    }

    fun updateData(newData: List<CardData>) { //update data
        cardDataList = newData
        notifyDataSetChanged()
    }

    private fun launchWhatsAppGroup(link: String) { //retrieve whatsapp group links
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(link)
        context.startActivity(intent)
    }
}
