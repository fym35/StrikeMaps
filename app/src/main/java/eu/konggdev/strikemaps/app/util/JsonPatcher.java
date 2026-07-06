package eu.konggdev.strikemaps.app.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.stream.StreamSupport;

public final class JsonPatcher {
    //Takes a json and a patch and applies it
    public static JsonNode patch(JsonNode current, JsonNode patch) throws Exception {
        if (current.isObject()) {
            ObjectNode result = current.deepCopy();

            patch.fields().forEachRemaining(entry -> { //For each
                String key = entry.getKey();
                JsonNode patchValue = entry.getValue();
                if(!result.has(key)) { //Not at all in current
                    result.set(key, patchValue); //Just append it to result
                } else if (result.has(key) && !patchValue.isEmpty()) { //Deeper depth possible
                    try {
                        result.set(key, patch(result.get(key), patchValue)); //Patch iteration with patch's value (traverse deeper)
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else if (result.has(key) && patchValue.isEmpty() && result.get(key).isEmpty()) { //Max depth match
                    result.set(key, patchValue); //Replace with patch's value
                }
            });

            return result;
        }

        if (current.isArray()) {
            ArrayNode result = current.deepCopy();
            for (JsonNode entry : patch) {
                if (StreamSupport.stream(result.spliterator(), false)
                        .noneMatch(node -> node.equals(entry))) { //If no exact entry, add entry (prevents duplicates)
                    result.add(entry);
                }
            }

            return result;
        }

        throw new Exception();
    }

    //Takes an original json, a patched json and a patch and removes that specific patch's changes from it
    public static JsonNode unpatch(JsonNode original, JsonNode current, JsonNode patch) throws Exception {
        if (current.isObject()) {
            ObjectNode result = current.deepCopy();  //Current contains the patch, deepcopy it and operate on the copy

            current.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();

                if (!original.has(key) && patch.has(key)) { //Value is in patch and current but was not at all in the original
                    result.remove(key); //Just remove the value
                } else if (!value.isEmpty() && patch.has(key)) { //Depper depth possible
                    try {
                        result.set(key, unpatch(original.get(key), result.get(key), patch.get(key))); //Replace with further unpatch of deeper level
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else if (value.isEmpty() && patch.has(key)) { //Max depth match
                    result.set(key, original.get(key)); //Replace with original unpatched value
                }
            });

            return result;
        }

        if (current.isArray()) {
            ArrayNode result = current.deepCopy();
            for (int i = 0; i < result.size(); i++) {
                JsonNode entry = result.get(i);
                if (StreamSupport.stream(original.spliterator(), false)
                        .noneMatch(on -> on.equals(entry)) //If not in original
                        &&
                        StreamSupport.stream(patch.spliterator(), false)
                                .noneMatch(rn -> rn.equals(entry))) { //But also in patch

                    result.remove(i); //Remove from result
                }
            }

            return result;
        }

        throw new Exception();
    }
}