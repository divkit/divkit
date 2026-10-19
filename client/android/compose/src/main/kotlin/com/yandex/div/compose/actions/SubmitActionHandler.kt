package com.yandex.div.compose.actions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.context.DivLocalComponentStorage
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.dagger.Names
import com.yandex.div.compose.utils.children
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.internal.actions.createSubmitBody
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionSubmit
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import com.yandex.yatagan.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

@DivViewScope
internal class SubmitActionHandler @Inject constructor(
    private val actionHandler: Lazy<DivActionHandler>,
    private val localComponentStorage: DivLocalComponentStorage,
    @Named(Names.MAIN_COROUTINE_SCOPE)
    private val mainCoroutineScope: CoroutineScope,
    private val networkClient: DivNetworkClient,
    private val reporter: DivReporter,
    private val states: MutableState<List<DivData.State>>,
) {
    private val jobs = mutableSetOf<Job>()

    fun handle(
        context: DivActionHandlingContext,
        action: DivActionSubmit,
    ) {
        val expressionResolver = context.expressionResolver
        val id = action.containerId.evaluate(expressionResolver)
        val targets = mutableListOf<DivBase>()
        states.value.forEach { find(it.div, id, targets) }
        if (targets.isEmpty()) {
            reporter.reportError("Failed to perform submit action. Container not found: $id")
            return
        }

        if (targets.size > 1) {
            reporter.reportError("Failed to perform submit action. Found multiple elements that respond to id: $id")
            return
        }

        val target = targets.single()
        val targetComponent = localComponentStorage.get(target)
        if (targetComponent == null) {
            reporter.reportError("Failed to perform submit action. Container component not found: $id")
            return
        }

        val method = action.request.method.evaluate(expressionResolver)
        val body = if (method == DivActionSubmit.Request.Method.GET ||
            method == DivActionSubmit.Request.Method.HEAD
        ) {
            null
        } else {
            createSubmitBody(
                variables = target.variables,
                expressionResolver = targetComponent.expressionResolver,
                reportError = reporter::reportError,
            ).toByteArray()
        }
        val url = action.request.url.evaluate(expressionResolver).toString()
        val request = DivNetworkRequest.Builder(url)
            .method(method.toString())
            .body(body)
            .apply {
                action.request.headers?.forEach {
                    addHeader(
                        name = it.name.evaluate(expressionResolver),
                        value = it.value.evaluate(expressionResolver)
                    )
                }
            }
            .build()
        execute(
            request,
            onSuccess = { handleActions(context, action.onSuccessActions.orEmpty()) },
            onFail = { handleActions(context, action.onFailActions.orEmpty()) },
        )
    }

    private fun handleActions(context: DivActionHandlingContext, actions: List<DivAction>) {
        actionHandler.get().handle(
            context = context,
            actions = actions,
            source = DivActionSource.SUBMIT,
        )
    }

    private fun find(
        div: Div,
        id: String,
        result: MutableList<DivBase>,
    ) {
        if (div.value().id == id) {
            result.add(div.value())
        }
        div.children().forEach { find(it, id, result) }
    }

    private fun execute(
        request: DivNetworkRequest,
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        val job = mainCoroutineScope.launch(start = CoroutineStart.LAZY) {
            try {
                val response = try {
                    networkClient.execute(request)
                } catch (e: IOException) {
                    reporter.reportError(e)
                    onFail()
                    return@launch
                }
                response.use {
                    if (it.isSuccessful) {
                        onSuccess()
                    } else {
                        reporter.reportError("HTTP ${it.code}")
                        onFail()
                    }
                }
            } finally {
                jobs.remove(currentCoroutineContext()[Job])
            }
        }
        jobs.add(job)
        job.start()
    }

    fun detach() {
        val pendingJobs = jobs.toList()
        jobs.clear()
        pendingJobs.forEach { it.cancel() }
    }
}

@Composable
internal fun SubmitActionHandler.observe() {
    DisposableEffect(this) {
        onDispose { detach() }
    }
}
