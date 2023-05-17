@file:Suppress("UNUSED_PARAMETER") // <- REMOVE
package galaxyraiders.core.physics

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.sqrt
import java.util.Locale

const val ANGLE = 180.0

@JsonIgnoreProperties("unit", "normal", "degree", "magnitude")
data class Vector2D(val dx: Double, val dy: Double) {
  override fun toString(): String {
    return "Vector2D(dx=$dx, dy=$dy)"
  }

  val magnitude: Double
    get() = sqrt(this.dx * this.dx + this.dy * this.dy)

  val radiant: Double
    get() {
      val tan = this.dy / this.dx
      var angle = atan(tan)
      // Arco tangente está definido entre -PI/2 e +PI/2
      // Se ângulo está no 3° quadrante é mandando para o 1°
      if (tan > 0 && this.dx < 0) return (angle - PI)
      // Se ângulo está no 2° quadrante é mandando para o 4°
      else if (tan < 0 && this.dx < 0) return (angle + PI)
      return angle
    }

  val degree: Double
    get() = this.radiant * ANGLE / PI

  val unit: Vector2D
    get() = this.div(this.magnitude)

  val normal: Vector2D
    get() = Vector2D(this.unit.dy, -this.unit.dx)

  operator fun times(scalar: Double): Vector2D {
    var new_dx: Double = this.dx * scalar
    var new_dy: Double = this.dy * scalar
    return Vector2D(new_dx, new_dy)
  }

  operator fun div(scalar: Double): Vector2D {
    var new_dx: Double = this.dx / scalar
    var new_dy: Double = this.dy / scalar
    return Vector2D(new_dx, new_dy)
  }

  operator fun times(v: Vector2D): Double {
    return v.dx * this.dx + v.dy * this.dy
  }

  operator fun plus(v: Vector2D): Vector2D {
    var new_dx: Double = this.dx + v.dx
    var new_dy: Double = this.dy + v.dy
    return Vector2D(new_dx, new_dy)
  }

  operator fun plus(p: Point2D): Point2D {
    var new_x: Double = p.x + this.dx
    var new_y: Double = p.y + this.dy
    return Point2D(new_x, new_y)
  }

  operator fun unaryMinus(): Vector2D {
    var new_dx: Double = -this.dx
    var new_dy: Double = -this.dy
    return Vector2D(new_dx, new_dy)
  }

  operator fun minus(v: Vector2D): Vector2D {
    var new_dx: Double = this.dx - v.dx
    var new_dy: Double = this.dy - v.dy
    return Vector2D(new_dx, new_dy)
  }

  fun scalarProject(target: Vector2D): Double {
    return this.times(target) / target.magnitude
  }

  fun vectorProject(target: Vector2D): Vector2D {
    var coef: Double = this.scalarProject(target)/target.magnitude
    val vector: Vector2D = target.times(coef)
    // Está ocorrendo um erro em um dos testes por causa de arredondamento e 
    //um colega (Vitor de Melo) conseguiu resolver com o código abaixo:
    val result = Vector2D(
      String.format(Locale.US, "%.2f",vector.dx).toDouble(),
      String.format(Locale.US, "%.2f",vector.dy).toDouble()
    )
    return result
  }
}

operator fun Double.times(v: Vector2D): Vector2D {
  var new_dx: Double = this * v.dx
  var new_dy: Double = this * v.dy
  return Vector2D(new_dx, new_dy)
}
