package com.checkaboy.comparator.path;

/**
 * @author Taras Shaptala
 */
public interface IComparisonPath {

    IComparisonPath child(String node);

    String asString();

}
