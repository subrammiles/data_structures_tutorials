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
    //cat =>3%2=1
    //rat =>3%2=1
    //dog =>3%2=1
    //art =>3%2=1
    //This is a bad hash approch.For every key hash,it is returning 1
    return index;
  }
}

const hashMap1 = new NaiveHashMap();

hashMap1.set('cat', 2);
hashMap1.set('rat', 7);
hashMap1.set('dog', 1);
hashMap1.set('art', 8);

console.log(hashMap1.buckets);
console.log(hashMap1.get('art')); // 8 this one is ok
console.log(hashMap1.get('tiger'));// 8. got overwritten by art 
console.log(hashMap1.get('lion'));// 8. got overwritten by art
console.log(hashMap1.get('ox'));// 8. got overwritten by art

/*
What is wrong with `NaiveHashMap` is that...

1) Hash function generates many duplicates. E.g.
hash('cat') // 3
hash('dog') // 3
This hash implementation will cause a lot of collisions.

2) Collisions are not handled at all. 
Both cat and dog will overwrite each other on position 3 of the 
Array (bucket#1).

3) Size of the Array even if we get a better hash function, 
we will get duplicates because the Array has a size of 3, 
which less than the number of elements that we want to fit. 
We want to have an initial capacity that is well beyond what we need to fit.

*/
