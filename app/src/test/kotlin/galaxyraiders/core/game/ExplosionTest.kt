package galaxyraiders.core.game

import galaxyraiders.core.physics.Point2D
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("Given an missile")
class ExplosionTest {
  private val explosion = Explosion(
    position = Point2D(1.0, 1.0),
    radius = 1.0,
    mass = 1.0
  )

  @Test
  fun `it has a type Explosion `() {
    assertEquals("Explosion", explosion.type)
  }

  @Test
  fun `it has a symbol asterisk `() {
    assertEquals('*', explosion.symbol)
  }

  @Test
  fun `it shows the type Explosion when converted to String `() {
    assertTrue(explosion.toString().contains("Explosion"))
  }
}
