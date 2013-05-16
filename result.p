nonSeasideCity(a2).
portCity(a4).
recreational(a1).
nonSeasideCity(a3).
portCity(a8).
recreational(a3).
rainyCity(a9).
has(a7, a7).
has(a9, a6).
onSea(a8, a6).
beach(X1) :- nonSeasideCity(X1).
rainyCity(X1) :- seasideCity(X1), onSea(X1, X2), portCity(X3), onSea(X1, X3), has(X2, X1), portCity(X2).
seasideCity(X1) :- seasideCity(X1), has(X1, X2), recreationalCity(X1), nonSeasideCity(X1), has(X1, X2), seasideCity(X1), not recreational(X2).
recreationalCity(X1) :- recreationalCity(X1), seasideCity(X1), rainyCity(X2), has(X1, X2), not onSea(X1, X2), not recreationalCity(X2), not has(X2, X1), not rainyCity(X1).
onSea(X1, X2) :- onSea(X1, X2), not has(X2, X1), not onSea(X2, X1), not nonSeasideCity(X2).
portCity(X1) :- onSea(X1, X2), not portCity(X1), not beach(X1), not seasideCity(X1), not rainyCity(X1).
has(X1, X2) :- rainyCity(X1), beach(X2), not onSea(X2, X1).
portCity(X1) :- rainyCity(X1), not portCity(X1).
seasideCity(X1) :- seasideCity(X1), not recreationalCity(X1), not beach(X1).
portCity(X1) :- rainyCity(X1).
