<script lang="ts">
    import { createEventDispatcher, type ComponentType, tick } from 'svelte';
    import { flip } from 'svelte/animate';
    import { slide } from 'svelte/transition';
    import MoveItem2 from './MoveItem2.svelte';

    interface Item {
        __key?: number | undefined;
    }

    export let values: object[] | undefined;
    export let itemView: ComponentType;
    export let readOnly = false;

    $: list = values as Item[] | undefined;

    const dispatch = createEventDispatcher();

    let listElem: HTMLElement;
    let movedItem: Item | undefined;
    let moveEnded = false;

    $: if (list) {
        const known = new Set<number>();
        let nextId = 0;
        list.forEach(val => {
            if (typeof val.__key === 'number') {
                known.add(val.__key);
            }
        });
        const genId = () => {
            // eslint-disable-next-line no-constant-condition
            while (1) {
                const id = nextId++;
                if (!known.has(id)) {
                    known.add(id);
                    return id;
                }
            }
        };
        list.forEach(val => {
            if (!('__key' in val)) {
                val.__key = genId();
            }
        });
    }

    function deleteAction(key: number | undefined): void {
        if (!list || typeof key !== 'number') {
            return;
        }

        const item = list.find(val => val.__key === key);
        values = list = list.filter(val => val.__key !== key);
        if (!list.length) {
            values = list = undefined;
        }
        dispatch('change');
        dispatch('delete', {
            item
        });
    }

    function moveStart(key: number | undefined, event: CustomEvent<{
        event: PointerEvent;
        elem: HTMLElement;
    }>): void {
        if (!list || list.length < 2 || typeof key !== 'number') {
            return;
        }

        moveEnded = false;
        const elem = event.detail.elem.parentElement;
        if (!elem || !(elem instanceof HTMLElement)) {
            return;
        }
        const elem2 = elem;
        const listTop = listElem.getBoundingClientRect().top;
        const startOffsetY = event.detail.event.clientY - elem.getBoundingClientRect().top;
        const index = list.findIndex(val => val.__key === key);
        if (index === -1) {
            return;
        }
        const item = movedItem = list[index];
        let prevPosition = index;

        const boxes = Array.from(listElem.children).map(it => it.getBoundingClientRect());
        const filteredBoxes = boxes.filter((_it, i) => i !== index);
        const currentHeight = boxes[index].height;
        const gap = boxes.length > 1 ? boxes[1].top - boxes[0].bottom : 0;

        function pointermove(event: PointerEvent): void {
            event.preventDefault();

            let bestIndex = 0;
            let newTop = listTop + filteredBoxes[0].height + gap;
            for (let i = 1; i <= filteredBoxes.length; ++i) {
                if (event.clientY - startOffsetY > newTop - filteredBoxes[i - 1].height / 2) {
                    bestIndex = i;
                }
                if (i < filteredBoxes.length) {
                    newTop += filteredBoxes[i].height + gap;
                }
            }

            const decidedTop = listTop +
                filteredBoxes
                    .slice(0, bestIndex)
                    .reduce((acc, item) => acc + item.height, 0) +
                gap * bestIndex;
            let offset = event.clientY - startOffsetY - decidedTop;
            offset = Math.min(offset, boxes[boxes.length - 1].bottom - decidedTop - currentHeight);
            offset = Math.max(offset, boxes[0].top - decidedTop);

            elem2.style.transform = `translateY(${offset}px)`;

            if (prevPosition === bestIndex || !list) {
                return;
            }

            const newList = list.slice();
            newList.splice(prevPosition, 1);
            newList.splice(bestIndex, 0, item);
            list = values = newList;

            prevPosition = bestIndex;

            dispatch('reorder', {
                values: list
            });
        }

        function pointerup(event: PointerEvent): void {
            event.preventDefault();
            document.body.removeEventListener('pointermove', pointermove);
            document.body.removeEventListener('pointerup', pointerup);
            document.body.removeEventListener('pointercancel', pointerup);
            moveEnded = true;
            tick().then(() => {
                elem2.style.transform = '';
            });
        }

        document.body.addEventListener('pointermove', pointermove);
        document.body.addEventListener('pointerup', pointerup);
        document.body.addEventListener('pointercancel', pointerup);
    }
</script>

<ul class="move-list__list" bind:this={listElem}>
    {#each (list || []) as item (item.__key)}
        {@const isMoving = movedItem && item.__key === movedItem.__key}

        <li
            class="move-list__item"
            class:move-list__item_moved={isMoving}
            class:move-list__item_animate={isMoving && moveEnded}
            transition:slide|local={{ duration: 200 }}
            animate:flip|local={{ duration: isMoving ? 0 : 200 }}
        >
            <MoveItem2
                bind:value={item}
                {itemView}
                {readOnly}
                isMoving={isMoving && !moveEnded}
                isReoderInProgress={movedItem && !moveEnded}
                canBeMoved={list && list.length >= 2}
                on:change
                on:delete={() => deleteAction(item.__key)}
                on:movestart={event => moveStart(item.__key, event)}
            />
        </li>
    {/each}
</ul>

<style>
    .move-list__list {
        display: flex;
        flex-direction: column;
        gap: 4px;
        flex: 1 1 auto;
        margin: 0 0 12px;
        padding: 0;
        list-style: none;
    }

    .move-list__item {
        /* prevent fix_position() (animate:flip) from svelte,
        because we need size of the element to push down others */
        position: relative !important;
        z-index: 1;
    }

    .move-list__item_moved {
        z-index: 2;
    }

    .move-list__item_animate {
        transition: transform .1s ease-in-out;
    }
</style>
