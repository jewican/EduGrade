import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Subject(
    val name: String,
    val assessmentTypes: List<AssessmentType>,
    val overallGrade: Double = 0.0
)

data class AssessmentType(
    val name: String,
    val grade: Double,
    val weight: Double
)

class SubjectStorage(private val context: Context) {
    private val gson = Gson()
    private val filename = "subjects.json"

    fun saveSubjects(subjects: List<Subject>) {
        val jsonString = gson.toJson(subjects)
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }

    fun loadSubjects(): List<Subject> {
        return try {
            val jsonString = context.openFileInput(filename).bufferedReader().use {
                it.readText()
            }
            val type = object : TypeToken<List<Subject>>() {}.type
            gson.fromJson(jsonString, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}