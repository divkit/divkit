import React from 'react';
import { render as clientRender } from '@divkitframework/divkit/client-hydratable';
import type { DivKitProps } from '../typings/divkit';

export function DivKit(props: DivKitProps) {
    const ref = React.useRef(null);

    React.useEffect(() => {
        if (ref.current) {
            const instance = clientRender({
                ...props,
                target: ref.current,
                hydrate: true
            });

            return () => instance.$destroy();
        }
    }, [
        props
    ]);

    return <div ref={ref} suppressHydrationWarning dangerouslySetInnerHTML={{ __html: '' }} />;
}
