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
     */
    public static JsonNode parseJson(String jsonString) throws IOException {
        return objectMapper.readTree(jsonString);
    }

    /**
     * Compares a target JSON against a reference JSON based on specified conditions.
     */
    public static boolean matchJson(JsonNode referenceJson, JsonNode targetJson) {
        return matchesReferenceRecursive(targetJson, referenceJson);
    }

    /**
     * Filters a list of target JSON objects, returning only those that match the reference conditions.
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
                return false;
            }

            // Handle numeric comparisons
            if (referenceValue.isTextual() && isComparisonOperator(referenceValue.asText())) {
                if (!compareValues(referenceValue.asText(), targetValue)) {
                    return false;
                }
            }

            else if (referenceValue.isObject() && targetValue.isObject()) {
                if (!matchesReferenceRecursive(targetValue, referenceValue)) {
                    return false;
                }
            }

            else if (referenceValue.isArray() && targetValue.isArray()) {
                if (!validateArray(referenceValue, targetValue)) {
                    return false;
                }
            }

            else if (!targetValue.equals(referenceValue)) {
                return false;
            }
        }
        return true;
    }

    // Helper to determine if a string represents a comparison operator
    private static boolean isComparisonOperator(String value) {
        return value.startsWith(">") || value.startsWith("<") || value.contains("-");
    }

    // Helper to compare values using specified conditions
    private static boolean compareValues(String condition, JsonNode targetValue) {
        if (!targetValue.isNumber()) {
            return false;
        }
        double targetNumber = targetValue.asDouble();

        if (condition.startsWith(">")) {
            double comparisonValue = Double.parseDouble(condition.substring(1));
            return targetNumber > comparisonValue;
        }
        if (condition.startsWith("<")) {
            double comparisonValue = Double.parseDouble(condition.substring(1));
            return targetNumber < comparisonValue;
        }
        if (condition.contains("-")) {
            String[] bounds = condition.split("-");
            double lowerBound = Double.parseDouble(bounds[0]);
            double upperBound = Double.parseDouble(bounds[1]);
            return targetNumber >= lowerBound && targetNumber <= upperBound;
        }
        return targetNumber == Double.parseDouble(condition);
    }

    // Array validation based on different conditions specified in the reference
    private static boolean validateArray(JsonNode referenceArray, JsonNode targetArray) {
        // Size only validation
        if (referenceArray.size() == 1 && referenceArray.get(0).isTextual() && referenceArray.get(0).asText().startsWith("size")) {
            String sizeCondition = referenceArray.get(0).asText().substring(4);
            return validateSizeCondition(sizeCondition, targetArray.size());
        }

        // Check if target contains all reference elements
        for (JsonNode referenceElement : referenceArray) {
            boolean found = false;
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

        }

        return true;
    }

    // Helper to validate size conditions like ">", "<", "=", and ranges
    private static boolean validateSizeCondition(String condition, int size) {
        if (condition.startsWith(">")) {
            int threshold = Integer.parseInt(condition.substring(1));
            return size > threshold;
        } else if (condition.startsWith("<")) {
            int threshold = Integer.parseInt(condition.substring(1));
            return size < threshold;
        } else if (condition.contains("-")) {
            String[] bounds = condition.split("-");
            int lowerBound = Integer.parseInt(bounds[0]);
            int upperBound = Integer.parseInt(bounds[1]);
            return size >= lowerBound && size <= upperBound;
        } else if (condition.equals("=") || condition.equals("==")) {
            int exactSize = Integer.parseInt(condition.substring(1));
            return size == exactSize;
        }
        return false;
    }
}
