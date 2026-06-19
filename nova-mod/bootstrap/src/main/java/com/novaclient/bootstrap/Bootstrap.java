package com.novaclient.bootstrap;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

public class Bootstrap {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[Nova Client] Bootstrap agent starting...");

        String classpathProp = System.getProperty("novaclient.classpath");
        if (classpathProp != null && !classpathProp.isEmpty()) {
            String[] paths = classpathProp.split(";");
            for (String p : paths) {
                String trimmed = p.trim();
                if (!trimmed.isEmpty()) {
                    File f = new File(trimmed);
                    if (f.exists() && f.getName().endsWith(".jar")) {
                        try {
                            inst.appendToSystemClassLoaderSearch(new JarFile(f));
                            System.out.println("[Nova Client] Added to classpath: " + trimmed);
                        } catch (Exception e) {
                            System.err.println("[Nova Client] Failed to add " + trimmed + ": " + e.getMessage());
                        }
                    }
                }
            }
        }

        String adapterClass = System.getProperty("novaclient.adapter");
        if (adapterClass != null && !adapterClass.isEmpty()) {
            try {
                Class.forName(adapterClass);
                System.out.println("[Nova Client] Adapter loaded: " + adapterClass);
            } catch (Throwable t) {
                System.err.println("[Nova Client] Failed to load adapter: " + t.getMessage());
            }
        }

        System.out.println("[Nova Client] Bootstrap done");
    }
}
