let res: boolean;

/**
 * Based on the Chris Smith work in Modernizr
 * https://github.com/Modernizr/Modernizr/blob/master/feature-detects/css/flexgap.js
 * MIT License
 */
export function hasGapSupport(): boolean {
    if (res !== undefined) {
        return res;
    }

    const flex = document.createElement('div');
    flex.style.position = 'absolute';
    flex.style.display = 'flex';
    flex.style.flexDirection = 'column';
    flex.style.gap = '1px';

    flex.appendChild(document.createElement('div'));
    flex.appendChild(document.createElement('div'));

    document.body.appendChild(flex);
    res = flex.scrollHeight === 1;
    document.body.removeChild(flex);

    return res;
}
