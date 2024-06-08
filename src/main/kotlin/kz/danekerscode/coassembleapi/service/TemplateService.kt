package kz.danekerscode.coassembleapi.service

import freemarker.template.Template
import reactor.core.publisher.Mono

/**
 * Service for working with Freemarker templates
 * */
interface TemplateService {

    /**
     * Get Freemarker template by name
     * @param templateName name of the template
     * @return Mono with the template
     * */
    fun getTemplate(templateName: String): Mono<Template>

}