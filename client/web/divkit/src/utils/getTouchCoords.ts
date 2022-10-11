export interface Coords {
    x: number;
    y: number
}

export function getTouchCoords(event: TouchEvent): Coords {
    const firstEvent = event.touches[0];
    const x = firstEvent.clientX || firstEvent.pageX;
    const y = firstEvent.clientY || firstEvent.pageY;

    return { x, y };
}
