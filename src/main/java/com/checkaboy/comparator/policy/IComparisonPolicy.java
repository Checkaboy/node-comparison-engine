package com.checkaboy.comparator.policy;

import com.checkaboy.comparator.path.IComparisonPath;

/**
 * @author Taras Shaptala
 */
public interface IComparisonPolicy {

    boolean isFieldEnabled(IComparisonPath path);

}
