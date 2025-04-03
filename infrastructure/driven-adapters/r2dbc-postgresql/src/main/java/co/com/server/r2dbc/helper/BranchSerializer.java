package co.com.server.r2dbc.helper;

import co.com.server.model.Branch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BranchSerializer {

    private final ObjectMapper objectMapper;

    public List<Branch> deserialize(String branchesJson) {
        try {
            List<Branch> branches = objectMapper.readValue(
                    branchesJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Branch.class));
            return branches != null ? branches : new ArrayList<>();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al deserializar sucursales", e);
        }
    }

    public String serialize(List<Branch> branches) {
        try {
            return objectMapper.writeValueAsString(branches);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar sucursales", e);
        }
    }
}

