package kz.danekerscode.coassembleapi.features.template.domain.service

import freemarker.template.Template

/**
 * Service for working with Freemarker templates
 * */
interface TemplateService {

    /**
     * Get Freemarker template by name
     * @param templateName name of the template
     * @return the template
     * */
    suspend fun getTemplate(templateName: String): Template

}