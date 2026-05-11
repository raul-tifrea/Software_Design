package com.microservices.property_service.property;

import com.microservices.property_service.property.cqrs.query.PropertyQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PropertyQueryServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PropertyQueryService propertyQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void getPropertyById_Success() {
        Property p = new Property();
        p.setId(1);
        p.setTitle("Casa");
        when(propertyRepository.findById(1)).thenReturn(Optional.of(p));
        Property result = propertyQueryService.getPropertyById(1);
        assertNotNull(result);
        assertEquals("Casa", result.getTitle());
    }

    @Test
    void getAllProperties_FilterByTitle_ReturnsTitleMatches() {
        Property p = new Property(); p.setTitle("Vila");
        when(propertyRepository.findByTitleContainingIgnoreCase(eq("vila"), any(Sort.class)))
                .thenReturn(List.of(p));
        List<Property> results = propertyQueryService.getAllProperties("title", "vila", "id", "asc");
        assertEquals(1, results.size());
        assertEquals("Vila", results.get(0).getTitle());
        verify(propertyRepository, times(1)).findByTitleContainingIgnoreCase(eq("vila"), any(Sort.class));
    }

    @Test
    void getAllProperties_FilterByLocation_ReturnsLocationMatches() {
        Property p = new Property(); p.setLocation("Bucuresti");
        when(propertyRepository.findByLocationContainingIgnoreCase(eq("bucuresti"), any(Sort.class)))
                .thenReturn(List.of(p));
        List<Property> results = propertyQueryService.getAllProperties("location", "bucuresti", "price", "desc");

        assertEquals(1, results.size());
        assertEquals("Bucuresti", results.get(0).getLocation());
        verify(propertyRepository, times(1)).findByLocationContainingIgnoreCase(eq("bucuresti"), any(Sort.class));
    }
}