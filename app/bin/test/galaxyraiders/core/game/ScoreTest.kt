package galaxyraiders.core.game

import com.google.gson.Gson
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

@DisplayName("Testing json File saving and Score")
class ScoreTest {
  private val score = Score(
    score = 10.0,
    asteroidsDestroyed = 3,
    date = "03/03/2023",
    path = Paths.get("").toAbsolutePath().toString() + "/src/test/kotlin/galaxyraiders/core/score"
  )

  @Test
  fun `add to Scoreboard working`() {
    /* Try to add an object to the Scoreboard.json file and check if the object was really added */
    var file = File(score.path + "/Scoreboard.json")
    file.writeText("")

    var jsonContent = file.readText()

    val gson = Gson()

    val oldData: List<Score> = gson.fromJson(jsonContent, Array<Score>::class.java)?.toList() ?: emptyList()
    val expectedData = oldData + score

    score.addToScoreboard()

    val newContent = file.readText()
    val savedData: List<Score> = gson.fromJson(newContent, Array<Score>::class.java)?.toList() ?: emptyList()
    assertEquals(expectedData, savedData)
  }

  @Test
  fun `add to Leaderboard working`() {
    /* Try to successively add new objects to Leaderboard.json 
    and check that the Scores are being sorted and keep only the top 3 */
    var file: File = File(score.path + "/Leaderboard.json")
    file.writeText("")

    var gson: Gson = Gson()

    // One element
    score.addToLeaderboard()
    var fileContent: String = file.readText()
    var fileData: List<Score> = gson.fromJson(fileContent, Array<Score>::class.java)?.toList() ?: emptyList()
    assertEquals(score, fileData.first())

    // Two elements
    score.score = 30.0
    score.addToLeaderboard()
    fileContent = file.readText()
    fileData = gson.fromJson(fileContent, Array<Score>::class.java)?.toList() ?: emptyList()
    assertEquals(score, fileData.first())

    // Three elements
    score.score = 20.0
    score.addToLeaderboard()
    fileContent = file.readText()
    fileData = gson.fromJson(fileContent, Array<Score>::class.java)?.toList() ?: emptyList()
    assertEquals(score, fileData.elementAt(1))

    // 4+ elements
    score.score = 15.0
    score.addToLeaderboard()
    fileContent = file.readText()
    fileData = gson.fromJson(fileContent, Array<Score>::class.java)?.toList() ?: emptyList()
    assertEquals(fileData.last(), score)
  }
}
