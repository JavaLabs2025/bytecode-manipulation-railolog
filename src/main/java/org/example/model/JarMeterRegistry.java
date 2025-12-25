package org.example.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

public class JarMeterRegistry {
    private Map<String, String> classToParent = new HashMap<>();
    private Map<String, Integer> classToFieldCount = new HashMap<>();
    private Map<String, Set<String>> classToMethods = new HashMap<>();
    private Map<String, Integer> classToOverriddenMethods = new HashMap<>();
    @Getter
    private AbcMeterRegistry abcMeterRegistry = new AbcMeterRegistry();

    public Results getResults() {
        classToParent.keySet()
                .forEach(this::countOverriddenMethods);

        List<Integer> depths = classToParent.keySet().stream()
                .map(this::computeInheritanceDepth)
                .toList();


        return new Results(
                Collections.max(depths),
                depths.stream().mapToInt(Integer::intValue).average().orElse(0.0),
                classToOverriddenMethods.values().stream().mapToInt(Integer::intValue).average().orElse(0.0),
                classToFieldCount.values().stream().mapToInt(Integer::intValue).average().orElse(0.0),
                abcMeterRegistry.getResults()
        );
    }

    public void addUV(String className, String superClassName) {
        classToParent.put(className, superClassName);
    }

    public void trackFieldVisit(String className) {
        classToFieldCount.put(className, classToFieldCount.getOrDefault(className, 0) + 1);
    }

    public void trackMethodVisit(String className, String fullName) {
        Set<String> methods = classToMethods.computeIfAbsent(className, (k) -> new HashSet<>());
        methods.add(fullName);
    }

    private void countOverriddenMethods(String className) {
        Set<String> parentsMethods = new HashSet<>();
        String parent = classToParent.get(className);
        while (parent != null) {
            parentsMethods.addAll(classToMethods.getOrDefault(parent, new HashSet<>()));
            parent = classToParent.get(parent);
        }

        classToOverriddenMethods.put(
                className,
                (int) classToMethods.getOrDefault(className, new HashSet<>()).stream()
                        .filter(parentsMethods::contains)
                        .count()
        );
    }

    private int computeInheritanceDepth(String className) {
        int depth = 0;
        String current = className;
        while (classToParent.containsKey(current)) {
            depth++;
            current = classToParent.get(current);
        }
        return depth;
    }
}
