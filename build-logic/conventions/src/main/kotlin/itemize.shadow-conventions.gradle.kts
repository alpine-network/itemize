/*
 * This file is part of Itemize - https://github.com/alpine-network/itemize
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("itemize.base-conventions")
    id("com.gradleup.shadow")
}

tasks {
    named<Jar>("jar") {
        enabled = false
    }
    named<ShadowJar>("shadowJar") {
        // https://gradleup.com/shadow/configuration/merging/#handling-duplicates-strategy
        duplicatesStrategy = DuplicatesStrategy.WARN
        failOnDuplicateEntries = true
        archiveClassifier.set("")

        mergeServiceFiles()
    }
    named("build") {
        dependsOn(named("shadowJar"))
    }
}