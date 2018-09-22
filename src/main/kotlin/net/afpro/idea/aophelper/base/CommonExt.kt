package net.afpro.idea.aophelper.base

val Boolean.asInt: Int
    get() = if (this) 1 else 0
