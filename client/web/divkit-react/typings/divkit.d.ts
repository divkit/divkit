import { render as clientRender } from '@divkitframework/divkit/client-hydratable';

export type DivKitProps = Omit<Parameters<typeof clientRender>[0], 'hydrate' | 'target'>;

export function DivKit(props: DivKitProps): JSX.Element;
