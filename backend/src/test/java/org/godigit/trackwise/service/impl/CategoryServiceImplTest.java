package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.dto.CategoryRequest;
import org.godigit.trackwise.dto.CategoryResponse;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.CategoryMapper;
import org.godigit.trackwise.model.AssetCategory;
import org.godigit.trackwise.repository.AssetCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private AssetCategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private UUID categoryId;
    private AssetCategory category;
    private CategoryRequest request;
    private CategoryResponse response;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();

        category = new AssetCategory();
        category.setId(categoryId);
        category.setName("Laptops");
        category.setDescription("Portable computers");

        request = new CategoryRequest();
        request.setName("Laptops");
        request.setDescription("Portable computers");

        response = new CategoryResponse();
        response.setId(categoryId);
        response.setName("Laptops");
        response.setDescription("Portable computers");
    }

    @Test
    void shouldCreateCategory() {
        AssetCategory saved = new AssetCategory();
        saved.setId(categoryId);
        saved.setName(request.getName());
        saved.setDescription(request.getDescription());

        when(categoryRepository.save(any(AssetCategory.class))).thenReturn(saved);

        try (MockedStatic<CategoryMapper> mocked = mockStatic(CategoryMapper.class)) {
            mocked.when(() -> CategoryMapper.toDto(saved)).thenReturn(response);

            CategoryResponse resp = categoryService.create(request);

            assertThat(resp).isNotNull();
            assertThat(resp.getId()).isEqualTo(categoryId);
            assertThat(resp.getName()).isEqualTo(request.getName());
            assertThat(resp.getDescription()).isEqualTo(request.getDescription());

            verify(categoryRepository).save(any(AssetCategory.class));
            mocked.verify(() -> CategoryMapper.toDto(saved));
        }
    }

    @Test
    void shouldGetById() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        try (MockedStatic<CategoryMapper> mocked = mockStatic(CategoryMapper.class)) {
            mocked.when(() -> CategoryMapper.toDto(category)).thenReturn(response);

            CategoryResponse resp = categoryService.getById(categoryId);

            assertThat(resp).isNotNull();
            assertThat(resp.getId()).isEqualTo(categoryId);
            assertThat(resp.getName()).isEqualTo(category.getName());

            verify(categoryRepository).findById(categoryId);
            mocked.verify(() -> CategoryMapper.toDto(category));
        }
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getById(categoryId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Asset Category not found");

        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void shouldListCategories() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AssetCategory> page = new PageImpl<>(List.of(category), pageable, 1);

        when(categoryRepository.findAll(pageable)).thenReturn(page);

        try (MockedStatic<CategoryMapper> mocked = mockStatic(CategoryMapper.class)) {
            mocked.when(() -> CategoryMapper.toDto(any(AssetCategory.class))).thenReturn(response);

            Page<CategoryResponse> result = categoryService.list(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(categoryId);

            verify(categoryRepository).findAll(pageable);
            mocked.verify(() -> CategoryMapper.toDto(category));
        }
    }

    @Test
    void shouldUpdateCategory() {
        CategoryRequest updateReq = new CategoryRequest();
        updateReq.setName("Monitors");
        updateReq.setDescription("Display devices");

        AssetCategory existing = new AssetCategory();
        existing.setId(categoryId);
        existing.setName("Old");
        existing.setDescription("Old desc");

        AssetCategory updated = new AssetCategory();
        updated.setId(categoryId);
        updated.setName(updateReq.getName());
        updated.setDescription(updateReq.getDescription());

        CategoryResponse updatedResp = new CategoryResponse();
        updatedResp.setId(categoryId);
        updatedResp.setName(updateReq.getName());
        updatedResp.setDescription(updateReq.getDescription());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(updated);

        try (MockedStatic<CategoryMapper> mocked = mockStatic(CategoryMapper.class)) {
            mocked.when(() -> CategoryMapper.toDto(updated)).thenReturn(updatedResp);

            CategoryResponse resp = categoryService.update(categoryId, updateReq);

            assertThat(resp).isNotNull();
            assertThat(resp.getName()).isEqualTo("Monitors");
            assertThat(resp.getDescription()).isEqualTo("Display devices");

            verify(categoryRepository).findById(categoryId);
            verify(categoryRepository).save(existing);
            mocked.verify(() -> CategoryMapper.toDto(updated));
        }
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        CategoryRequest updateReq = new CategoryRequest();
        updateReq.setName("Monitors");
        updateReq.setDescription("Display devices");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(categoryId, updateReq))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Asset Category not found");

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldDeleteCategory() {
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(categoryId);

        assertThatCode(() -> categoryService.delete(categoryId)).doesNotThrowAnyException();

        verify(categoryRepository).existsById(categoryId);
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.delete(categoryId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Asset Category not found");

        verify(categoryRepository).existsById(categoryId);
        verify(categoryRepository, never()).deleteById(any());
    }
}