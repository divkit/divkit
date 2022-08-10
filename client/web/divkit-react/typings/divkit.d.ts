import { render as clientRender } from '@yandex-int/divkit/client-hydratable';

export type DivkitProps = Omit<Parameters<typeof clientRender>[0], 'hydrate' | 'target'>;

export function Divkit(props: DivkitProps): JSX.Element;
