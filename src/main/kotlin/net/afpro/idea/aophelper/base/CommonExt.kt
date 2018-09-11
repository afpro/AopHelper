package net.afpro.idea.aophelper.base

val <T> T?.hasRefInt: Int
    get() {
        return if (this == null)
            1
        else
            0
    }
