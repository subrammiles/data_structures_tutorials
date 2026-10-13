class NaiveHashMap {
    /*We have an initial capacity of 2 (two buckets)
     */
  constructor(initialCapacity = 2) {
    this.buckets = new Array(initialCapacity);
  }
  set(key, value) {
    const index = this.getIndex(key);
    this.buckets[index] = value;
  }
  get(key) {
    const index = this.getIndex(key);
    return this.buckets[index];
  }
  hash(key) {
    return key.toString().length;
  }
  getIndex(key) {
    const indexHash = this.hash(key);
    const index = indexHash % this.buckets.length;
    return index;
  }
}

const hashMap1 = new NaiveHashMap(4);

hashMap1.set('cat', 2);
hashMap1.set('tiger', 7);
hashMap1.set('lion', 1);
hashMap1.set('ox', 8);

console.log(hashMap1.buckets);
console.log(hashMap.get('cat'));
console.log(hashMap.get('tiger'));
console.log(hashMap.get('lion'));
console.log(hashMap.get('ox'));
