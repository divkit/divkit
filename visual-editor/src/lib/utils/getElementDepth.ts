export function getElementDepth(elem: HTMLElement): number {
    let count = 0;
    let temp: Node | null = elem;

    while (temp) {
        ++count;
        temp = temp.parentNode;
    }

    return count;
}
