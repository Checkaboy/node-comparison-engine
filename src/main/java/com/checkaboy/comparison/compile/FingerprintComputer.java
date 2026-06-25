package com.checkaboy.comparison.compile;

import java.util.Map;

/**
 * Fingerprint utility for plan caching.
 *
 * @author Taras Shaptala
 */
public class FingerprintComputer {

    /**
     * Compute SHA256 fingerprint of a plan.
     */
    public static String computeFingerprint(
            String graphId,
            String graphVersion,
            String policyVersion,
            Map<String, String> providerVersions
    ) {
        StringBuilder input = new StringBuilder();
        input.append(graphId).append("|");
        input.append(graphVersion != null ? graphVersion : "1.0").append("|");
        input.append(policyVersion != null ? policyVersion : "1.0").append("|");

        // Sort provider versions for deterministic output
        providerVersions.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> input.append(e.getKey()).append(":").append(e.getValue()).append("|"));

        return sha256(input.toString());
    }

    /**
     * Compute SHA256 hash.
     */
    private static String sha256(String input) {
        try {
            java.security.MessageDigest digest =
                    java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

}