package com.checkaboy.comparator.policy;

import com.checkaboy.comparator.path.IComparisonPath;
import com.checkaboy.comparator.rule.ExcludeFieldRule;
import com.checkaboy.comparator.rule.IComparisonRule;
import com.checkaboy.comparator.rule.IncludeFieldRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Taras Shaptala
 */
public final class DeclarativeComparisonPolicy
        implements IComparisonPolicy {

    private final List<String> includes;
    private final List<String> excludes;

    public DeclarativeComparisonPolicy(List<IComparisonRule> rules) {
        List<String> in = new ArrayList<>();
        List<String> ex = new ArrayList<>();
        for (IComparisonRule rule : rules) {
            if (rule instanceof IncludeFieldRule) in.add(((IncludeFieldRule) rule).path());
            if (rule instanceof ExcludeFieldRule) ex.add(((ExcludeFieldRule) rule).path());
        }

        this.includes = Collections.unmodifiableList(in);
        this.excludes = Collections.unmodifiableList(ex);
    }

    @Override
    public boolean isFieldEnabled(IComparisonPath path) {
        String p = path.asString();
        for (String ex : excludes) if (matches(ex, p)) return false;
        if (includes.isEmpty()) return true;
        for (String in : includes) if (matches(in, p) || matches(p, in)) return true;
        return false;
    }

    private boolean matches(String rule, String path) {
        return rule.equals(path) || path.startsWith(rule + ".");
    }
}
