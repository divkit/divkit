export function rectAngleIntersection(width: number, height: number, angle: number): {
    from: {
        x: number;
        y: number;
    };
    to: {
        x: number;
        y: number;
    };
} {
    const dx = width / 2;
    const dy = height / 2;
    const tan = Math.tan(-angle);

    const y = tan * dx;
    const x = dy / tan;

    if (Math.abs(y) <= dy + 1e-6) {
        return {
            from: {
                x: 0,
                y: dy - y
            },
            to: {
                x: width,
                y: dy + y
            }
        };
    }

    return {
        from: {
            x: dx + x,
            y: height
        },
        to: {
            x: dx - x,
            y: 0
        }
    };
}
