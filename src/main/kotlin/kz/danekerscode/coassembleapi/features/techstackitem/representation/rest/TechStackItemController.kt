package kz.danekerscode.coassembleapi.features.techstackitem.representation.rest

import kz.danekerscode.coassembleapi.features.techstackitem.representation.dto.TechStackItemDto
import kz.danekerscode.coassembleapi.features.techstackitem.domain.service.TechStackItemService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tech-stack-items")
class TechStackItemController(
    private val techStackItemService: TechStackItemService
) {

    @GetMapping
    suspend fun allTechStackItems() = techStackItemService.findAll()

    @GetMapping("{id}")
    suspend fun techStackItemById(@PathVariable id: String) = techStackItemService.findById(id)

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    suspend fun createTechStackItem(@RequestBody @Validated techStackItem: TechStackItemDto) = techStackItemService.save(techStackItem)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{id}")
    suspend fun deleteTechStackItem(@PathVariable id: String) = techStackItemService.deleteById(id)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("{id}")
    suspend fun updateTechStackItem(
        @PathVariable id: String,
        @RequestBody techStackItem: TechStackItemDto
    ) = techStackItemService.update(id, techStackItem)
}