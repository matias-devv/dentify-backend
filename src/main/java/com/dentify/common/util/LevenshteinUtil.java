package com.dentify.common.util;

public class LevenshteinUtil {

    public static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int[] dp = new int[b.length() + 1];

        for (int i = 0; i <= b.length(); i++) dp[i] = i;

        for (int i = 1; i <= a.length(); i++) {

            int prev = i;

            for (int j = 1; j <= b.length(); j++) {

                int temp = dp[j];

                if (a.charAt(i-1) == b.charAt(j-1)) {

                    dp[j] = dp[j-1];

                } else {

                    dp[j] = 1 + Math.min(dp[j-1], Math.min(dp[j], prev));
                }

                dp[j-1] = prev;

                prev = temp;
            }
            dp[b.length()] = prev;
        }
        return dp[b.length()];
    }

    public static int minDistance(String q, String nombre, String apellido) {
        return Math.min(distance(q, nombre), distance(q, apellido));
    }
}
