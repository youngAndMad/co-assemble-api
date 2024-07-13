package kz.danekerscode.coassembleapi.core.representation.websocket

import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.user.representation.event.UserConnectionStateChangeEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class CoAssembleTextWebSocketHandler(
    private val eventBus: ApplicationEventPublisher
) : TextWebSocketHandler() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun afterConnectionEstablished(
        session: WebSocketSession
    ) {
        val currentUser = session.principal!! // principal is not nullable. Because it is secured by spring security

        if (currentUser !is CoAssembleUserDetails) {
            log.error("User is not instance of CoAssembleUserDetails ${currentUser.name}. Closing connection.")
            session.close(CloseStatus.NOT_ACCEPTABLE)
            return
        }

        publishUserStateChangeEvent(currentUser, true)
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        log.error("Error occurred in connection: ${session.principal?.name}", exception)

        super.handleTransportError(session, exception)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val currentUser =
            session.principal!! as CoAssembleUserDetails // principal is not nullable. Because it is secured by spring security

        log.info("Connection [${currentUser.username}] closed with status: $status")

        publishUserStateChangeEvent(currentUser, false)
    }


    /**
     * Publishes an event to notify the application that a user has connected or disconnected
     * @author Daneker
     * 13.07.2024
     */
    private fun publishUserStateChangeEvent(
        currentUser: CoAssembleUserDetails,
        online: Boolean
    ) {
        eventBus.publishEvent(
            UserConnectionStateChangeEvent(
                userId = currentUser.user.id!!,
                online = online
            )
        )
    }
}
