package com.yandex.div.compose.actions

import androidx.compose.runtime.MutableState
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.collection.CollectionItemScopeStorage
import com.yandex.div.compose.context.DivLocalComponentStorage
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.DivLocalScope
import com.yandex.div.compose.di.Names
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.compose.tooltips.TooltipStateStorage
import com.yandex.div.internal.core.DivBlockLocator
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import com.yandex.div2.DivSightAction
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named

/** Context of the element that triggered the action, including its available action scopes. */
@DivLocalScope
@Inject
public class DivActionHandlingContext internal constructor(
    @param:Named(Names.CARD_ID) public val cardId: String,
    public val expressionResolver: ExpressionResolver,
    private val localComponentStorage: DivLocalComponentStorage,
    private val collectionItemScopeStorage: CollectionItemScopeStorage,
    private val states: MutableState<List<DivData.State>>,
    private val stateStorage: DivStateStorage,
    private val tooltipStateStorage: TooltipStateStorage,
    private val reporter: DivReporter,
    @Named(Names.ROOT_LOCAL_COMPONENT)
    private val rootLocalComponent: Lazy<DivLocalComponent>,
    private val scopeSearchResolver: ExpressionResolver = expressionResolver,
) {
    internal val originExpressionResolver: ExpressionResolver
        get() = expressionResolver

    /** How many times each limited sight action of this scope has been logged. */
    internal val sightActionCounts = mutableMapOf<DivSightAction, Int>()

    internal fun findScopeResolver(scopeId: String?): ExpressionResolver? {
        if (scopeId == null) {
            return originExpressionResolver
        }
        val environment = ComposeDivBlockSearchEnvironment(
            state = stateStorage.getActiveRootState(states.value),
            rootComponent = rootLocalComponent,
            localComponents = localComponentStorage,
            collectionItems = collectionItemScopeStorage,
            stateStorage = stateStorage,
            tooltipStateStorage = tooltipStateStorage,
        )
        // The scope ID is the target itself; do not use the locator's missing-scope fallback.
        val target = DivBlockLocator(environment).findTarget(
            divId = scopeId,
            scopeId = null,
            actionResolver = scopeSearchResolver,
            matches = { true },
        )
        val error = target.exceptionOrNull()
        if (error != null) {
            reporter.reportError(
                IllegalArgumentException("Failed to resolve action scope '$scopeId'", error)
            )
        }
        return target.getOrNull()?.expressionResolver
    }

    /**
     * Adds an evaluation resolver while preserving the source scope used to locate nearby targets.
     */
    internal fun wrap(resolver: ExpressionResolver): DivActionHandlingContext {
        if (resolver === originExpressionResolver) {
            return this
        }
        return DivActionHandlingContext(
            cardId = cardId,
            expressionResolver = resolver,
            localComponentStorage = localComponentStorage,
            collectionItemScopeStorage = collectionItemScopeStorage,
            states = states,
            stateStorage = stateStorage,
            tooltipStateStorage = tooltipStateStorage,
            reporter = reporter,
            rootLocalComponent = rootLocalComponent,
            scopeSearchResolver = scopeSearchResolver,
        )
    }
}
