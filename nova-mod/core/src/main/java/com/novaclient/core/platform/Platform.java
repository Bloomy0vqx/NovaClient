package com.novaclient.core.platform;

/**
 * Static accessor for the platform adapter.
 * Adapters register themselves here during initialization.
 */
public class Platform {
    private static PlatformAdapter adapter;

    public static void setAdapter(PlatformAdapter adapter) {
        Platform.adapter = adapter;
    }

    public static PlatformAdapter get() {
        if (adapter == null) {
            throw new IllegalStateException("PlatformAdapter not registered! Ensure your version adapter initializes before using core features.");
        }
        return adapter;
    }

    public static boolean isInitialized() {
        return adapter != null;
    }
}
