package kz.danekerscode.coassembleapi.service.impl

import freemarker.template.Configuration
import freemarker.template.Template
import kz.danekerscode.coassembleapi.service.TemplateService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TemplateServiceImpl(
    private var ftlConfiguration: Configuration
) : TemplateService {

    override fun getTemplate(templateName: String): Mono<Template> =
        Mono.just(ftlConfiguration.getTemplate(templateName))

}