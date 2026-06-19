package com.novaclient.core;

import com.novaclient.core.module.ModuleManager;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Nova Client Core Test Runner");
        System.out.println("============================");
        NovaCore.getInstance().init();
        System.out.println("Nova Core loaded successfully!");
        System.out.println("Modules: " + ModuleManager.getInstance().getModules().size());
    }
}
