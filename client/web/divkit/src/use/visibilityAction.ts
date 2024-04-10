import { derived, type Readable } from 'svelte/store';
import type { BooleanInt, DisappearAction, VisibilityAction } from '../../typings/common';
import type { RootCtxValue } from '../context/root';
import type { MaybeMissing } from '../expressions/json';
import type { ComponentContext } from '../types/componentContext';
import { getUrlSchema, isBuiltinSchema } from '../utils/url';
import { correctNonNegativeNumber } from '../utils/correctNonNegativeNumber';

interface CalcedAction {
    index: number | undefined;
    visibility_percentage: number | undefined;
    visibility_duration: number | undefined;
    log_limit: number | undefined;
    is_enabled: BooleanInt | undefined;
}

interface IndexedCalcedAction extends CalcedAction {
    index: number;
}

interface VisibilityStatus {
    type: 'visibility' | 'disappear';
    index: number;
    action: MaybeMissing<VisibilityAction | DisappearAction>;
    visible: boolean;
    count: number;
    finished: boolean;
    timer?: ReturnType<typeof setTimeout>;
}

function checkPercentage(isVisibility: boolean, val: number | undefined, defaultVal: number): number {
    if (typeof val === 'number') {
        if (
            isVisibility && val > 0 && val <= 100 ||
            !isVisibility && val >= 0 && val < 100
        ) {
            return val;
        }
    }
    return defaultVal;
}

function filterActions(it: CalcedAction): it is IndexedCalcedAction {
    return it.is_enabled !== 0 && it.is_enabled !== false && it.index !== undefined;
}

export function visibilityAction(node: HTMLElement, {
    visibilityActions,
    disappearActions,
    rootCtx,
    componentContext
}: {
    visibilityActions?: MaybeMissing<VisibilityAction>[];
    disappearActions?: MaybeMissing<DisappearAction>[];
    rootCtx: RootCtxValue;
    componentContext: ComponentContext;
}) {
    const visibilityStatus: VisibilityStatus[] = [];

    if (visibilityActions) {
        visibilityActions.forEach(it => {
            visibilityStatus.push({
                type: 'visibility',
                index: visibilityStatus.length,
                action: it,
                visible: false,
                count: 0,
                finished: false
            });
        });
    }

    if (disappearActions) {
        disappearActions.forEach(it => {
            visibilityStatus.push({
                type: 'disappear',
                index: visibilityStatus.length,
                action: it,
                // false, so disappear only works after the element becomes visible
                visible: false,
                count: 0,
                finished: false
            });
        });
    }

    const calcedList: Readable<CalcedAction>[] = visibilityStatus.map((it, index) => {
        const isVisibility = it.type === 'visibility';

        return componentContext.getDerivedFromVars({
            index,
            visibility_percentage: it.action.visibility_percentage,
            visibility_duration: isVisibility ?
                (it.action as VisibilityAction).visibility_duration :
                (it.action as DisappearAction).disappear_duration,
            log_limit: it.action.log_limit,
            is_enabled: it.action.is_enabled,
        });
    });

    let observer: IntersectionObserver | undefined;
    const cleanup = () => {
        if (observer) {
            observer.disconnect();
        }

        visibilityStatus.forEach(status => {
            if (status.timer) {
                clearTimeout(status.timer);
            }
        });
    };

    const totalStore = derived(calcedList, values => values);
    let filtered: IndexedCalcedAction[];

    const callAction = (status: VisibilityStatus) => {
        const isVisibility = status.type === 'visibility';
        const calcedAction = componentContext.getJsonWithVars(status.action);
        const actionUrl = calcedAction.url;
        const actionTyped = calcedAction.typed;
        if (actionUrl) {
            const schema = getUrlSchema(actionUrl);
            if (schema && !isBuiltinSchema(schema, rootCtx.getBuiltinProtocols())) {
                if (schema === 'div-action') {
                    rootCtx.execAction(calcedAction);
                } else if (calcedAction.log_id) {
                    rootCtx.execCustomAction(
                        calcedAction as VisibilityAction & { url: string }
                    );
                }
            }
        } else if (actionTyped) {
            rootCtx.execAction(calcedAction);
        }

        rootCtx.logStat(isVisibility ? 'visible' : 'disappear', calcedAction);
    };

    const unsubscribe = totalStore.subscribe(values => {
        filtered = values.filter(filterActions);

        const map: Record<number, IndexedCalcedAction> = {};
        filtered.forEach(it => {
            map[it.index] = it;
        });

        cleanup();

        const thresholds = [...new Set(filtered.map(it => {
            const isVisibility = visibilityStatus[it.index].type === 'visibility';

            return checkPercentage(
                isVisibility,
                it.visibility_percentage,
                isVisibility ? 50 : 0
            ) / 100;
        }))];

        if (!thresholds.length) {
            return;
        }

        const observerCallback = (entries: IntersectionObserverEntry[]): void => {
            entries.forEach(entry => {
                filtered.forEach(calcedParams => {
                    const status = visibilityStatus[calcedParams.index];
                    const isVisibility = status.type === 'visibility';
                    const percentage = checkPercentage(
                        isVisibility,
                        calcedParams.visibility_percentage,
                        isVisibility ? 50 : 0
                    );

                    let nowVisible;
                    if (percentage === 0) {
                        nowVisible = entry.intersectionRatio >= 1e-12;
                    } else {
                        nowVisible = entry.intersectionRatio >= (percentage / 100);
                    }

                    const shouldProc = isVisibility ?
                        !status.visible && nowVisible :
                        status.visible && !nowVisible;
                    const shouldClear = isVisibility ?
                        !nowVisible :
                        nowVisible;

                    if (shouldProc) {
                        if (!status.finished) {
                            status.timer = setTimeout(() => {
                                ++status.count;

                                const limit = calcedParams.log_limit === 0 ? Infinity : (calcedParams.log_limit || 1);
                                if (++status.count >= limit) {
                                    status.finished = true;
                                }

                                callAction(status);
                            }, correctNonNegativeNumber(calcedParams.visibility_duration, 800));
                        }
                    } else if (shouldClear) {
                        if (status.timer) {
                            clearTimeout(status.timer);
                        }
                    }
                    status.visible = nowVisible;
                });
            });
        };

        observer = new IntersectionObserver(observerCallback, {
            threshold: thresholds
        });

        observer.observe(node);
    });

    return {
        destroy() {
            filtered?.forEach(calcedAction => {
                const status = visibilityStatus[calcedAction.index];

                if (!status || status.type !== 'disappear' || !status.visible || status.finished) {
                    return;
                }

                rootCtx.registerTimeout(window.setTimeout(() => {
                    callAction(status);
                }, calcedAction.visibility_duration));
            });

            cleanup();

            unsubscribe();
        }
    };
}
