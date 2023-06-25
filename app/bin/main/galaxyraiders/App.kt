@file:Suppress("MatchingDeclarationName")
package galaxyraiders

import galaxyraiders.adapters.BasicRandomGenerator
import galaxyraiders.adapters.tui.TextUserInterface
import galaxyraiders.adapters.web.WebUserInterface
import galaxyraiders.core.game.GameEngine
import galaxyraiders.core.game.Score
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
  // val protectionDomain = ::main.javaClass.protectionDomain
  // val codeSource = protectionDomain.codeSource
  // val location = codeSource.location
  // val moduleName = ClassLoader.getSystemClassLoader().javaClass.module.name
  // val moduleName = location.module
  // println("Nome do módulo: $moduleName")

  val generator = BasicRandomGenerator(
    rng = Random(seed = AppConfig.randomSeed)
  )

  val ui = when (AppConfig.operationMode) {
    OperationMode.Text -> TextUserInterface()
    OperationMode.Web -> WebUserInterface()
  }

  val (controller, visualizer) = ui.build()

  val dateTime = LocalDateTime.now() // Obtém o LocalDateTime atual

  val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss") // Define o padrão desejado
  val formattedDateTime = dateTime.format(formatter)

  val path: String = Paths.get("").toAbsolutePath().toString() + "/src/main/kotlin/galaxyraiders/core/score"

  val scoreCount = Score(
    ZERO_DOUBLE, ZERO_INT, formattedDateTime, path
  )

  val gameEngine = GameEngine(
    generator, controller, visualizer, scoreCount
  )

  thread { gameEngine.execute() }

  ui.start()
}
