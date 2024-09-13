# Dynamic JSON Filter Library

The Dynamic JSON Filter Library is a Java-based library that allows filtering of target JSON objects based on a reference JSON. It provides powerful and flexible comparison and validation logic, making it ideal for various data validation, filtering, and matching use cases.

## **Features**

1. **Numeric Comparisons:**
    - Perform numeric comparisons such as:
        - Greater than (`>`)
        - Less than (`<`)
        - Between ranges (`min-max`)
        - Exact value (`=`)

2. **Array Validation:**
    - Validate arrays with multiple conditions:
        - Check array size alone (`size>`, `size<`, `size=`)
        - Validate that the target array contains all elements in the reference array.
        - Check if the target array contains any elements from the reference array.
        - Validate the exact match between arrays.

3. **Nested Object Validation:**
    - Supports validation of nested objects within the target JSON, ensuring the structure and values match those defined in the reference JSON.

4. **Flexible Validation Logic:**
    - Supports multiple validation types within the same reference JSON, allowing complex filtering scenarios with a combination of numeric, array, and object validations.

5. **Customizable Reference Rules:**
    - Easily specify validation rules within the reference JSON, which are then used to filter and validate target JSON objects.

## **Usage**

1. Add the library as a dependency to your Java project.
2. Use the `JSONFilter` class to filter target JSON objects based on your specified reference JSON conditions.

## **Example**

Here is a simple example of using `JSONFilter`:

```java
String referenceJson = "{ \"age\": \">30\", \"items\": [\"size>2\"] }";
String targetJson = "{ \"age\": 35, \"items\": [\"item1\", \"item2\", \"item3\"] }";
 
boolean isMatch = JSONFilter.matchJson(referenceJson, targetJson);
System.out.println("Does the target match the reference? " + isMatch);