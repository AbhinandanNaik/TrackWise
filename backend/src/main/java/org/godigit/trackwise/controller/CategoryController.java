package org.godigit.trackwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.CategoryRequest;
import org.godigit.trackwise.model.AssetCategory;
import org.godigit.trackwise.repository.AssetCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "10. Asset Categories", description = "Manage asset categories")
public class CategoryController {

    private final AssetCategoryRepository categoryRepository;

    @GetMapping
    @Operation(summary = "List all categories")
    public ResponseEntity<List<AssetCategory>> list(){
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @PostMapping
    @Operation(summary = "Create a category")
    public ResponseEntity<AssetCategory> create(@RequestBody CategoryRequest req){
        AssetCategory c = new AssetCategory();
        c.setName(req.getName());
        c.setDescription(req.getDescription());
        return new ResponseEntity<>(categoryRepository.save(c), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category")
    public ResponseEntity<AssetCategory> update(@PathVariable UUID id, @RequestBody CategoryRequest req){
        AssetCategory c = categoryRepository.findById(id).orElseThrow();
        c.setName(req.getName());
        c.setDescription(req.getDescription());
        return ResponseEntity.ok(categoryRepository.save(c));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


