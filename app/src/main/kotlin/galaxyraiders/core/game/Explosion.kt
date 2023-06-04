package galaxyraiders.core.game

import galaxyraiders.core.physics.Point2D
import galaxyraiders.core.physics.Vector2D

// const val RADIUS = 3.0
// const val MASS = 1.0
const val MAX_CYCLES = 3

class Explosion(
  position: Point2D,
  radius: Double,
  mass: Double
) : SpaceObject("Explosion", '*', position, Vector2D(0.0, 0.0), radius, mass) {
  var cyclesRemaining = MAX_CYCLES

  fun decreaseCyclesRemaining() {
    this.cyclesRemaining -= 1
  }

  fun explosionEnded(): Boolean {
    return !(this.cyclesRemaining < 0)
  }
}
