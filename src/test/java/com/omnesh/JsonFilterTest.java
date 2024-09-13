package com.omnesh;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JsonFilterTest {

    // Helper method to parse JSON strings into JsonNode
    private JsonNode parseJson(String jsonString) throws Exception {
        return JsonFilter.parseJson(jsonString);
    }

    @Test
    public void testNumericComparisons() throws Exception {
        String referenceJson = """
            {
                "age": ">25",
                "price": "100-200",
                "rating": "<4.5",
                "exactValue": 100
            }
            """;

        String targetJson1 = """
            {
                "age": 30,
                "price": 150,
                "rating": 4.0,
                "exactValue": 100
            }
            """;

        String targetJson2 = """
            {
                "age": 20,
                "price": 150,
                "rating": 4.0,
                "exactValue": 100
            }
            """;

        JsonNode referenceNode = parseJson(referenceJson);
        JsonNode targetNode1 = parseJson(targetJson1);
        JsonNode targetNode2 = parseJson(targetJson2);

        assertTrue(JsonFilter.matchJson(referenceNode, targetNode1));
        assertFalse(JsonFilter.matchJson(referenceNode, targetNode2));
    }

    @Test
    public void testArraySizeValidation() throws Exception {
        String referenceJson = """
            {
                "items": ["size>2"]
            }
            """;

        String targetJson1 = """
            {
                "items": ["item1", "item2", "item3"]
            }
            """;

        String targetJson2 = """
            {
                "items": ["item1"]
            }
            """;

        JsonNode referenceNode = parseJson(referenceJson);
        JsonNode targetNode1 = parseJson(targetJson1);
        JsonNode targetNode2 = parseJson(targetJson2);

        assertTrue(JsonFilter.matchJson(referenceNode, targetNode1));
        assertFalse(JsonFilter.matchJson(referenceNode, targetNode2));
    }

    @Test
    public void testArrayContentValidation() throws Exception {
        String referenceJson = """
            {
                "tags": ["tag1", "tag2"]
            }
            """;

        String targetJson1 = """
            {
                "tags": ["tag1", "tag2", "tag3"]
            }
            """;

        String targetJson2 = """
            {
                "tags": ["tag2", "tag3"]
            }
            """;

        JsonNode referenceNode = parseJson(referenceJson);
        JsonNode targetNode1 = parseJson(targetJson1);
        JsonNode targetNode2 = parseJson(targetJson2);

        assertTrue(JsonFilter.matchJson(referenceNode, targetNode1));
    }

//    @Test
//    void testNestedObjectValidation() throws Exception {
//        String referenceJson = """
//            {
//                "user": {
//                    "status": "active",
//                    "score": ">50"
//                }
//            }
//            """;
//
//        String targetJson1 = """
//            {
//                "user": {
//                    "status": "active",
//                    "score": 75
//                }
//            }
//            """;
//
//        String targetJson2 = """
//            {
//                "user": {
//                    "status": "inactive",
//                    "score": 75
//                }
//            }
//            """;
//
//        JsonNode referenceNode = parseJson(referenceJson);
//        JsonNode targetNode1 = parseJson(targetJson1);
//        JsonNode targetNode2 = parseJson(targetJson2);
//
//        assertTrue(JSONFilter.matchJson(referenceNode, targetNode1), "Target 1 should match the nested object validation.");
//        assertFalse(JSONFilter.matchJson(referenceNode, targetNode2), "Target 2 should not match because of the 'status' field.");
//    }
//
//    @Test
//    void testCombinedConditions() throws Exception {
//        String referenceJson = """
//            {
//                "age": ">20",
//                "items": ["size>1", "item1"],
//                "user": {
//                    "score": "30-50"
//                }
//            }
//            """;
//
//        String targetJson1 = """
//            {
//                "age": 25,
//                "items": ["item1", "item2"],
//                "user": {
//                    "score": 40
//                }
//            }
//            """;
//
//        String targetJson2 = """
//            {
//                "age": 19,
//                "items": ["item1", "item2"],
//                "user": {
//                    "score": 40
//                }
//            }
//            """;
//
//        JsonNode referenceNode = parseJson(referenceJson);
//        JsonNode targetNode1 = parseJson(targetJson1);
//        JsonNode targetNode2 = parseJson(targetJson2);
//
//        assertTrue(JSONFilter.matchJson(referenceNode, targetNode1), "Target 1 should match all combined conditions.");
//        assertFalse(JSONFilter.matchJson(referenceNode, targetNode2), "Target 2 should not match due to age condition.");
//    }


}
