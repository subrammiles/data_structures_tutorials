/*
 * Trie (prefix tree). insert / search / startsWith are O(L) for a word of length L.
 * Each node counts how many words pass through it, which makes delete and prefix counts easy.
 */
const assert = require('node:assert/strict');

const newNode = () => ({ children: new Map(), isEnd: false, prefixCount: 0 });

class Trie {
  root = newNode();

  insert(word) {
    if (this.search(word)) return;
    let n = this.root;
    for (const ch of word) {
      if (!n.children.has(ch)) n.children.set(ch, newNode());
      n = n.children.get(ch);
      n.prefixCount++;
    }
    n.isEnd = true;
  }

  #walk(s) {
    let n = this.root;
    for (const ch of s) {
      n = n.children.get(ch);
      if (!n) return null;
    }
    return n;
  }

  search(word) {
    return this.#walk(word)?.isEnd ?? false;
  }

  startsWith(prefix) {
    return this.#walk(prefix) !== null;
  }

  countWordsWithPrefix(prefix) {
    return this.#walk(prefix)?.prefixCount ?? 0;
  }

  delete(word) {
    if (!this.search(word)) return false;
    let n = this.root;
    for (const ch of word) {
      const child = n.children.get(ch);
      if (--child.prefixCount === 0) {
        n.children.delete(ch); // nobody else uses this branch
        return true;
      }
      n = child;
    }
    n.isEnd = false;
    return true;
  }

  wordsWithPrefix(prefix) {
    const out = [];
    const collect = (n, path) => {
      if (n.isEnd) out.push(path);
      for (const ch of [...n.children.keys()].sort()) collect(n.children.get(ch), path + ch);
    };
    const start = this.#walk(prefix);
    if (start) collect(start, prefix);
    return out;
  }
}

const t = new Trie();
['car', 'cat', 'cart', 'dog'].forEach((w) => t.insert(w));
assert.ok(t.search('car'));
assert.equal(t.search('ca'), false);
assert.ok(t.startsWith('ca'));
assert.equal(t.countWordsWithPrefix('ca'), 3);
assert.deepEqual(t.wordsWithPrefix('car'), ['car', 'cart']);
assert.ok(t.delete('car'));
assert.equal(t.search('car'), false);
assert.ok(t.search('cart'));
t.delete('dog');
assert.equal(t.startsWith('d'), false);
assert.equal(t.countWordsWithPrefix('c'), 2);
console.log('all checks passed');
