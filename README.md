


### Specifikace požadavků

1. Základní požadavky

    -   aplikace zobrazí mapový podklad skladu, na který poté přenáší informace o pohybu vozíků
        -   základní mapový podklad je tvořen cestami, regály a výdejním/nákladním místem
        -   tento základní koncept můžete jakkoliv rozšířit – např. o parkování volných vozíků
        -   mapový podklad se načte po spuštění ze souboru (formát je na vašem uvážení)
        -   mapový podklad je možné přibližovat a oddalovat (zoom)
    -   systém regálů
        -   každý regál musí obsahovat alespoň jeden typ zboží (vč. informace o počtu kusů; může být i prázdný)
        -   aplikace umožní inicializaci skladu načtením dat ze souboru (formát je na vás)
        -   aplikace bude podporovat minimálně 50 druhů zboží
        -   po najetí/kliknutí na pozici regálu se zobrazí jeho aktuální obsah
    -   systém požadavků
        -   požadavek je soupis zboží a počet kusů, které se mají převézt na výdejní místo
        -   aplikace umožňuje načítání požadavků ze souboru i ruční zadávání
    -   systém vozíků
        -   vozíky si sami určují (pokud možno efektivní) cestu ke splnění požadavku
        -   každá cesta je definována seznamem regálů ke nakládá zboží
	         (výdejní místo – regál 1 - … - regál x - výdejní místo)
        -   je třeba zohlednit kapacitu vozíku
            -   postačí jedno kritérium (např. maximální počet zboží na vozík 5ks nebo maximální váha zboží 100kg)
            -   není třeba kritéria kombinovat
            -   můžete zvážit různé typy vozíky s různými hodnotami parametrů
        -   naložení zboží na vozík trvá stejnou dobu

2. Pohyb vozíků

    -   systém obsahuje vlastní hodiny, které lze nastavit na výchozí hodnotu a různou rychlost
    -   po načtení mapy a obsahu skladu začne systém zobrazovat zpracování jednotlivých požadavků 
	    (způsob zobrazení je na vaší invenci, postačí značka, kolečko, ...)
    -   symbol vozíku se postupně posunuje podle aktuálního času a požadavků
		(aktualizace zobrazení může být např. každých N sekund); pohyb spoje na trase je tedy simulován
    -   po najetí/kliknutí na symbol vozíku se zvýrazní trasa v mapě a zobrazí jeho aktuální náklad

3. Interaktivní zásahy

    -   Je možné definovat možnost uzavření uličky
        -   vozík si automaticky zvolí objízdnou trasu (a aktualizuje ji i ve vizualizaci trasy)
        -   objízdná trasa může vynechat některou ze zastávek, pokud není možné požadavek obsloužit jinak
 

### Podmínky vypracování projektu

-   Pro realizaci projektu použijte Java SE 8 nebo Java SE 11.
-   Pro grafické uživatelské rozhraní použijte JavaFX.
-   Pro překlad a spuštění projektu bude použita aplikace  [ant](http://ant.apache.org/ "http://ant.apache.org/").

-   Projekt bude tvořen následující adresářovou strukturou, která bude umístěna v kořenovém adresáři, jehož název odpovídá loginu vedoucího týmu:

    

    >     src/           - (adres.) zdrojové soubory (hierarchie balíků)
    >     data/          - (adres.) připravené datové soubory
    >     build/     	   - (adres.) přeložené class soubory
    >     doc/           - (adres.) vygenerovaná programová dokumentace
    >     dest/          - (adres.) umístění výsledného jar archivu (+ dalších potřebných souborů) 
    >     	                         po kompilaci aplikace, 
    >     	                         tento adresář bude představovat adresář spustitelné aplikace
    >     lib/   	       - (adres.) externí soubory a knihovny (balíky třetích stran, obrázky apod.),
    >     	                         které vaše aplikace využívá
    >     readme.txt     - (soubor) základní popis projektu (název, členové týmu, ...)
    >     rozdeleni.txt  - (soubor) soubor obsahuje rozdělení bodů mezi členy týmu; 
    >     	                         pokud tento soubor neexistuje, předpokládá se rovnoměrné rozdělení, 
    >     	                         vizte hodnocení projektu
    >     build.xml      - (soubor) build file pro aplikaci ant

-   Všechny zdrojové soubory musí obsahovat na začátku dokumentační komentář se jmény autorů a popisem obsahu.
-   Součástí projektu bude programová dokumentace vygenerovaná nástrojem javadoc. Dokumentace bude uložena v adresáři  doc.
-   Pokud vaše řešení vyžaduje další externí soubory (obrázky, jiné balíky apod.), umístěte je do adresáře  lib.
-   Přeložení aplikace:
    -   v kořenovém adresáři se provede příkazem  ant compile
    -   zkompilují se zdrojové texty, class soubory budou umístěny v adresáři  build
    -   vytvoří se jar archiv s názvem  ija-app.jar  v adresáři  dest; do tohoto adresáře se vytvoří/nakopírují další potřebné soubory a archivy
-   Spuštění aplikace:
    -   spouštět se bude vytvořený archiv jar
    -   v kořenovém adresáři se provede příkazem  ant run
