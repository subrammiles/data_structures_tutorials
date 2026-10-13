"""Trie (prefix tree): every path from the root spells a prefix.

insert / search / starts_with are O(L) for a word of length L, no matter how many words are stored.
A set can't answer "which words start with 'ca'?" - a trie can.
Uses: autocomplete, spell check, longest-prefix match in IP routing, word games.
"""


class Node:
    def __init__(self):
        self.children = {}
        self.is_end = False
        self.prefix_count = 0  # how many words pass through this node


class Trie:
    def __init__(self):
        self.root = Node()

    def insert(self, word):
        if self.search(word):
            return  # keep counts correct for duplicates
        n = self.root
        for ch in word:
            n = n.children.setdefault(ch, Node())
            n.prefix_count += 1
        n.is_end = True

    def _walk(self, s):
        n = self.root
        for ch in s:
            n = n.children.get(ch)
            if n is None:
                return None
        return n

    def search(self, word):
        n = self._walk(word)
        return n is not None and n.is_end

    def starts_with(self, prefix):
        return self._walk(prefix) is not None

    def count_words_with_prefix(self, prefix):
        n = self._walk(prefix)
        return n.prefix_count if n else 0

    def delete(self, word):
        if not self.search(word):
            return False
        n = self.root
        for ch in word:
            child = n.children[ch]
            child.prefix_count -= 1
            if child.prefix_count == 0:  # nobody else uses this branch: cut it off
                del n.children[ch]
                return True
            n = child
        n.is_end = False
        return True

    def words_with_prefix(self, prefix):
        out, start = [], self._walk(prefix)

        def collect(n, path):
            if n.is_end:
                out.append(path)
            for ch in sorted(n.children):
                collect(n.children[ch], path + ch)

        if start:
            collect(start, prefix)
        return out


if __name__ == "__main__":
    t = Trie()
    for w in ("car", "cat", "cart", "dog"):
        t.insert(w)
    assert t.search("car") and not t.search("ca") and t.starts_with("ca")
    assert t.count_words_with_prefix("ca") == 3
    assert t.words_with_prefix("car") == ["car", "cart"]
    assert t.delete("car") and not t.search("car") and t.search("cart")
    t.delete("dog")
    assert not t.starts_with("d")
    assert t.count_words_with_prefix("c") == 2
    print("all checks passed")
