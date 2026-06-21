# Utilizarea Agenților AI în Dezvoltare

Acest document descrie modul în care au fost folosiți agenți AI (în special **Claude Code**) pe
parcursul dezvoltării proiectului. Sunt prezentate două categorii principale, fiecare cu câte
două subcategorii.

---

## 1. Documentație generată automat

### 1.1. Documentația din README

Documentația din [`README.md`](README.md) a fost realizată cu ajutorul **Claude Code**. Agentul a
explorat codul sursă al proiectului (controllere, configurări de securitate, `pom.xml`,
`package.json`, fișierele Docker) și a generat automat secțiunile complete: descrierea
proiectului, arhitectura, instrucțiunile de instalare, documentația API (cu toate endpoint-urile
extrase direct din controllere), capturile de ecran/diagramele și contribuțiile membrilor echipei.

### 1.2. Acest document

Inclusiv fișierul de față, [`AI Agents Dezvoltare.md`](AI%20Agents%20Dezvoltare.md), a fost
generat cu ajutorul **Claude Code**, pe baza indicațiilor echipei privind structura și conținutul
dorit.

**Beneficiile acestui approach:**

- **Rapiditate** — documentația extinsă (tabele de API, liste de funcționalități, diagrame) este
  produsă în câteva minute, în loc de ore de scriere manuală.
- **Caracter întreprinzător / proactiv** — agentul nu se limitează la ce i se cere explicit: scanează
  codul, deduce structura reală a aplicației și propune secțiuni și detalii relevante (de exemplu,
  exemple `curl`, tabele de endpoint-uri, descrierea stratificării backend-ului) pe care altfel
  echipa le-ar fi putut omite.
- **Consistență** — formatul, denumirile și stilul rămân uniforme pe tot parcursul documentației.
- **Actualizare ușoară** — modificările (limbă, conținut, structură) se aplică rapid, fără a
  rescrie manual secțiuni întregi.

---

## 2. Pair programming

A doua categorie de utilizare a agenților AI a fost **pair programming** — colaborarea directă cu
agentul pe sarcini de cod, în special pe partea de testare.

### 2.1. Teste unitare

Pe stratul de servicii, un membru al echipei (Iulian) a scris întâi câteva teste unitare de
referință. Apoi, **Claude Code** a fost pus să genereze, pe baza acestora, alte teste asemănătoare
pentru a crește acoperirea (coverage) codului.

Testele unitare se găsesc în
`backend/src/test/java/com/example/proiect_awbd/service/`:

- **`UserServiceTest`** — verifică logica de gestionare a utilizatorilor (înregistrare,
  autentificare, validări).
- **`FolderServiceTest`** — verifică operațiunile pe foldere (creare, mutare, ștergere, listare
  conținut).
- **`GroupServiceTest`** — verifică gestionarea grupurilor (creare, adăugare/eliminare membri).

### 2.2. Teste de integrare

Pornind de la același tipar — câteva teste scrise manual de echipă, apoi extinse de agent pentru
coverage — au fost realizate și testele de integrare din
`backend/src/test/java/com/example/proiect_awbd/integration/`:

- **`AuthFlowIntegrationTest`** — testează fluxul complet de autentificare (înregistrare → login →
  acces la endpoint-uri protejate cu token JWT), folosind o bază de date H2 in-memory pentru
  profilul de test.

**Workflow-ul de pair programming folosit:**

1. Un membru al echipei (Iulian) a scris manual câteva teste de referință, stabilind tiparul și
   convențiile.
2. **Claude Code** a analizat aceste teste și a generat alte teste asemănătoare pentru cazurile
   neacoperite, mărind astfel coverage-ul.
3. Echipa a revizuit și validat testele generate, asigurându-se că reflectă corect comportamentul
   așteptat al aplicației.

Acoperirea de cod este măsurată cu **JaCoCo** (raport disponibil în
`backend/target/site/jacoco/index.html` după rularea `./mvnw test`).
