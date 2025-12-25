package org.example.model;

public record Results(
        int maxInheritanceDepth,
        double avgInheritanceDepth,
        double avgOverriddenMethods,
        double avgFields,
        AbcResults abcResults
) {
}
