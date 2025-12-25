package org.example.model;

public class AbcMeterRegistry {
    private int assignments = 0;
    private int branches = 0;
    private int conditions = 0;

    public void trackAssignment() {
        assignments++;
    }

    public void trackBranch() {
        branches++;
    }

    public void trackCondition() {
        conditions++;
    }

    public AbcResults getResults() {
        return new AbcResults(
                assignments,
                branches,
                conditions,
                Math.sqrt(assignments * assignments + branches * branches + conditions * conditions)
        );
    }
}
