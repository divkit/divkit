export function filterHTMLElements(it: Node): it is HTMLElement {
    return it instanceof HTMLElement;
}
