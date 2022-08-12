import type { VisibilityAction } from '../types/visibilityAction';
import type { MaybeMissing } from '../expressions/json';
import { RootCtxValue } from '../context/root';
import { getUrlSchema, isBuiltinSchema } from '../utils/url';

export function visibilityAction(node: HTMLElement, {
    visibilityActions,
    rootCtx
}: {
    visibilityActions?: MaybeMissing<VisibilityAction[]>;
    rootCtx: RootCtxValue;
}) {
    if (!visibilityActions?.length) {
        return;
    }

    const visibilityStatus: {
        action: MaybeMissing<VisibilityAction>;
        visible: boolean;
        count: number;
        finished: boolean;
        timer?: ReturnType<typeof setTimeout>;
    }[] = visibilityActions.map(it => ({
        action: it,
        visible: false,
        count: 0,
        finished: false
    }));

    const thresholds = [...new Set([...visibilityActions.map(it => (it.visibility_percentage || 50) / 100)])];

    const observer = new IntersectionObserver(entries => {
        entries.forEach(entry => {
            visibilityStatus.forEach(status => {
                const nowVisible = entry.intersectionRatio >= (status.action.visibility_percentage || 50) / 100;

                if (!status.visible && nowVisible) {
                    if (!status.finished) {
                        status.timer = setTimeout(() => {
                            ++status.count;
                            const limit = status.action.log_limit === 0 ? Infinity : (status.action.log_limit || 1);
                            if (++status.count >= limit) {
                                status.finished = true;
                            }

                            const actionUrl = status.action.url;
                            if (actionUrl) {
                                const schema = getUrlSchema(actionUrl);
                                if (schema && !isBuiltinSchema(schema)) {
                                    if (schema === 'div-action') {
                                        rootCtx.execAction(status.action);
                                    } else if (status.action.log_id) {
                                        rootCtx.execCustomAction(status.action as VisibilityAction & { url: string });
                                    }
                                }
                            }

                            rootCtx.logStat('visible', status.action);
                        }, status.action.visibility_duration || 800);
                    }
                } else if (!nowVisible) {
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
        }
    };
}
