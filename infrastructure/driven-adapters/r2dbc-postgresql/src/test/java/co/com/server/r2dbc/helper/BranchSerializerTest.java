package co.com.server.r2dbc.helper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.com.server.model.Branch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BranchSerializerTest {

    private BranchSerializer branchSerializer;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        branchSerializer = new BranchSerializer(objectMapper);
    }

    @Test
    void serialize_ValidList_ReturnsExpectedJson() {
        Branch branch1 = new Branch("branch1", "Sucursal Uno", new ArrayList<>());
        Branch branch2 = new Branch("branch2", "Sucursal Dos", new ArrayList<>());
        List<Branch> branches = Arrays.asList(branch1, branch2);

        String jsonResult = branchSerializer.serialize(branches);

        assertNotNull(jsonResult);
        assertTrue(jsonResult.contains("\"id\":\"branch1\""));
        assertTrue(jsonResult.contains("\"name\":\"Sucursal Uno\""));
        assertTrue(jsonResult.contains("\"id\":\"branch2\""));
        assertTrue(jsonResult.contains("\"name\":\"Sucursal Dos\""));
    }

    @Test
    void deserialize_ValidJson_ReturnsExpectedList() {
        String json = "[{\"id\":\"branch1\",\"name\":\"Sucursal Uno\",\"products\":[]},"
                + "{\"id\":\"branch2\",\"name\":\"Sucursal Dos\",\"products\":[]}]";

        List<Branch> branches = branchSerializer.deserialize(json);

        assertNotNull(branches);
        assertEquals(2, branches.size());
        assertEquals("branch1", branches.get(0).getId());
        assertEquals("Sucursal Uno", branches.get(0).getName());
        assertEquals("branch2", branches.get(1).getId());
        assertEquals("Sucursal Dos", branches.get(1).getName());
    }

    @Test
    void deserialize_InvalidJson_ThrowsRuntimeException() {
        String invalidJson = "invalid json";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            branchSerializer.deserialize(invalidJson);
        });
        assertTrue(exception.getMessage().contains("Error al deserializar sucursales"));
    }

    @Test
    void serialize_InvalidInput_ThrowsRuntimeException() throws JsonProcessingException {
        ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
        BranchSerializer serializer = new BranchSerializer(mockMapper);
        List<Branch> branches = new ArrayList<>();

        when(mockMapper.writeValueAsString(any(List.class)))
                .thenThrow(new JsonProcessingException("Forced exception") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serializer.serialize(branches);
        });
        assertTrue(exception.getMessage().contains("Error al serializar sucursales"));
    }
}