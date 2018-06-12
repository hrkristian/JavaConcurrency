# JavaConcurrency
Øvelser basert på kapitler i boka Java Concurrency in Practice

## Indeks
<ol>
  <li>Generelle begreper</li>
  <li>Java-spesifikke begreper</li>
  <li>Kapittel 2 - Thread Safety</li>
  <li>Kapittel 3 - Sharing Objects</li>
  <li></li>
</ol>

---

## 1. Generelle begreper
### Mutable / Immutable
Mutable betyr objektet/variabelen kan endres. I java betyr dette variabler satt med final, og objekter der objektets tilstand ikke kan endres (altså objektets innhold).<br>
For at et objekts tilstand ikke kan endres må variabler være *final* eller *effectively final*, dette må også være sant rekursivt for eventuelle underobjekter.
```
(...) {
  final int immutableIntegerVariable = 10;
  ImmutableClass ic = new ImmutableClass(20);
}
class ImmutableClass() {
  final int y;
  ImmutableClass(int y) { this.y = y; }
}
```
### Context switch
Disse skjer når CPU scheduleren må bytte ut en kjørende tråd med en annen. Hver tråd kjører i en gitt "context", 
feks ligger det variabler i registerne som må byttes ut.   
Dette har ytelses-kostnader.

### Volatile variable

### Correctness

###  Atomicity


### Race condition
En race condition oppstår når en operasjons "*correctness*" beror seg på timingen, 
altså kan uheldig timing føre til tap av *correctness*. 

### Lazy initialisation


### Mutex
En *mutual exclusion lock* betyr bare én tråd kan "eie" en låst blokk med kode. I java innebærer dette en *synchronized* blokk.  
Dette opprettholder "atomicity" av innholdet i blokkene.

### Reentrancy
Reentrancy er at en tråd som har en lås på en blokk kan opprette en ny lås på samme blokk.
Altså at låsing skjer på tråd-basis.    
Java (JVM) implementerer en lås-teller, og hver gang en tråd på nytt går inn i samme synkroniserte blokk telles den opp.
Når tråden så går ut av blokken telles lås-telleren ned. Låsen forsvinner når telleren når 0.  
Dette betyr blant annet en tråd kan gjøre rekursive kall på synkroniserte metoder. 

### Visibility
Visibility er hvorvidt en variabels synlige verdi er den tiltenkte (av programmereren) på et gitt tidspunkt.  
Flere egenskaper ved moderne CPU'er tillater kompilatorer å optimalisere kjøring av programmer ved å 
endre rekkefølgen på handlinger. Det er derfor ikke garantert at to verdi-tilordninger vil skje slik de
er definert i programkoden; kompilatoren gjør som det føler for så lenge resultatet er det forventede *på én tråd*.  
Når data ikke er som forventet kalles det *Stale Data*; utgått data. 

### Publishing
Publishing er å gjøre et objekt eller en klasses *state* tilgjengelig til andre deler av programmet og/eller tråder. 

### Thread confinement
#### Ad-hoc thread confinement
Når ansvaret for å holde objekter privat til tråden.
#### Stack confinement
Å holde objekter private ved å sørge for at referanser til de bare eksisterer i stack'en; gjerne lokale variabler.   
Primitive variabler er typisk stack confined, gitt at de er private og ingen setter-metode eksisterer; en verdi 
kan leses men referansen til verdien i minnet er ikke mulig å få tak i.

---

## 2. Java-spesifikke begreper
### Timer
Timer er en klasse i Java som kjører på sin egen tråd, og har ansvar for å starte og kjøre definerte jobber på gitte tidspunkt.   
Slike oppgaver bør jobbe mot *thread-safe* objekter, istedenfor å programmere hele programmet rundt et usikkert objekt.

### Servlet
Servlets er et rammeverk for levering av data til klienter på internett.

### Remote Method Invocation
RMI er å kalle på metoder i objekter som eksisterer i en annen JVM.

### *syncronized*
Keyword i java som sikrer variabel- og metode-*locking*. Altså, lese- og skrivetilgang gis bare til én tråd om gangen.



---

## Kapittel 2 - Thread Safety
Thread-safety er å skrive kode som takler lese- og skrivetilgang på objekter med *shared, mutable states.*
Et program kan anses usikkert når en mutable variabel kan aksesseres av flere tråder. For å unngå dette kan man gjøre følgende:
- Ikke del aksess over flere tråder
- Gjør variabelen immutable
- Bruk synkronisering

En klasse anses som thread-safe når den kan bli aksessert fra flere tråder samtidig, uten at koden i disse trådene trenger å ta hensyn til synkronisering eller annen koordinering.

``` 
–––––––––––––––––––––––––––––––––––––
# ThreadSafeClassExample.java
–––––––––––––––––––––––––––––––––––––
public class ThreadSafeClassExample {
  public static void main(String[] args) {
    final ThreadSafeClass tsc = new ThreadSafeClass(10);
    new Thread(() -> {
      for (int i = 0; i < 100; i++)
        tsc.changeUnsafeInteger( tsc.getUnsafeInteger()+2 );
    }).start();
    new Thread(() -> {
      for (int i = 0; i < 100; i++)
        tsc.changeUnsafeInteger( tsc.getUnsafeInteger()+2 );
    }).start();
  }
}
–––––––––––––––––––––––––––––––––––––
# ThreadSafeClass.java
–––––––––––––––––––––––––––––––––––––
public ThreadSafeClass{
  private int unsafeInteger;
  public ThreadSafeClass(int unsafeInteger) {this.unsafeInteger = unsafeInteger}
  syncronized public int getUnsafeInteger() { return unsafeInteger; }
  syncronized public void changeUnsafeInteger(int newIntegerValue) {
    this.unsafeInteger = newIntegerValue;
  }
}
```

Det er viktig å veie ytelse og simplisitet opp mot hverandre når man designer for tråd-sikkerhet. 
Ytelse er ikke alltid det viktigste, om følgen er et system som har blitt alt for komplekst. 

Unngå låsing på lange operasjoner, eller operasjoner med usikker lengde, som for eksempel nettverkstrafikk.


## Kapittel 3 - Sharing Objects

## Kapittel 4 - Composing Objects

### Designing a thread safe class
Design-prosessen når man skal lage tråd-sikre klasser er:
* Identifiser hvilke variabler som bestemmer objektets tilstand
* Identifiser hvilke verdier (*invariants*) disse variablene kan være i
* Bestem hvordan paralell tilgang på objektets tilstand skal foregå.

#### State dependency
State dependency er at en operasjon har forutsetninger (*pre-conditions*) for å utføres.  

#### State ownership
Et objekt sin tilstand er bestemt av de data objektet eier. 
```
class IndependentClass {
  private int stateInteger = 5;
}
```
Data kan deles mellom klasser, dette er delt eierskap.
```
class CodependentClassA {
  Integer stateInteger;
  CodependentClassA(int initInteger) {
    this.stateInteger = initInteger;
  }
}
class CodependentClassB {
  Integer stateInteger;
  CodependentClassB(CodependentClassA theFirstClass) {
    this.stateInteger = theFirstClass.stateInteger;
  }
}
```

### Instance Confinement

### Delegating thread safety


### Adding functionality to existing thread safe classes 