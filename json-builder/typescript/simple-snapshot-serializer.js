// The standard snapshot serializer is too accurate for us
// It stores all the "undefined" values, of which there are many in our data
module.exports = {
    serialize(val) {
        return JSON.stringify(val, null, 2);
    },
    test(val) {
        return true;
    }
}
