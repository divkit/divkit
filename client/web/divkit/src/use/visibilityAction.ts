import type { DisappearAction, VisibilityAction } from '../types/visibilityAction';
import { RootCtxValue } from '../context/root';
import { getUrlSchema, isBuiltinSchema } from '../utils/url';

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

    const calcedList = visibilityStatus.map(it => rootCtx.getJsonWithVars({
        visibility_percentage: it.action.visibility_percentage,
        visibility_duration: it.type === 'visibility' ?
            (it.action as VisibilityAction).visibility_duration :
            (it.action as DisappearAction).disappear_duration,
        log_limit: it.action.log_limit
    }));

    const thresholds = [...new Set([...visibilityStatus.map((_it, index) =>
        (calcedList[index].visibility_percentage || 50) / 100
    )])];

    const observer = new IntersectionObserver(entries => {
        entries.forEach(entry => {
            visibilityStatus.forEach(status => {
                const nowVisible = entry.intersectionRatio >= (status.action.visibility_percentage || 50) / 100;
                const shouldProc = status.type === 'visibility' ?
                    !status.visible && nowVisible :
                    status.visible && !nowVisible;
                const shouldClear = status.type === 'visibility' ?
                    !nowVisible :
                    nowVisible;

                if (shouldProc) {
                    if (!status.finished) {
                        status.timer = setTimeout(() => {
                            ++status.count;

                            const calcedParams = calcedList[status.index];
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

                            rootCtx.logStat(status.type === 'visibility' ? 'visible' : 'disappear', calcedAction);
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
