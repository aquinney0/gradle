/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.buildinit.plugins

import org.gradle.buildinit.plugins.fixtures.ScriptDslFixture
import org.gradle.test.fixtures.file.LeaksFileHandles

import static org.gradle.buildinit.plugins.internal.modifiers.BuildInitDsl.KOTLIN

@LeaksFileHandles
class KotlinApplicationInitIntegrationTest extends AbstractJvmLibraryInitIntegrationSpec {

    public static final String SAMPLE_APP_CLASS = "org/example/App.kt"
    public static final String SAMPLE_APP_TEST_CLASS = "org/example/AppTest.kt"

    @Override
    String subprojectName() { 'app' }

    def "defaults to kotlin build scripts"() {
        when:
        run ('init', '--type', 'kotlin-application')

        then:
        dslFixtureFor(KOTLIN).assertGradleFilesGenerated()
    }

    def "creates sample source if no source present with #scriptDsl build scripts"() {
        when:
        run('init', '--type', 'kotlin-application', '--dsl', scriptDsl.id)

        then:
        subprojectDir.file("src/main/kotlin").assertHasDescendants(SAMPLE_APP_CLASS)
        subprojectDir.file("src/test/kotlin").assertHasDescendants(SAMPLE_APP_TEST_CLASS)

        and:
        commonJvmFilesGenerated(scriptDsl)

        when:
        run("build")

        then:
        assertTestPassed("org.example.AppTest", "appHasAGreeting")

        when:
        run("run")

        then:
        outputContains("Hello World!")

        where:
        scriptDsl << ScriptDslFixture.SCRIPT_DSLS
    }

    def "creates build using test suites with #scriptDsl build scripts when using --incubating"() {
        def dslFixture = dslFixtureFor(scriptDsl)

        when:
        run('init', '--type', 'kotlin-application', '--dsl', scriptDsl.id, '--incubating')

        then:
        subprojectDir.file("src/main/kotlin").assertHasDescendants(SAMPLE_APP_CLASS)
        subprojectDir.file("src/test/kotlin").assertHasDescendants(SAMPLE_APP_TEST_CLASS)

        and:
        commonJvmFilesGenerated(scriptDsl)
        dslFixture.assertHasTestSuite('test')

        when:
        run("build")

        then:
        assertTestPassed("org.example.AppTest", "appHasAGreeting")

        when:
        run("run")

        then:
        outputContains("Hello World!")

        where:
        scriptDsl << ScriptDslFixture.SCRIPT_DSLS
    }

    def "creates with gradle.properties when using #scriptDsl build scripts with --incubating"() {
        when:
        run('init', '--type', 'kotlin-application', '--dsl', scriptDsl.id, '--incubating')

        then:
        gradlePropertiesGenerated()

        when:
        run("build")

        then:
        assertTestPassed("org.example.AppTest", "appHasAGreeting")

        when:
        run("run")

        then:
        outputContains("Hello World!")

        where:
        scriptDsl << ScriptDslFixture.SCRIPT_DSLS
    }

    def "creates sample source with package and #scriptDsl build scripts"() {
        when:
        run('init', '--type', 'kotlin-application', '--package', 'my.app', '--dsl', scriptDsl.id)

        then:
        subprojectDir.file("src/main/kotlin").assertHasDescendants("my/app/App.kt")
        subprojectDir.file("src/test/kotlin").assertHasDescendants("my/app/AppTest.kt")

        and:
        commonJvmFilesGenerated(scriptDsl)

        when:
        run("build")

        then:
        assertTestPassed("my.app.AppTest", "appHasAGreeting")

        when:
        run("run")

        then:
        outputContains("Hello World!")

        where:
        scriptDsl << ScriptDslFixture.SCRIPT_DSLS
    }

    def "setupProjectLayout is skipped when kotlin sources detected with #scriptDsl build scripts"() {
        setup:
        subprojectDir.file("src/main/kotlin/org/acme/SampleMain.kt") << """
        package org.acme

        class SampleMain {
        }
"""
        subprojectDir.file("src/test/kotlin/org/acme/SampleMainTest.kt") << """
                package org.acme

                class SampleMainTest {

                    @org.junit.jupiter.api.Test
                    fun sampleTest() { }
                }
        """
        when:
        run('init', '--type', 'kotlin-application', '--dsl', scriptDsl.id, '--overwrite')

        then:
        subprojectDir.file("src/main/kotlin").assertHasDescendants("org/acme/SampleMain.kt")
        subprojectDir.file("src/test/kotlin").assertHasDescendants("org/acme/SampleMainTest.kt")
        dslFixtureFor(scriptDsl).assertGradleFilesGenerated()

        when:
        run("build")

        then:
        executed(":app:test")

        where:
        scriptDsl << ScriptDslFixture.SCRIPT_DSLS
    }
}
