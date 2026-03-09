package edu.touro.las.mcon364.test;

import javax.swing.text.html.Option;
import java.util.*;

public class StudyTracker {

    private final Map<String, List<Integer>> scoresByLearner = new HashMap<>();
    private final Deque<UndoStep> undoStack = new ArrayDeque<>();
    // Helper methods already provided for tests and local inspection.
    public Optional<List<Integer>> scoresFor(String name) {
        return Optional.ofNullable(scoresByLearner.get(name));
    }

    public Set<String> learnerNames() {
        return scoresByLearner.keySet();
    }
    /**
     * Problem 11
     * Add a learner with an empty score list.
     *
     * Return:
     * - true if the learner was added
     * - false if the learner already exists
     *
     * Throw IllegalArgumentException if name is null or blank.
     */
    public boolean addLearner(String name) {
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        var scoresFor = scoresFor(name);
        if (scoresFor.isEmpty()){
            scoresByLearner.put(name, new ArrayList<>());
            return true;
        }
        return false;
    }

    /**
     * Problem 12
     * Add a score to an existing learner.
     *
     * Return:
     * - true if the score was added
     * - false if the learner does not exist
     *
     * Valid scores are 0 through 100 inclusive.
     * Throw IllegalArgumentException for invalid scores.
     *
     * This operation should be undoable.
     */
    public boolean addScore(String name, int score) {
        if (score < 0 || score > 100){
            throw new IllegalArgumentException("Score has to be a value between 0 and 0");
        }
        var scoresFor = scoresFor(name);
        return scoresFor.map(scores -> {
            scores.add(score);
            undoStack.push(() -> scores.remove(scores.size() - 1));
            return true;
            }
        ).orElse(false);
    }

    /**
     * Problem 13
     * Return the average score for one learner.
     *
     * Return Optional.empty() if:
     * - the learner does not exist, or
     * - the learner has no scores
     */
    public Optional<Double> averageFor(String name) {
        try {
            double average = scoresFor(name).map(scores ->
                    scores.stream().mapToInt(Integer::intValue).average().orElseThrow()).orElseThrow();
            return Optional.of(average);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Problem 14
     * Convert a learner average into a letter band.
     *
     * A: 90+
     * B: 80-89.999...
     * C: 70-79.999...
     * D: 60-69.999...
     * F: below 60
     *
     * Return Optional.empty() when no average exists.
     */
    public Optional<String> letterBandFor(String name) {
        var averageOptional = averageFor(name);
        return averageOptional.map(
                average-> {
                    var firstDigit = (int)(average/10);
                    var grade = switch (firstDigit){
                        case 9,10 -> "A";
                        case 8 -> "B";
                        case 7 -> "C";
                        case 6 -> "D";
                        default -> {
                            yield "F";
                        }
                    };
                    return Optional.of(grade);
                }
        ).orElse(Optional.empty());
    }

    /**
     * Problem 15
     * Undo the most recent state-changing operation.
     *
     * Return true if something was undone.
     * Return false if there is nothing to undo.
     */
    public boolean undoLastChange() {
        if (undoStack.isEmpty()) {
            return false;
        }
        var action = undoStack.pop();
        try {
            action.undo();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }


}
