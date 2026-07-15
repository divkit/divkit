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

    @Test
    fun `find scenario by case id`() = runTest(testDispatcher) {
        dataSource.addScenario(Scenario(title = "Simple animation", case_id = 42))
        dataSource.addScenario(Scenario(title = "Variable test", case_id = 100))

        val scenario = underTest.findScenarioByCaseId(100)
        Assert.assertEquals("Variable test", scenario?.title)
        Assert.assertNull(underTest.findScenarioByCaseId(999))
    }

    @Test
    fun `find scenario by title case insensitive`() = runTest(testDispatcher) {
        dataSource.addScenario(Scenario(title = "Variable test", case_id = 100))

        Assert.assertEquals(100, underTest.findScenarioByTitle("VARIABLE TEST")?.case_id)
        Assert.assertNull(underTest.findScenarioByTitle("Unknown"))
    }

    @Test
    fun `find scenario excludes ios-only tests`() = runTest(testDispatcher) {
        dataSource.addScenario(Scenario(title = "ios only", case_id = 1, platforms = listOf(Platforms.ios)))
        dataSource.addScenario(Scenario(title = "android", case_id = 2, platforms = listOf(Platforms.android)))

        Assert.assertNull(underTest.findScenarioByCaseId(1))
        Assert.assertEquals("android", underTest.findScenarioByCaseId(2)?.title)
    }
}
