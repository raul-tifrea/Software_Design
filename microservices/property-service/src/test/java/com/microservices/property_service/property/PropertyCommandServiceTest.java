package com.microservices.property_service.property;

import com.microservices.property_service.property.cqrs.command.PropertyCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PropertyCommandServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PropertyCommandService propertyCommandService;

    @BeforeEach
    void setUp() {
        // Initialize the mocks before each test
        MockitoAnnotations.openMocks(this);
        // We need to inject the @Value manually for testing
        org.springframework.test.util.ReflectionTestUtils.setField(propertyCommandService, "queueName", "testQueue");
    }

    @Test
    void createProperty_Success() {
        // 1. ARRANGE (Set up our fake data)
        Integer sellerId = 1;
        Property inputProperty = new Property();
        inputProperty.setTitle("Test Villa");

        Property savedProperty = new Property();
        savedProperty.setId(100);
        savedProperty.setTitle("Test Villa");
        savedProperty.setSellerId(sellerId);

        UserDto fakeAdminUser = new UserDto();
        fakeAdminUser.setUsername("admin");
        fakeAdminUser.setEmail("admin@test.com");
        fakeAdminUser.setRoleName("ADMIN");

        // Tell Mockito what to do when our service calls external things
        when(restTemplate.getForObject(anyString(), eq(UserDto.class))).thenReturn(fakeAdminUser);
        when(propertyRepository.save(any(Property.class))).thenReturn(savedProperty);

        // 2. ACT (Run the actual method)
        Property result = propertyCommandService.createProperty(inputProperty, sellerId);

        // 3. ASSERT (Verify it worked)
        assertNotNull(result);
        assertEquals(100, result.getId());
        assertEquals("Test Villa", result.getTitle());

        // Verify the Command correctly saved the property
        verify(propertyRepository, times(1)).save(inputProperty);

        // Verify the RabbitMQ notification was triggered
        verify(rabbitTemplate, times(1)).convertAndSend(eq("testQueue"), anyString());
    }
}