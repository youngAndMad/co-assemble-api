package kz.danekerscode.coassembleapi.features.mail.domain.service.impl

import freemarker.template.Configuration
import freemarker.template.Template
import kz.danekerscode.coassembleapi.features.mail.domain.service.TemplateService
import org.springframework.stereotype.Service

@Service
class TemplateServiceImpl(
    private var ftlConfiguration: Configuration
) : TemplateService {

    override suspend fun getTemplate(templateName: String): Template =
        ftlConfiguration.getTemplate(templateName)

}