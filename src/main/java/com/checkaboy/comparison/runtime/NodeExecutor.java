package com.checkaboy.comparison.runtime;

import com.checkaboy.comparison.spi.ExecutionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Node executor - executes nodes.
 *
 * @author Taras Shaptala
 */
public class NodeExecutor {

    private static final AtomicLong eventIdCounter = new AtomicLong(0);

    /**
     * Execute a node.
     */
    public ExecutionOutcome executeNode(
            CompiledNodePlan nodePlan,
            Object sourceValue,
            Object targetValue,
            ExecutionContext context,
            ExecutionStateManager stateManager
    ) {
        String eventId = "node-" + eventIdCounter.incrementAndGet();
        long timestamp = System.currentTimeMillis();
        String nodeId = nodePlan.getNodeId();

        try {
            // Check cycle detection
            if (!stateManager.canTraverse(nodeId, sourceValue, targetValue)) {
                if (context.getExecutionSemantics().getCycleHandling()
                        == com.checkaboy.comparison.config.CycleHandlingMode.ERROR) {
                    throw new ExecutionException(
                            "Cycle detected for node: " + nodeId,
                            "CYCLE_DETECTED"
                    );
                } else {
                    return new ExecutionOutcome(
                            eventId,
                            timestamp,
                            ExecutionEventType.NODE_COMPLETED,
                            ResultStatus.SKIPPED,
                            sourceValue,
                            targetValue,
                            null,
                            null,
                            null,
                            null,
                            context.getCurrentPath(),
                            new HashMap<>()
                    );
                }
            }

            // Record traversal
            stateManager.recordTraversal(nodeId, sourceValue, targetValue);
            stateManager.beginPath(nodeId);

            // Execute node behavior (orchestrates edges)
            EdgeCallback edgeCallback = (edgePlan, srcVal, tgtVal, ctx) ->
                    new EdgeExecutor().executeEdge(edgePlan, srcVal, tgtVal, ctx);

            // Simplified - would invoke nodeBehavior.orchestrate()
            List<ExecutionOutcome> edgeOutcomes = new ArrayList<>();

            for (CompiledEdgePlan edgePlan : nodePlan.getEdgePlans()) {
                ExecutionOutcome edgeOutcome = new EdgeExecutor().executeEdge(
                        edgePlan,
                        sourceValue,
                        targetValue,
                        context
                );
                edgeOutcomes.add(edgeOutcome);
            }

            // Aggregate outcomes
            ResultStatus aggregatedStatus = aggregateStatus(edgeOutcomes);

            stateManager.endPath();

            ExecutionOutcome nodeOutcome = new ExecutionOutcome(
                    eventId,
                    timestamp,
                    ExecutionEventType.NODE_COMPLETED,
                    aggregatedStatus,
                    sourceValue,
                    targetValue,
                    null,
                    null,
                    null,
                    null,
                    context.getCurrentPath(),
                    Map.of(
                            "edge_count", edgeOutcomes.size(),
                            "matched_edges", (int)edgeOutcomes.stream()
                                    .filter(ExecutionOutcome::isMatch)
                                    .count()
                    )
            );

            context.getResultSink().reportOutcome(nodeOutcome);
            return nodeOutcome;

        } catch (Exception e) {
            stateManager.endPath();

            ExecutionError error = new ExecutionError(
                    "NODE_EXECUTION_ERROR",
                    e,
                    "NodeExecutor",
                    nodeId,
                    false,
                    e.getMessage(),
                    Map.of("node_id", nodeId)
            );

            ExecutionOutcome nodeOutcome = new ExecutionOutcome(
                    eventId,
                    timestamp,
                    ExecutionEventType.NODE_ERROR,
                    ResultStatus.ERROR,
                    sourceValue,
                    targetValue,
                    null,
                    null,
                    null,
                    error,
                    context.getCurrentPath(),
                    new HashMap<>()
            );

            context.getResultSink().reportOutcome(nodeOutcome);

            if (context.getExecutionSemantics().getErrorHandling()
                    == com.checkaboy.comparison.config.ErrorHandlingMode.FAIL_FAST) {
                throw new ExecutionException(e.getMessage(), e);
            }

            return nodeOutcome;
        }
    }

    /**
     * Aggregate edge outcomes to node status.
     */
    private ResultStatus aggregateStatus(List<ExecutionOutcome> outcomes) {
        if (outcomes.isEmpty()) {
            return ResultStatus.MATCH;
        }

        boolean hasError = outcomes.stream().anyMatch(ExecutionOutcome::isError);
        if (hasError) {
            return ResultStatus.ERROR;
        }

        boolean allMatch = outcomes.stream().allMatch(ExecutionOutcome::isMatch);
        return allMatch ? ResultStatus.MATCH : ResultStatus.MISMATCH;
    }

}