package com.yandex.divkit.regression.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
class ScenariosRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dataSource = FakeScenariosDataSource()

    private val underTest = ScenariosRepository(dataSource, testDispatcher)

    @Test
    fun `load scenarios sorted by priority and title`() = runTest(testDispatcher) {
        dataSource.addScenario(Scenario(title = "foo", priority = Priority.normal))
        dataSource.addScenario(Scenario(title = "fuz", priority = Priority.minor))
        dataSource.addScenario(Scenario(title = "bar", priority = Priority.blocker))
        dataSource.addScenario(Scenario(title = "buz", priority = Priority.normal))
        dataSource.addScenario(Scenario(title = "buc", priority = Priority.critical))

        val scenarios = underTest.loadScenarios()
        Assert.assertEquals(
            listOf("bar", "buc", "buz", "foo", "fuz"),
            scenarios.map { it.title }
        )
    }

    @Test
    fun `load only android tests`() = runTest(testDispatcher) {
        dataSource.addScenario(Scenario(title = "foo", platforms = listOf(Platforms.android)))
        dataSource.addScenario(Scenario(title = "fuz", platforms = listOf(Platforms.ios)))
        dataSource.addScenario(Scenario(title = "bar", platforms = listOf(Platforms.web)))

        val scenarios = underTest.loadScenarios()
        Assert.assertEquals(
            listOf("foo"),
            scenarios.map { it.title }
        )
    }

    @Test
    fun `return item count`() = runTest(testDispatcher) {
        repeat(3) {
            dataSource.addScenario(Scenario())
        }

        val count = underTest.itemCount()
        Assert.assertEquals(3, count)
    }
}
