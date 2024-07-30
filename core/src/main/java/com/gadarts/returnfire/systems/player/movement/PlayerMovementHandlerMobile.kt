package com.gadarts.returnfire.systems.player.movement

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.gadarts.returnfire.components.ComponentsMapper
import com.gadarts.returnfire.model.GameMap


class PlayerMovementHandlerMobile : PlayerMovementHandler() {
    private val desiredDirection = Vector2()
    private lateinit var camera: PerspectiveCamera
    private var desiredDirectionChanged: Boolean = false


    override fun thrust(player: Entity, directionX: Float, directionY: Float, reverse: Boolean) {
        if (directionX != 0F || directionY != 0F) {
            updateDesiredDirection(directionX, directionY)
        }
    }

    private fun updateDesiredDirection(directionX: Float, directionY: Float) {
        desiredDirectionChanged = true
        desiredDirection.set(directionX, directionY)
    }

    override fun update(
        player: Entity,
        deltaTime: Float,
        currentMap: GameMap,
        dispatcher: MessageDispatcher
    ) {
        val rigidBody = ComponentsMapper.physics.get(player).rigidBody
        val forward =
            rigidBody.worldTransform.getRotation(auxQuaternion)
                .transform(auxVector3.set(1F, 0F, 0F))

        if (!desiredDirection.isZero && !MathUtils.isEqual(
                auxQuaternion.yaw + (if (auxQuaternion.yaw >= 0) 0F else 360F),
                desiredDirection.angleDeg(),
                1F
            )
        ) {
            val diff = desiredDirection.angleDeg() - auxQuaternion.yaw
            val negativeRotation = auxVector2.set(1F, 0F).setAngleDeg(diff).angleDeg() > 180
            rotate(rigidBody, if (negativeRotation) -1 else 1)
        } else {
            desiredDirection.setZero()
            rigidBody.angularVelocity = auxVector3.setZero()
        }
    }

    override fun applyRotation(clockwise: Int) {

    }

    override fun reverse(player: Entity) {

    }


    override fun onTouchUp(keycode: Int) {

    }


    override fun initialize(camera: PerspectiveCamera) {
        this.camera = camera
    }

    companion object {
        private val auxVector3 = Vector3()
        private val auxVector2 = Vector2()
        private val auxQuaternion = Quaternion()
    }
}
