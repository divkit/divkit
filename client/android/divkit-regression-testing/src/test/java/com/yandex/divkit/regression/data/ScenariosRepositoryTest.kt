package com.yandex.divkit.regression.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
class ScenariosRepositoryTest {

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val dataSource = FakeScenariosDataSource()

    private val underTest = ScenariosRepository(dataSource, testCoroutineDispatcher)

    @After
    fun tearDown() {
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `load scenarios sorted by priority and title`() = testCoroutineDispatcher.runBlockingTest {
        dataSource.addScenario(Scenario(title = "foo", priority = Priority.normal))
        dataSource.addScenario(Scenario(title = "fuz", priority = Priority.minor))
        dataSource.addScenario(Scenario(title = "bar", priority = Priority.blocker))
        dataSource.addScenario(Scenario(title = "buz", priority = Priority.normal))
        dataSource.addScenario(Scenario(title = "buc", priority = Priority.critical))

        launch {
            val scenarios = underTest.loadScenarios()
            Assert.assertEquals(
                listOf("bar", "buc", "buz", "foo", "fuz"),
                scenarios.map { it.title }
            )
        }
    }

    @Test
    fun `load only android tests`() = testCoroutineDispatcher.runBlockingTest {
        dataSource.addScenario(Scenario(title = "foo", platforms = listOf(Platforms.android)))
        dataSource.addScenario(Scenario(title = "fuz", platforms = listOf(Platforms.ios)))
        dataSource.addScenario(Scenario(title = "bar", platforms = listOf(Platforms.web)))

        launch {
            val scenarios = underTest.loadScenarios()
            Assert.assertEquals(
                listOf("foo"),
                scenarios.map { it.title }
            )
        }
    }

    @Test
    fun `return item count`() = testCoroutineDispatcher.runBlockingTest {
        repeat(3) {
            dataSource.addScenario(Scenario())
        }

        launch {
            val count = underTest.itemCount()
            Assert.assertEquals(3, count)
        }
    }
}
