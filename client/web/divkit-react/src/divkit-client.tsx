import React from 'react';
import { render as clientRender } from '@divkitframework/divkit/client-hydratable';
import type { DivKitHandle, DivKitProps } from '../typings/divkit';

export const DivKit = React.forwardRef<DivKitHandle, DivKitProps>(function DivKit(props, ref) {
    const containerRef = React.useRef<HTMLDivElement>(null);
    const instanceRef = React.useRef<ReturnType<typeof clientRender> | null>(null);
    // Keep latest props without remount: parent re-renders create a new `props` object every time,
    // but we only recreate the DivKit instance when json/id change (see deps below).
    const propsRef = React.useRef(props);
    const { id, json } = props;

    propsRef.current = props;

    React.useImperativeHandle(ref, () => ({
        applyPatch(patch) {
            if (!instanceRef.current) {
                return false;
            }

            return instanceRef.current.applyPatch(patch);
        }
    }));

    React.useEffect(() => {
        if (!containerRef.current) {
            return;
        }

        const instance = clientRender({
            ...propsRef.current,
            target: containerRef.current,
            hydrate: true
        });

        instanceRef.current = instance;

        return () => {
            instance.$destroy();
            instanceRef.current = null;
        };
    }, [id, json]);

    return <div ref={containerRef} suppressHydrationWarning dangerouslySetInnerHTML={{ __html: '' }} />;
});
