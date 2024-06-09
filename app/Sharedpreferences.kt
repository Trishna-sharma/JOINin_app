import android.content.Context
import android.content.SharedPreferences

const val PREFS_NAME = "MyPrefs"
const val CATEGORY_PREFIX = "category_"

// Function to get SharedPreferences for a specific category
fun getSharedPreferencesForCategory(context: Context, category: String): SharedPreferences {
    return context.getSharedPreferences("$PREFS_NAME$category", Context.MODE_PRIVATE)
}

// Function to save data to a specific category in SharedPreferences
fun saveDataToCategory(context: Context, category: String, data: String) {
    val prefs = getSharedPreferencesForCategory(context, category)
    val editor = prefs.edit()
    editor.putString(CATEGORY_PREFIX + category, data)
    editor.apply()
}

// Function to retrieve data from a specific category in SharedPreferences
fun getDataFromCategory(context: Context, category: String): String? {
    val prefs = getSharedPreferencesForCategory(context, category)
    return prefs.getString(CATEGORY_PREFIX + category, null)
}