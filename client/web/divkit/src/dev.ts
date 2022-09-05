import Root from './components/Root.svelte';

const json = {
    "templates": {},
    "card": {
            "type": "div2",
            "log_id": "snapshot_test_card",
            "states": [
                {
                    "state_id": 0,
                    "div": {
                        "type": "text",
                        "text": "Hello world"
                    }
                }
            ]
        }
};

window.root = new Root({
    target: document.body,
    props: {
        id: 'abcde',
        json,
        onStat(arg) {
            console.log(arg);
        }
    }
});
