import type { DisappearAction, VisibilityAction } from '../../typings/common';
import type { RootCtxValue } from '../context/root';
import { getUrlSchema, isBuiltinSchema } from '../utils/url';

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

export function visibilityAction(node: HTMLElement, {
    visibilityActions,
    disappearActions,
    rootCtx
}: {
    visibilityActions?: VisibilityAction[];
    disappearActions?: DisappearAction[];
    rootCtx: RootCtxValue;
}) {
    const visibilityStatus: {
        type: 'visibility' | 'disappear';
        index: number;
        action: VisibilityAction | DisappearAction;
        visible: boolean;
        count: number;
        finished: boolean;
        timer?: ReturnType<typeof setTimeout>;
    }[] = [];

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
                visible: false,
                count: 0,
                // false, so disappear only works after the element becomes visible
                finished: false
            });
        });
    }

    const calcedList: {
        visibility_percentage: number;
        visibility_duration: number | undefined;
        log_limit: number | undefined;
    }[] = visibilityStatus.map(it => {
        const isVisibility = it.type === 'visibility';
        const calced = rootCtx.getJsonWithVars({
            visibility_percentage: it.action.visibility_percentage,
            visibility_duration: isVisibility ?
                (it.action as VisibilityAction).visibility_duration :
                (it.action as DisappearAction).disappear_duration,
            log_limit: it.action.log_limit
        });

        return {
            ...calced,
            visibility_percentage: checkPercentage(
                isVisibility,
                calced.visibility_percentage,
                isVisibility ? 50 : 0
            )
        };
    });

    const thresholds = [...new Set(calcedList.map(it =>
        it.visibility_percentage / 100
    ))];

    const observer = new IntersectionObserver(entries => {
        entries.forEach(entry => {
            visibilityStatus.forEach(status => {
                const calcedParams = calcedList[status.index];
                const isVisibility = status.type === 'visibility';
                let nowVisible;
                if (calcedParams.visibility_percentage === 0) {
                    nowVisible = entry.intersectionRatio >= 1e-12;
                } else {
                    nowVisible = entry.intersectionRatio >= (calcedParams.visibility_percentage / 100);
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

                            const calcedAction = rootCtx.getJsonWithVars(status.action);
                            const actionUrl = calcedAction.url;
                            if (actionUrl) {
                                const schema = getUrlSchema(actionUrl);
                                if (schema && !isBuiltinSchema(schema, rootCtx.getBuiltinProtocols())) {
                                    if (schema === 'div-action') {
                                        rootCtx.execAction(calcedAction);
                                    } else if (calcedAction.log_id) {
                                        rootCtx.execCustomAction(calcedAction as VisibilityAction & { url: string });
                                    }
                                }
                            }

                            rootCtx.logStat(isVisibility ? 'visible' : 'disappear', calcedAction);
                        }, calcedList[status.index].visibility_duration || 800);
                    }
                } else if (shouldClear) {
                    if (status.timer) {
                        clearTimeout(status.timer);
                    }
                }
                status.visible = nowVisible;
            });
        });
    }, {
        threshold: thresholds
    });

    observer.observe(node);

    return {
        destroy() {
            observer.disconnect();

            visibilityStatus.forEach(status => {
                if (status.timer) {
                    clearTimeout(status.timer);
                }
            });
        }
    };
}
