lugar_seguro(Mama, Papa, Lugar) :-
    local(Lugar),
    Lugar \= Mama,
    Lugar \= Papa.
