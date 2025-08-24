package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.dto.AssetRequest;
import org.godigit.trackwise.dto.AssetResponse;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.AssetMapper;
import org.godigit.trackwise.model.*;
import org.godigit.trackwise.model.Enum.AssetStatus;
import org.godigit.trackwise.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssetServiceImplTest {

    @Mock private AssetRepository assetRepository;
    @Mock private AssetCategoryRepository categoryRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private WarrantyRepository warrantyRepository;
    @Mock private AssetMapper assetMapper;

    @InjectMocks private AssetServiceImpl assetService;

    private UUID assetId;
    private UUID categoryId;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        assetId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        employeeId = UUID.randomUUID();
    }

    private AssetRequest sampleRequest() {
        AssetRequest request = new AssetRequest();
        request.setName("Laptop");
        request.setCategoryId(categoryId);
        request.setEmployeeId(employeeId);
        request.setStatus("AVAILABLE");
        request.setPurchaseDate(LocalDate.of(2024, 1, 1));
        request.setSerialNumber("SN123456");
        request.setWarrantyExpiryDate(LocalDate.of(2026, 1, 1));
        return request;
    }

    private Asset sampleAsset() {
        Asset asset = new Asset();
        asset.setId(assetId);
        asset.setName("Laptop");
        asset.setStatus(AssetStatus.AVAILABLE);
        return asset;
    }

    private AssetResponse sampleResponse() {
        AssetResponse response = new AssetResponse();
        response.setId(assetId);
        response.setName("Laptop");
        response.setStatus("AVAILABLE");
        return response;
    }

    @Test
    void shouldCreateAssetWithWarranty() {
        AssetRequest request = sampleRequest();
        AssetCategory category = new AssetCategory();
        Employee employee = new Employee();
        Asset asset = sampleAsset();
        Warranty warranty = new Warranty();
        Asset savedAsset = sampleAsset();
        savedAsset.setWarranty(warranty);
        AssetResponse response = sampleResponse();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(assetMapper.toEntity(request, category, employee)).thenReturn(asset);
        when(assetRepository.save(asset)).thenReturn(savedAsset);
        when(warrantyRepository.save(any())).thenReturn(warranty);
        when(assetMapper.toResponse(savedAsset)).thenReturn(response);

        AssetResponse result = assetService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop");
        verify(assetRepository).save(asset);
        verify(warrantyRepository).save(any());
    }

    @Test
    void shouldThrowExceptionIfCategoryNotFoundOnCreate() {
        AssetRequest request = sampleRequest();
        request.setCategoryId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.create(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    void shouldGetAssetById() {
        Asset asset = sampleAsset();
        AssetResponse response = sampleResponse();

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(assetMapper.toResponse(asset)).thenReturn(response);

        AssetResponse result = assetService.getById(assetId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(assetId);
    }

    @Test
    void shouldThrowExceptionIfAssetNotFoundOnGet() {
        when(assetRepository.findById(assetId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.getById(assetId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Asset not found");
    }

    @Test
    void shouldListAssets() {
        Asset asset = sampleAsset();
        AssetResponse response = sampleResponse();

        when(assetRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(asset)));
        when(assetMapper.toResponse(asset)).thenReturn(response);

        var result = assetService.list(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldUpdateAssetWithWarranty() {
        AssetRequest request = sampleRequest();
        Asset asset = sampleAsset();
        AssetCategory category = new AssetCategory();
        Employee employee = new Employee();
        Warranty warranty = new Warranty();
        asset.setWarranty(warranty);
        AssetResponse response = sampleResponse();

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        doNothing().when(assetMapper).updateEntity(asset, request, category, employee);
        when(warrantyRepository.save(any())).thenReturn(warranty);
        when(assetRepository.save(asset)).thenReturn(asset);
        when(assetMapper.toResponse(asset)).thenReturn(response);

        AssetResponse result = assetService.update(assetId, request);

        assertThat(result).isNotNull();
        verify(warrantyRepository).save(any());
    }

    @Test
    void shouldDeleteAsset() {
        Asset asset = sampleAsset();

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        doNothing().when(assetRepository).delete(asset);

        assetService.delete(assetId);

        verify(assetRepository).delete(asset);
    }

    @Test
    void shouldAssignToEmployee() {
        Asset asset = sampleAsset();
        Employee employee = new Employee();
        AssetResponse response = sampleResponse();

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(assetRepository.save(asset)).thenReturn(asset);
        when(assetMapper.toResponse(asset)).thenReturn(response);

        AssetResponse result = assetService.assignToEmployee(assetId, employeeId);

        assertThat(result).isNotNull();
        verify(assetRepository).save(asset);
    }

    @Test
    void shouldUnassignAsset() {
        Asset asset = sampleAsset();
        AssetResponse response = sampleResponse();

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(assetRepository.save(asset)).thenReturn(asset);
        when(assetMapper.toResponse(asset)).thenReturn(response);

        AssetResponse result = assetService.unassign(assetId);

        assertThat(result).isNotNull();
        verify(assetRepository).save(asset);
    }

    @Test
    void shouldFindByStatus() {
        Asset asset = sampleAsset();
        AssetResponse response = sampleResponse();

        when(assetRepository.findByStatus(AssetStatus.AVAILABLE)).thenReturn(List.of(asset));
        when(assetMapper.toResponse(asset)).thenReturn(response);

        List<AssetResponse> result = assetService.findByStatus(AssetStatus.AVAILABLE);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldFindWithWarrantyExpiringBetween() {
        Warranty warranty = new Warranty();
        Asset asset = sampleAsset();
        warranty.setAsset(asset);
        AssetResponse response = sampleResponse();

        when(warrantyRepository.findByEndDateBetween(any(), any())).thenReturn(List.of(warranty));
        when(assetMapper.toResponse(asset)).thenReturn(response);

        List<AssetResponse> result = assetService.findWithWarrantyExpiringBetween(
                LocalDate.of(2025, 1, 1), LocalDate.of(2026, 1, 1));

        assertThat(result).hasSize(1);
    }
}
