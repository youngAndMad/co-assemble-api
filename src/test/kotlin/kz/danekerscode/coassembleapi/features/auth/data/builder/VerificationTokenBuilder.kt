package kz.danekerscode.coassembleapi.features.auth.data.builder

import kz.danekerscode.coassembleapi.core.data.Builder
import kz.danekerscode.coassembleapi.features.auth.data.entity.VerificationToken
import kz.danekerscode.coassembleapi.features.auth.data.enums.VerificationTokenType
import java.util.*

class VerificationTokenBuilder : Builder<VerificationToken> {
    private var value: String = UUID.randomUUID().toString()
    private var userEmail: String = "keremetuser@gmail.com"
    private var type: VerificationTokenType = VerificationTokenType.MAIL_VERIFICATION
    private var enabled: Boolean = true
    private var isUser: Boolean = false

    override fun build(): VerificationToken {
        return null!! // todo
    }
}