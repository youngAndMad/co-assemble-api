package kz.danekerscode.coassembleapi.controller

import kz.danekerscode.coassembleapi.model.dto.user.TechStackItemDto
import kz.danekerscode.coassembleapi.service.TechStackItemService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tech-stack-items")
class TechStackItemController(
    private val techStackItemService: TechStackItemService
) {

    @GetMapping
    fun allTechStackItems() = techStackItemService.findAll()

    @GetMapping("{id}")
    fun techStackItemById(@PathVariable id: String) = techStackItemService.findById(id)

    @PostMapping
    fun createTechStackItem(@RequestBody techStackItem: TechStackItemDto) = techStackItemService.save(techStackItem)

    @DeleteMapping("{id}")
    fun deleteTechStackItem(@PathVariable id: String) = techStackItemService.deleteById(id)

    @PutMapping("{id}")
    fun updateTechStackItem(
        @PathVariable id: String,
        @RequestBody techStackItem: TechStackItemDto
    ) = techStackItemService.update(id, techStackItem)
}