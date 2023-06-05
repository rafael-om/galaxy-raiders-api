package galaxyraiders.core.game

import com.google.gson.Gson
import java.io.File
import java.time.LocalDateTime

const val MIN_SCORE = 3

data class Score(
  var score: Double,
  var asteroidsDestroyed: Int,
  var date: String
) {
  // var score: Double = score
  // var asteroidsDestroyed: Int = asteroidsDestroyed

  fun addToScoreboard() {
    val file = File("Scoreboard.json")
    file.createNewFile()
    val jsonContent = file.readText()
    val gson = Gson()
    var existingData: List<Score> = gson.fromJson(jsonContent, Array<Score>::class.java)?.toList() ?: emptyList()
    //val existingData: List<Score> = gson.fromJson(jsonContent, Array<Score>::class.java).toList()
    var updatedData = existingData + this
    val updatedJsonContent = gson.toJson(updatedData)
    file.writeText(updatedJsonContent)
  }

  fun addToLeaderboard() {
    val file = File("Leaderboard.json")
    file.createNewFile()
    val jsonContent = file.readText()
    val gson = Gson()
    val existingData: List<Score> = gson.fromJson(jsonContent, Array<Score>::class.java)?.toList() ?: emptyList()
    //val existingData: List<Score> = gson.fromJson(jsonContent, Array<Score>::class.java).toList()
    var resultData: List<Score> = emptyList()
    var updatedData = existingData + this
    if (updatedData.size > MIN_SCORE) {
      var lowerScore: Double = Double.POSITIVE_INFINITY
      var lowerObj: Score? = null
      for (score in updatedData) {
        if (score.score < lowerScore) {
          lowerScore = score.score
          lowerObj = score
        }
      }
      for (score in updatedData) {
        if (score != lowerObj) {
          resultData = resultData + score
        }
      }
      //updatedData.remove(lowerObj)
    }
    val updatedJsonContent = gson.toJson(resultData)
    file.writeText(updatedJsonContent)
  }
}
