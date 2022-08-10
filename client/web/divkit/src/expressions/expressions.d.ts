import type { Node } from './ast';

export function parse(str: string, opts?: {
    startRule?: 'JsonStringContents' | 'start';
}): Node;
