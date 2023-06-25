package galaxyraiders.core.game

import galaxyraiders.Config
import galaxyraiders.ports.RandomGenerator
import galaxyraiders.ports.ui.Controller
import galaxyraiders.ports.ui.Controller.PlayerCommand
import galaxyraiders.ports.ui.Visualizer
import kotlin.math.pow
import kotlin.system.measureTimeMillis

const val MILLISECONDS_PER_SECOND: Int = 1000

object GameEngineConfig {
  private val config = Config(prefix = "GR__CORE__GAME__GAME_ENGINE__")

  val frameRate = config.get<Int>("FRAME_RATE")
  val spaceFieldWidth = config.get<Int>("SPACEFIELD_WIDTH")
  val spaceFieldHeight = config.get<Int>("SPACEFIELD_HEIGHT")
  val asteroidProbability = config.get<Double>("ASTEROID_PROBABILITY")
  val coefficientRestitution = config.get<Double>("COEFFICIENT_RESTITUTION")

  val msPerFrame: Int = MILLISECONDS_PER_SECOND / this.frameRate
}

@Suppress("TooManyFunctions")
class GameEngine(
  val generator: RandomGenerator,
  val controller: Controller,
  val visualizer: Visualizer,
  val scoreCount: Score
) {
  val field = SpaceField(
    width = GameEngineConfig.spaceFieldWidth,
    height = GameEngineConfig.spaceFieldHeight,
    generator = generator
  )

  var playing = true

  fun execute() {
    while (true) {
      val duration = measureTimeMillis { this.tick() }

      Thread.sleep(
        maxOf(0, GameEngineConfig.msPerFrame - duration)
      )
    }
  }

  fun execute(maxIterations: Int) {
    repeat(maxIterations) {
      this.tick()
    }
  }

  fun tick() {
    this.processPlayerInput()
    this.updateSpaceObjects()
    this.renderSpaceField()
  }

  fun processPlayerInput() {
    this.controller.nextPlayerCommand()?.also {
      when (it) {
        PlayerCommand.MOVE_SHIP_UP ->
          this.field.ship.boostUp()
        PlayerCommand.MOVE_SHIP_DOWN ->
          this.field.ship.boostDown()
        PlayerCommand.MOVE_SHIP_LEFT ->
          this.field.ship.boostLeft()
        PlayerCommand.MOVE_SHIP_RIGHT ->
          this.field.ship.boostRight()
        PlayerCommand.LAUNCH_MISSILE ->
          this.field.generateMissile()
        PlayerCommand.PAUSE_GAME ->
          this.playing = !this.playing
      }
    }
  }

  fun updateSpaceObjects() {
    if (!this.playing) return
    this.handleCollisions()
    this.moveSpaceObjects()
    this.trimSpaceObjects()
    this.generateAsteroids()
    this.changeCyclesExplosions()
  }

  fun increaseScore(asteroid: SpaceObject) {
    /* Calculate the value of an asteroid destroyed, add this value
    to the Score and update the Scoreboard and the Leaderboard */
    this.scoreCount.score += asteroid.mass.pow(2) / asteroid.radius
    this.scoreCount.asteroidsDestroyed += 1
    this.scoreCount.addToScoreboard()
    this.scoreCount.addToLeaderboard()
  }

  fun changeCyclesExplosions() {
    /* Decreases the lifetime of the explosions */
    for (explosion in this.field.explosions) {
      explosion.decreaseCyclesRemaining()
    }
  }

  fun isMissile(objectt: SpaceObject): Boolean {
    /* Verify if the object is a Missile */
    return (objectt is Missile)
  }

  fun isAsteroid(objectt: SpaceObject): Boolean {
    /* Verify if the object is a Asteroid */
    return (objectt is Asteroid)
  }

  fun missileAndAsteroidToExplosion(missile: SpaceObject, asteroid: SpaceObject) {
    /* Increase the score, generate a Explosion and 'remove' the missile and the asteroid */
    this.increaseScore(asteroid)
    this.field.generateExplosion(asteroid)
    missile.colision = true
    asteroid.colision = true
  }

  fun handleCollisions() {
    /* Verify if a collision ocurred, and generate an Explosion
    if the Objects are a Missile and an Asteroid */
    this.field.spaceObjects.forEachPair {
        (first, second) ->
      if (first.impacts(second)) {
        if (this.isMissile(first) && this.isAsteroid(second)) {
          this.missileAndAsteroidToExplosion(first, second)
        } else if (this.isMissile(second) && this.isAsteroid(first)) {
          this.missileAndAsteroidToExplosion(second, first)
        }
        first.collideWith(second, GameEngineConfig.coefficientRestitution)
      }
    }
  }

  fun moveSpaceObjects() {
    this.field.moveShip()
    this.field.moveAsteroids()
    this.field.moveMissiles()
  }

  fun trimSpaceObjects() {
    this.field.trimAsteroids()
    this.field.trimMissiles()
    this.field.trimExplosions()
  }

  fun generateAsteroids() {
    val probability = generator.generateProbability()

    if (probability <= GameEngineConfig.asteroidProbability) {
      this.field.generateAsteroid()
    }
  }

  fun renderSpaceField() {
    this.visualizer.renderSpaceField(this.field)
  }
}

fun <T> List<T>.forEachPair(action: (Pair<T, T>) -> Unit) {
  for (i in 0 until this.size) {
    for (j in i + 1 until this.size) {
      action(Pair(this[i], this[j]))
    }
  }
}
