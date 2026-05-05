package com.veritrabajo.backend.workerprofile.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable owned-tool set; updates return new instances.
 */
public final class OwnedTools {

    private final Set<String> tools;

    private OwnedTools(Set<String> tools) {
        this.tools = Collections.unmodifiableSet(new HashSet<>(tools));
    }

    public static OwnedTools empty() {
        return new OwnedTools(new HashSet<>());
    }

    /** Builds from raw strings, trimming and skipping blanks/nulls. */
    public static OwnedTools of(Set<String> tools) {
        if (tools == null) {
            return empty();
        }
        Set<String> cleanTools = new HashSet<>();
        for (String tool : tools) {
            if (tool != null && !tool.isBlank()) {
                cleanTools.add(tool.trim());
            }
        }
        return new OwnedTools(cleanTools);
    }

    public OwnedTools addTool(String toolName) {
        if (toolName == null || toolName.isBlank()) {
            throw new IllegalArgumentException(
                    "Tool name cannot be blank"
            );
        }
        Set<String> updated = new HashSet<>(tools);
        updated.add(toolName.trim());
        return new OwnedTools(updated);
    }

    public Set<String> getTools() {
        return tools;
    }

    public boolean isEmpty() {
        return tools.isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OwnedTools that)) {
            return false;
        }
        return tools.equals(that.tools);
    }

    @Override
    public int hashCode() {
        return tools.hashCode();
    }

    @Override
    public String toString() {
        return "OwnedTools{tools=" + tools + "}";
    }
}
