import React from 'react';
import { render as serverRender } from '@yandex-int/divkit/server';
import { render as clientRender } from '@yandex-int/divkit/client-hydratable';
import { DivkitProps } from '../typings/divkit';

export function Divkit(props: DivkitProps) {
    if (process.env.IS_SERVER) {
        return <div
            dangerouslySetInnerHTML={{
                __html: serverRender(props)
            }} />;
    }
    // eslint-disable-next-line react-hooks/rules-of-hooks
    const ref = React.useRef(null);

    // eslint-disable-next-line react-hooks/rules-of-hooks
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
