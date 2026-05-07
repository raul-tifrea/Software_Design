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
    void getAllProperties_Success() {
        // ARRANGE
        Property p1 = new Property(); p1.setTitle("Villa");
        Property p2 = new Property(); p2.setTitle("Apartment");

        // Fix: Change it to findAll() without arguments to match your Service exactly!
        when(propertyRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        // ACT
        List<Property> results = propertyQueryService.getAllProperties(null, null, "id", "asc");

        // ASSERT
        assertEquals(2, results.size());
        assertEquals("Villa", results.get(0).getTitle());
        verify(propertyRepository, times(1)).findAll(); // Verify the correct method was called
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
}