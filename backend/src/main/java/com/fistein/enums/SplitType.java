package com.fistein.enums;

/**
 * Enum representing different ways to split an expense
 */
public enum SplitType {
    /**
     * Split equally among all participants
     */
    EQUAL,
    
    /**
     * Split with exact amounts specified for each participant
     */
    EXACT,
    
    /**
     * Split based on percentage for each participant
     */
    PERCENTAGE
}