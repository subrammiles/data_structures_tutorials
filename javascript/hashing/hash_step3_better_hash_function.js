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
    /*
    Instead of using the string’s length, 
    let’s sum each character ascii code.
    */
    let hashValue = 0;
    const stringKey = key.toString();

    for (let index = 0; index < stringKey.length; index++) {
      const charCode = stringKey.charCodeAt(index);
      hashValue += charCode;
    }

   /* hash Value of 'cat'= // 312  (c=99 + a=97 + t=116)
     hash Value of 'dog' // 314 (d=100 + o=111 + g=103)*/


    return hashValue;
  }
  getIndex(key) {
    const indexHash = this.hash(key);
    const index = indexHash % this.buckets.length;

    return index;
  }
}

const hashMap1 = new NaiveHashMap();

hashMap1.set("cat", 2);
hashMap1.set("rat", 7);
hashMap1.set("dog", 1);
hashMap1.set("art", 8);

console.log(hashMap1.buckets);
console.log(hashMap1.get("art"));
console.log(hashMap1.get("tiger"));
console.log(hashMap1.get("lion"));
console.log(hashMap1.get("ox"));

/*
there’s still an issue! Because rat and art are both 327, collision!
*/

/*
We can fix that by offsetting the sum with the position
*/

//try expermenting
function hashImproced1(key) {
    let hashValue = 0;
    const stringKey = `${key}`;
  
    for (let index = 0; index < stringKey.length; index++) {
      const charCode = stringKey.charCodeAt(index);
      hashValue += charCode << (index * 8);
    }
  /**
   
  // r = 114 or 0x72; a = 97 or 0x61; t = 116 or 0x74
hash('rat'); // 7,627,122 (r: 114 * 1 + a: 97 * 256 + t: 116 * 65,536) or in hex: 0x746172 (r: 0x72 + a: 0x6100 + t: 0x740000)
hash('art'); // 7,631,457 or 0x747261
   */
    return hashValue;
  }

  //
