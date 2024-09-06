package com.omnesh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonFilter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parses a JSON string into a JsonNode object.
     *
     * @param jsonString the JSON string to parse
     * @return the parsed JsonNode
     * @throws IOException if the JSON string is invalid
     */
    public static JsonNode parseJson(String jsonString) throws IOException {
        return objectMapper.readTree(jsonString);
    }

    /**
     * Compares a target JSON against a reference JSON based on specified conditions.
     *
     * @param referenceJson the reference JSON containing validation criteria
     * @param targetJson    the target JSON to be validated
     * @return true if the target JSON matches the reference JSON criteria, false otherwise
     */
    public static boolean matchJson(JsonNode referenceJson, JsonNode targetJson) {
        return matchesReferenceRecursive(targetJson, referenceJson);
    }

    /**
     * Filters a list of target JSON objects, returning only those that match the reference conditions.
     *
     * @param referenceJson the reference JSON containing validation criteria
     * @param targetJsons   the list of target JSONs to be filtered
     * @return a list of matching target JSONs
     */
    public static List<JsonNode> filterMatchingJsons(JsonNode referenceJson, List<JsonNode> targetJsons) {
        return targetJsons.stream()
                .filter(targetJson -> matchJson(referenceJson, targetJson))
                .collect(Collectors.toList());
    }

    // Recursive matching logic for JSON nodes
    private static boolean matchesReferenceRecursive(JsonNode target, JsonNode reference) {
        // Iterate over the fields in the reference JSON
        Iterator<Map.Entry<String, JsonNode>> fields = reference.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode referenceValue = field.getValue();
            JsonNode targetValue = target.get(fieldName);

            // Check if the target field exists
            if (targetValue == null) {
                return false; // Reference field not present in target
            }

            // Handle arrays, objects, and primitive values
            if (referenceValue.isArray() && targetValue.isArray()) {
                if (!matchArray(referenceValue, targetValue)) {
                    return false;
                }
            } else if (referenceValue.isObject() && targetValue.isObject()) {
                if (!matchesReferenceRecursive(targetValue, referenceValue)) {
                    return false;
                }
            } else if (!targetValue.equals(referenceValue)) {
                return false; // Primitive value does not match
            }
        }
        return true;
    }

    // Simplified array matching logic
    private static boolean matchArray(JsonNode referenceArray, JsonNode targetArray) {
        // Handle empty array validation
        if (referenceArray.isEmpty() && !targetArray.isEmpty()) {
            return false;
        }

        // Check if all elements in the reference array are present in the target array
        for (JsonNode referenceElement : referenceArray) {
            boolean found = false;

            // Compare array elements based on their type (string or object)
            if (referenceElement.isObject()) {
                for (JsonNode targetElement : targetArray) {
                    if (targetElement.isObject() && matchesReferenceRecursive(targetElement, referenceElement)) {
                        found = true;
                        break;
                    }
                }
            } else if (referenceElement.isTextual()) {
                for (JsonNode targetElement : targetArray) {
                    if (targetElement.isTextual() && targetElement.asText().equals(referenceElement.asText())) {
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                return false;
            }
        }
        return true;
    }

}
