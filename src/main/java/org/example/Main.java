package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.JarMeterRegistry;
import org.example.model.Results;
import org.example.visitor.MeteredClassVisitor;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {

    public static void main(String[] args) throws IOException {
        Path jarPath = Path.of("src/main/resources/guava.zip");
        Path outputPath = Path.of("output.json");

        if (args.length != 2) {
            System.out.println("Usage: java -jar jaranalyzer.jar <jar file path> <output file path>");
            System.out.println("Using default args");
        } else {
            jarPath = Paths.get(args[0]);
            outputPath = Paths.get(args[1]);
        }

        try (JarFile sampleJar = new JarFile(jarPath.toString())) {
            JarMeterRegistry jarMeterRegistry = new JarMeterRegistry();
            Enumeration<JarEntry> enumeration = sampleJar.entries();

            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                if (entry.getName().endsWith(".class")) {
                    MeteredClassVisitor cp = new MeteredClassVisitor(jarMeterRegistry);
                    ClassReader cr = new ClassReader(sampleJar.getInputStream(entry));
                    cr.accept(cp, 0);
                }
            }

            Results results = jarMeterRegistry.getResults();

            System.out.println("=== JAR Analysis Results ===");
            System.out.println("Max Inheritance Depth: " + results.maxInheritanceDepth());
            System.out.printf("Average Inheritance Depth: %.2f%n", results.avgInheritanceDepth());
            System.out.printf("Average Overridden Methods: %.2f%n", results.avgOverriddenMethods());
            System.out.printf("Average Fields: %.2f%n", results.avgFields());
            System.out.println("ABC Metrics:");
            System.out.println("  Assignments: " + results.abcResults().assignments());
            System.out.println("  Branches: " + results.abcResults().branches());
            System.out.println("  Conditions: " + results.abcResults().conditions());
            System.out.printf("  ABC Score: %.2f%n", results.abcResults().score());

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(results);
            Files.writeString(outputPath, jsonOutput);

            System.out.println("\nResults saved to: " + outputPath.toAbsolutePath());
        }
    }
}
