package com.novaclient.core.event;

public class AttackEvent extends Event {
    private final Object target;

    public AttackEvent(Object target) {
        this.target = target;
    }

    public Object getTarget() {
        return target;
    }
}
