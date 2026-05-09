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
    void getAllProperties_NoFilter_ReturnsAllSorted() {
        // ARRANGE
        Property p1 = new Property(); p1.setTitle("Villa");
        Property p2 = new Property(); p2.setTitle("Apartment");
        when(propertyRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(p1, p2));

        // ACT
        List<Property> results = propertyQueryService.getAllProperties(null, null, "id", "asc");

        // ASSERT
        assertEquals(2, results.size());
        assertEquals("Villa", results.get(0).getTitle());
        verify(propertyRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void getPropertyById_Success() {
        // ARRANGE
        Property p = new Property();
        p.setId(1);
        p.setTitle("Cabin");
        when(propertyRepository.findById(1)).thenReturn(Optional.of(p));

        // ACT
        Property result = propertyQueryService.getPropertyById(1);

        // ASSERT
        assertNotNull(result);
        assertEquals("Cabin", result.getTitle());
    }

    @Test
    void getAllProperties_FilterByTitle_ReturnsTitleMatches() {
        // ARRANGE
        Property p = new Property(); p.setTitle("Beach Villa");
        when(propertyRepository.findByTitleContainingIgnoreCase(eq("villa"), any(Sort.class)))
                .thenReturn(List.of(p));

        // ACT
        List<Property> results = propertyQueryService.getAllProperties("title", "villa", "id", "asc");

        // ASSERT
        assertEquals(1, results.size());
        assertEquals("Beach Villa", results.get(0).getTitle());
        verify(propertyRepository, times(1)).findByTitleContainingIgnoreCase(eq("villa"), any(Sort.class));
    }

    @Test
    void getAllProperties_FilterByLocation_ReturnsLocationMatches() {
        // ARRANGE
        Property p = new Property(); p.setLocation("Bucharest");
        when(propertyRepository.findByLocationContainingIgnoreCase(eq("bucharest"), any(Sort.class)))
                .thenReturn(List.of(p));

        // ACT
        List<Property> results = propertyQueryService.getAllProperties("location", "bucharest", "price", "desc");

        // ASSERT
        assertEquals(1, results.size());
        assertEquals("Bucharest", results.get(0).getLocation());
        verify(propertyRepository, times(1)).findByLocationContainingIgnoreCase(eq("bucharest"), any(Sort.class));
    }
}