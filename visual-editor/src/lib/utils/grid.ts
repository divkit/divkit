export interface GridProps {
    columnsStart: number[];
    rowsStart: number[];
    columns: number[];
    rows: number[];
    columnsCount: number;
    rowsCount: number;
}

export function getGridProps(grid: HTMLElement): GridProps {
    const children = Array.from(grid.children);
    const columns: number[] = [];
    const rows: number[] = [];
    const columnsGuess: number[] = [];
    const rowsGuess: number[] = [];
    const gridBbox = grid.getBoundingClientRect();
    const gridComputed = getComputedStyle(grid);
    const gridPaddingLeft = parseFloat(gridComputed.paddingLeft);
    const gridPaddingTop = parseFloat(gridComputed.paddingTop);

    children.forEach((child, index) => {
        const childBbox = child.getBoundingClientRect();
        const computed = getComputedStyle(child);
        const columnStart = Number(computed.gridColumnStart) - 1;
        const rowStart = Number(computed.gridRowStart) - 1;
        const columnSpan = computed.gridColumnEnd.startsWith('span ') ? Number(computed.gridColumnEnd.split(' ')[1]) : 1;
        const rowSpan = computed.gridRowEnd.startsWith('span ') ? Number(computed.gridRowEnd.split(' ')[1]) : 1;
        const leftMargin = parseFloat(computed.marginLeft);
        const topMargin = parseFloat(computed.marginTop);
        let width = childBbox.width + leftMargin + parseFloat(computed.marginRight);
        let height = childBbox.height + topMargin + parseFloat(computed.marginBottom);

        if (index === 0) {
            width += childBbox.left - gridBbox.left - leftMargin - gridPaddingLeft;
            height += childBbox.top - gridBbox.top - topMargin - gridPaddingTop;
        }

        if (columnSpan === 1) {
            columns[columnStart] = Math.max(columns[columnStart] || 0, width);
        } else {
            for (let i = 0; i < columnSpan; ++i) {
                columnsGuess[columnStart + i] = width / columnSpan;
            }
        }
        if (rowSpan === 1) {
            rows[rowStart] = Math.max(rows[rowStart] || 0, height);
        } else {
            for (let i = 0; i < rowSpan; ++i) {
                rowsGuess[rowStart + i] = height / rowSpan;
            }
        }
    });

    const columnsCount = Math.max(columns.length, columnsGuess.length);
    const rowsCount = Math.max(rows.length, rowsGuess.length);

    for (let i = 0; i < columnsCount; ++i) {
        if (!columns[i] && columnsGuess[i]) {
            columns[i] = columnsGuess[i];
        }
    }
    for (let i = 0; i < rowsCount; ++i) {
        if (!rows[i] && rowsGuess[i]) {
            rows[i] = rowsGuess[i];
        }
    }

    const columnsStart = columns.reduce((acc, item) => {
        acc.push((acc[acc.length - 1] || 0) + item);
        return acc;
    }, [0]);
    const rowsStart = rows.reduce((acc, item) => {
        acc.push((acc[acc.length - 1] || 0) + item);
        return acc;
    }, [0]);

    return {
        columnsStart,
        rowsStart,
        columns,
        rows,
        columnsCount,
        rowsCount
    };
}

export function getGridPosition(node: HTMLElement) {
    const computed = getComputedStyle(node);

    const columnStart = Number(computed.gridColumnStart) - 1;
    const rowStart = Number(computed.gridRowStart) - 1;
    const columnSpan = computed.gridColumnEnd.startsWith('span ') ? Number(computed.gridColumnEnd.split(' ')[1]) : 1;
    const rowSpan = computed.gridRowEnd.startsWith('span ') ? Number(computed.gridRowEnd.split(' ')[1]) : 1;

    return {
        columnStart,
        columnSpan,
        rowStart,
        rowSpan
    };
}
