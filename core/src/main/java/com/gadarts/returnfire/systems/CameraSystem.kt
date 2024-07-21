package com.gadarts.returnfire.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.gadarts.returnfire.Managers
import com.gadarts.returnfire.components.ComponentsMapper
import com.gadarts.returnfire.components.PlayerComponent
import com.gadarts.returnfire.systems.events.SystemEvents

class CameraSystem : GameEntitySystem() {


    private var cameraTarget = Vector3()

    override val subscribedEvents: Map<SystemEvents, HandlerOnEvent> = emptyMap()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        gameSessionData.camera.update()
        followPlayer()
    }

    override fun resume(delta: Long) {
    }

    override fun dispose() {
    }

    override fun initialize(gameSessionData: GameSessionData, managers: Managers) {
        super.initialize(gameSessionData, managers)
        initializeCamera()
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
    }

    private fun followPlayer() {
        val player = gameSessionData.gameSessionDataEntities.player
        val transform =
            ComponentsMapper.modelInstance.get(player).gameModelInstance.modelInstance.transform
        val playerPosition = transform.getTranslation(auxVector3_1)
        val playerComp = ComponentsMapper.player.get(player)
        followPlayerRegularMovement(playerComp, playerPosition)
    }

    private fun followPlayerRegularMovement(
        playerComp: PlayerComponent,
        playerPosition: Vector3
    ) {
        if (gameSessionData.runsOnMobile) {
            playerComp.getCurrentVelocity(auxVector2).nor().setLength2(5F)
        } else {
            auxVector2.set(1F, 0F).setAngleDeg(
                ComponentsMapper.modelInstance.get(gameSessionData.gameSessionDataEntities.player).gameModelInstance.modelInstance.transform.getRotation(
                    auxQuat
                ).yaw
            )
        }
        cameraTarget =
            playerPosition.add(auxVector2.x, 0F, -auxVector2.y + Z_OFFSET)
        cameraTarget.y = gameSessionData.camera.position.y
        gameSessionData.camera.position.interpolate(cameraTarget, 0.2F, Interpolation.exp5)
    }


    private fun initializeCamera() {
        gameSessionData.camera.near = NEAR
        gameSessionData.camera.far = FAR
        gameSessionData.camera.update()
        val get = ComponentsMapper.modelInstance.get(gameSessionData.gameSessionDataEntities.player)
        val playerPosition = get.gameModelInstance.modelInstance.transform.getTranslation(
            auxVector3_1
        )
        gameSessionData.camera.position.set(playerPosition.x, INITIAL_Y, playerPosition.z + Z_OFFSET)
        gameSessionData.camera.rotate(Vector3.X, -45F)
        gameSessionData.camera.lookAt(
            playerPosition
        )
    }

    companion object {
        private const val NEAR = 0.1F
        private const val FAR = 300F
        private const val INITIAL_Y = 8F
        private const val Z_OFFSET = 2F
        private val auxVector2 = Vector2()
        private val auxVector3_1 = Vector3()
        private val auxQuat = Quaternion()
    }

}
