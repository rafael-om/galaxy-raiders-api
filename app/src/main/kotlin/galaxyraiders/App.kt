@file:Suppress("MatchingDeclarationName")
package galaxyraiders

import galaxyraiders.adapters.BasicRandomGenerator
import galaxyraiders.adapters.tui.TextUserInterface
import galaxyraiders.adapters.web.WebUserInterface
import galaxyraiders.core.game.GameEngine
import galaxyraiders.core.game.Score
import java.time.LocalDateTime
import kotlin.concurrent.thread
import kotlin.random.Random

const val ZERO_DOUBLE = 0.0
const val ZERO_INT = 0

object AppConfig {
  val config = Config("GR__APP__")

  val randomSeed = config.get<Int>("RANDOM_SEED")
  val operationMode = config.get<OperationMode>("OPERATION_MODE")
}

fun main() {
  val generator = BasicRandomGenerator(
    rng = Random(seed = AppConfig.randomSeed)
  )

  val ui = when (AppConfig.operationMode) {
    OperationMode.Text -> TextUserInterface()
    OperationMode.Web -> WebUserInterface()
  }

  val (controller, visualizer) = ui.build()

  val scoreCount = Score(
    ZERO_DOUBLE, ZERO_INT, LocalDateTime.now()
  )

  val gameEngine = GameEngine(
    generator, controller, visualizer, scoreCount
  )

  thread { gameEngine.execute() }

  scoreCount.addToScoreboard()
  scoreCount.addToLeaderboard()

  ui.start()
}
